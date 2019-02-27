package com.huatu.teacheronline.personal.bean;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

import java.io.Serializable;
import java.util.List;

/**
 * Created by 18250 on 2017/8/15.
 */
@JsonObject(fieldDetectionPolicy = JsonObject.FieldDetectionPolicy.NONPRIVATE_FIELDS_AND_ACCESSORS)
public class DeedDetailiBean implements Serializable{

    /**
     * code : 1
     * data : {"content":"里一生一世一双人","pic":["ssssssss"],"feedback":"","feedback_time":null}
     * message : success
     */
    @JsonField
    private String code;
    @JsonField
    private DataBean data;
    @JsonField
    private String message;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @JsonObject(fieldDetectionPolicy = JsonObject.FieldDetectionPolicy.NONPRIVATE_FIELDS_AND_ACCESSORS)
    public static class DataBean implements Serializable{
        /**
         * content : 里一生一世一双人
         * pic : ["ssssssss"]
         * feedback :
         * feedback_time : null
         */
        private String content;
        private String feedback;
        private String feedback_time;

        private List<String> pic;

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getFeedback() {
            return feedback;
        }

        public void setFeedback(String feedback) {
            this.feedback = feedback;
        }

        public String getFeedback_time() {
            return feedback_time;
        }

        public void setFeedback_time(String feedback_time) {
            this.feedback_time = feedback_time;
        }

        public List<String> getPic() {
            return pic;
        }

        public void setPic(List<String> pic) {
            this.pic = pic;
        }
    }

}
