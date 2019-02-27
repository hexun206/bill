package com.huatu.teacheronline.personal.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by admin on 2017/4/6. 获得我的班级详情 实体类
 */

public class ClassDetaliBean implements Serializable{

    /**
     * reminder : 请接受我诚挚祝福，出入平安，一帆风顺！lalal
     * startDate : 2017-04-11
     * classId : 1
     * subject : 科目
     * catalog : 班别
     * number : 00001
     * endDate : 2017-04-19
     * sysAttribution : 体系归属
     * updateItem : ["title","address"]
     * title : 冲刺班标题121q1124ffggy
     * area : 福建省厦门市
     * turnBack : 0500-0000
     * address : 鹭江道23411gffy
     * customService : 客服提醒
     */
    private String reminder;
    private String startDate;
    private int classId;
    private String subject;
    private String catalog;
    private String number;
    private String endDate;
    private String sysAttribution;
    private List<String> updateItem;
    private String title;
    private String area;
    private String turnBack;
    private String address;
    private String customService;
    private String schedule;

    public String getschedule() {
        return schedule;
    }
    public void setschedule(String schedule) {
        this.schedule = schedule;
    }

    public void setReminder(String reminder) {
        this.reminder = reminder;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public void setClassId(int classId) {
        this.classId = classId;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public void setCatalog(String catalog) {
        this.catalog = catalog;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public void setSysAttribution(String sysAttribution) {
        this.sysAttribution = sysAttribution;
    }

    public void setUpdateItem(List<String> updateItem) {
        this.updateItem = updateItem;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public void setTurnBack(String turnBack) {
        this.turnBack = turnBack;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setCustomService(String customService) {
        this.customService = customService;
    }

    public String getReminder() {
        return reminder;
    }

    public String getStartDate() {
        return startDate;
    }

    public int getClassId() {
        return classId;
    }

    public String getSubject() {
        return subject;
    }

    public String getCatalog() {
        return catalog;
    }

    public String getNumber() {
        return number;
    }

    public String getEndDate() {
        return endDate;
    }

    public String getSysAttribution() {
        return sysAttribution;
    }

    public List<String> getUpdateItem() {
        return updateItem;
    }

    public String getTitle() {
        return title;
    }

    public String getArea() {
        return area;
    }

    public String getTurnBack() {
        return turnBack;
    }

    public String getAddress() {
        return address;
    }

    public String getCustomService() {
        return customService;
    }
}
