package com.huatu.teacheronline.personal.bean;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by kinndann on 2018/8/29.
 * description:用户提交面试视频的相关参数
 */
public class InteviewVideoInfoUploadRequest implements Serializable {

    /**
     * 上传百家云的最大次数
     */
    public static final int UPLOAD_MAX_COUNT = 4;

    private String userid;

    private String netclassid;

    private String classtitle;

    private String classphase;

    private String classsubject;

    private String versions;

    private String questions;

    private String questionsDetail;


    private int bjyvideoid;


    /**
     * 订单状态：0可修改订单，1不可修改订单
     */
    private int ostatus;

    /**
     * 订单id
     */
    @SerializedName("id")
    private Integer orderId;

    /**
     * 0是不同意，1是同意
     */
    private int agreedisplay;

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getNetclassid() {
        return netclassid;
    }

    public void setNetclassid(String netclassid) {
        this.netclassid = netclassid;
    }

    public String getClasstitle() {
        return classtitle;
    }

    public void setClasstitle(String classtitle) {
        this.classtitle = classtitle;
    }

    public String getClassphase() {
        return classphase;
    }

    public void setClassphase(String classphase) {
        this.classphase = classphase;
    }

    public String getClasssubject() {
        return classsubject;
    }

    public void setClasssubject(String classsubject) {
        this.classsubject = classsubject;
    }

    public String getVersions() {
        return versions;
    }

    public void setVersions(String versions) {
        this.versions = versions;
    }

    public String getQuestions() {
        return questions;
    }

    public void setQuestions(String questions) {
        this.questions = questions;
    }

    public String getQuestionsDetail() {
        return questionsDetail;
    }

    public void setQuestionsDetail(String questionsDetail) {
        this.questionsDetail = questionsDetail;
    }

    public int getBjyvideoid() {
        return bjyvideoid;
    }

    public void setBjyvideoid(int bjyvideoid) {
        this.bjyvideoid = bjyvideoid;
    }

    public int getOstatus() {
        return ostatus;
    }

    public void setOstatus(int ostatus) {
        this.ostatus = ostatus;
    }

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public int getAgreedisplay() {
        return agreedisplay;
    }

    public void setAgreedisplay(int agreedisplay) {
        this.agreedisplay = agreedisplay;
    }
}
