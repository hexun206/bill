package com.huatu.teacheronline.vipexercise.vipbean;

import java.io.Serializable;
import java.util.List;

/**
 * vip题库留言列表实体类
 * Created by 18250 on 2017/10/11.
 */
public class LeaveBean implements Serializable {


    /**
     * message : 获取我的VIP题库留言列表成功！a
     * data : [{"content":"嗯 ，ok","createTime":"2017-10-11 16:12:47","teacherPortrait":"","count":3,"teacherName":"蔡先生","teacherId":77},{"content":"好的 ，老师我明白了 。","createTime":"2017-10-11 16:11:25","teacherPortrait":"http://120.24.158.188:8088/group1/M00/00/01/eBievFnd1OaAdMncAAARDzraW8w362_150x150.jpg","count":0,"teacherName":"郑丽丽","teacherId":76},{"content":"老师您好，有个问题想请教您一下。您现在有时间吗？为什么第一题是这样的？","createTime":"2017-10-11 16:06:29","teacherPortrait":"http://120.24.158.188:8088/group1/M00/00/01/eBievFnd10KAKm80AAAZMi_E83o825_150x150.jpg","count":0,"teacherName":"周boss","teacherId":75},{"content":"老师您好，有个问题想请教您一下。您现在有时间吗？为什么第一题是这样的？","createTime":"2017-10-11 16:06:26","teacherPortrait":"http://120.24.158.188:8088/group1/M00/00/01/eBievFnd12WAGZpEAAAXfsx43wM138_150x150.jpg","count":0,"teacherName":"李老师","teacherId":74},{"content":"老师您好，有个问题想请教您一下。您现在有时间吗？为什么第一题是这样的？","createTime":"2017-10-11 16:04:16","teacherPortrait":"","count":0,"teacherName":"孙老师","teacherId":73},{"content":"老师您好，有个问题想请教您一下。您现在有时间吗？为什么第一题是这样的？","createTime":"2017-10-11 16:04:13","teacherPortrait":"","count":0,"teacherName":"钱老大","teacherId":72}]
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

    public class DataEntity implements Serializable{
        /**
         * content : 嗯 ，ok
         * createTime : 2017-10-11 16:12:47
         * teacherPortrait :
         * count : 3
         * teacherName : 蔡先生
         * teacherId : 77
         */
        private String content;
        private String createTime;
        private String teacherPortrait;
        private int count;
        private String teacherName;
        private int teacherId;

        public void setContent(String content) {
            this.content = content;
        }

        public void setCreateTime(String createTime) {
            this.createTime = createTime;
        }

        public void setTeacherPortrait(String teacherPortrait) {
            this.teacherPortrait = teacherPortrait;
        }

        public void setCount(int count) {
            this.count = count;
        }

        public void setTeacherName(String teacherName) {
            this.teacherName = teacherName;
        }

        public void setTeacherId(int teacherId) {
            this.teacherId = teacherId;
        }

        public String getContent() {
            return content;
        }

        public String getCreateTime() {
            return createTime;
        }

        public String getTeacherPortrait() {
            return teacherPortrait;
        }

        public int getCount() {
            return count;
        }

        public String getTeacherName() {
            return teacherName;
        }

        public int getTeacherId() {
            return teacherId;
        }

        @Override
        public String toString() {
            return "DataEntity{" +
                    "content='" + content + '\'' +
                    ", createTime='" + createTime + '\'' +
                    ", teacherPortrait='" + teacherPortrait + '\'' +
                    ", count=" + count +
                    ", teacherName='" + teacherName + '\'' +
                    ", teacherId=" + teacherId +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "LeaveBean{" +
                "message='" + message + '\'' +
                ", data=" + data +
                ", code='" + code + '\'' +
                '}';
    }
}
