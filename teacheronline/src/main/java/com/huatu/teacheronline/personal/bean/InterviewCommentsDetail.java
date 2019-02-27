package com.huatu.teacheronline.personal.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by kinndann on 2018/11/9.
 * description:
 */
public class InterviewCommentsDetail {


    /**
     * code : 1
     * message :
     * data : [{"id":"15","classtitle":"测试","classphase":"初中","classsubject":"美术","versions":"人美版","questions":[{"id":"31","type":"自我认知","questionname":"教师这个职业有发展前途吗?"},{"id":"95","type":"综合分析","questionname":"有人说：\u201c当老师有爱心耐心就行了，其他不重要\u201d。你认为对吗?"}],"bjyvideoid":"","teaaudio":"","submittime":"2018-11-15 09:10:24","commenttime":null,"examplestatus":"0","commentstatus":"0","reward_gold":"0","orderstatus":"0","readstatus":"0"},{"id":"14","classtitle":"测试","classphase":"初中","classsubject":"美术","versions":"人美版","questions":[{"id":"31","type":"自我认知","questionname":"教师这个职业有发展前途吗?"},{"id":"95","type":"综合分析","questionname":"有人说：\u201c当老师有爱心耐心就行了，其他不重要\u201d。你认为对吗?"}],"bjyvideoid":"","teaaudio":"","submittime":"2018-11-15 09:09:53","commenttime":null,"examplestatus":"0","commentstatus":"0","reward_gold":"0","orderstatus":"0","readstatus":"0"}]
     */

    private String code;
    private String message;
    private ArrayList<CommentDetail> data;

    public boolean success() {
        return "1".equals(code);

    }


    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ArrayList<CommentDetail> getData() {
        return data;
    }

    public void setData(ArrayList<CommentDetail> data) {
        this.data = data;
    }

    public static class CommentDetail implements Serializable {
        /**
         * id : 15
         * classtitle : 测试
         * classphase : 初中
         * classsubject : 美术
         * versions : 人美版
         * questions : [{"id":"31","type":"自我认知","questionname":"教师这个职业有发展前途吗?"},{"id":"95","type":"综合分析","questionname":"有人说：\u201c当老师有爱心耐心就行了，其他不重要\u201d。你认为对吗?"}]
         * bjyvideoid :
         * teaaudio :
         * submittime : 2018-11-15 09:10:24
         * commenttime : null
         * examplestatus : 0
         * commentstatus : 0
         * reward_gold : 0
         * orderstatus : 0
         * readstatus : 0
         */

        private String id;
        private String classtitle;
        private String classphase;
        private String classsubject;
        private String versions;
        private String bjyvideoid;
        private String teaaudio;
        private String submittime;
        private String commenttime;
        private String examplestatus;
        private String commentstatus;
        private String reward_gold;
        private String orderstatus;
        private String readstatus;
        private String token;
        private List<QuestionsBean.Question> questions;


        /**
         * 是否可修改
         *
         * @return
         */
        public boolean isUnmodifiable() {
            return "1".equals(orderstatus);
        }

        /**
         * 是否为精选范例
         *
         * @return
         */
        public boolean isExample() {
            return "1".equals(examplestatus);
        }

        /**
         * 老师已点评
         *
         * @return
         */
        public boolean haveComments() {
            return "1".equals(commentstatus);
        }


        /**
         * 是否未阅读
         *
         * @return
         */
        public boolean isUnRead() {
            return "1".equals(readstatus);
        }


        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getClasstitle() {
            return classtitle;
        }

        public void setClasstitle(String classtitle) {
            this.classtitle = classtitle;
        }

        public String getClassphase() {
            return classphase;
        }

        public void setClassphase(String classphase) {
            this.classphase = classphase;
        }

        public String getClasssubject() {
            return classsubject;
        }

        public void setClasssubject(String classsubject) {
            this.classsubject = classsubject;
        }

        public String getVersions() {
            return versions;
        }

        public void setVersions(String versions) {
            this.versions = versions;
        }

        public String getBjyvideoid() {
            return bjyvideoid;
        }

        public void setBjyvideoid(String bjyvideoid) {
            this.bjyvideoid = bjyvideoid;
        }

        public String getTeaaudio() {
            return teaaudio;
        }

        public void setTeaaudio(String teaaudio) {
            this.teaaudio = teaaudio;
        }

        public String getSubmittime() {
            return submittime;
        }

        public void setSubmittime(String submittime) {
            this.submittime = submittime;
        }

        public String getCommenttime() {
            return commenttime;
        }

        public void setCommenttime(String commenttime) {
            this.commenttime = commenttime;
        }

        public String getExamplestatus() {
            return examplestatus;
        }

        public void setExamplestatus(String examplestatus) {
            this.examplestatus = examplestatus;
        }

        public String getCommentstatus() {
            return commentstatus;
        }

        public void setCommentstatus(String commentstatus) {
            this.commentstatus = commentstatus;
        }

        public String getReward_gold() {
            return reward_gold;
        }

        public void setReward_gold(String reward_gold) {
            this.reward_gold = reward_gold;
        }

        public String getOrderstatus() {
            return orderstatus;
        }

        public void setOrderstatus(String orderstatus) {
            this.orderstatus = orderstatus;
        }

        public String getReadstatus() {
            return readstatus;
        }

        public void setReadstatus(String readstatus) {
            this.readstatus = readstatus;
        }

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public List<QuestionsBean.Question> getQuestions() {
            return questions;
        }

        public void setQuestions(List<QuestionsBean.Question> questions) {
            this.questions = questions;
        }

    }
}
