package com.huatu.teacheronline.message.bean;

import com.bluelinelabs.logansquare.annotation.JsonObject;

import java.io.Serializable;

/**
 * 公众号
 * Created by ljzyuhenda on 15/3/21.
 */
@JsonObject(fieldDetectionPolicy = JsonObject.FieldDetectionPolicy.NONPRIVATE_FIELDS_AND_ACCESSORS)
public class SubscriptionPushBeanInfo implements Serializable {
    private String push_url;
    private String push_title;
    private String push_pic;
    private String push_flag;
    private String push_time;

    public String getPush_url() {
        return push_url;
    }

    public void setPush_url(String push_url) {
        this.push_url = push_url;
    }

    public String getPush_title() {
        return push_title;
    }

    public void setPush_title(String push_title) {
        this.push_title = push_title;
    }

    public String getPush_pic() {
        return push_pic;
    }

    public void setPush_pic(String push_pic) {
        this.push_pic = push_pic;
    }

    public String getPush_flag() {
        return push_flag;
    }

    public void setPush_flag(String push_flag) {
        this.push_flag = push_flag;
    }

    public String getPush_time() {
        return push_time;
    }

    public void setPush_time(String push_time) {
        this.push_time = push_time;
    }
}
