package com.huatu.teacheronline.engine;

/**
 * Created by ljzyuhenda on 15/7/6.
 */
public abstract class ObtainDataFromNetListener<T, F> {
    public void onProcess(long bytesWritten, long totalSize) {
    }

    public void onStart() {
    }

    public abstract void onSuccess(T res);

    public abstract void onFailure(F res);
}
