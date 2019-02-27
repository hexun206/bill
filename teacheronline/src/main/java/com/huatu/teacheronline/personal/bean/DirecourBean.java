package com.huatu.teacheronline.personal.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by 18250 on 2017/10/10.
 * 我的课程-直播课-通知详情实体类
 */
public class DirecourBean implements Serializable{


    /**
     * message : success
     * data : [{"reminder":"请带上纸笔，提前进入直播间","TeacherName":"姜丽华","zhibotime":"11:15:00","Courseware_title":"密卷解析\u2014密卷二"}]
     * code : 1
     */
    private String message;
    private List<DataEntity> data;
    private String code;

    public void setMessage(String message) {
        this.message = message;
    }

    public void setData(List<DataEntity> data) {
        this.data = data;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public List<DataEntity> getData() {
        return data;
    }

    public String getCode() {
        return code;
    }

    public class DataEntity implements  Serializable {
        /**
         * reminder : 请带上纸笔，提前进入直播间
         * TeacherName : 姜丽华
         * zhibotime : 11:15:00
         * Courseware_title : 密卷解析—密卷二
         */
        private String reminder;
        private String TeacherName;
        private String zhibotime;
        private String Courseware_title;

        public void setReminder(String reminder) {
            this.reminder = reminder;
        }

        public void setTeacherName(String TeacherName) {
            this.TeacherName = TeacherName;
        }

        public void setZhibotime(String zhibotime) {
            this.zhibotime = zhibotime;
        }

        public void setCourseware_title(String Courseware_title) {
            this.Courseware_title = Courseware_title;
        }

        public String getReminder() {
            return reminder;
        }

        public String getTeacherName() {
            return TeacherName;
        }

        public String getZhibotime() {
            return zhibotime;
        }

        public String getCourseware_title() {
            return Courseware_title;
        }
    }
}
