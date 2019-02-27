package com.huatu.teacheronline.personal.bean;

/**
 * Created by kinndann on 2018/11/19.
 * description:根据订单id消除阅读状态事件
 */
public class ChangeReadStatusEvent {
    private String orderId;

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }
}
