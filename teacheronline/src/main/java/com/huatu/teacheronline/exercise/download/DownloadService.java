package com.huatu.teacheronline.exercise.download;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import com.huatu.teacheronline.CustomApplication;
import com.huatu.teacheronline.utils.DebugUtil;

import java.util.List;

/**
 * Author: ljzyuhenda
 * Date: 14-1-27
 */
public class DownloadService extends Service {

    private static DownLoadManagerForExercise mDownLoadManagerForExercise;

    public static DownLoadManagerForExercise getDownloadManager_exercise() {
        if (!DownloadService.isServiceRunning(CustomApplication.applicationContext)) {
            Intent downloadSvr = new Intent();
            downloadSvr.setAction("huatu.download.service.action");
            downloadSvr.setPackage(CustomApplication.applicationContext.getPackageName());
            CustomApplication.applicationContext.startService(downloadSvr);
        }

        if (DownloadService.mDownLoadManagerForExercise == null) {
            DownloadService.mDownLoadManagerForExercise = new DownLoadManagerForExercise();
        }

        return mDownLoadManagerForExercise;
    }

    public DownloadService() {
        super();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
    }

    @Override
    public void onDestroy() {
        try {
            if (mDownLoadManagerForExercise != null) {
//                mDownLoadManagerStudySchedule.stopAllDownload();
//                mDownLoadManagerStudySchedule.backupDownloadInfoList();
            }

        } catch (Exception e) {
            DebugUtil.e(e.getMessage());
        }
        super.onDestroy();
    }

    public static boolean isServiceRunning(Context context) {
        boolean isRunning = false;

        ActivityManager activityManager =
                (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> serviceList
                = activityManager.getRunningServices(Integer.MAX_VALUE);

        if (serviceList == null || serviceList.size() == 0) {
            return false;
        }

        for (int i = 0; i < serviceList.size(); i++) {
            if (serviceList.get(i).service.getClassName().equals(DownloadService.class.getName())) {
                isRunning = true;
                break;
            }
        }
        return isRunning;
    }
}
