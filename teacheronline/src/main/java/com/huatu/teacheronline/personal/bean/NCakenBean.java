package com.huatu.teacheronline.personal.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by 18250 on 2017/5/5.
 */
public class NCakenBean implements Serializable{
    /**  签到日历详情实体类
     * code : 1
     * data : [{"day":"1","sign":0,"give_gold":"+0金币","give_point":"+0积分","type":"past","done":0},{"day":"2","sign":0,"give_gold":"+0金币","give_point":"+0积分","type":"past","done":0},{"day":"3","sign":1,"give_gold":"+1金币","give_point":"+5积分","type":"past","done":0},{"day":"4","sign":0,"give_gold":"+0金币","give_point":"+0积分","type":"past","done":0},{"day":"5","sign":1,"give_gold":"+1金币","give_point":"+5积分","type":"today","done":0},{"day":"6","sign":0,"give_gold":"+1金币","give_point":"+20积分","type":"future","done":0},{"day":"7","sign":0,"give_gold":"+1金币","give_point":"+25积分","type":"future","done":0},{"day":"8","sign":0,"give_gold":"+1金币","give_point":"+5积分","type":"future","done":0},{"day":"9","sign":0,"give_gold":"+1金币","give_point":"+5积分","type":"future","done":0},{"day":"10","sign":0,"give_gold":"+1金币","give_point":"+5积分","type":"future","done":0},{"day":"11","sign":0,"give_gold":"+4金币","give_point":"+100积分","type":"future","done":0},{"day":"12","sign":0,"give_gold":"+1金币","give_point":"+5积分","type":"future","done":0},{"day":"13","sign":0,"give_gold":"+1金币","give_point":"+5积分","type":"future","done":0},{"day":"14","sign":0,"give_gold":"+1金币","give_point":"+5积分","type":"future","done":0},{"day":"15","sign":0,"give_gold":"+1金币","give_point":"+5积分","type":"future","done":0},{"day":"16","sign":0,"give_gold":"+1金币","give_point":"+5积分","type":"future","done":0},{"day":"17","sign":0,"give_gold":"+1金币","give_point":"+5积分","type":"future","done":0},{"day":"18","sign":0,"give_gold":"+10金币","give_point":"+200积分","type":"future","done":0},{"day":"19","sign":0,"give_gold":"+1金币","give_point":"+5积分","type":"future","done":0},{"day":"20","sign":0,"give_gold":"+1金币","give_point":"+5积分","type":"future","done":0},{"day":"21","sign":0,"give_gold":"+1金币","give_point":"+5积分","type":"future","done":0},{"day":"22","sign":0,"give_gold":"+1金币","give_point":"+5积分","type":"future","done":0},{"day":"23","sign":0,"give_gold":"+1金币","give_point":"+5积分","type":"future","done":0},{"day":"24","sign":0,"give_gold":"+1金币","give_point":"+5积分","type":"future","done":0},{"day":"25","sign":0,"give_gold":"+1金币","give_point":"+5积分","type":"future","done":0},{"day":"26","sign":0,"give_gold":"+20金币","give_point":"+400积分","type":"future","done":0},{"day":"27","sign":0,"give_gold":"+1金币","give_point":"+5积分","type":"future","done":0},{"day":"28","sign":0,"give_gold":"+1金币","give_point":"+20积分","type":"future","done":0},{"day":"29","sign":0,"give_gold":"+1金币","give_point":"+25积分","type":"future","done":0},{"day":"30","sign":0,"give_gold":"+1金币","give_point":"+5积分","type":"future","done":0},{"day":"31","sign":0,"give_gold":"+1金币","give_point":"+5积分","type":"future","done":0}]
     * continuous_sign : 1
     * month : 5
     * year : 2017
     * recentlyAccuracy : [0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,35,0,0,0,0,0,9.8]
     */

    private String code;
    private int continuous_sign;
    private String month;
    private String year;
    private List<DataBean> data;
    private List<Double> recentlyAccuracy;
    private int cycle;//签到周期天数

    public int getcycle() {
        return cycle;
    }

    public void setcycle(int cycle) {
        this.cycle = cycle;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public int getContinuous_sign() {
        return continuous_sign;
    }

    public void setContinuous_sign(int continuous_sign) {
        this.continuous_sign = continuous_sign;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public List<Double> getRecentlyAccuracy() {
        return recentlyAccuracy;
    }

    public void setRecentlyAccuracy(List<Double> recentlyAccuracy) {
        this.recentlyAccuracy = recentlyAccuracy;
    }

    public static class DataBean implements Serializable {
        /**
         * day : 1
         * sign : 0
         * give_gold : +0金币
         * give_point : +0积分
         * type : past
         * done : 0
         */

        private String day;
        private int sign;
        private String give_gold;
        private String give_point;
        private String type;
        private int done;

        public String getDay() {
            return day;
        }

        public void setDay(String day) {
            this.day = day;
        }

        public int getSign() {
            return sign;
        }

        public void setSign(int sign) {
            this.sign = sign;
        }

        public String getGive_gold() {
            return give_gold;
        }

        public void setGive_gold(String give_gold) {
            this.give_gold = give_gold;
        }

        public String getGive_point() {
            return give_point;
        }

        public void setGive_point(String give_point) {
            this.give_point = give_point;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public int getDone() {
            return done;
        }

        public void setDone(int done) {
            this.done = done;
        }
    }
}
