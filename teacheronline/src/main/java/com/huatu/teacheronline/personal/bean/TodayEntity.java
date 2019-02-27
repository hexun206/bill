package com.huatu.teacheronline.personal.bean;

import com.bluelinelabs.logansquare.annotation.JsonObject;

import java.io.Serializable;

/**
 * 24小时直播今天直播
 * Created by 18250 on 2017/10/24.
 */
@JsonObject(fieldDetectionPolicy = JsonObject.FieldDetectionPolicy.NONPRIVATE_FIELDS_AND_ACCESSORS)
public class TodayEntity implements Serializable {
        /**
         * netclass_pdf :
         * video_status : 0
         * isTrial : 0
         * ccCourses_id : null
         * ccApi_key : null
         * camera : 1
         * lubourl :
         * number :
         * hd :
         * pic :
         * is_buy : 1
         * videoType : 1
         * Title : 中学科目二笔试备考
         * kouling :
         * NetClassId : 1920
         * classTitle : 24小时大咖直播在线
         * ccUid : null
         * zhibourl : be44b3a821dc4245a6398c00577aeec4
         * zhibotime : 09:30-11:00
         * is_next_day : 0
         * iscamera : 1
         */
        private String netclass_pdf;
        private String video_status;
        private int isTrial;
        private String ccCourses_id;
        private String ccApi_key;
        private String camera;
        private String lubourl;
        private String number;
        private String hd;
        private String pic;
        private int is_buy;
        private int videoType;
        private String Title;
        private String kouling;
        private String NetClassId;
        private String classTitle;
        private String ccUid;
        private String zhibourl;
        private String zhibotime;
        private String is_next_day;
        private int iscamera;

        public void setNetclass_pdf(String netclass_pdf) {
            this.netclass_pdf = netclass_pdf;
        }

        public void setVideo_status(String video_status) {
            this.video_status = video_status;
        }

        public void setIsTrial(int isTrial) {
            this.isTrial = isTrial;
        }

        public void setCcCourses_id(String ccCourses_id) {
            this.ccCourses_id = ccCourses_id;
        }

        public void setCcApi_key(String ccApi_key) {
            this.ccApi_key = ccApi_key;
        }

        public void setCamera(String camera) {
            this.camera = camera;
        }

        public void setLubourl(String lubourl) {
            this.lubourl = lubourl;
        }

        public void setNumber(String number) {
            this.number = number;
        }

        public void setHd(String hd) {
            this.hd = hd;
        }

        public void setPic(String pic) {
            this.pic = pic;
        }

        public void setIs_buy(int is_buy) {
            this.is_buy = is_buy;
        }

        public void setVideoType(int videoType) {
            this.videoType = videoType;
        }

        public void setTitle(String Title) {
            this.Title = Title;
        }

        public void setKouling(String kouling) {
            this.kouling = kouling;
        }

        public void setNetClassId(String NetClassId) {
            this.NetClassId = NetClassId;
        }

        public void setClassTitle(String classTitle) {
            this.classTitle = classTitle;
        }

        public void setCcUid(String ccUid) {
            this.ccUid = ccUid;
        }

        public void setZhibourl(String zhibourl) {
            this.zhibourl = zhibourl;
        }

        public void setZhibotime(String zhibotime) {
            this.zhibotime = zhibotime;
        }

        public void setIs_next_day(String is_next_day) {
            this.is_next_day = is_next_day;
        }

        public void setIscamera(int iscamera) {
            this.iscamera = iscamera;
        }

        public String getNetclass_pdf() {
            return netclass_pdf;
        }

        public String getVideo_status() {
            return video_status;
        }

        public int getIsTrial() {
            return isTrial;
        }

        public String getCcCourses_id() {
            return ccCourses_id;
        }

        public String getCcApi_key() {
            return ccApi_key;
        }

        public String getCamera() {
            return camera;
        }

        public String getLubourl() {
            return lubourl;
        }

        public String getNumber() {
            return number;
        }

        public String getHd() {
            return hd;
        }

        public String getPic() {
            return pic;
        }

        public int getIs_buy() {
            return is_buy;
        }

        public int getVideoType() {
            return videoType;
        }

        public String getTitle() {
            return Title;
        }

        public String getKouling() {
            return kouling;
        }

        public String getNetClassId() {
            return NetClassId;
        }

        public String getClassTitle() {
            return classTitle;
        }

        public String getCcUid() {
            return ccUid;
        }

        public String getZhibourl() {
            return zhibourl;
        }

        public String getZhibotime() {
            return zhibotime;
        }

        public String getIs_next_day() {
            return is_next_day;
        }

        public int getIscamera() {
            return iscamera;
        }
    }
