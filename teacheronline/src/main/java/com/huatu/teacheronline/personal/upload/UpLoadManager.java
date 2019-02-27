package com.huatu.teacheronline.personal.upload;

import com.artifex.mupdflib.AsyncTask;
import com.huatu.teacheronline.engine.BJ_CloudApi;
import com.huatu.teacheronline.engine.ObtainDataFromNetListener;
import com.huatu.teacheronline.personal.InterviewVideoUploadEditActivity;
import com.huatu.teacheronline.personal.bean.UploadFromBreakBean;
import com.huatu.teacheronline.utils.GsonUtils;
import com.huatu.teacheronline.utils.StringUtils;
import com.orhanobut.logger.Logger;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by kinndann on 2018/11/6.
 * description:
 */
public class UpLoadManager implements FileProgressRequestBody.ProgressListener {

    private final String TAG = "UpLoadManager :";
    private Builder mBuilder;
    private int mOffset;
    private File mFile;
    private int curBlockSize;
    private long mTotalSize;
    private OkHttpClient mClient;
    private Call call;
    private volatile boolean mIsCancel;
    private volatile String mMsg;
    private int mLastPercent;
    private ExecutorService mSingleThreadExecutor;
    private AsyncTask<Void, Void, Integer> mUpLoadTask;

    private void UpLoadManager() {
    }


    private static class Holder {
        private final static UpLoadManager instance = new UpLoadManager();

    }

    public static UpLoadManager getInstance() {
        return Holder.instance;
    }


    @Override
    public void transferred(long size) {
        int percent = (int) ((mOffset + size) / (double) mTotalSize * 100);
        if ((percent - mLastPercent) > 0.9) {
            mBuilder.listener.onProgress(percent);
            mLastPercent = percent;
        } else if (percent == 100) {
            mBuilder.listener.onProgress(percent);
            mLastPercent = percent;
        }


    }


    public void init(Builder builder) {
        cancel();
        mLastPercent = 0;
        mBuilder = builder;
        mFile = builder.file;
        mTotalSize = mFile.length();
        mSingleThreadExecutor = Executors.newSingleThreadExecutor();
        initOkHttpClient();
        startUpload();

    }


    /**
     * 先获取续传地址再上传
     */
    private void startUpload() {

        BJ_CloudApi.getVideoUploadFromBreakUrl(mBuilder.videoId, new ObtainDataFromNetListener<String, Throwable>() {
            @Override
            public void onSuccess(String res) {

                String message = GsonUtils.getJson(res, "message");
                String code = GsonUtils.getJson(res, "code");
                String data = GsonUtils.getJson(res, "data");
                if ("1".equals(code)) {

                    UploadFromBreakBean uploadFromBreakBean = GsonUtils.parseJSON(data, UploadFromBreakBean.class);
                    mOffset = uploadFromBreakBean.getUpload_size();
                    doUpload(uploadFromBreakBean.getUpload_url());


                } else if (!StringUtils.isEmpty(message)) {
                    mMsg = message;
                    mBuilder.listener.onError(mMsg);
                } else {
                    mMsg = "服务器异常,请稍后尝试!";
                    mBuilder.listener.onError(mMsg);
                }


            }

            @Override
            public void onFailure(Throwable res) {
                mMsg = "网络异常,请检查网络状况!";
                mBuilder.listener.onError(mMsg);
            }
        });


    }


