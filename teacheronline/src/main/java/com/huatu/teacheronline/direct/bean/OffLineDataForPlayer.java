package com.huatu.teacheronline.direct.bean;

import com.huatu.teacheronline.direct.db.PlayerDataBase;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

/**
 * Created by kinndann on 2018/9/5.
 * description:
 */
@Table(database = PlayerDataBase.class)
public class OffLineDataForPlayer extends BaseModel {

    @PrimaryKey(autoincrement = true)
    @Column
    private long ID;
    @Column
    private String uid;
    @Column
    private String rid;
    @Column
    private String json;


    public long getID() {
        return ID;
    }

    public void setID(long ID) {
        this.ID = ID;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getRid() {
        return rid;
    }

    public void setRid(String rid) {
        this.rid = rid;
    }

    public String getJson() {
        return json;
    }

    public void setJson(String json) {
        this.json = json;
    }
}
