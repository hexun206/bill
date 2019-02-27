package com.huatu.teacheronline.direct.bean;

import com.huatu.teacheronline.direct.db.PlayerDataBase;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

/**
 * Created by kinndann on 2018/8/27.
 * description:观看记录 自动续看用
 */

@Table(database = PlayerDataBase.class)
public class WatchRecord extends BaseModel {


    @PrimaryKey(autoincrement = true)
    @Column
    private long ID;

    @Column
    String UserId;

    @Column
    String rid;

    @Column
    String watchTime;


    @Column
    Integer position;

    @Column
    String courseWareId;

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

    public String getWatchTime() {
        return watchTime;
    }

    public void setWatchTime(String watchTime) {
        this.watchTime = watchTime;
    }

    public String getRid() {
        return rid;
    }

    public void setRid(String rid) {
        this.rid = rid;
    }

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }

    public String getCourseWareId() {
        return courseWareId;
    }

    public void setCourseWareId(String courseWareId) {
        this.courseWareId = courseWareId;
    }
}
