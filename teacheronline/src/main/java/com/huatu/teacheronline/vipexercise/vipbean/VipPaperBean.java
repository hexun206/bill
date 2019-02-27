package com.huatu.teacheronline.vipexercise.vipbean;

import java.io.Serializable;

/**
 * vip试题bean
 * Created by ljyu on 2017/10/10.
 */
public class VipPaperBean implements Serializable{

    /**
     * createUserId : 22
     * createTime : 2017-09-28 11:47:11
     * count : 5
     * createUser : 教师
     * exclusiveId : 59cc70bfdbb2022cf402f31f
     * title : 开发测试，勿动
     */

    private int createUserId;
    private String createTime;
    private int count;
    private String createUser;
    private String exclusiveId;
    private String title;

    public int getCreateUserId() {
        return createUserId;
    }

    public void setCreateUserId(int createUserId) {
        this.createUserId = createUserId;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    public String getExclusiveId() {
        return exclusiveId;
    }

    public void setExclusiveId(String exclusiveId) {
        this.exclusiveId = exclusiveId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String toString() {
        return "VipPaperBean{" +
                "createUserId=" + createUserId +
                ", createTime='" + createTime + '\'' +
                ", count=" + count +
                ", createUser='" + createUser + '\'' +
                ", exclusiveId='" + exclusiveId + '\'' +
                ", title='" + title + '\'' +
                '}';
    }
}
