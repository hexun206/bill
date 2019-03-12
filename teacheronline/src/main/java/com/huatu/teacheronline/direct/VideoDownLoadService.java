package com.huatu.teacheronline.direct;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.widget.Toast;

import com.baijiahulian.common.networkv2.HttpException;
import com.baijiayun.constant.VideoDefinition;
import com.baijiayun.download.DownloadListener;
import com.baijiayun.download.DownloadManager;
import com.baijiayun.download.DownloadService;
import com.baijiayun.download.DownloadTask;
import com.greendao.DaoUtils;
import com.greendao.DirectBean;
import com.huatu.teacheronline.CustomApplication;
import com.huatu.teacheronline.R;
import com.huatu.teacheronline.utils.CommonUtils;
import com.huatu.teacheronline.utils.FileUtils;
import com.huatu.teacheronline.utils.StringUtils;
import com.huatu.teacheronline.utils.ToastUtils;
import com.huatu.teacheronline.utils.UserInfo;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import io.reactivex.android.schedulers.AndroidSchedulers;

public class VideoDownLoadService extends Service {
    private String userid;//用户id
    private static final String TAG = "VodDownLoadService";
    private DaoUtils daoUtils;

    private SimpleBinder simpleBinder = new SimpleBinder();
    private DownloadManager manager;//百家云下载
    private DownloadTask bjyTask;//百家云task
    private List<VideoDefinition> definitionList = new ArrayList<>(Arrays.asList(VideoDefinition._720P,
            VideoDefinition.SHD, VideoDefinition.HD, VideoDefinition.SD, VideoDefinition._1080P));


    /**
     * 同时下载的视频最大数量
     */
    private final int DOWNLOAD_MAX = 5;

    /**
     * 正在下载的视频列表
     */
    private CopyOnWriteArrayList<DirectBean> downLoadingList = new CopyOnWriteArrayList();

    private ArrayList<DirectBean> waitingList = new ArrayList<>();


    public VideoDownLoadService() {
    }

