package com.huatu.teacheronline.direct.bean;

import com.google.gson.annotations.Expose;
import com.huatu.teacheronline.direct.db.PlayerDataBase;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ConflictAction;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.annotation.Unique;
import com.raizlabs.android.dbflow.structure.BaseModel;

/**
 * Created by kinndann on 2018/8/9/009.
 * description:
 */
@Table(database = PlayerDataBase.class)
public class RecodeRequestFailure extends BaseModel {

    @PrimaryKey(autoincrement = true)
    @Column
    private long ID;

    @Column
    @Expose
    String UserId;
    @Column
    @Expose
    String UserName;
    @Column
    @Expose
    String OrderNum;
    @Unique(onUniqueConflict = ConflictAction.REPLACE)
    @Column
    @Expose
    String JoinTime;
    @Column
    @Expose
    String Leavetime;
    @Column
    @Expose
    String Netclassid;
    @Column
    @Expose
    String Lessionid;
    @Column
    @Expose
    String Params;

    public RecodeRequestFailure() {
    }

    public RecodeRequestFailure(String userId, String userName, String orderNum, String joinTime, String leavetime, String netclassid, String lessionid, String params) {
        UserId = userId;
        UserName = userName;
        OrderNum = orderNum;
        JoinTime = joinTime;
        Leavetime = leavetime;
        Netclassid = netclassid;
        Lessionid = lessionid;
        Params = params;
    }

    public long getID() {
        return ID;
    }

    public void setID(long ID) {
        this.ID = ID;
    }

    public String getUserId() {
        return UserId;
    }

    public void setUserId(String userId) {
        UserId = userId;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public String getOrderNum() {
        return OrderNum;
    }

    public void setOrderNum(String orderNum) {
        OrderNum = orderNum;
    }

    public String getJoinTime() {
        return JoinTime;
    }

    public void setJoinTime(String joinTime) {
        JoinTime = joinTime;
    }

    public String getLeavetime() {
        return Leavetime;
    }

    public void setLeavetime(String leavetime) {
        Leavetime = leavetime;
    }

    public String getNetclassid() {
        return Netclassid;
    }

    public void setNetclassid(String netclassid) {
        Netclassid = netclassid;
    }

    public String getLessionid() {
        return Lessionid;
    }

    public void setLessionid(String lessionid) {
        Lessionid = lessionid;
    }

    public String getParams() {
        return Params;
    }

    public void setParams(String params) {
        Params = params;
    }
}
