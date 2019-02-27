package com.huatu.teacheronline.personal;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.PowerManager;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.huatu.teacheronline.BaseActivity;
import com.huatu.teacheronline.R;
import com.huatu.teacheronline.direct.bean.NetWorkChangeEvent;
import com.huatu.teacheronline.direct.receiver.NetworkChangeReceiver;
import com.huatu.teacheronline.engine.BJ_CloudApi;
import com.huatu.teacheronline.engine.HTeacherApi;
import com.huatu.teacheronline.engine.ObtainDataFromNetListener;
import com.huatu.teacheronline.personal.bean.BjyUploadUrlBean;
import com.huatu.teacheronline.personal.bean.InteviewVideoInfoUploadRequest;
import com.huatu.teacheronline.personal.bean.NetWorkErrorEvent;
import com.huatu.teacheronline.personal.bean.UploadEvent;
import com.huatu.teacheronline.personal.db.VideoUploadInfo;
import com.huatu.teacheronline.personal.db.VideoUploadInfoManager;
import com.huatu.teacheronline.personal.upload.UpLoadManager;
import com.huatu.teacheronline.personal.upload.UploadListener;
import com.huatu.teacheronline.utils.CommonUtils;
import com.huatu.teacheronline.utils.GsonUtils;
import com.huatu.teacheronline.utils.NetWorkUtils;
import com.huatu.teacheronline.utils.PermissionCheckUtil;
import com.huatu.teacheronline.utils.StringUtils;
import com.huatu.teacheronline.utils.ToastUtils;
import com.huatu.teacheronline.widget.VideoPopupWindow;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.orhanobut.logger.Logger;
import com.tbruyelle.rxpermissions2.RxPermissions;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONObject;

import java.io.File;
import java.util.List;

