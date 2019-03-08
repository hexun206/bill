package com.huatu.teacheronline.direct;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;

import com.baijiahulian.common.networkv2.HttpException;
import com.baijiayun.constant.VideoDefinition;
import com.baijiayun.download.DownloadListener;
import com.baijiayun.download.DownloadManager;
import com.baijiayun.download.DownloadTask;
import com.gensee.common.ServiceType;
import com.gensee.entity.InitParam;
import com.gensee.utils.StringUtil;
import com.greendao.DaoUtils;
import com.greendao.DirectBean;
import com.huatu.teacheronline.CustomApplication;
import com.huatu.teacheronline.R;
import com.huatu.teacheronline.engine.SendRequest;
import com.huatu.teacheronline.utils.CommonUtils;
import com.huatu.teacheronline.utils.DebugUtil;
import com.huatu.teacheronline.utils.FileUtils;
import com.huatu.teacheronline.utils.GsonUtils;
import com.huatu.teacheronline.utils.StringUtils;
import com.huatu.teacheronline.utils.ToastUtils;
import com.huatu.teacheronline.utils.UserInfo;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;


/**
 * vod视频下载service
 *
 * @author ljyu
 * @date 2016-7-26 09:57:02
 */
public class VodDownLoadService extends Service implements DownloadListener {
    private DirectBean directBean;//正在下载的
    private String userid;//用户id
    private String nickName;//用户昵称
    private static final String TAG = "VodDownLoadService";
    private DaoUtils daoUtils;

    private SimpleBinder simpleBinder = new SimpleBinder();
    private ArrayList<DirectBean> downLoaderList = new ArrayList<DirectBean>();//下载列表，临时缓存
    private String account = "";
    private DownloadManager manager;//百家云下载
    private DownloadTask bjyTask;//百家云task
    private List<VideoDefinition> definitionList = new ArrayList<>(Arrays.asList(VideoDefinition._720P,
            VideoDefinition.SHD, VideoDefinition.HD, VideoDefinition.SD, VideoDefinition._1080P));
//    private PlaybackDownloader mPlaybackDownloader;


    public VodDownLoadService() {
    }

