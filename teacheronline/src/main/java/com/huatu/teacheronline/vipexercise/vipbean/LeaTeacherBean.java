package com.huatu.teacheronline.vipexercise.vipbean;

import java.io.Serializable;
import java.util.List;

/**
 * vip留言详情实体类
 * Created by 18250 on 2017/10/11.
 */
public class LeaTeacherBean implements Serializable {


    /**
     * message : 获取教师留言回复详细信息成功！a
     * data : [{"content":"老师您好，有个问题想请教您一下。您现在有时间吗？为什么第一题是这样的？","createTime":"2017-10-11 16:06:29","teacherPortrait":"http://120.24.158.188:8088/group1/M00/00/01/eBievFnd10KAKm80AAAZMi_E83o825_150x150.jpg","userPortrait":"http://5beike.hteacher.net/teacherTest/uploads/member/20170811111318598d20cea04f9.jpg","type":0,"isRead":0}]
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
         * content : 老师您好，有个问题想请教您一下。您现在有时间吗？为什么第一题是这样的？
         * createTime : 2017-10-11 16:06:29
         * teacherPortrait : http://120.24.158.188:8088/group1/M00/00/01/eBievFnd10KAKm80AAAZMi_E83o825_150x150.jpg
         * userPortrait : http://5beike.hteacher.net/teacherTest/uploads/member/20170811111318598d20cea04f9.jpg
         * type : 0
         * isRead : 0
         */
        private String content;
        private String createTime;
        private String teacherPortrait;
        private String userPortrait;
        private int type;
        private int isRead;

        public void setContent(String content) {
            this.content = content;
        }

        public void setCreateTime(String createTime) {
            this.createTime = createTime;
        }

        public void setTeacherPortrait(String teacherPortrait) {
            this.teacherPortrait = teacherPortrait;
        }

        public void setUserPortrait(String userPortrait) {
            this.userPortrait = userPortrait;
        }

        public void setType(int type) {
            this.type = type;
        }

        public void setIsRead(int isRead) {
            this.isRead = isRead;
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

        public String getUserPortrait() {
            return userPortrait;
        }

        public int getType() {
            return type;
        }

        public int getIsRead() {
            return isRead;
        }
    }
}
