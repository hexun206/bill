package com.huatu.teacheronline.personal.bean;

import java.io.Serializable;

/**
 * Created by admin on 2017/4/7. 获取课程通知信息实体类
 */

public class SnoticeBean implements Serializable {
    /**
     * content : 内容 1
     * title : 标题
     * checkedCount : 0
     * pushTime : 2017-04-21 10:17:05
     * type : 开课通知
     * checked : 0
     */
    private String content;
    private String title;
    private int checkedCount;
    private String pushTime;
    private String type;
    private int checked;
    private int noticeId;
    private int messageId;


    public void setmessageId(int messageId) {
        this.messageId = messageId;
    }
    public int getmessageId() {
        return messageId;
    }

    public void setnoticeId(int noticeId) {
        this.noticeId = noticeId;
    }
    public int getnoticeId() {
        return noticeId;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setCheckedCount(int checkedCount) {
        this.checkedCount = checkedCount;
    }

    public void setPushTime(String pushTime) {
        this.pushTime = pushTime;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setChecked(int checked) {
        this.checked = checked;
    }


    public String getContent() {
        return content;
    }

    public String getTitle() {
        return title;
    }

    public int getCheckedCount() {
        return checkedCount;
    }

    public String getPushTime() {
        return pushTime;
    }

    public String getType() {
        return type;
    }

    public int getChecked() {
        return checked;
    }
}
