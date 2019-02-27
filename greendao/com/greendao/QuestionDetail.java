package com.greendao;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit. 

/**
 * Entity mapped to table QUESTION_DETAIL.
 */
public class QuestionDetail {

    private String id;
    private String choices;
    private String qid;
    private String answers;
    private String author;
    private String comment;
    private String question;
    private String qpoint;
    private String qrootpoint;
    private String content;
    private String score;
    private String qvideo;
    private String qtype;

    public QuestionDetail() {
    }

    public QuestionDetail(String id, String choices, String qid, String answers, String author, String comment, String question, String qpoint, String
            qrootpoint, String content, String score, String qvideo, String qtype) {
        this.id = id;
        this.choices = choices;
        this.qid = qid;
        this.answers = answers;
        this.author = author;
        this.comment = comment;
        this.question = question;
        this.qpoint = qpoint;
        this.qrootpoint = qrootpoint;
        this.content = content;
        this.score = score;
        this.qvideo = qvideo;
        this.qtype = qtype;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getChoices() {
        return choices;
    }

    public void setChoices(String choices) {
        this.choices = choices;
    }

    public String getQid() {
        return qid;
    }

    public void setQid(String qid) {
        this.qid = qid;
    }

    public String getAnswers() {
        return answers;
    }

    public void setAnswers(String answers) {
        this.answers = answers;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getQpoint() {
        return qpoint;
    }

    public void setQpoint(String qpoint) {
        this.qpoint = qpoint;
    }

    public String getQrootpoint() {
        return qrootpoint;
    }

    public void setQrootpoint(String qrootpoint) {
        this.qrootpoint = qrootpoint;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public String getQvideo() {
        return qvideo;
    }

    public void setQvideo(String qvideo) {
        this.qvideo = qvideo;
    }

    public String getQtype() {
        return qtype;
    }

    public void setQtype(String qtype) {
        this.qtype = qtype;
    }

}
