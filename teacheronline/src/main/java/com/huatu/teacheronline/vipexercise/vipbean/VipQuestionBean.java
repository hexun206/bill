package com.huatu.teacheronline.vipexercise.vipbean;

import java.io.Serializable;
import java.util.List;

/**
 * Vip试题详情bean
 * Created by ljyu on 2017/10/13.
 */
public class VipQuestionBean implements Serializable {


    /**
     * isPushAnalysis : 1
     * isPushAnswer : 0
     * count : 10
     * title : 开发测试，勿动
     * resultList : [{"standard":"","childContent":"","questionId":"59cc703ddbb2022cf402f318","childScore":0,"childTypeName":"","typeName":"单选题","index":1,
     * "childStandard":"","analysis":"解释：人体中，肝含水量为86%，肾含水量为83%，眼球含水量为99%，心脏含水量为75% ","childId":"","childOptions":[],"content":"1111人体含水量百分比最高的器官是:  ",
     * "isChildFlg":false,"typeCode":"80001","childAnswer":[],"score":1.5,"answer":["C"],"options":["肝 ","肾","眼球","心脏"],"childAnalysis":"",
     * "childTypeCode":""},{"standard":"","childContent":"","questionId":"59ccb63fdbb202471cea5eb5","childScore":0,"childTypeName":"","typeName":"单选题",
     * "index":2,"childStandard":"","analysis":"<p>adada444<\/p>\n","childId":"","childOptions":[],"content":"<p>adada<\/p>\n","isChildFlg":false,
     * "typeCode":"80001","childAnswer":[],"score":1,"answer":["A"],"options":["<p>adada<\/p>\n","<p>adada<\/p>\n","<p>adada<\/p>\n","<p>adada<\/p>\n"],
     * "childAnalysis":"","childTypeCode":""},{"standard":"","childContent":"","questionId":"59cdc1e6f070c61647407675","childScore":0,"childTypeName":"",
     * "typeName":"单选题","index":3,"childStandard":"","analysis":"<p>444<\/p>\n","childId":"","childOptions":[],"content":"<p>444个哈哈哈哈<\/p>\n",
     * "isChildFlg":false,"typeCode":"80001","childAnswer":[],"score":442522,"answer":["D"],"options":["<p>442555<\/p>\n","<p>44不不不<\/p>\n","<p>55尴尬<\/p>\n",
     * "<p>444<\/p>\n"],"childAnalysis":"","childTypeCode":""},{"standard":"","childContent":"","questionId":"59cc703ddbb2022cf402f31a","childScore":0,
     * "childTypeName":"","typeName":"多选题","index":4,"childStandard":"","analysis":"解释：单宁酸无臭，微有特殊气味，味极涩。","childId":"","childOptions":[],
     * "content":"下列作品中是沈从文有（     ）","isChildFlg":false,"typeCode":"80002","childAnswer":[],"score":2,"answer":["B","C"],"options":["《围城》","《边城》","《萧萧》",
     * "《骆驼祥子》","《寒夜》"],"childAnalysis":"","childTypeCode":""},{"standard":"","childContent":"","questionId":"59cc703ddbb2022cf402f31b","childScore":0,
     * "childTypeName":"","typeName":"多选题","index":5,"childStandard":"","analysis":"解释：单宁酸无臭，微有特殊气味，味极涩。","childId":"","childOptions":[],
     * "content":"古代\u201c四大名著\u201d指（     ）","isChildFlg":false,"typeCode":"80002","childAnswer":[],"score":2,"answer":["A","B","C","D"],
     * "options":["《三国演义》","《水浒传》","《红楼梦》","《西游记》","《金瓶梅词话》"],"childAnalysis":"","childTypeCode":""},{"standard":"","childContent":"",
     * "questionId":"59cc703ddbb2022cf402f31c","childScore":0,"childTypeName":"","typeName":"判断题","index":6,"childStandard":"","analysis":"解释：此茶应该是龙井茶。",
     * "childId":"","childOptions":[],"content":"产于浙江杭州，以色翠、香味浓郁、味甘、形美四绝而著称于世，有\u201c国茶\u201d之称的是碧螺春。","isChildFlg":false,"typeCode":"80003",
     * "childAnswer":[],"score":2,"answer":["B"],"options":["对","错"],"childAnalysis":"","childTypeCode":""},{"standard":"","childContent":"",
     * "questionId":"59cc703ddbb2022cf402f31d","childScore":0,"childTypeName":"","typeName":"判断题","index":7,"childStandard":"",
     * "analysis":"<p>解释：单宁酸无臭，微有特殊气味，味极涩。<\/p>\n","childId":"","childOptions":[],"content":"<p>四川小吃担担面是因为沿街挑担叫卖而得名。<\/p>\n","isChildFlg":false,
     * "typeCode":"80003","childAnswer":[],"score":2,"answer":["A"],"options":["对","错"],"childAnalysis":"","childTypeCode":""},{"standard":"",
     * "childContent":"common1人体含水量百分比最高的器官是:  ","questionId":"59cc703ddbb2022cf402f319","childScore":1.5,"childTypeName":"单选题","typeName":"共用题干题","index":8,
     * "childStandard":"","analysis":"","childId":"d32cd82d08964c2eb50e42e96bdbde7b","childOptions":["肝 ","肾","眼球","心脏"],"content":"人体含水量百分比最高的器官是共用题干题:  ",
     * "isChildFlg":true,"typeCode":"80005","childAnswer":[],"score":3.5,"answer":"","options":[],
     * "childAnalysis":"解释：人体中，肝含水量为86%，肾含水量为83%，眼球含水量为99%，心脏含水量为75% ","childTypeCode":"80001"},{"standard":"","childContent":"common2下面哪种酸，人在品尝时不是酸味的:",
     * "questionId":"59cc703ddbb2022cf402f319","childScore":2,"childTypeName":"多选题","typeName":"共用题干题","index":9,"childStandard":"","analysis":"",
     * "childId":"dfc31024ddb9475195cfbb49ea79b926","childOptions":["琥珀酸 ","苹果酸 ","柠檬酸","单宁酸 "],"content":"人体含水量百分比最高的器官是共用题干题:  ","isChildFlg":true,
     * "typeCode":"80005","childAnswer":[],"score":3.5,"answer":"","options":[],"childAnalysis":"解释：单宁酸无臭，微有特殊气味，味极涩。","childTypeCode":"80002"},
     * {"standard":"好的班集体具有的特征四个方面，共5分。","childContent":"","questionId":"59cc703ddbb2022cf402f31e","childScore":0,"childTypeName":"","typeName":"简答题",
     * "index":10,"childStandard":"","analysis":"在领口上撒一些盐末，轻轻揉搓，用水漂去盐分即可。因为人的汗液\n\n中含蛋白质，不能在水中溶解，而在食盐中就能很快溶解。","childId":"","childOptions":[],
     * "content":"衣服领口很脏，怎样可以去除污渍？","isChildFlg":false,"typeCode":"80004","childAnswer":[],"score":2,"answer":[],"options":["",""],"childAnalysis":"",
     * "childTypeCode":""}]
     */

