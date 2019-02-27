package com.huatu.teacheronline.exercise.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by ljzyuhenda on 16/2/15.
 */
public class PaperPo implements Serializable {
    private String userno;// 用户id
    private String type;// 类型  0今日特训 1模块题海 2真题测评 4我的收藏 3错题不用提交
    private String pid;// 试卷id  真题测评为必填 今日特训和模块题海不填
    private String point;// 知识点id  今日特训和模块题海必填，真题测评不填不填
    private String second; // 做题用的时间
    private String score;// 试卷分数结果
    private List<QuestionPo> queslist;
    private String mostsecond; // 试卷总时长 只有真题才有

    public String getUserno() {
        return userno;
    }

    public void setUserno(String userno) {
        this.userno = userno;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getPoint() {
        return point;
    }

    public void setPoint(String point) {
        this.point = point;
    }

    public String getSecond() {
        return second;
    }

    public void setSecond(String second) {
        this.second = second;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public List<QuestionPo> getQueslist() {
        return queslist;
    }

    public void setQueslist(List<QuestionPo> queslist) {
        this.queslist = queslist;
    }

    public String getMostsecond() {
        return mostsecond;
    }

    public void setMostsecond(String mostsecond) {
        this.mostsecond = mostsecond;
    }

    @Override
    public String toString() {
        return "PaperPo{" +
                "userno='" + userno + '\'' +
                ", type='" + type + '\'' +
                ", pid='" + pid + '\'' +
                ", point='" + point + '\'' +
                ", second='" + second + '\'' +
                ", score='" + score + '\'' +
                ", queslist=" + queslist +
                ", mostsecond='" + mostsecond + '\'' +
                '}';
    }
}