    @Override
    public void onCreate() {
        userid = CommonUtils.getSharedPreferenceItem(null, UserInfo.KEY_SP_USERID, "");
        nickName = CommonUtils.getSharedPreferenceItem(null, UserInfo.KEY_SP_NICKNAME, "");
        account = CommonUtils.getSharedPreferenceItem(null, UserInfo.KEY_SP_ACCOUNT, "");
        daoUtils = DaoUtils.getInstance();


        //回放下载的初始化
//        mPlaybackDownloader = new PlaybackDownloader(this, CustomApplication.BJPlayerView_partnerId,
//                FileUtils.getBjyVideoDiskCacheDir(), 1);
//
//        //初始化下载
//        manager = mPlaybackDownloader.getManager();
//        //设置缓存文件路径
//        manager.setTargetFolder(FileUtils.getBjyVideoDiskCacheDir());
//        //读取磁盘缓存的下载任务
//        manager.loadDownloadInfo(CustomApplication.BJPlayerView_partnerId);
//        PlayerConstants.DEPLOY_TYPE = BJPlayerView.PLAYER_DEPLOY_ONLINE;
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


    /**
     * 在 Local Service 中我们直接继承 Binder 而不是 IBinder,因为 Binder 实现了 IBinder 接口，这样我们可以少做很多工作。
     *
     * @author newcj
     */
    public class SimpleBinder extends Binder {
        /**
         * 获取 Service 实例
         *
         * @return
         */
        public VodDownLoadService getService() {
            return VodDownLoadService.this;
        }


        public DownloadManager getBjydownloader() {
            return manager;
        }

        public DirectBean getDirectBean() {
            return directBean;
        }

        public ArrayList<DirectBean> getDownLoaderList() {
            return downLoaderList;
        }

        public SimpleBinder() {
        }


        /**
         * 下载百家云视频
         *
         * @param directBean
         */
        public void starBjyDownload(DirectBean directBean) {
            DebugUtil.e("Dwlist:" + downLoaderList.toString());
            boolean b = checkIsdownLoaderList(directBean);
            if (b) {
                if (directBean.equals(downLoaderList.get(0))) {
                    if (bjyTask != null) {
                        if (directBean.getDown_status().equals(DownManageActivity.CCDOWNSTATE_PAUSE + "")) {
                            bjyTask.start();
                        } else if (directBean.getDown_status().equals(DownManageActivity.CCDOWNSTATE_STAR + "")) {
                            bjyTask.pause();
                        } else if (directBean.getDown_status().equals(DownManageActivity.CCDOWNSTATE_OTHER + "")) {
                            bjyTask.start();
                        } else {
                            //下载失败重新下载
                            downLoadBjyVideo(directBean);
                        }
                    } else {
                        downLoadBjyVideo(directBean);
                    }
                } else {
                    downLoadBjyVideo(directBean);
                }
            } else {
                directBean.setDown_status("600");
                sendBroadcastByDirectBean(directBean);
                downLoaderList.add(directBean);
                //如果本身是第一个，则下载
                if (downLoaderList.get(0).equals(directBean)) {
                    downLoadBjyVideo(directBean);
                }
            }
        }


        /**
         * 删除下载等待项
         *
         * @param directBean
         */
        public void delete(DirectBean directBean) {
            if (downLoaderList.size() == 0) {
                //不在下载队列里面
                directBean.setDown_status(DownManageActivity.CCDOWNSTATE_WAIT + "");
                sendBroadcastByDirectBean(directBean);
            } else {
                //在下载队列里面
                for (int i = 0; i < downLoaderList.size(); i++) {
                    if (directBean.equals(downLoaderList.get(i))) {
                        downLoaderList.remove(i);
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

    /**
     * 检查是否在下载队列里面
     *
     * @param directBean
     */
    private boolean checkIsdownLoaderList(DirectBean directBean) {
        for (int i = 0; i < downLoaderList.size(); i++) {
            if (directBean.equals(downLoaderList.get(i))) {
                return true;
            } else return false;
        }
        return false;
    }

    private void iniVodDownload(final DirectBean directBean) {
        InitParam initParam = new InitParam();
        initParam.setDomain(SendRequest.url_liveGensee);//域名
        initParam.setLiveId(directBean.getLubourl());//录播id
        initParam.setLoginAccount("");//站点认证帐号
        initParam.setLoginPwd(""); //站点认证密码
        initParam.setVodPwd(directBean.getKouling());//点播口令
        initParam.setNickName(StringUtil.isEmpty(nickName) ? account : nickName);//昵称  用于统计和显示
        initParam.setServiceType(ServiceType.ST_CASTLINE);
        directBean.setUserid(userid);
        directBean.setFootprint(3);
        this.directBean = directBean;
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
    }


    /**
     * 下载下一个
     */
    private void nextDownLoad() {
        if (downLoaderList.size() > 0) {
            DirectBean directBean = downLoaderList.get(0);
            downLoadBjyVideo(directBean);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //退出时把所有数据清空，并且把等待下载的状态改变
        for (int i = 0; i < downLoaderList.size(); i++) {
            DirectBean directBean = downLoaderList.get(i);
            directBean.setDown_status(DownManageActivity.CCDOWNSTATE_WAIT + "");
            daoUtils.insertOrUpdateDirectBean(directBean, 0);
        }
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

    /**
     * 下载bjy视频和bjy回放
     */
    public void downLoadBjyVideo(final DirectBean directBean) {

//        Logger.e("live download:" + GsonUtils.toJson(directBean));
//
//        String localPath = null;
//
//        if (directBean.getVideoType() == 0) {
//            DebugUtil.e(TAG, "当前下载的bjy direct：" + directBean.getBjyvideoid());
//            localPath = FileUtils.getBjyVideoDiskCacheDir() + directBean.getBjyvideoid();
//        } else {
//            DebugUtil.e(TAG, "当前下载的bjy playback：" + directBean.getRoom_id() + directBean.getSession_id());
//            localPath = FileUtils.getBjyVideoDiskCacheDir() + directBean.getRoom_id() + "_" + directBean.getSession_id() + ".mp4";
//        }
//
//        directBean.setLocalPath(localPath);
//        directBean.setUserid(userid);
//        directBean.setFootprint(3);
//        this.directBean = directBean;
//        daoUtils.getDaoSession().runInTx(new Runnable() {
//            @Override
//            public void run() {
//                daoUtils.insertOrUpdateDirectBean(directBean, 0);
//            }
//        });
//
//
//        if (directBean.getVideoType() == 0) {
//            String videoId = directBean.getBjyvideoid().trim();
//            DebugUtil.e("bjy  videoId:" + videoId + " getUid:" + userid + " getApi_token:" + directBean.getBjytoken() + " localPath:" + localPath);
//
//            DebugUtil.e(TAG, videoId + " Long.parseLong(videoId):" + Long.parseLong(videoId) + " directBean.getBjytoken():" + directBean.getBjytoken());
//            manager.newDownloadTask(videoId, Long.parseLong(videoId), directBean.getBjytoken(), definitionList, 0, "haha")
//                    .observeOn(AndroidSchedulers.mainThread())
//                    .subscribe(new Action1<DownloadTask>() {
//                        @Override
//                        public void call(DownloadTask task) {
//                            DebugUtil.e(TAG, "bjy 开始下载:");
//                            VodDownLoadService.this.bjyTask = task;
//                            //直接开始下载
//                            task.start();
//                            task.setDownloadListener(VodDownLoadService.this);
//                        }
//                    }, new Action1<Throwable>() {
//                        @Override
//                        public void call(Throwable throwable) {
//                            throwable.printStackTrace();
//                            ToastUtils.showToast(R.string.server_error);
//                            directBean.setDown_status("500");
//                            directBean.setErrorcode("" + throwable.getMessage());
//                            daoUtils.updateDirectBean(directBean);
//                            sendBroadcastByDirectBean(directBean);
//                        }
//                    });
//
//
//        } else {
//
//            long sessionId = (StringUtils.isEmpty(directBean.getSession_id()) ? 0 : Long.parseLong(directBean.getSession_id()));
//            long roomId = Long.parseLong(directBean.getRoom_id());
//
//            mPlaybackDownloader.downloadRoomPackage(directBean.getRoom_id() + directBean.getSession_id(), roomId, sessionId, directBean.getBjyhftoken(), definitionList, 0, "kinn")
//                    .observeOn(AndroidSchedulers.mainThread())
//                    .subscribe(new Action1<DownloadTask>() {
//                        @Override
//                        public void call(DownloadTask task) {
//                            DebugUtil.e(TAG, "bjy 开始下载:");
//                            VodDownLoadService.this.bjyTask = task;
//                            //直接开始下载
//                            task.start();
//                            task.setDownloadListener(VodDownLoadService.this);
//                        }
//                    }, new Action1<Throwable>() {
//                        @Override
//                        public void call(Throwable throwable) {
//                            throwable.printStackTrace();
//                            ToastUtils.showToast(R.string.server_error);
//                            directBean.setDown_status("500");
//                            directBean.setErrorcode("" + throwable.getMessage());
////                            daoUtils.updateDirectBean(directBean);
//                            daoUtils.insertOrUpdateDirectBean(directBean, 0);
//                            sendBroadcastByDirectBean(directBean);
//
//                        }
//                    });
//
//        }
    }


    @Override
    public void onProgress(DownloadTask downloadTask) {

//        float progress = downloadTask.getProgress();
//        downloadTask.getTotalLength();
//        long videoId = downloadTask.getDownloadInfo().videoId;
//        DebugUtil.e(TAG, "bjy onProgress:" + progress + " directBean.getBjyvideoid:" + directBean.getBjyvideoid() + " DownloadInfo().videoId:" + downloadTask
//                .getVideoDownloadInfo().videoId + " getDownloadInfo().videoId:" + videoId);
//        if (!StringUtils.isEmpty(directBean.getBjyvideoid()) && Long.parseLong(directBean.getBjyvideoid()) == videoId) {
//            directBean.setStart((long) (progress));
//            directBean.setEnd(new Long(100));
//            daoUtils.insertOrUpdateDirectBean(directBean, 1);
//            directBean.setDown_status(DownManageActivity.CCDOWNSTATE_STAR + "");
//            sendBroadcastByDirectBean(directBean);
//        } else if (downloadTask.getDownloadInfo().roomId == Long.parseLong(directBean.getRoom_id())) {
//            if (!StringUtils.isEmpty(directBean.getSession_id()) && Long.parseLong(directBean.getSession_id()) != downloadTask.getDownloadInfo().sessionId) {
//                return;
//            }
//            directBean.setStart((long) (progress));
//            directBean.setEnd(new Long(100));
//            daoUtils.insertOrUpdateDirectBean(directBean, 1);
//            directBean.setDown_status(DownManageActivity.CCDOWNSTATE_STAR + "");
//            sendBroadcastByDirectBean(directBean);
//        }
    }

    @Override
    public void onError(DownloadTask downloadTask, HttpException e) {
        Logger.e("bjyDownload error:" + e.getMessage());

        directBean.setDown_status("500");
        directBean.setErrorcode("" + e.getMessage());
        daoUtils.updateDirectBean(directBean);
        sendBroadcastByDirectBean(directBean);
    }

    @Override
    public void onPaused(DownloadTask downloadTask) {
//        DebugUtil.e(TAG, "bjy onPaused:");
//        long videoId = downloadTask.getDownloadInfo().videoId;
//        if (!StringUtils.isEmpty(directBean.getBjyvideoid()) && Long.parseLong(directBean.getBjyvideoid()) == videoId) {
//            directBean.setDown_status(DownManageActivity.CCDOWNSTATE_PAUSE + "");
////                    DebugUtil.e("cc视频发暂停广播");
//            sendBroadcastByDirectBean(directBean);
//            daoUtils.insertOrUpdateDirectBean(directBean, 0);
//        } else if (downloadTask.getDownloadInfo().roomId == Long.parseLong(directBean.getRoom_id())) {
//            if (!StringUtils.isEmpty(directBean.getSession_id()) && Long.parseLong(directBean.getSession_id()) != downloadTask.getDownloadInfo().sessionId) {
//                return;
//            }
//            directBean.setDown_status(DownManageActivity.CCDOWNSTATE_PAUSE + "");
////                    DebugUtil.e("cc视频发暂停广播");
//            sendBroadcastByDirectBean(directBean);
//            daoUtils.insertOrUpdateDirectBean(directBean, 0);
//        }
    }

    @Override
    public void onStarted(DownloadTask downloadTask) {
//        DebugUtil.e(TAG, "bjy onPaused:");
//        long videoId = downloadTask.getDownloadInfo().videoId;
//        if (!StringUtils.isEmpty(directBean.getBjyvideoid()) && Long.parseLong(directBean.getBjyvideoid()) == videoId) {
//            directBean.setDown_status(DownManageActivity.CCDOWNSTATE_STAR + "");
//            sendBroadcastByDirectBean(directBean);
//            daoUtils.insertOrUpdateDirectBean(directBean, 0);
////                    DebugUtil.e("cc视频发开始下载广播");
//        } else if (downloadTask.getDownloadInfo().roomId == Long.parseLong(directBean.getRoom_id())) {
//            if (!StringUtils.isEmpty(directBean.getSession_id()) && Long.parseLong(directBean.getSession_id()) != downloadTask.getDownloadInfo().sessionId) {
//                return;
//            }
//            directBean.setDown_status(DownManageActivity.CCDOWNSTATE_STAR + "");
//            sendBroadcastByDirectBean(directBean);
//            daoUtils.insertOrUpdateDirectBean(directBean, 0);
//        }
    }

    @Override
    public void onFinish(DownloadTask downloadTask) {
//        String fileName = downloadTask.getFileName();
//        String url = downloadTask.getDownloadInfo().url;
//        String coverUrl = downloadTask.getDownloadInfo().coverUrl;
//        DebugUtil.e(TAG, "bjy onFinish:" + " fileName:" + fileName + " url:" + url + " coverUrl:" + coverUrl);
//
//        long videoId = downloadTask.getDownloadInfo().videoId;
//        if (!StringUtils.isEmpty(directBean.getBjyvideoid()) && Long.parseLong(directBean.getBjyvideoid()) == videoId) {
//            directBean.setDown_status(DownManageActivity.CCDOWNSTATE_COMPLETE + "");
//            DebugUtil.e(TAG, "bjyDOWNSTATE_COMPLETE" + directBean.toString());
//            directBean.setLocalPath(FileUtils.getBjyVideoDiskCacheDir() + fileName);
//            directBean.setStart((long) (100));
//            directBean.setEnd(new Long(100));
//            daoUtils.insertOrUpdateDirectBean(directBean, 0);
//            downLoaderList.remove(directBean);
//            sendBroadcastByDirectBean(directBean);
//            nextDownLoad();
//        } else if (downloadTask.getDownloadInfo().roomId == Long.parseLong(directBean.getRoom_id())) {
//            if (!StringUtils.isEmpty(directBean.getSession_id()) && Long.parseLong(directBean.getSession_id()) != downloadTask.getDownloadInfo().sessionId) {
//                return;
//            }
//
//            String signalFileName = downloadTask.getSignalFileName();
//            String targetName = downloadTask.getVideoDownloadInfo().targetName;
//
//            directBean.setDown_status(DownManageActivity.CCDOWNSTATE_COMPLETE + "");
//
//            directBean.setLocalPath(FileUtils.getBjyVideoDiskCacheDir() + targetName + ";" + FileUtils.getBjyVideoDiskCacheDir() + signalFileName);
//            directBean.setStart((long) (100));
//            directBean.setEnd(new Long(100));
//            daoUtils.insertOrUpdateDirectBean(directBean, 0);
//            downLoaderList.remove(directBean);
//            sendBroadcastByDirectBean(directBean);
//            nextDownLoad();
//        }
    }

    @Override
    public void onDeleted(DownloadTask downloadTask) {
        DebugUtil.e(TAG, "bjy onDeleted:");
    }

}
