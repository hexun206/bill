package com.huatu.teacheronline.bean;

import com.bluelinelabs.logansquare.annotation.JsonObject;

import java.io.Serializable;

/**
 * 首页推荐课程
 * Created by 18250 on 2017/8/23.
 */
@JsonObject(fieldDetectionPolicy = JsonObject.FieldDetectionPolicy.NONPRIVATE_FIELDS_AND_ACCESSORS)
public class RecomBean implements Serializable {

    private int ActualPrice;
    private String TeacherDesc;
    private String buy_lives;
    private String scaleimg;
    private int rid;
    private String is_zhibo;
    private int Province;
    private int lessionCount;
    private String is_ax_Type;
    private String is_fufei;
    private int videoType;
    private String Title;
    private String activity;//活动标签内容

    public String getactivity() {
        return activity;
    }

    public void setactivity(String activity) {
        this.activity = activity;
    }
    public void setActualPrice(int ActualPrice) {
        this.ActualPrice = ActualPrice;
    }

    public void setTeacherDesc(String TeacherDesc) {
        this.TeacherDesc = TeacherDesc;
    }

    public void setBuy_lives(String buy_lives) {
        this.buy_lives = buy_lives;
    }

    public void setScaleimg(String scaleimg) {
        this.scaleimg = scaleimg;
    }

    public void setRid(int rid) {
        this.rid = rid;
    }

    public void setIs_zhibo(String is_zhibo) {
        this.is_zhibo = is_zhibo;
    }

    public void setProvince(int Province) {
        this.Province = Province;
    }

    public void setLessionCount(int lessionCount) {
        this.lessionCount = lessionCount;
    }

    public void setIs_ax_Type(String is_ax_Type) {
        this.is_ax_Type = is_ax_Type;
    }

    public void setIs_fufei(String is_fufei) {
        this.is_fufei = is_fufei;
    }

    public void setVideoType(int videoType) {
        this.videoType = videoType;
    }

    public void setTitle(String Title) {
        this.Title = Title;
    }

    public int getActualPrice() {
        return ActualPrice;
    }

    public String getTeacherDesc() {
        return TeacherDesc;
    }

    public String getBuy_lives() {
        return buy_lives;
    }

    public String getScaleimg() {
        return scaleimg;
    }

    public int getRid() {
        return rid;
    }

    public String getIs_zhibo() {
        return is_zhibo;
    }

    public int getProvince() {
        return Province;
    }

    public int getLessionCount() {
        return lessionCount;
    }

    public String getIs_ax_Type() {
        return is_ax_Type;
    }

    public String getIs_fufei() {
        return is_fufei;
    }

    public int getVideoType() {
        return videoType;
    }

    public String getTitle() {
        return Title;
    }
}
