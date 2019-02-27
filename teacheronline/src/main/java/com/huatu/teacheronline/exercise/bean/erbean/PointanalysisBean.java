package com.huatu.teacheronline.exercise.bean.erbean;


import com.bluelinelabs.logansquare.annotation.JsonObject;

import java.util.List;

/**
 * 知识点分析bean
 * Created by ljyu on 2016/12/20.
 */
@JsonObject(fieldDetectionPolicy = JsonObject.FieldDetectionPolicy.NONPRIVATE_FIELDS_AND_ACCESSORS)
public class PointanalysisBean {
    private String point;//知识点名称
    private String count;//题目数
    private String isright;//答对数
    private String correctrate;//正确率
    private List<PointanalysisBean> childrens;//子节点

    public String getPoint() {
        return point;
    }

    public void setPoint(String point) {
        this.point = point;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public String getIsright() {
        return isright;
    }

    public void setIsright(String isright) {
        this.isright = isright;
    }

    public String getCorrectrate() {
        return correctrate;
    }

    public void setCorrectrate(String correctrate) {
        this.correctrate = correctrate;
    }

    public List<PointanalysisBean> getChildrens() {
        return childrens;
    }

    public void setChildrens(List<PointanalysisBean> childrens) {
        this.childrens = childrens;
    }

    @Override
    public String toString() {
        return "PointanalysisBean{" +
                "point='" + point + '\'' +
                ", count='" + count + '\'' +
                ", isright='" + isright + '\'' +
                ", correctrate='" + correctrate + '\'' +
                ", childrens=" + childrens +
                '}';
    }
}
