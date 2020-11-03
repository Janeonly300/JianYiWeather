package com.jianyiweather.jianyi.db;

import org.litepal.LitePal;
import org.litepal.crud.LitePalSupport;

public class Province extends LitePalSupport {
    private String id;
    private String provinceName; //省名字
    private int provinceCode; //省代号

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProvinceName() {
        return provinceName;
    }

    public void setProvinceName(String provinceName) {
        this.provinceName = provinceName;
    }

    public int getProvinceCode() {
        return provinceCode;
    }

    public void setProvinceCode(int provinceCode) {
        this.provinceCode = provinceCode;
    }
}
