package com.huatu.teacheronline.personal.bean;

import java.io.Serializable;
import java.util.List;

/**
 * 直播课-信息详情-实体类
 * Created by 18250 on 2017/10/10.
 */
public class InformaBean implements Serializable {

    /**
     * message : success
     * data : {"content":[{"TeacherName":"姜丽华","zhiboendtime":"2017-10-09 11:00:00.000","zhibotime":"2017-10-09 11:15:00.000","Courseware_title":"密卷解析\u2014密卷二"}],"reminder":"温馨提示","rid":"48967","transfer_class":"2018年1月3日","customer_service":"010-82982221      工作日 8:30-21:00 节假日 8:30-18:00"}
     * code : 1
     */
    private String message;
    private DataEntity data;
    private String code;

    public void setMessage(String message) {
        this.message = message;
    }

    public void setData(DataEntity data) {
        this.data = data;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public DataEntity getData() {
        return data;
    }

    public String getCode() {
        return code;
    }

    public class DataEntity implements Serializable{
        /**
         * content : [{"TeacherName":"姜丽华","zhiboendtime":"2017-10-09 11:00:00.000","zhibotime":"2017-10-09 11:15:00.000","Courseware_title":"密卷解析\u2014密卷二"}]
         * reminder : 温馨提示
         * rid : 48967
         * transfer_class : 2018年1月3日
         * customer_service : 010-82982221      工作日 8:30-21:00 节假日 8:30-18:00
         */
        private List<ContentEntity> content;
        private String reminder;
        private String rid;
        private String transfer_class;
        private String customer_service;

        public void setContent(List<ContentEntity> content) {
            this.content = content;
        }

        public void setReminder(String reminder) {
            this.reminder = reminder;
        }

        public void setRid(String rid) {
            this.rid = rid;
        }

        public void setTransfer_class(String transfer_class) {
            this.transfer_class = transfer_class;
        }

        public void setCustomer_service(String customer_service) {
            this.customer_service = customer_service;
        }

        public List<ContentEntity> getContent() {
            return content;
        }

        public String getReminder() {
            return reminder;
        }

        public String getRid() {
            return rid;
        }

        public String getTransfer_class() {
            return transfer_class;
        }

        public String getCustomer_service() {
            return customer_service;
        }

        public class ContentEntity implements Serializable{
            /**
             * TeacherName : 姜丽华
             * zhiboendtime : 2017-10-09 11:00:00.000
             * zhibotime : 2017-10-09 11:15:00.000
             * Courseware_title : 密卷解析—密卷二
             */
            private String TeacherName;
            private String zhibotime;
            private String Courseware_title;

            public void setTeacherName(String TeacherName) {
                this.TeacherName = TeacherName;
            }

            public void setZhibotime(String zhibotime) {
                this.zhibotime = zhibotime;
            }

            public void setCourseware_title(String Courseware_title) {
                this.Courseware_title = Courseware_title;
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
}
