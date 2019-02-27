package com.huatu.teacheronline.personal.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by kinndann on 2018/11/14.
 * description:结构化试题
 */
public class QuestionsBean {


    private String code;
    private String message;
    private Questions data;

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

    public Questions getData() {
        return data;
    }

    public void setData(Questions data) {
        this.data = data;
    }

    public boolean success() {
        return "1".equals(code);

    }


    public static class Question implements Serializable{
        /**
         * id : 55
         * type : 组织管理
         * questionname : 学校要组织一次教师培训，校长把这项工作交给你来做，你怎么开展?
         */

        private String id;
        private String type;
        private String questionname;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getQuestionname() {
            return questionname;
        }

        public void setQuestionname(String questionname) {
            this.questionname = questionname;
        }
    }


    public static class Questions {
        private List<Question> questions;

        public List<Question> getQuestions() {
            return questions;
        }

        public void setQuestions(List<Question> questions) {
            this.questions = questions;
        }
    }
}
