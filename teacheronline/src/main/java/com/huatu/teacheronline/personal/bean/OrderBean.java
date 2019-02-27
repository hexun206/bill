package com.huatu.teacheronline.personal.bean;

import java.io.Serializable;

/**
 * 订单实体类
 * Created by ljyu on 2016/8/8.
 */
public class OrderBean implements Serializable{

    /**
     * orderNo : ZB20160711180324U3321373A46809
     * Title : 教综易错题目大放送
     * scaleimg : http://upload.hteacher.net/classimg/class/16031711365817045.jpg
     * ActualPrice : 9
     * jsstatus : 2
     * nu : 1202036918878
     * com : 1
     * pay_time : 2016/07/22
     * overdue_time : 2017/07/17
     * is_overdue : 1
     */

    private String orderNo;//订单号
    private String Title;//标题
    private String scaleimg;//图片
    private String ActualPrice;//价格(原价)
    private String jsstatus;//物流状态 -1 教师网不支持 0该课程无寄送 1 未发货 2 已发货
    private String nu;//物流号
    private String com;//物流公司编码
    private String pay_time;//订单支付时间
    private String overdue_time;//过期时间
    private String is_overdue; //是否过期：1=没过期，-1过期
    private String adjust_price; // 订单修改后的价格（为空字符串则代表未修改过价格）
    private String pay_type; // 是否付款 1已付款 0未付款
    private String customer; // 马上学习的客服链接
    private String rid; // 课程id
    private String isTimeout;//判断该课程是否过期


    public String getIsTimeout() {
        return isTimeout;
    }

    public void setIsTimeout(String isTimeout) {
        this.isTimeout = isTimeout;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String Title) {
        this.Title = Title;
    }

    public String getScaleimg() {
        return scaleimg;
    }

    public void setScaleimg(String scaleimg) {
        this.scaleimg = scaleimg;
    }

    public String getActualPrice() {
        return ActualPrice;
    }

    public void setActualPrice(String ActualPrice) {
        this.ActualPrice = ActualPrice;
    }

    public String getJsstatus() {
        return jsstatus;
    }

    public void setJsstatus(String jsstatus) {
        this.jsstatus = jsstatus;
    }

    public String getNu() {
        return nu;
    }

    public void setNu(String nu) {
        this.nu = nu;
    }

    public String getCom() {
        return com;
    }

    public void setCom(String com) {
        this.com = com;
    }

    public String getPay_time() {
        return pay_time;
    }

    public void setPay_time(String pay_time) {
        this.pay_time = pay_time;
    }

    public String getOverdue_time() {
        return overdue_time;
    }

    public void setOverdue_time(String overdue_time) {
        this.overdue_time = overdue_time;
    }

    public String getIs_overdue() {
        return is_overdue;
    }

    public void setIs_overdue(String is_overdue) {
        this.is_overdue = is_overdue;
    }

    public String getAdjust_price() {
        return adjust_price;
    }

    public void setAdjust_price(String adjust_price) {
        this.adjust_price = adjust_price;
    }

    public String getPay_type() {
        return pay_type;
    }

    public void setPay_type(String pay_type) {
        this.pay_type = pay_type;
    }

    public String getCustomer() {
        return customer;
    }

    public void setCustomer(String customer) {
        this.customer = customer;
    }

    public String getRid() {
        return rid;
    }

    public void setRid(String rid) {
        this.rid = rid;
    }

    @Override
    public String toString() {
        return "OrderBean{" +
                "orderNo='" + orderNo + '\'' +
                ", Title='" + Title + '\'' +
                ", scaleimg='" + scaleimg + '\'' +
                ", ActualPrice='" + ActualPrice + '\'' +
                ", jsstatus='" + jsstatus + '\'' +
                ", nu='" + nu + '\'' +
                ", com='" + com + '\'' +
                ", pay_time='" + pay_time + '\'' +
                ", overdue_time='" + overdue_time + '\'' +
                ", is_overdue='" + is_overdue + '\'' +
                ", adjust_price='" + adjust_price + '\'' +
                ", pay_type='" + pay_type + '\'' +
                ", customer='" + customer + '\'' +
                ", rid='" + rid + '\'' +
                '}';
    }
}
