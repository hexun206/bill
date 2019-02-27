package com.huatu.teacheronline.bean;

import com.bluelinelabs.logansquare.annotation.JsonObject;

import java.io.Serializable;

/**
 * 广告实体类
 * Created by ljyu on 2016/6/2.
 */
@JsonObject(fieldDetectionPolicy = JsonObject.FieldDetectionPolicy.NONPRIVATE_FIELDS_AND_ACCESSORS)
public class AdBean implements Serializable {
    private String ad_title;//广告标题
    private String ad_url;//广告url
    private String ad_pic;//广告图片
    private String is_array;//类型（一开始表示是否传参） 1是需要带参数的url 0否不需要带参数的url 2 表示课程
    private String gg_rid;//广告的rid

    public String getAd_title() {
        return ad_title;
    }

    public void setAd_title(String ad_title) {
        this.ad_title = ad_title;
    }

    public String getAd_url() {
        return ad_url;
    }

    public void setAd_url(String ad_url) {
        this.ad_url = ad_url;
    }

    public String getAd_pic() {
        return ad_pic;
    }

    public void setAd_pic(String ad_pic) {
        this.ad_pic = ad_pic;
    }

    public String getIs_array() {
        return is_array;
    }

    public String getGg_rid() {
        return gg_rid;
    }

    public void setGg_rid(String gg_rid) {
        this.gg_rid = gg_rid;
    }

    public void setIs_array(String is_array) {
        this.is_array = is_array;
    }

    @Override
    public String toString() {
        return "AdBean{" +
                "ad_title='" + ad_title + '\'' +
                ", ad_url='" + ad_url + '\'' +
                ", ad_pic='" + ad_pic + '\'' +
                ", is_array='" + is_array + '\'' +
                ", gg_rid='" + gg_rid + '\'' +
                '}';
    }
}
