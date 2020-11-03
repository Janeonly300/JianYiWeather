package com.jianyiweather.jianyi.utils;



import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class HttpUtils {

    /**
     * 发送Http请求
     * @param address
     * @param callback
     */
    public static void sendHttpRequest(String address, Callback callback){
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(address)
                .get()
                .build();
        client.newCall(request).enqueue(callback);

    }
}
