package com.huatu.teacheronline.exercise.bean;

import com.bluelinelabs.logansquare.annotation.JsonObject;

import java.util.List;

/**
 * Created by zhangxm on 2015/6/26.
 * 交卷后返回的试卷分析属性
 */
@JsonObject(fieldDetectionPolicy = JsonObject.FieldDetectionPolicy.NONPRIVATE_FIELDS_AND_ACCESSORS)
public class PaperAttrVo {
    private String pid;// 试卷id
    private String theranking;// 本次排名
    private String headcount;// 答题总人数
    private String averagetime;// 平均用时
    private String averagescore;// 平均得分
    private List<String> utendlist;// 我的近几次得分情况(服务于折线图)
    private List<String> meantendlist;// 大家近几次得分情况(服务于折线图)
    private List<QuesAttrVo> qavList;// 试题分析结果

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getTheranking() {
        return theranking;
    }

    public void setTheranking(String theranking) {
        this.theranking = theranking;
    }

    public String getHeadcount() {
        return headcount;
    }

    public void setHeadcount(String headcount) {
        this.headcount = headcount;
    }

    public String getAveragetime() {
        return averagetime;
    }

    public void setAveragetime(String averagetime) {
        this.averagetime = averagetime;
    }

    public String getAveragescore() {
        return averagescore;
    }

    public void setAveragescore(String averagescore) {
        this.averagescore = averagescore;
    }

    public List<String> getUtendlist() {
        return utendlist;
    }

    public void setUtendlist(List<String> utendlist) {
        this.utendlist = utendlist;
    }

    public List<String> getMeantendlist() {
        return meantendlist;
    }

    public void setMeantendlist(List<String> meantendlist) {
        this.meantendlist = meantendlist;
    }

    public List<QuesAttrVo> getQavList() {
        return qavList;
    }

    public void setQavList(List<QuesAttrVo> qavList) {
        this.qavList = qavList;
    }

    @Override
    public String toString() {
        return "PaperAttrVo{" +
                "pid='" + pid + '\'' +
                ", theranking='" + theranking + '\'' +
                ", headcount='" + headcount + '\'' +
                ", averagetime='" + averagetime + '\'' +
                ", averagescore='" + averagescore + '\'' +
                ", utendlist=" + utendlist +
                ", meantendlist=" + meantendlist +
                ", qavList=" + qavList +
                '}';
    }
}
