package com.huatu.teacheronline.exercise.download;

import android.os.Environment;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.huatu.teacheronline.R;
import com.huatu.teacheronline.engine.AsyncHttpClientHelper;
import com.huatu.teacheronline.engine.SendRequest;
import com.huatu.teacheronline.exercise.SendRequestUtilsForExercise;
import com.huatu.teacheronline.utils.CommonUtils;
import com.huatu.teacheronline.utils.DebugUtil;
import com.huatu.teacheronline.utils.MD5;
import com.huatu.teacheronline.utils.StringUtils;
import com.huatu.teacheronline.utils.ToastUtils;
import com.huatu.teacheronline.utils.UserInfo;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by only on 2015/10/27.
 */
public class DownLoadManagerForExercise {
    private String target;
    public Map<String, DownLoadInfo> downloadInfoList;
    private int updateType; // 0完整包 1增量包

    public DownLoadManagerForExercise() {
        this.target = Environment.getExternalStorageDirectory() + File.separator + "Exercise";

        if (downloadInfoList == null) {
            downloadInfoList = new HashMap<>();
        }
    }

    /**
     * 开启下载
     *
     * @param key        取下载信息对应key, 模块题海或者今日特训是 getKeyForExercisePackageDownload;真题测评是 试卷id
     * @param type       0 模块题海或者今日特训; 1 真题测评
     * @param updateType 0 完整包 1 增量包
     */
    public void startDownLoadTask(final String key, int type, int updateType) {
        this.updateType = updateType;
        startDownLoadTask(key, type, null);
    }

