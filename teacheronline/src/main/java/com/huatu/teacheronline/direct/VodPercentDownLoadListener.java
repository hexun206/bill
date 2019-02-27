package com.huatu.teacheronline.direct;

/**
 *
 * @author ljyu
 * @time 2016/7/26.
 */
public interface VodPercentDownLoadListener {
    void onDLFinish(String vodId, String localPath);

    void onDLPrepare(String vodId);

    void onDLPosition(String vodId, int position);

    void onDLStart(String vodId);

    void onDLStop(String vodId);

    void onDLError(String vodId, int errorCode);
}
