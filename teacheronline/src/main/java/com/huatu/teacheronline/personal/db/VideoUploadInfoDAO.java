package com.huatu.teacheronline.personal.db;

import com.raizlabs.android.dbflow.sql.language.SQLite;

/**
 * Created by kinndann on 2018/10/31.
 * description:
 */
public class VideoUploadInfoDAO {
    private final String TAG = "VideoUploadInfoDAO :";


    private static class Holder {
        private final static VideoUploadInfoDAO instance = new VideoUploadInfoDAO();

    }

    public static VideoUploadInfoDAO getInstance() {
        return Holder.instance;
    }


    void save(VideoUploadInfo info) {
        delete(info.getUserid(), info.getNetclassid());
        info.save();


    }


    VideoUploadInfo query(String uid, String netClassid) {
        return SQLite.select().from(VideoUploadInfo.class)
                .where(VideoUploadInfo_Table.userid.eq(uid))
                .and(VideoUploadInfo_Table.netclassid.eq(netClassid))
                .querySingle();
    }


    public void delete(String uid, String netClassid) {
        VideoUploadInfo query = query(uid, netClassid);
        if (query != null) {
            query.delete();

        }
    }


}
