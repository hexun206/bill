package com.huatu.teacheronline.CCVideo;

import java.io.Serializable;

/**
 * Created by zhxm on 2016/1/7.
 * 面试视频实体类
 */
public class InterviewVideoBean implements Serializable {
    private String id;// 视频id
    private String title;// 视频标题
    private String thumb;// 视频图片
    private String click;// 点击次数
    private String text;// 简介
    private String infomation;// 课文
    private String teach_plan;// 教案
    private String uid;// cc账号唯一对应，用于播放
    private String api_key;// cc账号唯一对应，用于播放
    private String courses_id;// 课件id，用于播放视频用
    private String bjyid;// 百家云视频id
    private String token;// 百家云视频token

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getThumb() {
        return thumb;
    }

    public void setThumb(String thumb) {
        this.thumb = thumb;
    }

    public String getClick() {
        return click;
    }

    public void setClick(String click) {
        this.click = click;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getInfomation() {
        return infomation;
    }

    public void setInfomation(String infomation) {
        this.infomation = infomation;
    }

    public String getTeach_plan() {
        return teach_plan;
    }

    public void setTeach_plan(String teach_plan) {
        this.teach_plan = teach_plan;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getApi_key() {
        return api_key;
    }

    public void setApi_key(String api_key) {
        this.api_key = api_key;
    }

    public String getCourses_id() {
        return courses_id;
    }

    public void setCourses_id(String courses_id) {
        this.courses_id = courses_id;
    }

    public String getBjyid() {
        return bjyid;
    }

    public void setBjyid(String bjyid) {
        this.bjyid = bjyid;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public String toString() {
        return "InterviewVideoBean{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", thumb='" + thumb + '\'' +
                ", click='" + click + '\'' +
                ", text='" + text + '\'' +
                ", infomation='" + infomation + '\'' +
                ", teach_plan='" + teach_plan + '\'' +
                ", uid='" + uid + '\'' +
                ", api_key='" + api_key + '\'' +
                ", courses_id='" + courses_id + '\'' +
                ", bjyid='" + bjyid + '\'' +
                ", token='" + token + '\'' +
                '}';
    }
}
