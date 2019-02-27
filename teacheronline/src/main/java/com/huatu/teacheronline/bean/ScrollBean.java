package com.huatu.teacheronline.bean;

import com.bluelinelabs.logansquare.annotation.JsonObject;

import java.io.Serializable;

/**
 * Created by admin on 2017/4/7. 首页跑马灯
 */
@JsonObject(fieldDetectionPolicy = JsonObject.FieldDetectionPolicy.NONPRIVATE_FIELDS_AND_ACCESSORS)

public class ScrollBean implements Serializable {
    /**
     * type : 2
     * objectId : 48064
     * title : 2017年福建省教师招聘 教育综合知识全程通关班（一期）
     */

    private String type;
    private String objectId;
    private String title;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
