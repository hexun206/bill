package com.huatu.teacheronline.exercise.bean.erbean;

import com.bluelinelabs.logansquare.annotation.JsonObject;

import java.util.ArrayList;

/**
 * 真题试卷试题分析
 * Created by ljyu on 2016/12/20.
 */
@JsonObject(fieldDetectionPolicy = JsonObject.FieldDetectionPolicy.NONPRIVATE_FIELDS_AND_ACCESSORS)
public class QuestionsBean {
    private String qid;//试题id
    private String answercount;// 试题总共被作答次数
    private String precision;//正确率
    private String fallibility;//易错项
    private String userscore;//学员所得分值
    private String score;//试题分值
    private String point;//知识点id
    private String qattribute;//试题类型0客观题，1主观题
    private String iserror;//是否正确1正确，0错误，-1未答题
    private ArrayList<String> qusanswer;// 试题答案
    private ArrayList<String> useranswers;//学员所选答案

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

    public String getPrecision() {
        return precision;
    }

    public void setPrecision(String precision) {
        this.precision = precision;
    }

    public String getFallibility() {
        return fallibility;
    }

    public void setFallibility(String fallibility) {
        this.fallibility = fallibility;
    }

    public String getUserscore() {
        return userscore;
    }

    public void setUserscore(String userscore) {
        this.userscore = userscore;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public String getPoint() {
        return point;
    }

    public void setPoint(String point) {
        this.point = point;
    }

    public String getQattribute() {
        return qattribute;
    }

    public void setQattribute(String qattribute) {
        this.qattribute = qattribute;
    }

    public String getIserror() {
        return iserror;
    }

    public void setIserror(String iserror) {
        this.iserror = iserror;
    }

    public ArrayList<String> getQusanswer() {
        return qusanswer;
    }

    public void setQusanswer(ArrayList<String> qusanswer) {
        this.qusanswer = qusanswer;
    }

    public ArrayList<String> getUseranswers() {
        return useranswers;
    }

    public void setUseranswers(ArrayList<String> useranswers) {
        this.useranswers = useranswers;
    }

    @Override
    public String toString() {
        return "QuestionsBean{" +
                "qid='" + qid + '\'' +
                ", answercount='" + answercount + '\'' +
                ", precision='" + precision + '\'' +
                ", fallibility='" + fallibility + '\'' +
                ", userscore='" + userscore + '\'' +
                ", score='" + score + '\'' +
                ", point='" + point + '\'' +
                ", qattribute='" + qattribute + '\'' +
                ", iserror='" + iserror + '\'' +
                ", qusanswer=" + qusanswer +
                ", useranswers=" + useranswers +
                '}';
    }
}
