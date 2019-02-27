package com.huatu.teacheronline.direct.bean;

import java.io.Serializable;

/**
 * 记录时间返回的bean
 * Created by ljyu on 2018/3/14.
 */
public class WithdrawalBean implements Serializable{

    /**
     * msg : success
     * fstatus : 0
     * progress : 6
     * lessionid : 775919
     */

    private String msg;
    private int fstatus;
    private int progress;
    private String lessionid;

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getFstatus() {
        return fstatus;
    }

    public void setFstatus(int fstatus) {
        this.fstatus = fstatus;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public String getLessionid() {
        return lessionid;
    }

    public void setLessionid(String lessionid) {
        this.lessionid = lessionid;
    }

    @Override
    public String toString() {
        return "WithdrawalBean{" +
                "msg='" + msg + '\'' +
                ", fstatus=" + fstatus +
                ", progress=" + progress +
                ", lessionid='" + lessionid + '\'' +
                '}';
    }
}
