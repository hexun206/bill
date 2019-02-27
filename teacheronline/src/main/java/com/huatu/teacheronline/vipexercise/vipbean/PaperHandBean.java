package com.huatu.teacheronline.vipexercise.vipbean;

import java.io.Serializable;
import java.util.List;

/**
 * Vip交卷bean
 * Created by ljyu on 2017/10/16.
 */
public class PaperHandBean implements Serializable{

    /**
     * questionId : 59cc703ddbb2022cf402f318
     * result : ["A","B"]
     * createTime : 1505971634337
     */

    private String questionId;
    private String childId;
    private String createTime;
    private List<String> results;

    public String getQuestionId() {
        return questionId;
    }

    public void setQuestionId(String questionId) {
        this.questionId = questionId;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public List<String> getResult() {
        return results;
    }

    public void setResult(List<String> results) {
        this.results = results;
    }

    public String getChildId() {
        return childId;
    }

    public void setChildId(String childId) {
        this.childId = childId;
    }

    @Override
    public String toString() {
        return "PaperHandBean{" +
                "questionId='" + questionId + '\'' +
                ", childId='" + childId + '\'' +
                ", createTime=" + createTime +
                ", results=" + results +
                '}';
    }
}
