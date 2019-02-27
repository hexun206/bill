package com.huatu.teacheronline.personal.bean;

import java.io.Serializable;

/**
 * 我的消息实体类
 * Created by ljyu on 2016/8/15.
 */
public class MessageBean implements Serializable{

    /**
     * id : 34
     * title : 5555555555555555
     * update_time : 1471188762
     * stype : 0
     * user_type : 0
     * status : 0
     * now_time : 1471242711
     */

    private String id;
    private String rid;
    private String title;
    private Long update_time;
    private String stype;
    private String user_type;
    private String status;
    private String article_url;
    private Long now_time;
    private String contentWithoutHtml;

    public String getcontentWithoutHtml() {
        return contentWithoutHtml;
    }

    public void setcontentWithoutHtml(String contentWithoutHtml) {
        this.contentWithoutHtml = contentWithoutHtml;
    }

    public String getRid() {
        return rid;
    }

    public void setRid(String rid) {
        this.rid = rid;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Long getUpdate_time() {
        return update_time;
    }

    public void setUpdate_time(Long update_time) {
        this.update_time = update_time;
    }

    public String getStype() {
        return stype;
    }

    public void setStype(String stype) {
        this.stype = stype;
    }

    public String getUser_type() {
        return user_type;
    }

    public void setUser_type(String user_type) {
        this.user_type = user_type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getNow_time() {
        return now_time;
    }

    public void setNow_time(Long now_time) {
        this.now_time = now_time;
    }

    public String getArticle_url() {
        return article_url;
    }

    public void setArticle_url(String article_url) {
        this.article_url = article_url;
    }

    @Override
    public String toString() {
        return "MessageBean{" +
                "id='" + id + '\'' +
                ", rid='" + rid + '\'' +
                ", title='" + title + '\'' +
                ", update_time=" + update_time +
                ", stype='" + stype + '\'' +
                ", user_type='" + user_type + '\'' +
                ", status='" + status + '\'' +
                ", article_url='" + article_url + '\'' +
                ", now_time=" + now_time +
                '}';
    }
}
