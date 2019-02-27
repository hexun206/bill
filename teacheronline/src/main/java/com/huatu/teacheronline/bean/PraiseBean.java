package com.huatu.teacheronline.bean;

import com.bluelinelabs.logansquare.annotation.JsonObject;

import java.io.Serializable;

/**
 * 学员好评实体类
 * Created by 18250 on 2017/9/19.
 */
@JsonObject(fieldDetectionPolicy = JsonObject.FieldDetectionPolicy.NONPRIVATE_FIELDS_AND_ACCESSORS)

public class PraiseBean implements Serializable {
    private String id;
    private String TeacherName;
    private String remark;
    private String rateDate;
    private String UserFace;
    private String UserName;

    public void setId(String id) {
        this.id = id;
    }

    public void setTeacherName(String TeacherName) {
        this.TeacherName = TeacherName;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public void setRateDate(String rateDate) {
        this.rateDate = rateDate;
    }

    public void setUserFace(String UserFace) {
        this.UserFace = UserFace;
    }

    public void setUserName(String UserName) {
        this.UserName = UserName;
    }

    public String getId() {
        return id;
    }

    public String getTeacherName() {
        return TeacherName;
    }

    public String getRemark() {
        return remark;
    }

    public String getRateDate() {
        return rateDate;
    }

    public String getUserFace() {
        return UserFace;
    }

    public String getUserName() {
        return UserName;
    }
}
