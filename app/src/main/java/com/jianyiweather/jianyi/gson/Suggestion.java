package com.jianyiweather.jianyi.gson;

import com.google.gson.annotations.SerializedName;

public class Suggestion {
    @SerializedName("comf")
    public Comfort comfort;
    @SerializedName("sport")
    public Sport sport;
    @SerializedName("cw")
    public CarWash carWash;

    public class Comfort{
        @SerializedName("txt")
        public String info;
    }
    public class Sport{
        @SerializedName("txt")
        public String info ;
    }
    public class CarWash{
        @SerializedName("txt")
        public String info;
    }
}
