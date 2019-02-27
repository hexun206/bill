package com.huatu.teacheronline.direct.bean;

import com.huatu.teacheronline.direct.db.PlayerDataBase;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

/**
 * Created by kinndann on 2018/10/19.
 * description:
 */
@Table(database = PlayerDataBase.class)
public class CrashInfo extends BaseModel {
    @PrimaryKey(autoincrement = true)
    @Column
    private long ID;
    @Column
    private String time;
    @Column
    private String content;

    public long getID() {
        return ID;
    }

    public void setID(long ID) {
        this.ID = ID;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
