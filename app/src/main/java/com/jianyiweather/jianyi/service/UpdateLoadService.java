package com.jianyiweather.jianyi.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;

import com.jianyiweather.jianyi.gson.Weather;
import com.jianyiweather.jianyi.utils.HttpUtils;
import com.jianyiweather.jianyi.utils.Utillity;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class UpdateLoadService extends Service {
    public UpdateLoadService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        updatePic();
        updateWeather();
        AlarmManager manager = (AlarmManager)getSystemService(ALARM_SERVICE);
        int hour = 1000*60*60*8;//八小时
        long time = hour+ SystemClock.elapsedRealtime();
        Intent intent1 = new Intent(getApplicationContext(),UpdateLoadService.class);
        PendingIntent pendingIntent = PendingIntent.getService(getApplicationContext(),0,intent1,0);
        manager.cancel(pendingIntent);
        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,time,pendingIntent);

        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * 更新天气应用
     */
    private void  updateWeather(){
        SharedPreferences pres = PreferenceManager.getDefaultSharedPreferences(this);
        String weatherJson = pres.getString("weather", null);
        if(weatherJson!=null){
            //如果有缓存，直接解析缓存
            final Weather weather = Utillity.handWeatherResponse(weatherJson);
            String weatherId = weather.basic.weatherId;
            String weatherUrl = "http://guolin.tech/api/weather?cityid="+weatherId+"&key=bc0418b57b2d4918819d3974ac1285d9";
            //发送Http请求
            HttpUtils.sendHttpRequest(weatherUrl, new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {

                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    String string = response.body().string();
                    Weather weather1 = Utillity.handWeatherResponse(string);
                    if(weather1!=null && weather1.basic.equals("ok")){
                        //更新缓存
                        SharedPreferences.Editor edit = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit();
                        edit.putString("weather",string);
                        edit.apply();
                    }
                }
            });
        }
    }

    private void updatePic(){
        final String requestUrl = "http://guolin.tech/api/bing_pic";
        HttpUtils.sendHttpRequest(requestUrl, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String string = response.body().string();
                SharedPreferences.Editor edit = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit();
                edit.putString("bing_pic",string);
                edit.apply();
            }
        });
    }
}
