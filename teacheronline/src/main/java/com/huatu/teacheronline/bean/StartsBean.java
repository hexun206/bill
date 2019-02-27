package com.huatu.teacheronline.bean;

import java.io.Serializable;

/**
 * Created by admin on 2017/4/8. 启动页广告
 */
public class StartsBean  implements Serializable{
    /**
     * type : 1
     * resource :
     * popup_ads : {"url":"http://5beike.hteacher.net/teacherTest/attachments/2017/03/1490767852c593eb1bfc4c37c6.png","type":"1","resource":"https://www.baidu.com/","title":"测试标题"}
     * code : 1
     * data : http://5beike.hteacher.net/teacherTest/attachments/2017/03/14897203220b92263373892415_4.png
     * message : success
     */

    private String type;
    private String resource;
    private PopupAdsBean popup_ads;
    private String code;
    private String data;
    private String message;

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

    public PopupAdsBean getPopup_ads() {
        return popup_ads;
    }

    public void setPopup_ads(PopupAdsBean popup_ads) {
        this.popup_ads = popup_ads;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
