package com.huatu.teacheronline.exercise.bean.erbean;

import com.bluelinelabs.logansquare.annotation.JsonObject;

import java.util.ArrayList;

/**
 * 真题评测报告分析
 * Created by ljyu on 2016/12/20.
 */
@JsonObject(fieldDetectionPolicy = JsonObject.FieldDetectionPolicy.NONPRIVATE_FIELDS_AND_ACCESSORS)
public class ErPaperAttrVo {

    /**
     * pid : 949  试卷id
     * theranking : 1
     * headcount : 12743
     * averagetime : 99
     * averagescore : 57.95
     * overmember : 0
     * pointanalysis : [{"point":"中国古代工艺美术","count":"5","isright":"3","correctrate":"0.06"},{"point":"中国古代建筑艺术","count":"3","isright":"1","correctrate":"0
     * .033"}]
     * maxscore : 60.0
     * minscore : 60.0
     * mostscore : 8.0
     * userscore : 60.0
     * mostsecond : 120
     * usersecond : 100
     * utendArrayList : null
     * meantendArrayList : null
     * qavArrayList : null
     * paperAnalysisTypeVo : [{"type":"1","analysis":0.075,"questions":[{"qid":"26500","answercount":"12698","precision":"0.0040163808","fallibility":"B",
     * "userscore":"1","score":"1","point":"206","qattribute":"1","qusanswer":["D"],"useranswers":["B"],"iserror":"0"},{"qid":"26497","answercount":"12548",
     * "precision":"0.99577624","fallibility":"A","userscore":"1","score":"1","point":"206","qattribute":"1","qusanswer":["C"],"useranswers":["C"],
     * "iserror":"1"},{"qid":"f632cbf7d28c4120af75ad7a09bb1a98","answercount":"12388","precision":"0.9916855","fallibility":"A","userscore":"1","score":"1",
     * "point":"206","qattribute":"1","qusanswer":["C"],"useranswers":["C"],"iserror":"1"},{"qid":"6819eac769e048d2b49e2800063ddb08","answercount":"12392",
     * "precision":"0.9982247","fallibility":"A","userscore":"1","score":"1","point":"206","qattribute":"1","qusanswer":["C"],"useranswers":["C"],
     * "iserror":"1"}],"typemostscore":"4.0","typeuserscore":"3.0"},{"type":"0","analysis":0.025,"questions":[{"qid":"1a1435d3f62e4782a72be5a869adccf2",
     * "answercount":"12355","precision":"0.9943343","fallibility":"D","userscore":"1","score":"1","point":"208","qattribute":"0","qusanswer":["C"],
     * "useranswers":["C"],"iserror":"1"},{"qid":"b1f046c38502460595f11cf35f9cb644","answercount":"12392","precision":"0.008231116","fallibility":"D",
     * "userscore":"1","score":"1","point":"208","qattribute":"0","qusanswer":["C"],"useranswers":["D"],"iserror":"0"},
     * {"qid":"f2548bf204f74e609bd5823a215bc35d","answercount":"12393","precision":"0.010409102","fallibility":"D","userscore":"1","score":"1","point":"208",
     * "qattribute":"0","qusanswer":["C"],"useranswers":["D"],"iserror":"0"},{"qid":"26498","answercount":"140","precision":"0.85714287","fallibility":"A",
     * "userscore":"1","score":"1","point":"206","qattribute":"0","qusanswer":["C"],"useranswers":[],"iserror":"-1"}],"typemostscore":"4.0","typeuserscore
     * ":"1.0"}]
     */

    private String pid;//试卷id
    private String theranking;//排名
    private String headcount;//
    private String content;//打败人数
    private String averagetime;//试卷被作答次数
    private String averagescore;//
    private String overmember;//试卷平均用时
    private String maxscore;//学员所得最高分
    private String minscore;//学员所得最低分
    private String mostscore;//试卷总分
    private String userscore;//学员所得分
    private String mostsecond;//学员该次试卷所用时间
    private String usersecond;//该试卷推荐时长
    private Object utendArrayList;//
    private Object meantendArrayList;//
    private Object qavArrayList;//
    /**
     * 根据知识点获取学员分析
     * point : 中国古代工艺美术
     * count : 5
     * isright : 3
     * correctrate : 0.06
     */

