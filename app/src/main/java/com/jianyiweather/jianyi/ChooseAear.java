package com.jianyiweather.jianyi;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.jianyiweather.jianyi.db.City;
import com.jianyiweather.jianyi.db.County;
import com.jianyiweather.jianyi.db.Province;
import com.jianyiweather.jianyi.gson.Weather;
import com.jianyiweather.jianyi.utils.HttpUtils;
import com.jianyiweather.jianyi.utils.Utillity;

import org.jetbrains.annotations.NotNull;
import org.litepal.LitePal;
import org.litepal.crud.LitePalSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChooseAear extends Fragment {
    public static final int LEVEL_PROVINCE = 0;
    public static final int LEVEL_CITY = 1;
    public static final int LEVEL_COUNTY = 2;

    private static ProgressDialog progressDialog;

    private TextView titleText; //标题
    private Button btuBack; //回退按钮
    private ListView listView;
    private ArrayAdapter<String> adapter; //适配器
    private List<String> dataList = new ArrayList<>(); //数据

    /**
     * 省级列表
     */
    private List<Province> provincesList;

    /**
     * 市级列表
     */
    private List<City> cityList;

    /**
     * 县级列表
     */
    private List<County> countyList;

    /**
     * 选中的省份
     */
    private Province selectedProvince;
    /**
     * 当前选中城市
      */
    private City selectedCity;

    //当前选中等级
    private static int curLevel;


    public ChooseAear() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_choose_aear, container, false);
        initView(view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(LEVEL_PROVINCE == curLevel){ //如果当前为省级，则查询城市
                     selectedProvince = provincesList.get(i);
                    queryCity();
                }else if (LEVEL_CITY == curLevel){//如果当前市级，则查询县
                    selectedCity = cityList.get(i);
                    queryCounty();
                }else if (curLevel == LEVEL_COUNTY){
                   String weatherId = countyList.get(i).getWeatherId();
                    if(getActivity() instanceof MainActivity){
                        //获取天气Id
                        Intent intent = new Intent(getContext(), WeatherActivity.class);
                        intent.putExtra("weather_id",weatherId);
                        startActivity(intent);
                        getActivity().finish();
                    }else if(getActivity() instanceof WeatherActivity){
                        WeatherActivity weatherActivity = (WeatherActivity)getActivity();
                        weatherActivity.drawerLayout.closeDrawers();//关闭
                        weatherActivity.swipeRefreshLayout.setRefreshing(true);
                        weatherActivity.requestWeatherForServer(weatherId);
                    }
                }
            }
        });
        btuBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(LEVEL_CITY == curLevel){
                    queryProvince();
                }else if (LEVEL_COUNTY==curLevel){
                    queryCity();
                }
            }
        });
        queryProvince();
    }



    /**
     * 查询省级
     */
    private void queryProvince() {
        titleText.setText("中国");
        btuBack.setVisibility(View.GONE);
        provincesList = LitePal.findAll(Province.class);
            if(provincesList.size()>0){ //如果查询有结果
            dataList.clear();
            for(Province province:provincesList){
                dataList.add(province.getProvinceName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            curLevel = LEVEL_PROVINCE;
        }else{ //如果省份数据库查询无结果
            String address ="http://guolin.tech/api/china";
            queryForServer(address,"province");
        }
    }

    /**
     * 查询城市，如果没有 则发送请求查询
     */
    private void queryCity() {
        titleText.setText(selectedProvince.getProvinceName());
        btuBack.setVisibility(View.VISIBLE);
        cityList = LitePal.where("provinceId = ?",selectedProvince.getId()+"").find(City.class);
        if(cityList.size()>0){
            dataList.clear();
            for (City city : cityList) {
                dataList.add(city.getCityName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            curLevel = LEVEL_CITY;
        }else{
            //没有找到
            String address = "http://guolin.tech/api/china/"+selectedProvince.getProvinceCode();
            queryForServer(address,"city");
        }
    }

    /**
     * 查询县城，如果没有，则发送请求查询
     */
    private void queryCounty() {
        titleText.setText(selectedCity.getCityName());
        countyList = LitePal.where(" cityId = ?",selectedCity.getId()+"").find(County.class);
        if(countyList.size()>0){
            dataList.clear();
            for (County county:countyList){
                dataList.add(county.getCountyName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            curLevel = LEVEL_COUNTY;
        }else{
            //如果没找到
            String address = "http://guolin.tech/api/china/"+selectedProvince.getProvinceCode()+"/"+selectedCity.getCityCode();
            queryForServer(address,"county");
        }
    }

    /**
     * 网络发送请求查询
     * @param address
     * @param type
     */
    private void queryForServer(String address, final String type) {
        showProgress(getActivity());
        HttpUtils.sendHttpRequest(address, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
               getActivity().runOnUiThread(new Runnable() {
                   @Override
                   public void run() {
                       coloseProgress();
                       Toast.makeText(getContext(),"加载失败",Toast.LENGTH_SHORT).show();
                   }
               });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String string = response.body().string();
                boolean result = false;
                if("province".equals(type)){
                   result =  Utillity.handleProvinceResponse(string);
                }else if ("city".equals(type)){
                    result = Utillity.handleCityResponse(string,selectedProvince.getId());
                }else if ("county".equals(type)){
                    result = Utillity.handleCountyResponse(string,selectedCity.getId());
                }

                if(result){
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            coloseProgress();
                            if("province".equals(type)){
                                queryProvince();
                            }else if ("city".equals(type)){
                                queryCity();
                            }else if ("county".equals(type)){
                                queryCounty();
                            }
                        }
                    });
                }
            }
        });
    }

    private static void showProgress(Activity activity){
        if(progressDialog == null){
            progressDialog = new ProgressDialog(activity);
            progressDialog.setMessage("加载中.....");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }
    private static void coloseProgress(){
        if(progressDialog!=null){
            progressDialog.dismiss();
        }

    }

    private static final String TAG = "ChooseAear";

    /**
     * 初始化控件
     * @param view
     */
    private void initView(View view) {
        titleText = view.findViewById(R.id.title_text);
        btuBack = view.findViewById(R.id.btu_back);
        listView = view.findViewById(R.id.list_view);
        adapter = new ArrayAdapter<String>(getContext(),android.R.layout.simple_list_item_1,dataList);
        listView.setAdapter(adapter);
    }
}
