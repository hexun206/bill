package com.huatu.teacheronline.direct.manager;

import android.os.Environment;

import com.huatu.teacheronline.direct.bean.PdfBean;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.FileCallback;
import com.lzy.okgo.model.Progress;
import com.lzy.okgo.model.Response;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by kinndann on 2018/9/4.
 * description:
 */
public class PdfDownloadManager {

    private final String TAG = "PdfDownloadManager :";

    private void PdfDownloadManager() {
    }

    private static class Holder {
        private final static PdfDownloadManager instance = new PdfDownloadManager();

    }

    public static PdfDownloadManager getInstance() {
        return Holder.instance;
    }


    private final int DOWNLOAD_MAX = 5;

    private CopyOnWriteArrayList<PdfBean> downLoadingList = new CopyOnWriteArrayList();

    private ArrayList<PdfBean> waitingList = new ArrayList<>();

    public void addDownloadTask(PdfBean info) {
        if (downLoadingList.size() >= DOWNLOAD_MAX) {
            if (!waitingList.contains(info)) {
                waitingList.add(info);
                info.setState(2);
                EventBus.getDefault().post(info);
            }

        } else {
            waitingList.remove(info);
            downLoadingList.add(info);
            downLoadTask(info);
        }


    }

    private void downLoadTask(final PdfBean info) {
        String url = info.getFileUrl();
        String[] split = url.split("/");
        String fileName = split[split.length - 1];
        String path = Environment.getExternalStorageDirectory() + "/jy/" + fileName;
        final File file = new File(path);
        file.mkdirs();
        info.setState(4);
        EventBus.getDefault().post(info);
        OkGo.<File>get(url)
                .tag(this)
                .execute(new FileCallback(Environment.getExternalStorageDirectory() + "/jy/", fileName) {


                    @Override
                    public void onSuccess(Response<File> response) {
                        info.setState(1);
                        EventBus.getDefault().post(info);
                        downLoadingList.remove(info);
                        nextDownLoad();
                    }


                    @Override
                    public void downloadProgress(Progress progress) {
                        info.setState(4);
                        info.setProgress((int) (progress.fraction * 100));
                        EventBus.getDefault().post(info);
                    }

                    @Override
                    public void onError(Response<File> response) {
                        downLoadingList.remove(info);
                        info.setState(3);
                        info.setErrorMessage(response.getException().getMessage());
                        EventBus.getDefault().post(info);
                        file.delete();


                    }
                });


    }


    private void nextDownLoad() {

        if (waitingList.size() != 0) {
            PdfBean info = waitingList.get(0);
            addDownloadTask(info);


        }


    }


    public void cancelWaitTask(PdfBean info) {
        if (waitingList.contains(info)) {
            waitingList.remove(info);
            info.setState(0);
            EventBus.getDefault().post(info);

        }


    }


    public void destory() {
        OkGo.getInstance().cancelTag(this);
        for (PdfBean pdfBean : downLoadingList) {
            String url = pdfBean.getFileUrl();
            String[] split = url.split("/");
            String fileName = split[split.length - 1];
            String path = Environment.getExternalStorageDirectory() + "/jy/" + fileName;
            File file = new File(path);
            file.delete();


        }

    }


}
