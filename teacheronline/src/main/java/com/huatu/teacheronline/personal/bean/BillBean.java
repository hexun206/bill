package com.huatu.teacheronline.personal.bean;

import com.bluelinelabs.logansquare.annotation.JsonObject;

/**
 * 账单Bean
 * Created by ply on 2016/2/16.
 */
@JsonObject(fieldDetectionPolicy = JsonObject.FieldDetectionPolicy.NONPRIVATE_FIELDS_AND_ACCESSORS)
public class BillBean {
    String note;//标题
    String gold;//金币数
    String type;//1充值 2消费 3任务

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getGold() {
        return gold;
    }

    public void setGold(String gold) {
        this.gold = gold;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