    @Override
    public void onCreate() {
        userid = CommonUtils.getSharedPreferenceItem(null, UserInfo.KEY_SP_USERID, "");
        daoUtils = DaoUtils.getInstance();

        manager = DownloadService.getDownloadManager(getApplicationContext());

        //回放下载的初始化
//        mPlaybackDownloader = new PlaybackDownloader(this, CustomApplication.BJPlayerView_partnerId,
//                FileUtils.getBjyVideoDiskCacheDir(), 1);
//
        //初始化下载
        manager = DownloadService.getDownloadManager(getApplicationContext());

        //设置缓存文件路径
        manager.setTargetFolder(FileUtils.getBjyVideoDiskCacheDir());
//        //读取磁盘缓存的下载任务
//        manager.loadDownloadInfo(CustomApplication.BJPlayerView_partnerId);
//        PlayerConstants.DEPLOY_TYPE = BJPlayerView.PLAYER_DEPLOY_ONLINE;
        manager.setPreferredDefinitionList(definitionList);
        //迁移下载记录
        manager.upgrade();
        manager.loadDownloadInfo();
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public SimpleBinder onBind(Intent intent) {
        return simpleBinder;
    }


    public class SimpleBinder extends Binder {
        /**
         * 获取 Service 实例
         *
         * @return
         */
        public VideoDownLoadService getService() {
            return VideoDownLoadService.this;
        }


        public DownloadManager getBjydownloader() {
            return manager;
        }


        public SimpleBinder() {
        }


        public void addDownloadTask(DirectBean directBean) {

            if (downLoadingList.contains(directBean)) {

                DownloadTask task = null;


                if (directBean.getVideoType() == 0) {
                    String videoId = directBean.getBjyvideoid().trim();
                    task = manager.getTaskByVideoId(Long.parseLong(videoId));
                } else {
                    long sessionId = (StringUtils.isEmpty(directBean.getSession_id()) ? 0 : Long.parseLong(directBean.getSession_id()));
                    long roomId = Long.parseLong(directBean.getRoom_id());
                    task = manager.getTaskByRoom(roomId, sessionId);
                }
                if (task != null && directBean.getDown_status() != null && directBean.getDown_status().equals(DownManageActivity.CCDOWNSTATE_STAR + "")) {
                    task.pause();

                }
                return;
            }

            if (downLoadingList.size() >= DOWNLOAD_MAX) {
                if (!waitingList.contains(directBean)) {
                    directBean.setDown_status("600");
                    sendBroadcastByDirectBean(directBean);
                    waitingList.add(directBean);
                }

            } else {
                waitingList.remove(directBean);
                downLoadingList.add(directBean);
                downLoadTask(directBean);
            }


        }

        private void downLoadTask(final DirectBean directBean) {

            if (!CommonUtils.isAvaiableSpace(500)) {
                ToastUtils.showToast(R.string.sd_is_full_info);
                downLoadingList.remove(directBean);
                directBean.setDown_status("500");
                directBean.setErrorcode(getResources().getString(R.string.sd_is_full_info));
                daoUtils.updateDirectBean(directBean);
                sendBroadcastByDirectBean(directBean);
                return;

            }

//            DownloadTask task = null;
            directBean.setUserid(userid);


//
            if (directBean.getVideoType() == 0) {
                String videoId = directBean.getBjyvideoid().trim();
                manager.newVideoDownloadTask(videoId, Long.parseLong(videoId), directBean.getBjytoken(), "haha")
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                downloadTask -> {
                                    //直接开始下载
                                    downloadTask.start();
                                    downloadTask.setDownloadListener(new VideoDownloadListener(directBean));
                                }
                                , throwable -> {
                                    throwable.printStackTrace();
                                    downLoadingList.remove(directBean);
                                    throwable.printStackTrace();
                                    ToastUtils.showToast(R.string.network);
                                    directBean.setDown_status("500");
                                    directBean.setErrorcode("" + throwable.getMessage());
                                    daoUtils.updateDirectBean(directBean);
                                    sendBroadcastByDirectBean(directBean);
                                });


            } else {
                long sessionId = (StringUtils.isEmpty(directBean.getSession_id()) ? 0 : Long.parseLong(directBean.getSession_id()));
                long roomId = Long.parseLong(directBean.getRoom_id());
                manager.newPlaybackDownloadTask(directBean.getRoom_id() + directBean.getSession_id(), roomId, sessionId, directBean.getBjyhftoken(), "kinn")
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                downloadTask -> {
                                    //直接开始下载
                                    downloadTask.start();
                                    downloadTask.setDownloadListener(new VideoDownloadListener(directBean));
                                }
                                , throwable -> {
                                    throwable.printStackTrace();
                                    downLoadingList.remove(directBean);
                                    throwable.printStackTrace();
                                    ToastUtils.showToast(R.string.network);
                                    directBean.setDown_status("500");
                                    directBean.setErrorcode("" + throwable.getMessage());
//                                daoUtils.insertOrUpdateDirectBean(directBean, 0);
                                    sendBroadcastByDirectBean(directBean);
                                });

            }


//            }


        }

        private class VideoDownloadListener implements DownloadListener {
            private DirectBean mDirectBean;
            private long time = 0;

            public VideoDownloadListener(DirectBean directBean) {

                mDirectBean = directBean;

            }

            @Override
            public void onProgress(DownloadTask downloadTask) {
                long nowTime = new Date().getTime();

                mDirectBean.setStart((long) (downloadTask.getProgress()));
                mDirectBean.setEnd(new Long(100));
                mDirectBean.setDown_status(DownManageActivity.CCDOWNSTATE_STAR + "");
                if ((nowTime - time) > 5 * 1000) {
                    time = nowTime;
                    daoUtils.insertOrUpdateDirectBean(mDirectBean, 1);

                    Logger.e("progress:" + downloadTask.getProgress());

                }

                sendBroadcastByDirectBean(mDirectBean);
            }

            @Override
            public void onError(DownloadTask downloadTask, HttpException e) {
                downloadTask.cancel();
                downLoadingList.remove(mDirectBean);
                mDirectBean.setDown_status("500");
                mDirectBean.setErrorcode("" + e.getMessage());
                daoUtils.updateDirectBean(mDirectBean);
                sendBroadcastByDirectBean(mDirectBean);

            }

