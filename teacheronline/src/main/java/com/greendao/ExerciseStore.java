package com.greendao;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit. 
/**
 * Entity mapped to table EXERCISE_STORE.
 */
public class ExerciseStore {

    private String qid;
    private String userid;

    public ExerciseStore() {
    }

    public ExerciseStore(String qid) {
        this.qid = qid;
    }

    public ExerciseStore(String qid, String userid) {
        this.qid = qid;
        this.userid = userid;
    }

    public String getQid() {
        return qid;
    }

    public void setQid(String qid) {
        this.qid = qid;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

}
