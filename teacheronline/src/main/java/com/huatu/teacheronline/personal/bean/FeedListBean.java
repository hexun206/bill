package com.huatu.teacheronline.personal.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by 18250 on 2017/8/15.
 */
public class FeedListBean implements Serializable{

    /**
     * message : success
     * data : [{"id":"427","is_feedback":0,"create_time":"2017-08-08 13:14"},{"id":"428","is_feedback":0,"create_time":"2017-08-08 13:17"},{"id":"429","is_feedback":0,"create_time":"2017-08-08 14:00"},{"id":"430","is_feedback":1,"create_time":"2017-08-08 15:37"}]
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
         * id : 427
         * is_feedback : 0
         * create_time : 2017-08-08 13:14
         */
        private String feedback_id;
        private String is_feedback;
        private String create_time;

        public void setfeedback_id(String feedback_id) {
            this.feedback_id = feedback_id;
        }

        public void setIs_feedback(String is_feedback) {
            this.is_feedback = is_feedback;
        }

        public void setCreate_time(String create_time) {
            this.create_time = create_time;
        }

        public String getfeedback_id() {
            return feedback_id;
        }

        public String getIs_feedback() {
            return is_feedback;
        }

        public String getCreate_time() {
            return create_time;
        }
    }
}
