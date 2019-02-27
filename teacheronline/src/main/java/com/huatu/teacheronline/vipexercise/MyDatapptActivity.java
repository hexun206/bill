package com.huatu.teacheronline.vipexercise;

import android.app.Activity;
import android.content.Intent;
import android.os.Environment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.artifex.mupdflib.FilePicker;
import com.artifex.mupdflib.MuPDFCore;
import com.artifex.mupdflib.MuPDFPageAdapter;
import com.artifex.mupdflib.MuPDFReaderView;
import com.gensee.utils.StringUtil;
import com.huatu.teacheronline.BaseActivity;
import com.huatu.teacheronline.R;
import com.huatu.teacheronline.engine.ObtainDataFromNetListener;
import com.huatu.teacheronline.engine.SendRequest;
import com.huatu.teacheronline.utils.CommonUtils;
import com.huatu.teacheronline.utils.DebugUtil;
import com.huatu.teacheronline.utils.DownLoadFileTask;
import com.huatu.teacheronline.utils.FileUtils;
import com.huatu.teacheronline.utils.ToastUtils;
import com.huatu.teacheronline.utils.UserInfo;
import com.huatu.teacheronline.widget.CustomAlertDialog;
import com.huatu.teacheronline.widget.CustomProgressDialog;

import java.io.File;
import java.lang.ref.WeakReference;

/**
 * vip我的资料详情讲义
* */
public class MyDatapptActivity extends BaseActivity implements FilePicker.FilePickerSupport {

    private String uid;
    private String materialId;
    private String jyDownLoadUrlForLocal = "";//讲义下载到本地地址
    private boolean isShowJy;
    private boolean isShowPdf = false;
    private CustomProgressDialog pd_update;//讲义下载的进度条
    private String mFileName;
    private MuPDFCore core;
    private int totalPageCount;
    private int lastPagePosition;
    private boolean isFinished;
    private MuPDFReaderView mDocView;
    private LinearLayout ll_content_play;
    private TextView tv_title;
    private String title;
    private CustomAlertDialog mCustomLoadingDialog;
    @Override
    public void initView() {
        setContentView(R.layout.activity_my_datappt);
        ll_content_play = (LinearLayout) findViewById(R.id.ll_content_play);
        tv_title = (TextView) findViewById(R.id.tv_main_title);
        uid = CommonUtils.getSharedPreferenceItem(null, UserInfo.KEY_SP_USERID, "");
        mCustomLoadingDialog = new CustomAlertDialog(this, R.layout.dialog_loading_custom);
        materialId = getIntent().getStringExtra("materialId");
        title = getIntent().getStringExtra("title");
        tv_title.setText(title);
        PdfData();
    }

    private void PdfData() {
        ObtainPdfLister obtainPdfLister=new ObtainPdfLister(this);
        SendRequest.getVipDatappt(uid,materialId,obtainPdfLister);
    }

    @Override
    public void setListener() {
        findViewById(R.id.rl_main_left).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.rl_main_left:
                back();
                break;

        }
    }

    public static void newIntent(Activity context , String materialId,String title) {
        Intent intent = new Intent(context, MyDatapptActivity.class);
        intent.putExtra("materialId",materialId);
        intent.putExtra("title",title);
        context.startActivity(intent);
    }

    @Override
    public void performPickFor(FilePicker picker) {

    }


    /****
     * vipPPt链接
     *****/
    private class ObtainPdfLister extends ObtainDataFromNetListener<String, String> {
        private MyDatapptActivity weak_activity;
        public ObtainPdfLister(MyDatapptActivity activity) {
            weak_activity = new WeakReference<>(activity).get();
        }

        @Override
        public void onStart() {
            super.onStart();
            weak_activity.mCustomLoadingDialog.show();
        }

        @Override
        public void onSuccess(final String res) {
            if (weak_activity != null) {
                weak_activity.mCustomLoadingDialog.dismiss();
                if (res != null) {
                    weak_activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            loadPDF(res);
                        }
                    });
                }
            }
        }

        @Override
        public void onFailure(final String res) {
            weak_activity.mCustomLoadingDialog.dismiss();
            if (weak_activity != null) {
                weak_activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (res.equals(SendRequest.ERROR_NETWORK)) {
                            ToastUtils.showToast(R.string.network);
                        } else if (res.equals(SendRequest.ERROR_SERVER)) {
                            ToastUtils.showToast(R.string.server_error);
                        }
                    }
                });
            }
        }
    }

    /**
     * 加载pdf信息
     * 本地有的话加载本地
     * 没有的话下载
     *
     * @param fileUrl 文件下载地址
     */
    public void loadPDF(String fileUrl) {
        if (!StringUtil.isEmpty(fileUrl)) {
            String[] split = fileUrl.split("/");
            String fileName = split[split.length - 1];
            jyDownLoadUrlForLocal = Environment.getExternalStorageDirectory() + "/wq/" + fileName;
            final File file = new File(jyDownLoadUrlForLocal);
            boolean exists = file.exists();
            if (file != null && exists) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        try {
//                DownLoadFileTask.makeRootDirectory(filepath);
//                            pdfView.fromFile(file)
//                                    .defaultPage(pageNumber)
//                                    .onPageChange(DirectPlayDetailsActivityForRtsdk.this)
//                                    .load();

                            isShowPdf = true;
                            initPdfView(jyDownLoadUrlForLocal);
                        } catch (Exception e) {
                            e.printStackTrace();
                            DebugUtil.e(e.toString());
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (pd_update != null) {
                                        pd_update.dismiss();
                                    }
                                    ToastUtils.showToast(R.string.pdfDownloadError);//下载失败
                                    file.delete();
                                }
                            });
                        }