    private int isPushAnalysis;
    private int isPushAnswer;
    private int count;
    private String title;
    /**
     * standard :
     * childContent :
     * questionId : 59cc703ddbb2022cf402f318
     * childScore : 0
     * childTypeName :
     * typeName : 单选题
     * index : 1
     * childStandard :
     * analysis : 解释：人体中，肝含水量为86%，肾含水量为83%，眼球含水量为99%，心脏含水量为75%
     * childId :
     * childOptions : []
     * content : 1111人体含水量百分比最高的器官是:
     * isChildFlg : false
     * typeCode : 80001
     * childAnswer : []
     * score : 1.5
     * answer : ["C"]
     * options : ["肝 ","肾","眼球","心脏"]
     * childAnalysis :
     * childTypeCode :
     */

    private List<ResultListBean> resultList;

    public int getIsPushAnalysis() {
        return isPushAnalysis;
    }

    public void setIsPushAnalysis(int isPushAnalysis) {
        this.isPushAnalysis = isPushAnalysis;
    }

    public int getIsPushAnswer() {
        return isPushAnswer;
    }

    public void setIsPushAnswer(int isPushAnswer) {
        this.isPushAnswer = isPushAnswer;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<ResultListBean> getResultList() {
        return resultList;
    }

    public void setResultList(List<ResultListBean> resultList) {
        this.resultList = resultList;
    }

    @Override
    public String toString() {
        return "VipQuestionBean{" +
                "isPushAnalysis=" + isPushAnalysis +
                ", isPushAnswer=" + isPushAnswer +
                ", count=" + count +
                ", title='" + title + '\'' +
                ", resultList=" + resultList +
                '}';
    }

    public static class ResultListBean implements Serializable {
        private String standard;
        private String childContent;
        private String questionId;
        private double childScore;
        private String childTypeName;
        private String typeName;
        private int index;
        private String childStandard;
        private String analysis;
        private String childId;
        private String content;
        private boolean isChildFlg;
        private String typeCode;
        private double score;
        private String childAnalysis;
        private String childTypeCode;
        private List<String> childOptions;
        private List<String> childAnswer;
        private List<String> answer;
        private List<String> options;

        public String getStandard() {
            return standard;
        }

        public void setStandard(String standard) {
            this.standard = standard;
        }

        public String getChildContent() {
            return childContent;
        }

        public void setChildContent(String childContent) {
            this.childContent = childContent;
        }

        public String getQuestionId() {
            return questionId;
        }

        public void setQuestionId(String questionId) {
            this.questionId = questionId;
        }

        public double getChildScore() {
            return childScore;
        }

        public void setChildScore(double childScore) {
            this.childScore = childScore;
        }

        public String getChildTypeName() {
            return childTypeName;
        }

        public void setChildTypeName(String childTypeName) {
            this.childTypeName = childTypeName;
        }

        public String getTypeName() {
            return typeName;
        }

        public void setTypeName(String typeName) {
            this.typeName = typeName;
        }

        public int getIndex() {
            return index;
        }

        public void setIndex(int index) {
            this.index = index;
        }

        public String getChildStandard() {
            return childStandard;
        }

        public void setChildStandard(String childStandard) {
            this.childStandard = childStandard;
        }

        public String getAnalysis() {
            return analysis;
        }

        public void setAnalysis(String analysis) {
            this.analysis = analysis;
        }

        public String getChildId() {
            return childId;
        }

        public void setChildId(String childId) {
            this.childId = childId;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public boolean isIsChildFlg() {
            return isChildFlg;
        }

        public void setIsChildFlg(boolean isChildFlg) {
            this.isChildFlg = isChildFlg;
        }

        public String getTypeCode() {
            return typeCode;
        }

        public void setTypeCode(String typeCode) {
            this.typeCode = typeCode;
        }

        public double getScore() {
            return score;
        }

        public void setScore(double score) {
            this.score = score;
        }

        public String getChildAnalysis() {
            return childAnalysis;
        }

        public void setChildAnalysis(String childAnalysis) {
            this.childAnalysis = childAnalysis;
        }

        public String getChildTypeCode() {
            return childTypeCode;
        }

        public void setChildTypeCode(String childTypeCode) {
            this.childTypeCode = childTypeCode;
        }

        public List<String> getChildOptions() {
            return childOptions;
        }

        public void setChildOptions(List<String> childOptions) {
            this.childOptions = childOptions;
        }

        public List<String> getChildAnswer() {
            return childAnswer;
        }

        public void setChildAnswer(List<String> childAnswer) {
            this.childAnswer = childAnswer;
        }

        public List<String> getAnswer() {
            return answer;
        }

        public void setAnswer(List<String> answer) {
            this.answer = answer;
        }

        public List<String> getOptions() {
            return options;
        }

        public void setOptions(List<String> options) {
            this.options = options;
        }

        @Override
        public String toString() {
            return "ResultListBean{" +
                    "standard='" + standard + '\'' +
                    ", childContent='" + childContent + '\'' +
                    ", questionId='" + questionId + '\'' +
                    ", childScore=" + childScore +
                    ", childTypeName='" + childTypeName + '\'' +
                    ", typeName='" + typeName + '\'' +
                    ", index=" + index +
                    ", childStandard='" + childStandard + '\'' +
                    ", analysis='" + analysis + '\'' +
                    ", childId='" + childId + '\'' +
                    ", content='" + content + '\'' +
                    ", isChildFlg=" + isChildFlg +
                    ", typeCode='" + typeCode + '\'' +
                    ", score=" + score +
                    ", childAnalysis='" + childAnalysis + '\'' +
                    ", childTypeCode='" + childTypeCode + '\'' +
                    ", childOptions=" + childOptions +
                    ", childAnswer=" + childAnswer +
                    ", answer=" + answer +
                    ", options=" + options +
                    '}';
        }
    }

}
