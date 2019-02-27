package com.huatu.teacheronline.personal.db;

import com.huatu.teacheronline.direct.db.PlayerDataBase;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import java.io.Serializable;

/**
 * Created by kinndann on 2018/8/29.
 * description:用户提交面试视频前的记录
 */
@Table(database = PlayerDataBase.class)
public class VideoUploadInfo extends BaseModel implements Serializable {

    /**
     * 上传百家云的最大次数
     */
    public static final int UPLOAD_MAX_COUNT = 3;

    @PrimaryKey(autoincrement = true)
    @Column
    private long ID;

    @Column

    private String userid;
    @Column

    private String netclassid;

    @Column

    private int bjyvideoid;

    /**
     * 已提交的次数
     */
    @Column
    private int uploadCount;

    /**
     * 上传的进度 不定准确  部分为上传失败分片的进度
     */
    @Column
    private int uploadProgress;

    /**
     * 上传的视频地址
     */
    @Column
    private String localPath;

    /**
     * 视频上传百家云的状态
     * 0:未上传 1:正在上传 2:暂停上传 3:上传失败  4:上传成功
     */

    @Column
    private int uploadState;

    /**
     * 是否供其他学员观看
     */
    @Column
    private boolean agree;


    public long getID() {
        return ID;
    }

    public void setID(long ID) {
        this.ID = ID;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getNetclassid() {
        return netclassid;
    }

    public void setNetclassid(String netclassid) {
        this.netclassid = netclassid;
    }

    public int getBjyvideoid() {
        return bjyvideoid;
    }

    public void setBjyvideoid(int bjyvideoid) {
        this.bjyvideoid = bjyvideoid;
    }

    public int getUploadCount() {
        return uploadCount;
    }

    public void setUploadCount(int uploadCount) {
        this.uploadCount = uploadCount;
    }

    public int getUploadProgress() {
        return uploadProgress;
    }

    public void setUploadProgress(int uploadProgress) {
        this.uploadProgress = uploadProgress;
    }

    public String getLocalPath() {
        return localPath;
    }

    public void setLocalPath(String localPath) {
        this.localPath = localPath;
    }

    public int getUploadState() {
        return uploadState;
    }

    public void setUploadState(int uploadState) {
        this.uploadState = uploadState;
    }

    public boolean isAgree() {
        return agree;
    }

    public void setAgree(boolean agree) {
        this.agree = agree;
    }
}
