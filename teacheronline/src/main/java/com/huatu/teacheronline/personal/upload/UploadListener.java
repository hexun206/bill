package com.huatu.teacheronline.personal.upload;

/**
 * Created by kinndann on 2018/11/6.
 * description:
 */
public interface UploadListener {


    void onPause();

    void onError(String msg);

    void onProgress(int percent);

    void onDataSegmentSuccess();

    void onSuccess();


}
