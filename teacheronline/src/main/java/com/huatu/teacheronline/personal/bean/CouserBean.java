package com.huatu.teacheronline.personal.bean;

import java.io.Serializable;

/**
 * Created by admin on 2017/4/6. 我的课程推送列表实体类
 */

public class CouserBean implements Serializable {
    /**
     * title : 冲刺班标题1
     * classId : 1
     * number : 00001
     * noticeCount : 0isUpdate
     * schoolTime : 2017-04-11~2017-04-19
     * period : 2课时
     * hasNoCheck : 0
     * isUpdate : 0
     * hasNoCheckCount:0 开课通知未读数量
     */
    private String title;
    private int classId;
    private String number;
    private int noticeCount;
    private String schoolTime;
    private String period;
    private int hasNoCheck;
    private int isUpdate;
    private int hasNoCheckCount;


    public int gethasNoCheckCount() {
        return hasNoCheckCount;
    }

    public void sethasNoCheckCount(int hasNoCheckCount) {
        this.hasNoCheckCount = hasNoCheckCount;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setClassId(int classId) {
        this.classId = classId;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public void setNoticeCount(int noticeCount) {
        this.noticeCount = noticeCount;
    }

    public void setSchoolTime(String schoolTime) {
        this.schoolTime = schoolTime;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public void setHasNoCheck(int hasNoCheck) {
        this.hasNoCheck = hasNoCheck;
    }

    public void setIsUpdate(int isUpdate) {
        this.isUpdate = isUpdate;
    }

    public String getTitle() {
        return title;
    }

    public int getClassId() {
        return classId;
    }

    public String getNumber() {
        return number;
    }

    public int getNoticeCount() {
        return noticeCount;
    }

    public String getSchoolTime() {
        return schoolTime;
    }

    public String getPeriod() {
        return period;
    }

    public int getHasNoCheck() {
        return hasNoCheck;
    }

    public int getIsUpdate() {
        return isUpdate;
    }
}
