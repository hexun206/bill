package com.huatu.teacheronline.exercise.bean;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

/**
 * Created by ljzyuhenda on 16/2/15.
 */
public class QuestionPo implements Serializable {
    private String userno;// 用户no
    private String qid;// 试题id
    private String[] qusanswer;// 试题原答案
    private List<String> useranswers;// 用户选择答案
    private String iserror;// 是否错误  0错误  1正确
    private String qscore;// 该试题分值 只有真题才有
    private String userqscore;// 学员该题所得分值 只有真题才有
    private String[] qpoint;// 试题所属知识点 只有真题才有
    private String qattribute;// 试题的类型：1客观题，0主观题 只有真题才有

    public String getUserno() {
        return userno;
    }

    public void setUserno(String userno) {
        this.userno = userno;
    }

    public String getQid() {
        return qid;
    }

    public void setQid(String qid) {
        this.qid = qid;
    }

    public String[] getQusanswer() {
        return qusanswer;
    }

    public void setQusanswer(String[] qusanswer) {
        this.qusanswer = qusanswer;
    }

    public List<String> getUseranswers() {
        return useranswers;
    }

    public void setUseranswers(List<String> useranswers) {
        this.useranswers = useranswers;
    }

    public String getIserror() {
        return iserror;
    }

    public void setIserror(String iserror) {
        this.iserror = iserror;
    }

    public String getQscore() {
        return qscore;
    }

    public void setQscore(String qscore) {
        this.qscore = qscore;
    }

    public String getUserqscore() {
        return userqscore;
    }

    public void setUserqscore(String userqscore) {
        this.userqscore = userqscore;
    }

    public String getQattribute() {
        return qattribute;
    }

    public void setQattribute(String qattribute) {
        this.qattribute = qattribute;
    }

    public String[] getQpoint() {
        return qpoint;
    }

    public void setQpoint(String[] qpoint) {
        this.qpoint = qpoint;
    }

    @Override
    public String toString() {
        return "QuestionPo{" +
                "userno='" + userno + '\'' +
                ", qid='" + qid + '\'' +
                ", qusanswer=" + Arrays.toString(qusanswer) +
                ", useranswers=" + useranswers +
                ", iserror='" + iserror + '\'' +
                ", qscore='" + qscore + '\'' +
                ", userqscore='" + userqscore + '\'' +
                ", qpoint=" + Arrays.toString(qpoint) +
                ", qattribute='" + qattribute + '\'' +
                '}';
    }
}
