package com.huatu.teacheronline.message.bean;

import com.bluelinelabs.logansquare.annotation.JsonObject;

import java.io.Serializable;
import java.util.List;

/**
 * 公众号
 * Created by ljzyuhenda on 15/3/21.
 */
@JsonObject(fieldDetectionPolicy = JsonObject.FieldDetectionPolicy.NONPRIVATE_FIELDS_AND_ACCESSORS)
public class SubscriptionBean implements Serializable {
    private String time;
    private List<SubscriptionPushBeanInfo> list;

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public List<SubscriptionPushBeanInfo> getList() {
        return list;
    }

    public void setList(List<SubscriptionPushBeanInfo> list) {
        this.list = list;
    }
}