    /**
     * 开启下载
     *
     * @param key      取下载信息对应key, 模块题海或者今日特训是 getKeyForExercisePackageDownload;真题测评是 试卷id
     * @param type     0 模块题海或者今日特训; 2 真题演练 5 在线模考
     * @param listener 只针对type 1 真题测评
     */
    public void startDownLoadTask(final String key, final int type, final DownLoadFilesListener listener) {
        DownLoadFilesListener listener_downLoadInfo;
        if (type == 0) {
            listener_downLoadInfo = downloadInfoList.get(key).listener;
        } else {
            listener_downLoadInfo = listener;
        }

//        long sdAvailableSize = CommonUtils.getSDAvailableSize();
//        long sdTotalSize = CommonUtils.getSDTotalSize();

//        DebugUtil.e("剩余空间："+sdAvailableSize+"   总空间："+sdTotalSize);
        if (!CommonUtils.isExitsSdcard()) {
//            Toast.makeText(context, "SD卡不可用,请检查后再次尝试", Toast.LENGTH_SHORT).show();
            ToastUtils.showToast(R.string.sdnotexit_info);

            if (listener_downLoadInfo != null) {
                listener_downLoadInfo.onFailure(DownLoadCallBackForExercise.DownLoadExerciseStatus.NeverStart);
            }
            return;
        }
        boolean avaiableSpace = CommonUtils.isAvaiableSpace(60);
//        DebugUtil.e("剩余空间：" + sdAvailableSize + "   总空间：" + sdTotalSize);
//        DebugUtil.e("是否大于60M"+ avaiableSpace);
        if (!avaiableSpace) {
            ToastUtils.showToast(R.string.sd_is_full_info);
            return;
        }
        if (!CommonUtils.isNetWorkAvilable()) {
            ToastUtils.showToast(R.string.network);
            if (listener_downLoadInfo != null) {
                listener_downLoadInfo.onFailure(DownLoadCallBackForExercise.DownLoadExerciseStatus.NeverStart);
            }
            return;
        }
        String requestUrl;
        if (type == 5) {
            //模考大赛
            requestUrl = SendRequest.url_getExercMoniTPaperId;
        } else if (type == 2) {
            //真题估分
            requestUrl = SendRequest.url_getExercisePackageById;
        } else {
            //type==0 模块题海或者今日特训
            requestUrl = SendRequest.url_getExercisePackage2;
        }
        AsyncHttpClient asyncHttpClient = AsyncHttpClientHelper.createInstance();
        File file = new File(getFilePath(key));
        SendRequestUtilsForExercise sendRequestUtilsForExercise = SendRequestUtilsForExercise.getInstance();
        sendRequestUtilsForExercise.assignDatas();
        JsonArray jsonArray = new JsonArray();
        jsonArray.add(sendRequestUtilsForExercise.subject);
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("userId", sendRequestUtilsForExercise.userId);
        jsonObject.addProperty("deviceId", sendRequestUtilsForExercise.deviceId);
        jsonObject.addProperty("examType", sendRequestUtilsForExercise.examType);
        jsonObject.addProperty("stage", sendRequestUtilsForExercise.stage);
        jsonObject.addProperty("area", sendRequestUtilsForExercise.area);
        jsonObject.add("examSubject", jsonArray);

        RequestParams params = new RequestParams();
        params.put("userInfo", jsonObject);
        if (type == 0) {
            requestUrl = requestUrl + "&type=" + updateType;
        }
        //TODO 获取version
        params.put("versions", sendRequestUtilsForExercise.versions);
        if (type == 2 || type == 5) {
            //获取真题
            params.put("id", key);
        }
        AsyncHttpResponseHandler httpResponseHandler = new AsyncHttpResponseHandler() {
            @Override
            public void onProgress(long bytesWritten, long totalSize) {
                DownLoadInfo downloadInfo = downloadInfoList.get(key);
                downloadInfo.fileLength = totalSize;
                downloadInfo.progress = bytesWritten;
                downloadInfo.status = DownLoadCallBackForExercise.DownLoadExerciseStatus.DownLoading;

                DownLoadFilesListener downloadListener;
                if (type == 0) {
                    downloadListener = downloadInfo.listener;
                } else {
                    downloadListener = listener;
                }

                if (downloadListener != null) {
                    downloadListener.onProcess(bytesWritten, totalSize, downloadInfo.status);
                }
            }

            @Override
            public void onStart() {
                DownLoadInfo downloadInfo = downloadInfoList.get(key);
                downloadInfo.status = DownLoadCallBackForExercise.DownLoadExerciseStatus.DownLoading;

                DownLoadFilesListener downloadListener;
                if (type == 0) {
                    downloadListener = downloadInfo.listener;
                } else {
                    downloadListener = listener;
                }

                if (downloadListener != null) {
                    downloadListener.onStart(downloadInfo.status);
                }
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                DownLoadInfo downloadInfo = downloadInfoList.get(key);
                downloadInfo.status = DownLoadCallBackForExercise.DownLoadExerciseStatus.Success;
                downloadInfo.isUpdate = false;
                DownLoadFilesListener downloadListener;
                if (type == 0) {
                    downloadListener = downloadInfo.listener;
                } else {
                    downloadListener = listener;
                }

                if (downloadListener != null) {
                    downloadListener.onSuccess(responseBody, key, downloadInfo.status);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                DownLoadInfo downloadInfo = downloadInfoList.get(key);
                downloadInfo.status = DownLoadCallBackForExercise.DownLoadExerciseStatus.NeverStart;
                DownLoadFilesListener downloadListener;
                if (type == 0) {
                    downloadListener = downloadInfo.listener;
                } else {
                    downloadListener = listener;
                }

                if (downloadListener != null) {
                    downloadListener.onFailure(downloadInfo.status);
                }
            }

            @Override
            public void onCancel() {
            }
        };
        DebugUtil.i("SendRequest:post" + " requestUrl:" + requestUrl + "&" + params.toString());
        httpResponseHandler.setUsePoolThread(true);
        asyncHttpClient.post(requestUrl, params, httpResponseHandler);
    }

    public interface DownLoadFilesListener {
        void onSuccess(byte[] responseBody, String key, DownLoadCallBackForExercise.DownLoadExerciseStatus status);

        void onStart(DownLoadCallBackForExercise.DownLoadExerciseStatus status);

        void onFailure(DownLoadCallBackForExercise.DownLoadExerciseStatus status);

        void onProcess(long bytesWritten, long totalSize, DownLoadCallBackForExercise.DownLoadExerciseStatus status);
    }

    public DownLoadInfo getDownLoadInfo(String key) {
        return downloadInfoList.get(key);
    }

    public String getFilePath(String key) {
        return target + File.separator + MD5.digest(key);
    }

}
