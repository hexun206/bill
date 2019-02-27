package com.huatu.teacheronline.exercise.bean.erbean;

import com.bluelinelabs.logansquare.annotation.JsonObject;

import java.util.ArrayList;

/**
 * Created by 79937 on 2016/12/20.
 */
@JsonObject(fieldDetectionPolicy = JsonObject.FieldDetectionPolicy.NONPRIVATE_FIELDS_AND_ACCESSORS)
public class PaperAnalysisTypeBean {
    private String type;//类型：0客观题，1主观题
    private double analysis;//得分率
    private String typemostscore;//各个模块(题型分类)的总分
    private String typeuserscore;//各个模块学员(题型分类)得分
    /**
     * qid : 26500
     * answercount : 12698
     * precision : 0.0040163808
     * fallibility : B
     * userscore : 1
     * score : 1
     * point : 206
     * qattribute : 1
     * qusanswer : ["D"]
     * useranswers : ["B"]
     * iserror : 0
     */

    private ArrayList<QuestionsBean> questions;//试题详细分析

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double getAnalysis() {
        return analysis;
    }

    public void setAnalysis(double analysis) {
        this.analysis = analysis;
    }

    public String getTypemostscore() {
        return typemostscore;
    }

    public void setTypemostscore(String typemostscore) {
        this.typemostscore = typemostscore;
    }

    public String getTypeuserscore() {
        return typeuserscore;
    }

    public void setTypeuserscore(String typeuserscore) {
        this.typeuserscore = typeuserscore;
    }

    public ArrayList<QuestionsBean> getQuestions() {
        return questions;
    }

    public void setQuestions(ArrayList<QuestionsBean> questions) {
        this.questions = questions;
    }

    @Override
    public String toString() {
        return "PaperAnalysisTypeVoBean{" +
                "type='" + type + '\'' +
                ", analysis=" + analysis +
                ", typemostscore='" + typemostscore + '\'' +
                ", typeuserscore='" + typeuserscore + '\'' +
                ", questions=" + questions +
                '}';
    }
}

