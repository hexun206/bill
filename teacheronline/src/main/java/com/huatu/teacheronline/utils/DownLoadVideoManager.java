package com.huatu.teacheronline.utils;


import com.bokecc.sdk.mobile.download.Downloader;
import com.greendao.DirectBean;
import com.huatu.teacheronline.direct.VodPercentDownLoadListener;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Vector;

/**
 * 下载管理类
 * Created by ljyu on 2016/7/22.
 */
public class DownLoadVideoManager {

    private static DownLoadVideoManager instance;
    private HashMap<String, Downloader> ccDownloader = new HashMap<String, Downloader>();
//    private HashMap<String, VodPercentDownLoadListener> vodDownloader = new HashMap<String, VodPercentDownLoadListener>();
    public Vector<VodPercentDownLoadListener> vodDownloader = new Vector<VodPercentDownLoadListener>();

    private DownLoadVideoManager() {
    }

    public static DownLoadVideoManager getInstance() {
        if (instance == null) {
            synchronized (DownLoadVideoManager.class) {
                if (instance == null) {
                    instance = new DownLoadVideoManager();
                }
            }
        }
        return instance;
    }

    public void putCCDownloader(String vedioId, Downloader downloader) {
        ccDownloader.put(vedioId, downloader);
    }

    public Downloader getCCDownloader(DirectBean interviewVideoBean) {
        DebugUtil.e(" getCCDownloader:" + hasCCDownloader(interviewVideoBean.getCcCourses_id()));
        if (hasCCDownloader(interviewVideoBean.getCcCourses_id())) {
            return ccDownloader.get(interviewVideoBean.getCcCourses_id());
        } else {
            String localPath = FileUtils.getVideoDiskCacheDir() + interviewVideoBean.getCcCourses_id().trim() + ".MP4";
            File file = new File(localPath);
            if (!file.exists()) {
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            Downloader downloader = new Downloader(file, interviewVideoBean.getCcCourses_id(), interviewVideoBean.getCcUid().trim(), interviewVideoBean
                    .getCcApi_key().trim());
            putCCDownloader(interviewVideoBean.getCcCourses_id(), downloader);
            return downloader;
        }
    }

    public void putVodDownloader(String vedioId, VodPercentDownLoadListener downloader) {
        vodDownloader.add(downloader);
    }

//    public VodPercentDownLoadListener getVodDownloader(String vedioId,Context context,VodPercentDownLoadListener OnDownloadListener) {
//        DebugUtil.e(" getVodDownloader:" + hasVodDownloader(vedioId));
//        if (hasVodDownloader(vedioId)){
//            return vodDownloader.get(vedioId);
//        }else{
//            downloader = OnDownloadListener;
//            return downloader;
//        }
//    }

    public Boolean hasCCDownloader(String vedioId) {
        if (ccDownloader.containsKey(vedioId)) {
            if (ccDownloader.get(vedioId) != null) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

//    private Boolean hasVodDownloader(String vedioId) {
//        if (vodDownloader.containsKey(vedioId)) {
//            if (vodDownloader.get(vedioId) != null) {
//                return true;
//            } else {
//                return false;
//            }
//        } else {
//            return false;
//        }
//    }


}