//                        pdfView.fromFile(file)
//                                .defaultPage(pageNumber)
//                                .onPageChange(DirectPlayDetailsActivityForRtsdk.this)
//                                .load();
                    }
                });
            } else {
                file.mkdirs();//记得创建文件夹
                pd_update = new CustomProgressDialog(this);
                pd_update.setLoadingMsg(getResources().getString(R.string.downloadpdf));
                pd_update.setCancelable(false);
                new Thread(new DownLoadPDFFileThreadTask(fileUrl, jyDownLoadUrlForLocal)).start();
            }
        } else {
            if (!isShowJy) {
                isShowJy = true;
                ToastUtils.showToast(R.string.noTranslation);
            }
        }
    }

    /**
     * pdf下载线程
     */
    public class DownLoadPDFFileThreadTask implements Runnable {

        private String path;
        private String filepath;

        public DownLoadPDFFileThreadTask(String path, String filepath) {
            this.path = path;
            this.filepath = filepath;
        }

        @Override
        public void run() {
            try {
//                DownLoadFileTask.makeRootDirectory(filepath);
                File file = DownLoadFileTask.getFile(path, filepath, pd_update);
                pd_update.dismiss();
//                pdfView.fromFile(new File(filepath))
//                        .defaultPage(pageNumber)
//                        .onPageChange(DirectPlayDetailsActivityForRtsdk.this)
//                        .load();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        isShowPdf = true;
                        initPdfView(filepath);
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                DebugUtil.e(e.toString());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        pd_update.dismiss();
                        FileUtils.deleteFolder(filepath);
                        ToastUtils.showToast(R.string.pdfDownloadError);//下载失败
                    }
                });
            }
        }
    }

    private void initPdfView(String path) {
        core = openFile(path);
        if(core==null){
            Toast.makeText(this, "open pdf file failed", Toast.LENGTH_SHORT).show();
//            finish();
        }
        totalPageCount = core.countPages();
        if(lastPagePosition>totalPageCount){
            lastPagePosition=totalPageCount;
            isFinished=true;
        }
        if (totalPageCount == 0) {
            Toast.makeText(this, "PDF file has format error", Toast.LENGTH_SHORT).show();
            finish();
        }
        //one page per screen
        core.setDisplayPages(1);

        mDocView = new MuPDFReaderView(this) {
            @Override
            protected void onMoveToChild(int i) {
                DebugUtil.e(TAG, "onMoveToChild " + i);
                super.onMoveToChild(i);
//                mTitle.setText(String.format(" %s / %s ",  i + 1, totalPageCount));
                if ((i + 1) == totalPageCount) {
                    isFinished = true;
                }
            }

            @Override
            protected void onTapMainDocArea() {
                //Log.d(TAG,"onTapMainDocArea");
            }

            @Override
            protected void onDocMotion() {
                //Log.d(TAG,"onDocMotion");
            }

        };
        mDocView.setAdapter(new MuPDFPageAdapter(this, this, core));
        mDocView.setKeepScreenOn(true);
        mDocView.setLinksHighlighted(false);
        mDocView.setScrollingDirectionHorizontal(true);

        ll_content_play.addView(mDocView, new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        mDocView.setDisplayedViewIndex(lastPagePosition - 1);
//        mTitle.setText(String.format(" %s / %s ", lastPagePosition, totalPageCount));
    }

    private MuPDFCore openFile(String path) {
        int lastSlashPos = path.lastIndexOf('/');
        mFileName = lastSlashPos == -1 ? path
                : path.substring(lastSlashPos + 1);
        System.out.println("Trying to open " + path);
        try {
            core = new MuPDFCore(this, path);
            // New file: drop the old outline data
            //OutlineActivityData.set(null);
//            PDFPreviewGridActivityData.set(null);
        } catch (Exception e) {
            System.out.println(e);
            DebugUtil.e(TAG,e.getMessage());
            return null;
        }
        return core;
    }

}