            @Override
            public void onPaused(DownloadTask downloadTask) {
                mDirectBean.setDown_status(DownManageActivity.CCDOWNSTATE_PAUSE + "");
                sendBroadcastByDirectBean(mDirectBean);
                daoUtils.insertOrUpdateDirectBean(mDirectBean, 0);
                downLoadingList.remove(mDirectBean);
            }

            @Override
            public void onStarted(DownloadTask downloadTask) {
                mDirectBean.setDown_status(DownManageActivity.CCDOWNSTATE_STAR + "");
                sendBroadcastByDirectBean(mDirectBean);
                daoUtils.insertOrUpdateDirectBean(mDirectBean, 0);
            }

            @Override
            public void onFinish(DownloadTask downloadTask) {

                downLoadingList.remove(mDirectBean);

                mDirectBean.setDown_status(DownManageActivity.CCDOWNSTATE_COMPLETE + "");
                mDirectBean.setStart((long) (100));
                mDirectBean.setEnd(new Long(100));

                if (mDirectBean.getVideoType() == 0) {
                    String fileName = downloadTask.getVideoFileName();
                    mDirectBean.setLocalPath(FileUtils.getBjyVideoDiskCacheDir() + fileName);
                } else {
                    String signalFileName = downloadTask.getSignalFileName();
                    String targetName = downloadTask.getVideoDownloadInfo().targetName;
                    mDirectBean.setLocalPath(FileUtils.getBjyVideoDiskCacheDir() + targetName + ";" + FileUtils.getBjyVideoDiskCacheDir() + signalFileName);
                }
                sendBroadcastByDirectBean(mDirectBean);
                daoUtils.insertOrUpdateDirectBean(mDirectBean, 0);


                nextDownLoad();

            }

            @Override
            public void onDeleted(DownloadTask downloadTask) {
                downLoadingList.remove(mDirectBean);
            }


        }

        /**
         * 下载下一个
         */
        private void nextDownLoad() {

            if (waitingList.size() != 0) {
                DirectBean directBean = waitingList.get(0);
                simpleBinder.addDownloadTask(directBean);


            }


        }


        /**
         * 删除下载等待项
         *
         * @param directBean
         */
        public void delete(DirectBean directBean) {
            if (waitingList.size() == 0) {
                //不在下载队列里面
                directBean.setDown_status(DownManageActivity.CCDOWNSTATE_WAIT + "");
                sendBroadcastByDirectBean(directBean);
            } else {
                //在下载队列里面
                for (int i = 0; i < waitingList.size(); i++) {
                    if (directBean.equals(waitingList.get(i))) {
                        waitingList.remove(i);
                        directBean.setDown_status(DownManageActivity.CCDOWNSTATE_WAIT + "");
                        sendBroadcastByDirectBean(directBean);
                    } else {
                        directBean.setDown_status(DownManageActivity.CCDOWNSTATE_WAIT + "");
                        sendBroadcastByDirectBean(directBean);
                    }
                }
            }

        }

    }


    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
    }


    @Override
    public void onDestroy() {

        for (DirectBean directBean : downLoadingList) {

            if ("200".equals(directBean.getDown_status())) {
                DownloadTask task = null;


                if (directBean.getVideoType() == 0) {
                    String videoId = directBean.getBjyvideoid().trim();

                    task = manager.getTaskByVideoId(Long.parseLong(videoId));
                } else {
                    long roomId = Long.parseLong(directBean.getRoom_id());
                    long sessionId = (StringUtils.isEmpty(directBean.getSession_id()) ? 0 : Long.parseLong(directBean.getSession_id()));
                    task = manager.getTaskByRoom(roomId, sessionId);
                }
                if (task != null) {
                    task.pause();

                }


            }


        }


        super.onDestroy();
    }


    /**
     * 更新状态后的directBean发广播给activity
     *
     * @param directBean
     */
    private void sendBroadcastByDirectBean(DirectBean directBean) {
        Intent intent = new Intent();
        intent.putExtra("DirectBean", directBean);
        intent.setAction(DownManageActivity.ACTION_REFRASH);
        sendBroadcast(intent);
    }


}
