package com.huatu.teacheronline.bean;

import com.bluelinelabs.logansquare.annotation.JsonObject;

import java.io.Serializable;

/**
 * Created by 18250 on 2017/4/10.
 */
@JsonObject(fieldDetectionPolicy = JsonObject.FieldDetectionPolicy.NONPRIVATE_FIELDS_AND_ACCESSORS)

public class PopupAdsBean implements Serializable {
    /**
     * url : http://5beike.hteacher.net/teacherTest/attachments/2017/03/1490767852c593eb1bfc4c37c6.png
     * type : 1
     * resource : https://www.baidu.com/
     * title : 测试标题
     */

    private String url;
    private String type;
    private String resource;
    private String title;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
