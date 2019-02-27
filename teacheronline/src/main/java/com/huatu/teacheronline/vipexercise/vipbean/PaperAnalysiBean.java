package com.huatu.teacheronline.vipexercise.vipbean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by 79937 on 2017/10/17.
 */
public class PaperAnalysiBean implements Serializable{

    /**
     * defeatNum : 0
     * objectiveAnalysis : [{"isRightFlg":false,"questionId":"59cdc1e6f070c61647407675","childId":""},{"isRightFlg":false,
     * "questionId":"59ccb63fdbb202471cea5eb5","childId":""},{"isRightFlg":false,"questionId":"59cc703ddbb2022cf402f31d","childId":""},{"isRightFlg":false,
     * "questionId":"59cc703ddbb2022cf402f31b","childId":""},{"isRightFlg":false,"questionId":"59cc703ddbb2022cf402f31c","childId":""},{"isRightFlg":false,
     * "questionId":"59cc703ddbb2022cf402f31a","childId":""},{"isRightFlg":false,"questionId":"59cc703ddbb2022cf402f318","childId":""},{"isRightFlg":true,
     * "questionId":"59cc703ddbb2022cf402f319","childId":"dfc31024ddb9475195cfbb49ea79b926"},{"isRightFlg":true,"questionId":"59cc703ddbb2022cf402f319",
     * "childId":"dfc31024ddb9475195cfbb49ea79b926"}]
     * objectiveScoreRate : 0.00%
     * objectiveScoreSum : 442536
     * subjectiveScoreRate : 50.00%
     * title : 开发测试，勿动
     * objectiveScoreAvg : 49170.67
     * pointAnalysis : [{"correctTimes":1,"name":"数学","count":10,"correctRate":"10.00%"}]
     * unansweredTimes : 6
     * difficulty : 8
     * actualTime : 50
     * subjectiveAnalysis : [{"score":2,"questionId":"59cc703ddbb2022cf402f31e","actualScore":1,"childId":""}]
     * correctTimes : 2
     * errorTimes : 1
     * questionNum : 10
     * ranking : 1
     * time : 205
     * subjectiveScoreAvg : 2
     * correctRate : 67.00%
     * subjectiveScoreSum : 2
     */

    private int defeatNum;//打败人数
    private String objectiveScoreRate;//客观题正确率
    private String objectiveScoreSum;//客观题总分
    private String subjectiveScoreRate;//主观题正确率
    private String title;//标题
    private double objectiveScoreAvg;//客观题平均分
    private int unansweredTimes;//未答次数
    private double difficulty;//难度
    private int actualTime;//实际用时
    private int correctTimes;//正确次数
    private int errorTimes;//做错次数
    private int questionNum;//试题数
    private int ranking;//顺序
    private int time;//推荐用时
    private int subjectiveScoreAvg;//主观题平均分
    private String correctRate;//正确率
    private int subjectiveScoreSum;//主观题总分
    private String objectiveActualScoreSum;//客观题总分
    /**
     * isRightFlg : false
     * questionId : 59cdc1e6f070c61647407675
     * childId :
     */

    private List<ObjectiveAnalysisBean> objectiveAnalysis; //客观题分析
    /**
     * correctTimes : 1
     * name : 数学
     * count : 10
     * correctRate : 10.00%
     */

    private List<PointAnalysisBean> pointAnalysis;//知识点分析
    /**
     * score : 2
     * questionId : 59cc703ddbb2022cf402f31e
     * actualScore : 1
     * childId :
     */

    private List<SubjectiveAnalysisBean> subjectiveAnalysis;

    public int getDefeatNum() {
        return defeatNum;
    }

    public void setDefeatNum(int defeatNum) {
        this.defeatNum = defeatNum;
    }

    public String getObjectiveScoreRate() {
        return objectiveScoreRate;
    }

    public void setObjectiveScoreRate(String objectiveScoreRate) {
        this.objectiveScoreRate = objectiveScoreRate;
    }

    public String getObjectiveScoreSum() {
        return objectiveScoreSum;
    }

    public void setObjectiveScoreSum(String objectiveScoreSum) {
        this.objectiveScoreSum = objectiveScoreSum;
    }

    public String getSubjectiveScoreRate() {
        return subjectiveScoreRate;
    }

