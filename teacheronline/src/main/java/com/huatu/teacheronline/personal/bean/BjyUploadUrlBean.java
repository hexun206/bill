package com.huatu.teacheronline.personal.bean;

/**
 * Created by kinndann on 2018/11/5.
 * description:百家云的上传地址及id
 */
public class BjyUploadUrlBean {


    /**
     * video_id : 13831425
     * upload_url : http://upload-video.baijiayun.com/upload?fid=13831425&ts=1540891973682&token=7428be47d8208fbd52c53046702dd41a
     */

    private int video_id;
    private String upload_url;

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


}
