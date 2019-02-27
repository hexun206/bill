package com.huatu.teacheronline.personal.bean;

/**
 * Created by kinndann on 2018/11/6.
 * description:断点续传相关数据
 */
public class UploadFromBreakBean {


    /**
     * video_id : 13989700
     * upload_url : http://upload-video.baijiayun.com/upload?fid=13989700&ts=1541465137536&token=633d58fd747a6de01aef6e010fb40de0
     * upload_size : 0
     */

    private int video_id;
    private String upload_url;
    private int upload_size;

    public int getVideo_id() {
        return video_id;
    }

    public void setVideo_id(int video_id) {
        this.video_id = video_id;
    }

    public String getUpload_url() {
        return upload_url;
    }

    public void setUpload_url(String upload_url) {
        this.upload_url = upload_url;
    }

    public int getUpload_size() {
        return upload_size;
    }

    public void setUpload_size(int upload_size) {
        this.upload_size = upload_size;
    }
}
