package com.huatu.teacheronline.exercise.bean;

import com.bluelinelabs.logansquare.annotation.JsonObject;

import java.io.Serializable;

/**
 * Created by zhangxm on 2015/6/26.
 * 交卷后返回的试题分析属性
 */
@JsonObject(fieldDetectionPolicy = JsonObject.FieldDetectionPolicy.NONPRIVATE_FIELDS_AND_ACCESSORS)
public class QuesAttrVo implements Serializable {
    private String qid;// 试题Id
    private String answercount;// 答题个数
    private String fallibility;// 易错项
    private String precision;// 准确率

    public String getQid() {
        return qid;
    }

    public void setQid(String qid) {
        this.qid = qid;
    }

    public String getAnswercount() {
        return answercount;
    }

    public void setAnswercount(String answercount) {
        this.answercount = answercount;
    }

    public String getFallibility() {
        return fallibility;
    }

    public void setFallibility(String fallibility) {
        this.fallibility = fallibility;
    }

    public String getPrecision() {
        return precision;
    }

    public void setPrecision(String precision) {
        this.precision = precision;
    }

    @Override
    public String toString() {
        return "QuesAttrVo{" +
                "qid='" + qid + '\'' +
                ", answercount='" + answercount + '\'' +
                ", fallibility='" + fallibility + '\'' +
                ", precision='" + precision + '\'' +
                '}';
    }
}
