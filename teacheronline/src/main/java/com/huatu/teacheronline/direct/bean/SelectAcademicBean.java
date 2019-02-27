package com.huatu.teacheronline.direct.bean;

import java.io.Serializable;
import java.util.List;

/**
 * 学段学科筛选
 * Created by ljyu on 2017/9/19.
 */
public class SelectAcademicBean implements Serializable{

    private List<String> studySection;
    private List<String> subject;

    public List<String> getStudySection() {
        return studySection;
    }

    public void setStudySection(List<String> studySection) {
        this.studySection = studySection;
    }

    public List<String> getSubject() {
        return subject;
    }

    public void setSubject(List<String> subject) {
        this.subject = subject;
    }

    @Override
    public String toString() {
        return "SelectAcademicBean{" +
                "studySection=" + studySection +
                ", subject=" + subject +
                '}';
    }
}