    private void initOkHttpClient() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {

                Request request = chain.request()
                        .newBuilder()
                        .header("Content-Type", "application/octet-stream")
//                        .header("Content-Length", String.valueOf(curBlockSize))
                        .build();


//                Request originalRequest = chain.request();
//                Request compressedRequest = originalRequest.newBuilder()
//                        .header("Content-Type", "application/octet-stream")
//                        .removeHeader("Content-Length")
//                        .addHeader("Content-Length", String.valueOf(curBlockSize))
//                        .build();
//
//
//                Response response = chain.proceed(compressedRequest);
                return chain.proceed(request);
            }
        });
        builder.connectTimeout(30, TimeUnit.SECONDS);
        builder.writeTimeout(30, TimeUnit.SECONDS);
        builder.readTimeout(30, TimeUnit.SECONDS);
        builder.retryOnConnectionFailure(false);
        mClient = builder.build();
    }

    private Request generateRequest(String url) {

        // 获取分块数据，按照每次10M的大小分块上传

        final int CHUNK_SIZE = 2 * 1024 * 1024;

        //切割文件为10M每份

        byte[] blockData = getBlock(mOffset, mFile, CHUNK_SIZE);

        if (blockData == null) {

            throw new RuntimeException(String.format("upload file get blockData faild，filePath:%s , offest:%d", mFile.getAbsoluteFile(), mOffset));

        }

        curBlockSize = blockData.length;

//        bytes 1-500/2000
        String contentRange = String.format("bytes %s-%s/%s", String.valueOf(mOffset + 1), String.valueOf(mOffset + curBlockSize), String.valueOf(mTotalSize));
        Logger.e(TAG + contentRange);

        RequestBody filePart = new ProgressRequestBody(blockData, "application/octet-stream", this);


//        MultipartBody requestBody = new MultipartBody.Builder()
////                .setType(MultipartBody.FORM)
//                .addFormDataPart("application/octet-stream", mFile.getName(), filePart)
//                .build();

        // 创建Request对象

        Request request = new Request.Builder()

                .url(url)
                .header("Content-Range", contentRange)
                .post(filePart)

                .build();

        return request;

    }


    /**
     * 文件分块工具
     *
     * @param offset    起始偏移位置
     * @param file      文件
     * @param blockSize 分块大小
     * @return 分块数据
     */

    private byte[] getBlock(long offset, File file, int blockSize) {

        byte[] result = new byte[blockSize];

        RandomAccessFile accessFile = null;

        try {

            accessFile = new RandomAccessFile(file, "r");

            accessFile.seek(offset);

            int readSize = accessFile.read(result);

            if (readSize == -1) {

                return null;

            } else if (readSize == blockSize) {

                return result;

            } else {

                byte[] tmpByte = new byte[readSize];

                System.arraycopy(result, 0, tmpByte, 0, readSize);

                return tmpByte;

            }

        } catch (IOException e) {

            e.printStackTrace();

        } finally {

            if (accessFile != null) {

                try {

                    accessFile.close();

                } catch (IOException e1) {

                }

            }

        }

        return null;

    }


    private void doUpload(String url) {

        mUpLoadTask = new AsyncTask<Void, Void, Integer>() {

            @Override
            protected Integer doInBackground(Void... voids) {
                try {
                    call = mClient.newCall(generateRequest(url));
                    Response response = call.execute();
                    String result = response.body().string();
                    Logger.e(TAG + "result  " + result);
                    if (response.isSuccessful() && GsonUtils.getIntJson(result, "code") == 1) {
                        return InterviewVideoUploadEditActivity.UPLOAD_STATE_SUCCESS;
                    } else {
                        return InterviewVideoUploadEditActivity.UPLOAD_STATE_ERROR;
                    }
                } catch (Exception e) {
                    Logger.e(TAG + e.getMessage());
                }
                return mIsCancel ? InterviewVideoUploadEditActivity.UPLOAD_STATE_PAUSE : InterviewVideoUploadEditActivity.UPLOAD_STATE_ERROR;


            }

            @Override
            protected void onPostExecute(Integer result) {
                if (result == InterviewVideoUploadEditActivity.UPLOAD_STATE_SUCCESS) {
                    if ((mOffset + curBlockSize) == mTotalSize) {
                        mBuilder.listener.onSuccess();
                    } else {
                        mBuilder.listener.onDataSegmentSuccess();
                        startUpload();
                    }


                } else if (result == InterviewVideoUploadEditActivity.UPLOAD_STATE_PAUSE) {
                    mBuilder.listener.onPause();
                } else {
                    mBuilder.listener.onError(mMsg);
                }


            }
        }.executeOnExecutor(mSingleThreadExecutor);


    }


    public void cancel() {
        if (call != null && !call.isCanceled()) {
            call.cancel();
            mIsCancel = true;
        }


        if (mUpLoadTask != null) {
            mUpLoadTask.cancel(true);

        }


    }


    public static class Builder {

        private String videoId;
        private File file;
        private UploadListener listener;


        public Builder setVideoId(String videoId) {
            this.videoId = videoId;
            return this;
        }

        public Builder setFile(File file) {
            this.file = file;
            return this;
        }


        public Builder setListener(UploadListener listener) {
            this.listener = listener;
            return this;
        }
    }


}
