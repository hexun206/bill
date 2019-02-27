package com.huatu.teacheronline.vipexercise.vipbean;

import java.io.Serializable;
import java.util.List;

/**
 * vip我的资料列表实体类
 * Created by 18250 on 2017/10/12.
 */
public class VipDataBean implements Serializable {

    /**
     * message : 获取我的资料列表成功！a
     * data : [{"createTime":"2017-10-10 19:53:47","title":"11","createUser":"教师","likeCount":0,"materialId":9,"imagePath":"http://120.24.158.188:8088/group1/M00/00/01/eBievFnctMqAS-E0AAEebkMjE20551_150x150.jpg","isLike":0,"readCount":0,"isRead":0,"createUserId":22},{"createTime":"2017-10-10 19:54:02","title":"彭于晏老师开课了888888811","createUser":"教师","likeCount":0,"materialId":10,"imagePath":"http://120.24.158.188:8088/group1/M00/00/01/eBievFnctNmAPKhdAARJmEZSoZ4734_150x150.jpg","isLike":0,"readCount":0,"isRead":0,"createUserId":22}]
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
         * createTime : 2017-10-10 19:53:47
         * title : 11
         * createUser : 教师
         * likeCount : 0
         * materialId : 9
         * imagePath : http://120.24.158.188:8088/group1/M00/00/01/eBievFnctMqAS-E0AAEebkMjE20551_150x150.jpg
         * isLike : 0
         * readCount : 0
         * isRead : 0
         * createUserId : 22
         */
        private String createTime;
        private String title;
        private String createUser;
        private int likeCount;
        private int materialId;
        private String imagePath;
        private int isLike;
        private int readCount;
        private int isRead;
        private int createUserId;

        public void setCreateTime(String createTime) {
            this.createTime = createTime;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public void setCreateUser(String createUser) {
            this.createUser = createUser;
        }

        public void setLikeCount(int likeCount) {
            this.likeCount = likeCount;
        }

        public void setMaterialId(int materialId) {
            this.materialId = materialId;
        }

        public void setImagePath(String imagePath) {
            this.imagePath = imagePath;
        }

        public void setIsLike(int isLike) {
            this.isLike = isLike;
        }

        public void setReadCount(int readCount) {
            this.readCount = readCount;
        }

        public void setIsRead(int isRead) {
            this.isRead = isRead;
        }

        public void setCreateUserId(int createUserId) {
            this.createUserId = createUserId;
        }

        public String getCreateTime() {
            return createTime;
        }

        public String getTitle() {
            return title;
        }

        public String getCreateUser() {
            return createUser;
        }

        public int getLikeCount() {
            return likeCount;
        }

        public int getMaterialId() {
            return materialId;
        }

        public String getImagePath() {
            return imagePath;
        }

        public int getIsLike() {
            return isLike;
        }

        public int getReadCount() {
            return readCount;
        }

        public int getIsRead() {
            return isRead;
        }

        public int getCreateUserId() {
            return createUserId;
        }
    }
}