    private ArrayList<PointanalysisBean> pointanalysis;
    /**
     * 根据试题类型获取学员分析
     * type : 1
     * analysis : 0.075
     * questions : [{"qid":"26500","answercount":"12698","precision":"0.0040163808","fallibility":"B","userscore":"1","score":"1","point":"206",
     * "qattribute":"1","qusanswer":["D"],"useranswers":["B"],"iserror":"0"},{"qid":"26497","answercount":"12548","precision":"0.99577624","fallibility":"A",
     * "userscore":"1","score":"1","point":"206","qattribute":"1","qusanswer":["C"],"useranswers":["C"],"iserror":"1"},
     * {"qid":"f632cbf7d28c4120af75ad7a09bb1a98","answercount":"12388","precision":"0.9916855","fallibility":"A","userscore":"1","score":"1","point":"206",
     * "qattribute":"1","qusanswer":["C"],"useranswers":["C"],"iserror":"1"},{"qid":"6819eac769e048d2b49e2800063ddb08","answercount":"12392","precision":"0
     * .9982247","fallibility":"A","userscore":"1","score":"1","point":"206","qattribute":"1","qusanswer":["C"],"useranswers":["C"],"iserror":"1"}]
     * typemostscore : 4.0
     * typeuserscore : 3.0
     */

    private ArrayList<PaperAnalysisTypeBean> paperAnalysisTypeVo;

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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
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

    public String getOvermember() {
        return overmember;
    }

    public void setOvermember(String overmember) {
        this.overmember = overmember;
    }

    public String getMaxscore() {
        return maxscore;
    }

    public void setMaxscore(String maxscore) {
        this.maxscore = maxscore;
    }

    public String getMinscore() {
        return minscore;
    }

    public void setMinscore(String minscore) {
        this.minscore = minscore;
    }

    public String getMostscore() {
        return mostscore;
    }

    public void setMostscore(String mostscore) {
        this.mostscore = mostscore;
    }

    public String getUserscore() {
        return userscore;
    }

    public void setUserscore(String userscore) {
        this.userscore = userscore;
    }

    public String getMostsecond() {
        return mostsecond;
    }

    public void setMostsecond(String mostsecond) {
        this.mostsecond = mostsecond;
    }

    public String getUsersecond() {
        return usersecond;
    }

    public void setUsersecond(String usersecond) {
        this.usersecond = usersecond;
    }

    public Object getUtendArrayList() {
        return utendArrayList;
    }

    public void setUtendArrayList(Object utendArrayList) {
        this.utendArrayList = utendArrayList;
    }

    public Object getMeantendArrayList() {
        return meantendArrayList;
    }

    public void setMeantendArrayList(Object meantendArrayList) {
        this.meantendArrayList = meantendArrayList;
    }

    public Object getQavArrayList() {
        return qavArrayList;
    }

    public void setQavArrayList(Object qavArrayList) {
        this.qavArrayList = qavArrayList;
    }

    public ArrayList<PointanalysisBean> getPointanalysis() {
        return pointanalysis;
    }

    public void setPointanalysis(ArrayList<PointanalysisBean> pointanalysis) {
        this.pointanalysis = pointanalysis;
    }

    public ArrayList<PaperAnalysisTypeBean> getPaperAnalysisTypeVo() {
        return paperAnalysisTypeVo;
    }

    public void setPaperAnalysisTypeVo(ArrayList<PaperAnalysisTypeBean> paperAnalysisTypeVo) {
        this.paperAnalysisTypeVo = paperAnalysisTypeVo;
    }

    @Override
    public String toString() {
        return "ErPaperAttrVo{" +
                "pid='" + pid + '\'' +
                ", theranking='" + theranking + '\'' +
                ", headcount='" + headcount + '\'' +
                ", averagetime='" + averagetime + '\'' +
                ", averagescore='" + averagescore + '\'' +
                ", overmember='" + overmember + '\'' +
                ", maxscore='" + maxscore + '\'' +
                ", minscore='" + minscore + '\'' +
                ", mostscore='" + mostscore + '\'' +
                ", userscore='" + userscore + '\'' +
                ", mostsecond='" + mostsecond + '\'' +
                ", usersecond='" + usersecond + '\'' +
                ", utendArrayList=" + utendArrayList +
                ", meantendArrayList=" + meantendArrayList +
                ", qavArrayList=" + qavArrayList +
                ", pointanalysis=" + pointanalysis +
                ", paperAnalysisTypeVo=" + paperAnalysisTypeVo +
                '}';
    }
}