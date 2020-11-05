package com.jianyiweather.jianyi.utils;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.jianyiweather.jianyi.db.City;
import com.jianyiweather.jianyi.db.County;
import com.jianyiweather.jianyi.db.Province;
import com.jianyiweather.jianyi.gson.Weather;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Utillity {

    /**
     * 处理省级数据
     * @param response
     * @return
     */
    public static boolean handleProvinceResponse(String response){
        try {
            if (!TextUtils.isEmpty(response)){
                JSONArray jsonArray = new JSONArray(response);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    Province province = new Province();
                    province.setProvinceName(jsonObject.getString("name"));
                    province.setProvinceCode(jsonObject.getInt("id"));
                    province.save();
                }

            }
            return true;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 处理市级数据
     * @param response
     * @param ProvinceId
     * @return
     */
    public static boolean handleCityResponse(String response,int ProvinceId){
        if(!(TextUtils.isEmpty(response))){
            try {
                JSONArray jsonArray = new JSONArray(response);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    City city = new City();
                    city.setCityCode(jsonObject.getInt("id"));
                    city.setCityName(jsonObject.getString("name"));
                    city.setProvinceId(ProvinceId);
                    city.save();
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 处理县数据
     * @param response
     * @param cityId
     * @returnb
     */
    public static boolean handleCountyResponse(String response,int cityId){
        if(!(TextUtils.isEmpty(response))){
            try {
                JSONArray jsonArray = new JSONArray(response);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    County county = new County();
                    county.setCityId(cityId);
                    county.setCountyCode(jsonObject.getInt("id"));
                    county.setCountyName(jsonObject.getString("name"));
                    county.setWeatherId(jsonObject.getString("weather_id"));
                    county.save();
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 将Json数据解析成weather对象
     * @param response
     * @return
     */
    public static Weather handWeatherResponse(String response){
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray hEweather = jsonObject.getJSONArray("HeWeather");
            //在解析成Json字符串
            String string = hEweather.getJSONObject(0).toString();
            Weather weather = new Gson().fromJson(string, Weather.class);
            return weather;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
