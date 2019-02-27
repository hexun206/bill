package com.huatu.teacheronline.direct.bean;

import java.io.Serializable;

/**
 * 直播--好友互动
 * Created by ply on 2015/4/16.
 */
public class DirectUserMssageBean implements Serializable {
    private String userName;
    private long userId;
    private String msgTime;
    private String msg;
    private String rich;//富文本

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getMsgTime() {
        return msgTime;
    }

    public void setMsgTime(String msgTime) {
        this.msgTime = msgTime;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getRich() {
        return rich;
    }

    public void setRich(String rich) {
        this.rich = rich;
    }

    @Override
    public String toString() {
        return "DirectUserMssageBean{" +
                "userName='" + userName + '\'' +
                ", userId=" + userId +
                ", msgTime='" + msgTime + '\'' +
                ", msg='" + msg + '\'' +
                ", rich='" + rich + '\'' +
                '}';
    }
}
