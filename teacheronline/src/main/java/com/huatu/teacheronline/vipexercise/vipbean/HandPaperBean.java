package com.huatu.teacheronline.vipexercise.vipbean;


import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

/**
 * 保存试卷的相关属性bean
 * Created by ljyu on 2017/10/26.
 */
public class HandPaperBean implements Serializable {
    private String uid;//用户id
    private String exclusiveId;//试卷id
    private int createUserId;//创建者id
    private int currentNum;//当前做到第几题
    private int time;//做题时间
    private List<PaperHandBean> paperHandBean;
    private String[] answersFlag;//用户的对错选项
    private List<String> wrongExerciseIdList;//错题的id
    private List<List> wrongChoiceList;//用户错题答案
    private List<VipQuestionBean.ResultListBean> wrongResultListBean;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getExclusiveId() {
        return exclusiveId;
    }

    public void setExclusiveId(String exclusiveId) {
        this.exclusiveId = exclusiveId;
    }

    public int getCreateUserId() {
        return createUserId;
    }

    public void setCreateUserId(int createUserId) {
        this.createUserId = createUserId;
    }

    public List<PaperHandBean> getPaperHandBean() {
        return paperHandBean;
    }

    public void setPaperHandBean(List<PaperHandBean> paperHandBean) {
        this.paperHandBean = paperHandBean;
    }

    public int getCurrentNum() {
        return currentNum;
    }

    public void setCurrentNum(int currentNum) {
        this.currentNum = currentNum;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public String[] getAnswersFlag() {
        return answersFlag;
    }

    public void setAnswersFlag(String[] answersFlag) {
        this.answersFlag = answersFlag;
    }

    public List<String> getWrongExerciseIdList() {
        return wrongExerciseIdList;
    }

    public void setWrongExerciseIdList(List<String> wrongExerciseIdList) {
        this.wrongExerciseIdList = wrongExerciseIdList;
    }

    public List<List> getWrongChoiceList() {
        return wrongChoiceList;
    }

    public void setWrongChoiceList(List<List> wrongChoiceList) {
        this.wrongChoiceList = wrongChoiceList;
    }

    public List<VipQuestionBean.ResultListBean> getWrongResultListBean() {
        return wrongResultListBean;
    }

    public void setWrongResultListBean(List<VipQuestionBean.ResultListBean> wrongResultListBean) {
        this.wrongResultListBean = wrongResultListBean;
    }

    @Override
    public String toString() {
        return "HandPaperBean{" +
                "uid='" + uid + '\'' +
                ", exclusiveId='" + exclusiveId + '\'' +
                ", createUserId=" + createUserId +
                ", currentNum=" + currentNum +
                ", time=" + time +
                ", paperHandBean=" + paperHandBean +
                ", answersFlag=" + Arrays.toString(answersFlag) +
                ", wrongExerciseIdList=" + wrongExerciseIdList +
                ", wrongChoiceList=" + wrongChoiceList +
                ", wrongResultListBean=" + wrongResultListBean +
                '}';
    }
}
