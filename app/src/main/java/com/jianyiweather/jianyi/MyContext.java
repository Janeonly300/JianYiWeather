package com.jianyiweather.jianyi;

import android.app.Application;
import android.content.Context;

import org.litepal.LitePal;

public class MyContext extends Application {
    private static Context context;
    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        //初始化LitePal
        LitePal.initialize(context);
    }

    public static Context getContext(){
        return context;
    }
}