    public void setSubjectiveScoreRate(String subjectiveScoreRate) {
        this.subjectiveScoreRate = subjectiveScoreRate;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public double getObjectiveScoreAvg() {
        return objectiveScoreAvg;
    }

    public void setObjectiveScoreAvg(double objectiveScoreAvg) {
        this.objectiveScoreAvg = objectiveScoreAvg;
    }

    public int getUnansweredTimes() {
        return unansweredTimes;
    }

    public void setUnansweredTimes(int unansweredTimes) {
        this.unansweredTimes = unansweredTimes;
    }

    public double getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(double difficulty) {
        this.difficulty = difficulty;
    }

    public int getActualTime() {
        return actualTime;
    }

    public void setActualTime(int actualTime) {
        this.actualTime = actualTime;
    }

    public int getCorrectTimes() {
        return correctTimes;
    }

    public void setCorrectTimes(int correctTimes) {
        this.correctTimes = correctTimes;
    }

    public int getErrorTimes() {
        return errorTimes;
    }

    public void setErrorTimes(int errorTimes) {
        this.errorTimes = errorTimes;
    }

    public int getQuestionNum() {
        return questionNum;
    }

    public void setQuestionNum(int questionNum) {
        this.questionNum = questionNum;
    }

    public int getRanking() {
        return ranking;
    }

    public void setRanking(int ranking) {
        this.ranking = ranking;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public int getSubjectiveScoreAvg() {
        return subjectiveScoreAvg;
    }

    public void setSubjectiveScoreAvg(int subjectiveScoreAvg) {
        this.subjectiveScoreAvg = subjectiveScoreAvg;
    }

    public String getCorrectRate() {
        return correctRate;
    }

    public void setCorrectRate(String correctRate) {
        this.correctRate = correctRate;
    }

    public int getSubjectiveScoreSum() {
        return subjectiveScoreSum;
    }

    public void setSubjectiveScoreSum(int subjectiveScoreSum) {
        this.subjectiveScoreSum = subjectiveScoreSum;
    }

    public List<ObjectiveAnalysisBean> getObjectiveAnalysis() {
        return objectiveAnalysis;
    }

    public void setObjectiveAnalysis(List<ObjectiveAnalysisBean> objectiveAnalysis) {
        this.objectiveAnalysis = objectiveAnalysis;
    }

    public List<PointAnalysisBean> getPointAnalysis() {
        return pointAnalysis;
    }

    public void setPointAnalysis(List<PointAnalysisBean> pointAnalysis) {
        this.pointAnalysis = pointAnalysis;
    }

    public List<SubjectiveAnalysisBean> getSubjectiveAnalysis() {
        return subjectiveAnalysis;
    }

    public void setSubjectiveAnalysis(List<SubjectiveAnalysisBean> subjectiveAnalysis) {
        this.subjectiveAnalysis = subjectiveAnalysis;
    }

    public String getObjectiveActualScoreSum() {
        return objectiveActualScoreSum;
    }

    public void setObjectiveActualScoreSum(String objectiveActualScoreSum) {
        this.objectiveActualScoreSum = objectiveActualScoreSum;
    }

    /**
     * 客观题分析
     */
    public static class ObjectiveAnalysisBean implements Serializable{
        private boolean isRightFlg;//是否正确
        private String questionId;//试题Id
        private String childId;//子试题Id
        private boolean isAnsweredFlg;//是否答题

        public boolean isIsRightFlg() {
            return isRightFlg;
        }

        public void setIsRightFlg(boolean isRightFlg) {
            this.isRightFlg = isRightFlg;
        }

        public boolean isAnsweredFlg() {
            return isAnsweredFlg;
        }

        public void setIsAnsweredFlg(boolean isAnsweredFlg) {
            this.isAnsweredFlg = isAnsweredFlg;
        }

        public String getQuestionId() {
            return questionId;
        }

        public void setQuestionId(String questionId) {
            this.questionId = questionId;
        }

        public String getChildId() {
            return childId;
        }

        public void setChildId(String childId) {
            this.childId = childId;
        }

        @Override
        public String toString() {
            return "ObjectiveAnalysisBean{" +
                    "isRightFlg=" + isRightFlg +
                    ", questionId='" + questionId + '\'' +
                    ", childId='" + childId + '\'' +
                    '}';
        }

    }

    /**
     * 知识点分析
     */
    public static class PointAnalysisBean implements Serializable{
        /**
         * correctTimes : 1
         * pointId : 0
         * name : 父级知识点
         * count : 10
         * childList : [{"correctTimes":1,"pointId":89,"name":"数学","count":10,"correctRate":"10.00"}]
         * correctRate : 10.00
         */

        private int correctTimes;
        private int pointId;
        private String name;
        private int count;
        private String correctRate;
        /**
         * correctTimes : 1
         * pointId : 89
         * name : 数学
         * count : 10
         * correctRate : 10.00
         */

        private List<ChildListBean> childList;

        public int getCorrectTimes() {
            return correctTimes;
        }

        public void setCorrectTimes(int correctTimes) {
            this.correctTimes = correctTimes;
        }

        public int getPointId() {
            return pointId;
        }

        public void setPointId(int pointId) {
            this.pointId = pointId;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }

        public String getCorrectRate() {
            return correctRate;
        }

        public void setCorrectRate(String correctRate) {
            this.correctRate = correctRate;
        }

        public List<ChildListBean> getChildList() {
            return childList;
        }

        public void setChildList(List<ChildListBean> childList) {
            this.childList = childList;
        }

        public static class ChildListBean implements Serializable{
            private int correctTimes;
            private int pointId;
            private String name;
            private int count;
            private String correctRate;

            public int getCorrectTimes() {
                return correctTimes;
            }

            public void setCorrectTimes(int correctTimes) {
                this.correctTimes = correctTimes;
            }

            public int getPointId() {
                return pointId;
            }

            public void setPointId(int pointId) {
                this.pointId = pointId;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public int getCount() {
                return count;
            }

            public void setCount(int count) {
                this.count = count;
            }

            public String getCorrectRate() {
                return correctRate;
            }

            public void setCorrectRate(String correctRate) {
                this.correctRate = correctRate;
            }
        }
//        private int correctTimes;//作对次数
//        private String name;//知识点名称
//        private int count;//题数
//        private String correctRate;//正确率
//        private List<ChildListBean> childListBeans; //子知识点
//
//        public int getCorrectTimes() {
//            return correctTimes;
//        }
//
//        public void setCorrectTimes(int correctTimes) {
//            this.correctTimes = correctTimes;
//        }
//
//        public String getName() {
//            return name;
//        }
//
//        public void setName(String name) {
//            this.name = name;
//        }
//
//        public int getCount() {
//            return count;
//        }
//
//        public void setCount(int count) {
//            this.count = count;
//        }
//
//        public String getCorrectRate() {
//            return correctRate;
//        }
//
//        public void setCorrectRate(String correctRate) {
//            this.correctRate = correctRate;
//        }
//
//        public List<ChildListBean> getChildListBeans() {
//            return childListBeans;
//        }
//
//        public void setChildListBeans(List<ChildListBean> childListBeans) {
//            this.childListBeans = childListBeans;
//        }
//
//        @Override
//        public String toString() {
//            return "PointAnalysisBean{" +
//                    "correctTimes=" + correctTimes +
//                    ", name='" + name + '\'' +
//                    ", count=" + count +
//                    ", correctRate='" + correctRate + '\'' +
//                    ", childListBeans=" + childListBeans +
//                    '}';
//        }
//
//        public static class ChildListBean{
//            private int correctTimes;//作对次数
//            private String name;//知识点名称
//            private int count;//题数
//            private String correctRate;//正确率
//
//            public int getCorrectTimes() {
//                return correctTimes;
//            }
//
//            public void setCorrectTimes(int correctTimes) {
//                this.correctTimes = correctTimes;
//            }
//
//            public String getName() {
//                return name;
//            }
//
//            public void setName(String name) {
//                this.name = name;
//            }
//
//            public int getCount() {
//                return count;
//            }
//
//            public void setCount(int count) {
//                this.count = count;
//            }
//
//            public String getCorrectRate() {
//                return correctRate;
//            }
//
//            public void setCorrectRate(String correctRate) {
//                this.correctRate = correctRate;
//            }
//
//            @Override
//            public String toString() {
//                return "ChildListBean{" +
//                        "correctTimes=" + correctTimes +
//                        ", name='" + name + '\'' +
//                        ", count=" + count +
//                        ", correctRate='" + correctRate + '\'' +
//                        '}';
//            }
//        }


    }

    /**
     * 主观题分析
     */
    public static class SubjectiveAnalysisBean implements Serializable{
        private int score;//分数
        private String questionId;//试题Id
        private int actualScore;//实际分数
        private String childId;//子试题Id

        public int getScore() {
            return score;
        }

        public void setScore(int score) {
            this.score = score;
        }

        public String getQuestionId() {
            return questionId;
        }

        public void setQuestionId(String questionId) {
            this.questionId = questionId;
        }

        public int getActualScore() {
            return actualScore;
        }

        public void setActualScore(int actualScore) {
            this.actualScore = actualScore;
        }

        public String getChildId() {
            return childId;
        }

        public void setChildId(String childId) {
            this.childId = childId;
        }

        @Override
        public String toString() {
            return "SubjectiveAnalysisBean{" +
                    "score=" + score +
                    ", questionId='" + questionId + '\'' +
                    ", actualScore=" + actualScore +
                    ", childId='" + childId + '\'' +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "PaperAnalysiBean{" +
                "defeatNum=" + defeatNum +
                ", objectiveScoreRate='" + objectiveScoreRate + '\'' +
                ", objectiveScoreSum=" + objectiveScoreSum +
                ", subjectiveScoreRate='" + subjectiveScoreRate + '\'' +
                ", title='" + title + '\'' +
                ", objectiveScoreAvg=" + objectiveScoreAvg +
                ", unansweredTimes=" + unansweredTimes +
                ", difficulty=" + difficulty +
                ", actualTime=" + actualTime +
                ", correctTimes=" + correctTimes +
                ", errorTimes=" + errorTimes +
                ", questionNum=" + questionNum +
                ", ranking=" + ranking +
                ", time=" + time +
                ", subjectiveScoreAvg=" + subjectiveScoreAvg +
                ", correctRate='" + correctRate + '\'' +
                ", subjectiveScoreSum=" + subjectiveScoreSum +
                ", objectiveAnalysis=" + objectiveAnalysis +
                ", pointAnalysis=" + pointAnalysis +
                ", subjectiveAnalysis=" + subjectiveAnalysis +
                '}';
    }
}
