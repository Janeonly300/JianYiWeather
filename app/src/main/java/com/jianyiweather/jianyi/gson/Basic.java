package com.jianyiweather.jianyi.gson;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
public class Basic {
        @SerializedName("city")
        public String cityName;
        @SerializedName("id")
        public String weatherId;
        public Update update;

        public static class Update {

            @SerializedName("loc")
            public String updateTime;

        }
}