import butterknife.BindDrawable;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class InterviewVideoUploadEditActivity extends BaseActivity {


    public final static String KEY_DATA = "KEY_DATA";

    @BindView(R.id.tv_agree)
    TextView mTvAgree;

    @BindView(R.id.img_video_edit_add)
    ImageView mImgVideoEditAdd;
    @BindView(R.id.img_video_edit_uploading)
    ImageView mImgVideoEditUploading;
    @BindView(R.id.pb_video_edit)
    ProgressBar mPbVideoEdit;
    @BindView(R.id.img_video_edit_state)
    ImageView mImgVideoEditState;
    @BindView(R.id.tv_video_edit_state)
    TextView mTvVideoEditState;
    @BindView(R.id.tv_video_edit_remain_count)
    TextView mTvVideoEditRemainCount;
    @BindView(R.id.tv_video_edit_pause)
    TextView mTvVideoEditPause;
    @BindView(R.id.tv_video_edit_continue)
    TextView mTvVideoEditContinue;
    @BindView(R.id.tv_video_edit_delete)
    TextView mTvVideoEditDelete;
    @BindView(R.id.tv_video_edit_restart)
    TextView mTvVideoEditRestart;
    @BindView(R.id.tv_video_edit_change)
    TextView mTvVideoEditChange;
    @BindView(R.id.tv_video_edit_preview)
    TextView mTvVideoEditPreview;
    @BindView(R.id.rel_video_edit)
    RelativeLayout mRelVideoEdit;

    @BindView(R.id.tv_video_edit_submit)
    TextView mTvVideoEditSubmit;

    @BindDrawable(R.drawable.login_check_nor)
    Drawable mDrawableNormal;
    @BindDrawable(R.drawable.login_check_selected)
    Drawable mDrawableSelected;

    @BindView(R.id.line_dash)
    View mLine;

    private InteviewVideoInfoUploadRequest mInfo;

    private NetworkChangeReceiver mNetworkChangeReceiver;


    public final static int UPLOAD_STATE_NONE = 0x00;
    public final static int UPLOAD_STATE_START = 0x02;
    public final static int UPLOAD_STATE_PAUSE = 0x03;
    public final static int UPLOAD_STATE_SUCCESS = 0x04;
    public final static int UPLOAD_STATE_ERROR = 0x05;

    private volatile int mUploadState = UPLOAD_STATE_NONE;
    private AlertDialog mNoticeDialog;
    private VideoPopupWindow mChoosePopupView;
    private VideoUploadInfo mVideoUploadInfo;
    private PowerManager.WakeLock mWakeLock;
    private AlertDialog mDownloadNoticeDialog;
    private TextView mTvDownloadNoticetittle;
    private boolean isFirstRegister = true;
    private TextView mTvContinueDownloadNoticetittle;
    private AlertDialog mContinueDownloadNoticeDialog;

    private boolean agree;


    public static void start(Context context, InteviewVideoInfoUploadRequest info) {
        Intent intent = new Intent(context, InterviewVideoUploadEditActivity.class);
        intent.putExtra(KEY_DATA, info);
        context.startActivity(intent);


    }


    @Override
    public void initView() {
        setContentView(R.layout.activity_interview_video_upload_edit);
        ButterKnife.bind(this);
        PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
        mWakeLock = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "upload");


        mNetworkChangeReceiver = new NetworkChangeReceiver();

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        intentFilter.addAction("android.net.wifi.WIFI_STATE_CHANGED");
        intentFilter.addAction("android.net.wifi.STATE_CHANGE");

        registerReceiver(mNetworkChangeReceiver, intentFilter);

        mLine.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        //常亮一小时
        mWakeLock.acquire(60 * 60 * 1000);
        mInfo = (InteviewVideoInfoUploadRequest) getIntent().getSerializableExtra(KEY_DATA);
        initNoticeDialog();
        initDownloadNoticeDialog();
        initContinueDownloadNoticeDialog();

        mVideoUploadInfo = VideoUploadInfoManager.getInstance().getInfo(mInfo.getUserid(), mInfo.getNetclassid());


        mChoosePopupView = new VideoPopupWindow(this, this);

        mPbVideoEdit.setProgress(mVideoUploadInfo.getUploadProgress());

        if (mVideoUploadInfo.getUploadState() == UPLOAD_STATE_START) {
            mVideoUploadInfo.setUploadState(UPLOAD_STATE_PAUSE);
        }


        agree = mVideoUploadInfo.isAgree();
        changeAgreeStatus();

        refreshState(mVideoUploadInfo.getUploadState());

    }


    private void refreshState(int state) {
        if (state != mUploadState) {
            clearState();
        }

        if (!StringUtils.isEmpty(mVideoUploadInfo.getLocalPath())) {
            RequestOptions options = new RequestOptions()
                    .transforms(new CenterCrop(), new RoundedCorners(20))
                    .placeholder(R.color.gray007)
                    .diskCacheStrategy(DiskCacheStrategy.ALL);

            Glide.with(this)
                    .load(mVideoUploadInfo.getLocalPath())
                    .apply(options)
                    .into(mImgVideoEditUploading);
        }

        mUploadState = state;

        mRelVideoEdit.setVisibility(mUploadState == UPLOAD_STATE_NONE ? View.GONE : View.VISIBLE);
        switch (mUploadState) {
            case UPLOAD_STATE_START:
                visible(mPbVideoEdit);
                visible(mTvVideoEditPause);
//                if (!isFirstEnter) {
//
//                    ToastUtils.showToast("开始上传");
//                }

                break;
            case UPLOAD_STATE_PAUSE:
                visible(mImgVideoEditState);
                visible(mTvVideoEditContinue);
                visible(mTvVideoEditDelete);
                mImgVideoEditState.setImageResource(R.drawable.ic_pause_vb);
//                if (!isFirstEnter) {
//                    ToastUtils.showToast("暂停上传");
//                }
                break;
            case UPLOAD_STATE_SUCCESS:
                visible(mImgVideoEditState);
                visible(mTvVideoEditState);
                visible(mTvVideoEditRemainCount);
                visible(mTvVideoEditChange);
                visible(mTvVideoEditPreview);
                mImgVideoEditState.setImageResource(R.drawable.ic_suc_vb);
                mTvVideoEditState.setText("上传成功");
//                if (!isFirstEnter) {
//                    ToastUtils.showToast("上传成功");
//                }

                int remain = InteviewVideoInfoUploadRequest.UPLOAD_MAX_COUNT - mVideoUploadInfo.getUploadCount();
                if (remain == 0) {
                    mTvVideoEditChange.setVisibility(View.GONE);
                }

                mTvVideoEditRemainCount.setText("剩余可替换次数：" + remain + "次");
                mTvVideoEditSubmit.setEnabled(true);

                break;
            case UPLOAD_STATE_ERROR:
                visible(mImgVideoEditState);
                visible(mTvVideoEditState);
                visible(mTvVideoEditDelete);
                visible(mTvVideoEditRestart);
                mImgVideoEditState.setImageResource(R.drawable.ic_warn_vb);
                mTvVideoEditState.setText("上传失败");
//                if (!isFirstEnter) {
//                    ToastUtils.showToast("上传失败");
//                }
                break;
            default:
                mImgVideoEditAdd.setVisibility(View.VISIBLE);
                break;
        }

        mVideoUploadInfo.setUploadState(mUploadState);
        VideoUploadInfoManager.getInstance().setInfo(mVideoUploadInfo);

    }

    private void visible(View v) {
        v.setVisibility(View.VISIBLE);
    }

    private void clearState() {
        mImgVideoEditAdd.setVisibility(View.GONE);
        mPbVideoEdit.setVisibility(View.GONE);
        mImgVideoEditState.setVisibility(View.GONE);
        mTvVideoEditState.setVisibility(View.GONE);
        mTvVideoEditRemainCount.setVisibility(View.GONE);
        mTvVideoEditPause.setVisibility(View.GONE);
        mTvVideoEditContinue.setVisibility(View.GONE);
        mTvVideoEditDelete.setVisibility(View.GONE);
        mTvVideoEditRestart.setVisibility(View.GONE);
        mTvVideoEditChange.setVisibility(View.GONE);
        mTvVideoEditPreview.setVisibility(View.GONE);

        mTvVideoEditSubmit.setEnabled(false);
    }

    @Override
    public void setListener() {

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_record:
                mChoosePopupView.dissmissWithNotAnim();
                new RxPermissions(InterviewVideoUploadEditActivity.this)
                        .requestEach(Manifest.permission.RECORD_AUDIO)
                        .subscribe(permission -> {
                            if (permission.granted) {
                                if (!PermissionCheckUtil.checkRecordAudio()) {
                                    ToastUtils.showToast("请允许授予录制权限!");
                                    return;
                                }

                                if (!CommonUtils.isAvaiableSpace(400)) {
                                    ToastUtils.showToast(R.string.sd_is_full_info);
                                } else {
                                    RecorderActivity.start(InterviewVideoUploadEditActivity.this);
                                }
                            } else if (permission.shouldShowRequestPermissionRationale) {
                                new RxPermissions(InterviewVideoUploadEditActivity.this)
                                        .request(permission.name)
                                        .subscribe(granted -> {
                                            if (!granted) {
                                                Toast.makeText(InterviewVideoUploadEditActivity.this, "请允许授予录制权限!", Toast.LENGTH_SHORT).show();

                                            }
                                        });

                            } else {
                                Toast.makeText(InterviewVideoUploadEditActivity.this, "请在设置界面授予录制权限!", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent();
                                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package", getPackageName(), null);
                                intent.setData(uri);
                                startActivity(intent);
                            }
                        });


                break;
            case R.id.tv_upload:
                mChoosePopupView.dismiss();
                chooseLocalVideo();
                break;
            case R.id.view_space:
            case R.id.tv_cancel:

                mChoosePopupView.dismiss();
                break;
        }
    }

    private void chooseLocalVideo() {

        PictureSelector.create(this)
                .openGallery(PictureMimeType.ofVideo())
//                .theme(R.style.picture_white_style)
                .maxSelectNum(1)
                .minSelectNum(1)
                .selectionMode(PictureConfig.MULTIPLE)
                .previewImage(true)
                .previewVideo(true)
                .enablePreviewAudio(true)
                .isCamera(false)
                .videoMaxSecond(21 * 60)
                .forResult(PictureConfig.CHOOSE_REQUEST);


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mWakeLock.release();
        if (mNetworkChangeReceiver != null) {
            unregisterReceiver(mNetworkChangeReceiver);

        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mUploadState == UPLOAD_STATE_START) {
            pauseUpload();
        }


    }

    @OnClick({R.id.img_back, R.id.img_video_edit_add, R.id.tv_video_edit_pause, R.id.tv_video_edit_continue, R.id.tv_video_edit_delete,
            R.id.tv_video_edit_restart, R.id.tv_video_edit_change, R.id.tv_video_edit_preview, R.id.tv_video_edit_submit, R.id.tv_agree})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.img_back:
                finish();
                break;
            case R.id.img_video_edit_add:
                mChoosePopupView.showAsDropDown(mImgVideoEditAdd);

                break;
            case R.id.tv_video_edit_pause:
                pauseUpload();

                break;
            case R.id.tv_video_edit_continue:
                uploadByNetStatus();
                break;
            case R.id.tv_video_edit_delete:
                deleteVideo();


                break;
            case R.id.tv_video_edit_restart:

                uploadByNetStatus();


                break;
            case R.id.tv_video_edit_change:
                int remain = InteviewVideoInfoUploadRequest.UPLOAD_MAX_COUNT - mVideoUploadInfo.getUploadCount();
                if (remain <= 0) {
                    ToastUtils.showToast("您已没有可替换次数了!");
                    return;
                }

                mChoosePopupView.showAsDropDown(mImgVideoEditAdd);

                break;
            case R.id.tv_video_edit_preview:
                File file = new File(mVideoUploadInfo.getLocalPath());
                if (file.exists()) {
                    PictureSelector.create(this).externalPictureVideo(mVideoUploadInfo.getLocalPath());
                } else {
                    ToastUtils.showToast("视频文件不存在,无法预览!");
                }


                break;
            case R.id.tv_video_edit_submit:
                mNoticeDialog.show();
                break;

            case R.id.tv_agree:
                agree = !agree;
                mVideoUploadInfo.setAgree(agree);
                changeAgreeStatus();
                VideoUploadInfoManager.getInstance().setInfo(mVideoUploadInfo);
                break;
        }
    }


    private void changeAgreeStatus() {
        if (agree) {
            mTvAgree.setCompoundDrawablesWithIntrinsicBounds(mDrawableSelected, null, null, null);
        } else {
            mTvAgree.setCompoundDrawablesWithIntrinsicBounds(mDrawableNormal, null, null, null);

        }


    }


    private void uploadByNetStatus() {
        int networkType = NetWorkUtils.getAPNType(this);
        if (networkType == 0) {
            ToastUtils.showToast(R.string.network);
        } else if (networkType > 1) {
            String netWorkName = "";
            switch (networkType) {
                case 2:
                    netWorkName = "2G网络";
                    break;
                case 3:
                    netWorkName = "3G网络";
                    break;
                case 4:
                    netWorkName = "4G网络";
                    break;
            }
            mTvContinueDownloadNoticetittle.setText("您正在使用" + netWorkName + ",是否使用流量上传?");
            mContinueDownloadNoticeDialog.show();


        } else {
            refreshState(UPLOAD_STATE_START);
            upload2BJCloud();
        }
    }

    private void pauseUpload() {
        UpLoadManager.getInstance().cancel();
        refreshState(UPLOAD_STATE_PAUSE);
    }


    private void initNoticeDialog() {
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_common_notice, null);
        view.findViewById(R.id.tv_dialog_cancel).setOnClickListener(cancel -> mNoticeDialog.dismiss());

        view.findViewById(R.id.tv_dialog_confirm).setOnClickListener(call -> {
            mNoticeDialog.dismiss();
            submit();

        });

        mNoticeDialog = new AlertDialog.Builder(this, R.style.dialog_notitle)
                .setView(view)
                .create();


    }

    private void initDownloadNoticeDialog() {
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_common_notice, null);

        mTvDownloadNoticetittle = (TextView) view.findViewById(R.id.tv_tittle);

        TextView tv_tittle_sub = (TextView) view.findViewById(R.id.tv_tittle_sub);
        tv_tittle_sub.setVisibility(View.GONE);

        view.findViewById(R.id.tv_dialog_cancel).setOnClickListener(cancel -> {
            mDownloadNoticeDialog.dismiss();

        });

        view.findViewById(R.id.tv_dialog_confirm).setOnClickListener(call -> {
            mDownloadNoticeDialog.dismiss();
            startUpload();

        });

        mDownloadNoticeDialog = new AlertDialog.Builder(this, R.style.dialog_notitle)
                .setView(view)
                .create();

    }

    private void initContinueDownloadNoticeDialog() {
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_common_notice, null);

        mTvContinueDownloadNoticetittle = (TextView) view.findViewById(R.id.tv_tittle);

        TextView tv_tittle_sub = (TextView) view.findViewById(R.id.tv_tittle_sub);
        tv_tittle_sub.setVisibility(View.GONE);

        view.findViewById(R.id.tv_dialog_cancel).setOnClickListener(cancel -> {
            mContinueDownloadNoticeDialog.dismiss();

        });

        view.findViewById(R.id.tv_dialog_confirm).setOnClickListener(call -> {
            mContinueDownloadNoticeDialog.dismiss();
            upload2BJCloud();
            refreshState(UPLOAD_STATE_START);

        });

        mContinueDownloadNoticeDialog = new AlertDialog.Builder(this, R.style.dialog_notitle)
                .setView(view)
                .create();

    }

    //提交面试视频信息
    private void submit() {
        showLoadingDialog();
        mInfo.setOstatus(1);
        mInfo.setBjyvideoid(mVideoUploadInfo.getBjyvideoid());
        mInfo.setAgreedisplay(agree ? 1 : 0);
        HTeacherApi.updateVideoInfo(mInfo, new ObtainDataFromNetListener<String, Throwable>() {
            @Override
            public void onSuccess(String res) {
                dismissLoadingDialog();
                String code = GsonUtils.getJson(res, "code");

                if ("1".equals(code)) {
                    ToastUtils.showToast("提交成功!");
                    VideoUploadInfoManager.getInstance().clear();
                    InterviewCommentsActivity.startClearTop(InterviewVideoUploadEditActivity.this);

                } else {
                    ToastUtils.showToast("提交失败!" + GsonUtils.getJson(res, "code"));

                }


            }

            @Override
            public void onFailure(Throwable res) {
                dismissLoadingDialog();

                ToastUtils.showToast(R.string.network);
            }
        });


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK) {
            if (requestCode == PictureConfig.CHOOSE_REQUEST) {
                List<LocalMedia> selectList = PictureSelector.obtainMultipleResult(data);
                if (selectList != null && selectList.size() != 0) {
                    String mSelectedVideoPath = selectList.get(0).getPath();
                    Logger.e("mSelectedVideoPath:" + mSelectedVideoPath);

                    onFilePathReturn(mSelectedVideoPath);

                }

            }

        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNetWorkStateChangeEvent(NetWorkChangeEvent event) {

        //屏蔽刚注册广播时的通知
        if (isFirstRegister) {
            isFirstRegister = false;
            return;
        }
        int networkType = NetWorkUtils.getAPNType(this);
        if (mUploadState == UPLOAD_STATE_START && networkType > 1) {
            String netWorkName = "";
            switch (networkType) {
                case 0:
                    netWorkName = "无网络";
                    break;
                case 1:
                    netWorkName = "WIFI网络";
                    break;
                case 2:
                    netWorkName = "2G网络";
                    break;
                case 3:
                    netWorkName = "3G网络";
                    break;
                case 4:
                    netWorkName = "4G网络";
                    break;
            }
            ToastUtils.showToast("您正在使用" + netWorkName + ",上传已暂停!");
            pauseUpload();
        }


    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNetWorkErrorEvent(NetWorkErrorEvent event) {

        if (mUploadState == UPLOAD_STATE_START) {
            UpLoadManager.getInstance().cancel();
            refreshState(UPLOAD_STATE_ERROR);
            ToastUtils.showToast(R.string.network);
        }


    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRecordSuccessEvent(UploadEvent event) {
        onFilePathReturn(event.getPath());

    }


    private void onFilePathReturn(String mSelectedVideoPath) {
        mVideoUploadInfo.setLocalPath(mSelectedVideoPath);


        //流量上传提示

        int networkType = NetWorkUtils.getAPNType(this);
        if (networkType == 0) {
            ToastUtils.showToast(R.string.network);

        } else if (networkType > 1) {
            String netWorkName = "";
            switch (networkType) {
                case 2:
                    netWorkName = "2G网络";
                    break;
                case 3:
                    netWorkName = "3G网络";
                    break;
                case 4:
                    netWorkName = "4G网络";
                    break;
            }
            mTvDownloadNoticetittle.setText("您正在使用" + netWorkName + ",是否使用流量上传?");
            mDownloadNoticeDialog.show();


        } else {
            startUpload();
        }


    }

    private void startUpload() {
        refreshState(UPLOAD_STATE_START);
        getUploadUrl();
    }


    private void deleteVideo() {
        showLoadingDialog();
        BJ_CloudApi.deleteVideoById(mVideoUploadInfo.getBjyvideoid() + "", new ObtainDataFromNetListener<String, Throwable>() {
            @Override
            public void onSuccess(String res) {
                if (isDestoryed) {
                    return;
                }
                dismissLoadingDialog();

//                if ("1".equals(GsonUtils.getJson(res, "code"))) {

                ToastUtils.showToast("删除成功");


//                } else {
//                    ToastUtils.showToast("删除失败");
//                }

                mVideoUploadInfo.setBjyvideoid(0);
                mVideoUploadInfo.setLocalPath("");
                mVideoUploadInfo.setUploadProgress(0);
                refreshState(UPLOAD_STATE_NONE);

            }

            @Override
            public void onFailure(Throwable res) {
                dismissLoadingDialog();
                ToastUtils.showToast(R.string.network);

            }
        });
    }


    /**
     * 上传视频至百家云
     */
    private void getUploadUrl() {
        //替换视频时删除旧视频    暂未设计失败记录重试
        if (mVideoUploadInfo.getBjyvideoid() != 0) {
            BJ_CloudApi.deleteVideoById(mVideoUploadInfo.getBjyvideoid() + "", new ObtainDataFromNetListener<String, Throwable>() {
                @Override
                public void onSuccess(String res) {
                }

                @Override
                public void onFailure(Throwable res) {
                }
            });

        }

        showLoadingDialog();
        File file = new File(mVideoUploadInfo.getLocalPath());
        BJ_CloudApi.getVideoUploadUrl(file.getName(), 1, new ObtainDataFromNetListener<String, Throwable>() {
            @Override
            public void onSuccess(String res) {
                if (isDestoryed) {
                    return;
                }
                dismissLoadingDialog();
                try {
                    JSONObject object = new JSONObject(res);
                    String code = object.getString("code");
                    String msg = object.getString("message");
                    if ("1".equals(code)) {
                        BjyUploadUrlBean info = GsonUtils.parseJSON(object.getString("data"), BjyUploadUrlBean.class);
                        mVideoUploadInfo.setBjyvideoid(info.getVideo_id());

                        VideoUploadInfoManager.getInstance().setInfo(mVideoUploadInfo);

                        upload2BJCloud();

                    } else {
                        ToastUtils.showToast(msg);
                        refreshState(UPLOAD_STATE_NONE);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    ToastUtils.showToast(R.string.server_error);
                    refreshState(UPLOAD_STATE_NONE);
                }

            }

            @Override
            public void onFailure(Throwable res) {
                if (isDestoryed) {
                    return;
                }
                dismissLoadingDialog();
                ToastUtils.showToast(R.string.network);
                refreshState(UPLOAD_STATE_NONE);
            }
        });


    }

    /**
     * 上传视频至百家云
     */
    private void upload2BJCloud() {

        UpLoadManager.Builder builder = new UpLoadManager.Builder()
                .setFile(new File(mVideoUploadInfo.getLocalPath()))
                .setVideoId(mVideoUploadInfo.getBjyvideoid() + "")
                .setListener(new UploadListener() {
                    @Override
                    public void onPause() {
                    }

                    @Override
                    public void onError(String msg) {
                        refreshState(UPLOAD_STATE_ERROR);
                    }

                    @Override
                    public void onProgress(int percent) {

                        mVideoUploadInfo.setUploadProgress(percent);
                        mPbVideoEdit.setProgress(percent);

                    }

                    @Override
                    public void onDataSegmentSuccess() {

                    }

                    @Override
                    public void onSuccess() {
                        mVideoUploadInfo.setUploadCount(mVideoUploadInfo.getUploadCount() + 1);
                        refreshState(UPLOAD_STATE_SUCCESS);

                    }
                });
        UpLoadManager.getInstance().init(builder);
    }


}
