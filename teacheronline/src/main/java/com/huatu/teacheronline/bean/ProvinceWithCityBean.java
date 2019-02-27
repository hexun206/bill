package com.huatu.teacheronline.bean;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

import java.io.Serializable;
import java.util.List;

/**
 * Created by ljzyuhenda on 16/1/15.
 */
@JsonObject
public class ProvinceWithCityBean implements Comparable<ProvinceWithCityBean>,Serializable{//让自定义类ProvinceWithCityBean的对象变成“可比较的”对象，能被排序、查找等各类工具比较使用
    @JsonField
    public String Name;
    @JsonField
    public String Xzqh;
    @JsonField
    public String Header;
    @JsonField
    public List<ProvinceBean> cityList;

    @Override
    public int compareTo(ProvinceWithCityBean provinceWithCityBean) {
        return this.Header.compareTo(provinceWithCityBean.Header);
    }

    public List<ProvinceBean> getCityList() {
        return cityList;
    }

    public void setCityList(List<ProvinceBean> cityList) {
        this.cityList = cityList;
    }
}
