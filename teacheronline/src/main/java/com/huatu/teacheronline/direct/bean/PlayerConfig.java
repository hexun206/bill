package com.huatu.teacheronline.direct.bean;

import com.huatu.teacheronline.direct.db.PlayerDataBase;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

/**
 * Created by kinndann on 2018/8/29.
 * description:播放相关配置
 */
@Table(database = PlayerDataBase.class)
public class PlayerConfig extends BaseModel {

    @PrimaryKey(autoincrement = true)
    @Column
    private long ID;

    @Column
    private String UserId;

    /**
     * 是否第一次观看视频 0：第一次观看 other：
     */
    @Column
    private Integer firstWatch = 0;


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

    public Integer getFirstWatch() {
        return firstWatch;
    }

    public void setFirstWatch(Integer firstWatch) {
        this.firstWatch = firstWatch;
    }

    public boolean isFirstWatch() {

        return firstWatch != null && firstWatch == 0;

    }
}
