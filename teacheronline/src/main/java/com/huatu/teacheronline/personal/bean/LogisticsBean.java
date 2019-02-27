package com.huatu.teacheronline.personal.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by 79937 on 2016/8/9.
 */
public class LogisticsBean implements Serializable{

    /**
     * message : 签收
     * nu : 1202036918878
     * companytype : 韵达快递
     * ischeck : 1
     * updatetime : 2016-08-09 13:33:58
     * data : [{"time":"2016-02-29 19:57:34","location":"","context":"在湖南长沙天心区东塘公司三重才园便利店分部进行签收扫描，快件已被 拍照 签收","ftime":"2016-02-29 19:57:34"},
     * {"time":"2016-02-29 19:55:00","location":"","context":"在湖南长沙天心区东塘公司三重才园便利店分部进行派件扫描；派送业务员：小彭；联系电话：18711030918","ftime":"2016-02-29 19:55:00"},
     * {"time":"2016-02-29 03:19:41","location":"","context":"到达目的地网点湖南长沙天心区东塘公司，快件将很快进行派送","ftime":"2016-02-29 03:19:41"},{"time":"2016-02-28 14:55:13",
     * "location":"","context":"从湖南长沙分拨中心发出，本次转运目的地：湖南长沙天心区东塘公司","ftime":"2016-02-28 14:55:13"},{"time":"2016-02-28 14:29:02","location":"",
     * "context":"在分拨中心湖南长沙分拨中心进行卸车扫描","ftime":"2016-02-28 14:29:02"},{"time":"2016-02-26 23:24:01","location":"","context":"在陕西西安分拨中心进行装车扫描，即将发往：湖南长沙分拨中心",
     * "ftime":"2016-02-26 23:24:01"},{"time":"2016-02-26 19:51:08","location":"","context":"在分拨中心陕西西安分拨中心进行卸车扫描","ftime":"2016-02-26 19:51:08"},
     * {"time":"2016-02-24 19:16:29","location":"","context":"在新疆乌鲁木齐分拨中心进行装车扫描，即将发往：北京网点包","ftime":"2016-02-24 19:16:29"},{"time":"2016-02-24 19:14:46",
     * "location":"","context":"在分拨中心新疆乌鲁木齐分拨中心进行称重扫描","ftime":"2016-02-24 19:14:46"},{"time":"2016-02-24 15:21:31","location":"",
     * "context":"在新疆乌鲁木齐头屯河区机场公司进行到件扫描","ftime":"2016-02-24 15:21:31"}]
     */

    private String message;
    private String nu;
    private String companytype;
    private String ischeck;
    private String updatetime;
    private String customer;//乐语咨询地址
    /**
     * time : 2016-02-29 19:57:34
     * location :
     * context : 在湖南长沙天心区东塘公司三重才园便利店分部进行签收扫描，快件已被 拍照 签收
     * ftime : 2016-02-29 19:57:34
     */

    private List<DataBean> data;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getNu() {
        return nu;
    }

    public void setNu(String nu) {
        this.nu = nu;
    }

    public String getCompanytype() {
        return companytype;
    }

    public void setCompanytype(String companytype) {
        this.companytype = companytype;
    }

    public String getIscheck() {
        return ischeck;
    }

    public void setIscheck(String ischeck) {
        this.ischeck = ischeck;
    }

    public String getUpdatetime() {
        return updatetime;
    }

    public void setUpdatetime(String updatetime) {
        this.updatetime = updatetime;
    }

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public String getCustomer() {
        return customer;
    }

    public void setCustomer(String customer) {
        this.customer = customer;
    }

    public static class DataBean implements Serializable{
        private String time;
        private String location;
        private String context;
        private String ftime;

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }

        public String getLocation() {
            return location;
        }

        public void setLocation(String location) {
            this.location = location;
        }

        public String getContext() {
            return context;
        }

        public void setContext(String context) {
            this.context = context;
        }

        public String getFtime() {
            return ftime;
        }

        public void setFtime(String ftime) {
            this.ftime = ftime;
        }
    }
}
