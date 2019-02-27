package com.huatu.teacheronline.personal.bean;

import java.io.Serializable;

/**
 * Created by admin on 2017/4/6.
 */

public class NoticeBean implements Serializable {
    /**
     * noticeId : 1
     * title : 标题
     * address : 鹭江道322
     * pushTime : 1492741025
     * checked : 0
     * nowTime : 1492754757
     */
    private int noticeId;
    private String title;
    private String address;
    private String pushTime;
    private int checked;
    private String nowTime;
    private String type;

    public String gettype() {
        return type;
    }

    public void setNoticeId(int noticeId) {
        this.noticeId = noticeId;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setPushTime(String pushTime) {
        this.pushTime = pushTime;
    }

    public void setChecked(int checked) {
        this.checked = checked;
    }

    public void setNowTime(String nowTime) {
        this.nowTime = nowTime;
    }

    public int getNoticeId() {
        return noticeId;
    }

    public String getTitle() {
        return title;
    }

    public String getAddress() {
        return address;
    }

    public String getPushTime() {
        return pushTime;
    }

    public int getChecked() {
        return checked;
    }

    public String getNowTime() {
        return nowTime;
    }
}
