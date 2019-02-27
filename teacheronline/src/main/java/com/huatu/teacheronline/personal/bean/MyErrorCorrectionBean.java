package com.huatu.teacheronline.personal.bean;

import java.io.Serializable;

/**
 * 我的纠错
 * Created by ply on 2016/3/22.
 */
public class MyErrorCorrectionBean implements Serializable {
    public String qcontent;//题干
    public String qstatus;//纠错状态
    public String createTime;//创建时间
    public int money;//奖励金币数
    public String opinion;//未通过原因
    public String updatetime;//审核中时间
    public String audittime;//审核完成时间
    public String module;//所属模块
    public String userSuggest;//错误内容标题
    public String qid;
    private transient boolean isCheck;

    public String getqid() {
        return qid;
    }
    public boolean getisCheck() {
        return isCheck;
    }

    public void setisCheck(boolean isCheck) {
        this.isCheck = isCheck;
    }

}
