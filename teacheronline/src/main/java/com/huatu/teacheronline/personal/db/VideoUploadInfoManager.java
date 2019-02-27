package com.huatu.teacheronline.personal.db;

/**
 * Created by kinndann on 2018/11/9.
 * description:当前正在处理面试视频数据
 */
public class VideoUploadInfoManager {

    private final String TAG = "VideoUploadInfoManager :";


    private static class Holder {
        private final static VideoUploadInfoManager instance = new VideoUploadInfoManager();

    }

    public static VideoUploadInfoManager getInstance() {
        return Holder.instance;
    }

    private VideoUploadInfo mInfo;

    public void destory() {
        mInfo = null;

    }

    /**
     * 提交后清除数据库该条数据
     */
    public void clear() {
        VideoUploadInfoDAO.getInstance().delete(mInfo.getUserid(), mInfo.getNetclassid());
        destory();
    }


    public void setInfo(VideoUploadInfo info) {

        mInfo = info;

        VideoUploadInfoDAO.getInstance().save(mInfo);

    }


    public VideoUploadInfo getInfo(String uid, String netClassid) {
        if (mInfo == null) {
            mInfo = VideoUploadInfoDAO.getInstance().query(uid, netClassid);
        }

        if (mInfo == null) {
            mInfo = new VideoUploadInfo();
            mInfo.setUserid(uid);
            mInfo.setNetclassid(netClassid);

        }

        return mInfo;

    }


}
