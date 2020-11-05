package com.jianyiweather.jianyi;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

import com.jianyiweather.jianyi.gson.Weather;
import com.jianyiweather.jianyi.utils.HttpUtils;
import com.jianyiweather.jianyi.utils.Utillity;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SharedPreferences pres = PreferenceManager.getDefaultSharedPreferences(this);
        if(pres.getString("weather",null)!=null){
            Intent intent = new Intent(this,WeatherActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private static final String TAG = "MainActivity";
}
