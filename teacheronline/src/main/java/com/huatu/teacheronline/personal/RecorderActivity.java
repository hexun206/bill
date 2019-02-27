package com.huatu.teacheronline.personal;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.PowerManager;
import android.os.SystemClock;
import android.support.v7.app.AlertDialog;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.huatu.teacheronline.BaseActivity;
import com.huatu.teacheronline.R;
import com.huatu.teacheronline.personal.upload.CameraPreview;
import com.huatu.teacheronline.personal.upload.VideoMerger;
import com.huatu.teacheronline.utils.ClickUtils;
import com.huatu.teacheronline.utils.FileUtils;
import com.huatu.teacheronline.utils.PermissionCheckUtil;
import com.huatu.teacheronline.utils.ToastUtils;
import com.huatu.teacheronline.widget.RoundProgress;
import com.orhanobut.logger.Logger;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.functions.Consumer;

public class RecorderActivity extends BaseActivity {


    @BindView(R.id.ll_recorder_preview)
    LinearLayout mLlRecorderPreview;
    @BindView(R.id.chronometer_recorder_time)
    Chronometer mChronometerRecorderTime;
    @BindView(R.id.view_recorder_status)
    View mViewRecorderStatus;
    @BindView(R.id.img_video_change_cam)
    ImageView mImgVideoChangeCam;
    @BindView(R.id.img_video_play_status)
    ImageView mImgVideoPlayStatus;
    @BindView(R.id.img_video_play_progress)
    RoundProgress mImgVideoPlayProgress;
    @BindView(R.id.tv_video_cancel)
    TextView tv_cancel;
    @BindView(R.id.tv_video_complete)
    TextView tv_complete;
    @BindView(R.id.tv_recorder_zoom)
    TextView tv_zoom;

    private final static String ZOOM_X1 = "x1";
    private final static String ZOOM_X2 = "x2";


    private final static int STATUS_NONE = -1;
    private final static int STATUS_START = 0;
    private final static int STATUS_PAUSE = 1;
    private long mRangeTime;
    private int mStatus = STATUS_NONE;

    public final static String TAG_FRONT = "front";
    public final static String TAG_BACK = "back";

    /**
     * 最大录制时间
     */
    private final double MAX_DURATION = 20 * 60 * 1000;
    private AlertDialog mNoticeDialog;


    private ArrayList<File> videoList = new ArrayList<>();
    private PowerManager.WakeLock mWakeLock;

    private String mZoom;

    private boolean isDefaultCancel = true;
    private boolean isFirstEnter = true;

    @Override
    public void initView() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_recorder);
        ButterKnife.bind(this);
        changeStatus(STATUS_NONE);


        PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
        mWakeLock = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "recorder");
        mWakeLock.acquire(); //设置保持唤醒

        initCancelNoticeDialog();

        initialize();


//        requestCameraPermission();


    }

    private void requestCameraPermission() {
        new RxPermissions(this)
                .request(Manifest.permission.CAMERA)
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        if (!aBoolean) {
                            Toast.makeText(getApplicationContext(), "请检查摄像头或授予摄像头权限!"
                                    , Toast.LENGTH_SHORT).show();
                            finish();
                        }

                    }


                });
    }

    public static void start(Context context) {
        context.startActivity(new Intent(context, RecorderActivity.class));

    }


    private void initCancelNoticeDialog() {

        View view = LayoutInflater.from(this).inflate(R.layout.dialog_common_notice, null);

        ViewHolder holder = new ViewHolder(view);
        holder.mTvTittle.setText("视频未保存，确定取消？");
        holder.mTvTittleSub.setText("取消后，不会保存已录制的视频");
        holder.mTvDialogCancel.setText("取消");
        holder.mTvDialogConfirm.setText("确定");


        view.findViewById(R.id.tv_dialog_cancel).setOnClickListener(cancel -> mNoticeDialog.dismiss());

        view.findViewById(R.id.tv_dialog_confirm).setOnClickListener(call -> {

            for (File file : videoList) {
                if (file.exists()) {
                    file.delete();
                }
            }
            mNoticeDialog.dismiss();


            finish();

        });

        mNoticeDialog = new AlertDialog.Builder(this, R.style.dialog_notitle)
                .setView(view)
                .create();


    }

    @Override
    public void setListener() {
        mChronometerRecorderTime.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
            @Override
            public void onChronometerTick(Chronometer chronometer) {
                if (mStatus != STATUS_START) {
                    return;
                }

                long time = SystemClock.elapsedRealtime() - chronometer.getBase();

                chronometer.setText(DateFormat.format("mm:ss", time));

                int progress = (int) (time / MAX_DURATION * 100);
                Logger.e("Recorder progress:" + progress);
                mImgVideoPlayProgress.setProgress(progress);

                //录制时长达到最大时长
                if (progress >= 100) {
                    stopRecord();
                    changeStatus(STATUS_PAUSE);
                    startMergeVideo();
                }


            }
        });
    }

    /**
     * 开始合成视频
     */
    private void startMergeVideo() {
        String finalVideoPath = FileUtils.getInterviewVideoMergedFolder() + new Date().getTime() + ".mp4";

        File finalVideoFile = new File(finalVideoPath);

        VideoMerger videoMerger = new VideoMerger(this, videoList, finalVideoFile);
        videoMerger.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        videoMerger.setVideoMergeListener(new VideoMerger.OnVideoListener() {
            @Override
            public void onVideoMerged() {
                //合并成功
                ToastUtils.showToast("视频录制成功!");

                sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(finalVideoFile)));
                UploadVideoConfirmActivity.start(RecorderActivity.this, finalVideoPath);
                finish();
            }

            @Override
            public void onVideoMergeFailed(Exception e) {
                //合并失败
                ToastUtils.showToast("视频处理失败!");
                finish();

            }
        });


    }

    @Override
    public void onClick(View v) {

    }


    @OnClick({R.id.img_video_change_cam, R.id.img_video_play_status, R.id.tv_video_cancel, R.id.tv_video_complete, R.id.tv_recorder_zoom})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.img_video_change_cam:
                if (ClickUtils.isFastClick()) {
                    return;
                }


                int camerasNumber = Camera.getNumberOfCameras();
                if (camerasNumber > 1) {
                    releaseCamera();
                    chooseCamera();
                } else {
                    //只有一个摄像头不允许切换
                    Toast.makeText(getApplicationContext(), "对不起,您只有一个摄像头,无法切换"
                            , Toast.LENGTH_SHORT).show();
                }


                break;
            case R.id.img_video_play_status:
                if (ClickUtils.isFastClick()) {
                    return;
                }

                if (recording) {
                    //如果正在录制点击这个按钮表示录制完成

                    stopRecord();
                    ToastUtils.showToast("暂停录制");

                } else {
                    isDefaultCancel = false;
                    startRecord();
                    ToastUtils.showToast("开始录制");
                }

                break;
            case R.id.tv_video_cancel:
                if (isDefaultCancel) {
                    finish();
                } else {
                    mNoticeDialog.show();
                }
                break;
            case R.id.tv_video_complete:
                startMergeVideo();
                break;
            case R.id.tv_recorder_zoom:

                zoomCamera(mZoom == ZOOM_X1 ? ZOOM_X2 : ZOOM_X1);


                break;
        }
    }


    private void changeStatus(int status) {
        mStatus = status;
        switch (status) {
            case STATUS_PAUSE:
                ToastUtils.showToast("暂停录制");
                mViewRecorderStatus.setVisibility(View.VISIBLE);
                mImgVideoChangeCam.setVisibility(View.INVISIBLE);
                mImgVideoPlayStatus.setImageResource(R.drawable.ic_video_play);
                tv_cancel.setVisibility(View.VISIBLE);
                tv_complete.setVisibility(View.VISIBLE);

                mChronometerRecorderTime.stop();
                mRangeTime = SystemClock.elapsedRealtime();


                break;
            case STATUS_START:

                if (mRangeTime != 0) {

                    mChronometerRecorderTime.setBase(mChronometerRecorderTime.getBase() + (SystemClock.elapsedRealtime() - mRangeTime));
                } else {

                    mChronometerRecorderTime.setBase(SystemClock.elapsedRealtime());
                }

                mChronometerRecorderTime.start();

                mViewRecorderStatus.setVisibility(View.VISIBLE);
                mImgVideoChangeCam.setVisibility(View.INVISIBLE);
                mImgVideoPlayStatus.setImageResource(R.drawable.ic_video_pause);
                tv_cancel.setVisibility(View.INVISIBLE);
                tv_complete.setVisibility(View.INVISIBLE);


                break;
            case STATUS_NONE:
                mChronometerRecorderTime.setBase(SystemClock.elapsedRealtime());
                mChronometerRecorderTime.stop();
                mViewRecorderStatus.setVisibility(View.INVISIBLE);
                mImgVideoPlayStatus.setImageResource(R.drawable.ic_video_play);
                mImgVideoPlayProgress.setProgress(0);
                mImgVideoChangeCam.setVisibility(View.VISIBLE);

                tv_cancel.setVisibility(View.VISIBLE);
                tv_complete.setVisibility(View.INVISIBLE);

                break;
        }


    }

    static class ViewHolder {
        @BindView(R.id.tv_tittle)
        TextView mTvTittle;
        @BindView(R.id.tv_tittle_sub)
        TextView mTvTittleSub;
        @BindView(R.id.line_horizontal)
        View mLineHorizontal;
        @BindView(R.id.tv_dialog_cancel)
        TextView mTvDialogCancel;
        @BindView(R.id.line_vertical)
        View mLineVertical;
        @BindView(R.id.tv_dialog_confirm)
        TextView mTvDialogConfirm;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }


    /********************录制相关**************************/

    private static final int FOCUS_AREA_SIZE = 500;
    private Camera mCamera;
    private CameraPreview mPreview;
    private MediaRecorder mediaRecorder;
    private String url_file;
    private static boolean cameraFront = false;
    private int quality = CamcorderProfile.QUALITY_480P;
    boolean recording = false;


    @Override
    public void onResume() {
        super.onResume();
        mWakeLock.acquire();
        if (!hasCamera(getApplicationContext())) {
            //这台设备没有发现摄像头
            Toast.makeText(getApplicationContext(), "对不起,没有发现摄像头,请检查"
                    , Toast.LENGTH_SHORT).show();
            releaseCamera();
            releaseMediaRecorder();
            finish();
        }
        if (mCamera == null) {
            releaseCamera();
            final boolean frontal = cameraFront;

            int cameraId = findFrontFacingCamera();
            if (cameraId < 0) {
                //尝试寻找后置摄像头
                cameraId = findBackFacingCamera();
            } else if (!frontal) {
                cameraId = findBackFacingCamera();
            }


            try {
                mCamera = Camera.open(cameraId);
                if (!PermissionCheckUtil.checkCamera(mCamera)) {
                    Toast.makeText(getApplicationContext(), "请检查摄像头或授予摄像头权限!"
                            , Toast.LENGTH_SHORT).show();
                    finish();
                }

                Camera.Parameters parameters = mCamera.getParameters();
                boolean zoomSupported = parameters.isZoomSupported();
                tv_zoom.setVisibility(zoomSupported ? View.VISIBLE : View.INVISIBLE);
                if (zoomSupported) {
                    zoomCamera(ZOOM_X1);
                }
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "请检查摄像头或授予摄像头权限!"
                        , Toast.LENGTH_SHORT).show();
                finish();

            }

            mPreview.refreshCamera(mCamera);

        }

        if (isFirstEnter&&!isFinishing()) {
            isFirstEnter = false;
            ToastUtils.showToast("建议：竖屏拍摄效果更佳哟～");
        }
    }

    private void zoomCamera(String zoom) {
        if (mCamera != null) {
            mZoom = zoom;

            int zoomIndexByZoomratio = getZoomIndexByZoomratio(zoom.equals(ZOOM_X1) ? 1 : 2);
            if (zoomIndexByZoomratio >= 0) {
                Camera.Parameters parameters = mCamera.getParameters();
                parameters.setZoom(zoomIndexByZoomratio);
                mCamera.setParameters(parameters);
                tv_zoom.setText(zoom);
            }


        }


    }

    @Override
    protected void onPause() {
        super.onPause();
        mWakeLock.release();
        if (recording) {
            stopRecord();
        }
        releaseCamera();
    }

    @Override
    public void onBackPressed() {
        if (recording) {
            stopRecord();
        }

        mNoticeDialog.show();


    }

    /**
     * 找前置摄像头,没有则返回-1
     *
     * @return cameraId
     */
    private int findFrontFacingCamera() {
        int cameraId = -1;
        //获取摄像头个数
        int numberOfCameras = Camera.getNumberOfCameras();
        for (int i = 0; i < numberOfCameras; i++) {
            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(i, info);
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                cameraId = i;
                cameraFront = true;
                break;
            }
        }
        return cameraId;
    }

    /**
     * 找后置摄像头,没有则返回-1
     *
     * @return cameraId
     */
    private int findBackFacingCamera() {
        int cameraId = -1;
        //获取摄像头个数
        int numberOfCameras = Camera.getNumberOfCameras();
        for (int i = 0; i < numberOfCameras; i++) {
            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(i, info);
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                cameraId = i;
                cameraFront = false;
                break;
            }
        }
        return cameraId;
    }


    //点击对焦
    public void initialize() {
        mPreview = new CameraPreview(this, mCamera);
        mLlRecorderPreview.addView(mPreview);
        mLlRecorderPreview.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                try {
                    focusOnTouch(event);
                } catch (Exception e) {
                    Logger.e("对焦失败");

                }
            }
            return true;
        });

    }


    //选择摄像头
    public void chooseCamera() {
        if (cameraFront) {
            //当前是前置摄像头
            int cameraId = findBackFacingCamera();
            if (cameraId >= 0) {
                // open the backFacingCamera
                // set a picture callback
                // refresh the preview
                mCamera = Camera.open(cameraId);
                // mPicture = getPictureCallback();
                mPreview.refreshCamera(mCamera);
            }
        } else {
            //当前为后置摄像头
            int cameraId = findFrontFacingCamera();
            if (cameraId >= 0) {
                // open the backFacingCamera
                // set a picture callback
                // refresh the preview
                mCamera = Camera.open(cameraId);
                // mPicture = getPictureCallback();
                mPreview.refreshCamera(mCamera);
            }
        }
    }


    //检查设备是否有摄像头
    private boolean hasCamera(Context context) {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            return true;
        } else {
            return false;
        }
    }

    private void stopRecord() {
        changeStatus(STATUS_PAUSE);
        mediaRecorder.stop(); //停止
//        changeRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
        releaseMediaRecorder();
        recording = false;

        videoList.add(new File(url_file));

    }

    private void startRecord() {
        changeStatus(STATUS_START);
        //准备开始录制视频
        if (!prepareMediaRecorder()) {
            Toast.makeText(this, "摄像头与音频录制初始化失败", Toast.LENGTH_SHORT).show();
            releaseCamera();
            releaseMediaRecorder();
            finish();
        }
        //开始录制视频
        // If there are stories, add them to the table
        try {
            mediaRecorder.start();
//            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
//                changeRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//            } else {
//                changeRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
//            }
        } catch (final Exception ex) {
            releaseCamera();
            releaseMediaRecorder();
            finish();
        }
        recording = true;
    }


    private void changeRequestedOrientation(int orientation) {
        setRequestedOrientation(orientation);
    }

    private void releaseMediaRecorder() {
        if (mediaRecorder != null) {
            mediaRecorder.reset();
            mediaRecorder.release();
            mediaRecorder = null;
            mCamera.lock();
        }
    }

    private boolean prepareMediaRecorder() {
        mediaRecorder = new MediaRecorder();
        mCamera.unlock();
        mediaRecorder.setCamera(mCamera);
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
        mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            if (cameraFront) {
                mediaRecorder.setOrientationHint(270);
            } else {
                mediaRecorder.setOrientationHint(90);
            }
        }

        mediaRecorder.setProfile(CamcorderProfile.get(quality));
        String folderPath = FileUtils.getInterviewVideoRecordFolder();


        Date d = new Date();
        String timestamp = String.valueOf(d.getTime());
//        url_file = Environment.getExternalStorageDirectory().getPath() + "/videoKit" + timestamp + ".mp4";
//        url_file = "/mnt/sdcard/videokit/in.mp4";
        String orientationTag = cameraFront ? TAG_FRONT : TAG_BACK;
        url_file = folderPath + timestamp + orientationTag + ".mp4";

        mediaRecorder.setOutputFile(url_file);


        try {
            mediaRecorder.prepare();
        } catch (IllegalStateException e) {
            e.printStackTrace();
            releaseMediaRecorder();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            releaseMediaRecorder();
            return false;
        }
        return true;

    }

    private void releaseCamera() {
        if (mCamera != null) {
            mCamera.release();
            mCamera = null;
        }
    }

    private void focusOnTouch(MotionEvent event) {
        if (mCamera != null) {
            Camera.Parameters parameters = mCamera.getParameters();
            if (parameters.getMaxNumMeteringAreas() > 0) {
                Rect rect = calculateFocusArea(event.getX(), event.getY());
                parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
                List<Camera.Area> meteringAreas = new ArrayList<Camera.Area>();
                meteringAreas.add(new Camera.Area(rect, 800));
                parameters.setFocusAreas(meteringAreas);
                mCamera.setParameters(parameters);
                mCamera.autoFocus(mAutoFocusTakePictureCallback);
            } else {
                mCamera.autoFocus(mAutoFocusTakePictureCallback);
            }
        }
    }

    private Rect calculateFocusArea(float x, float y) {
        int left = clamp(Float.valueOf((x / mPreview.getWidth()) * 2000 - 1000).intValue(), FOCUS_AREA_SIZE);
        int top = clamp(Float.valueOf((y / mPreview.getHeight()) * 2000 - 1000).intValue(), FOCUS_AREA_SIZE);
        return new Rect(left, top, left + FOCUS_AREA_SIZE, top + FOCUS_AREA_SIZE);
    }

    private int clamp(int touchCoordinateInCameraReper, int focusAreaSize) {
        int result;
        if (Math.abs(touchCoordinateInCameraReper) + focusAreaSize / 2 > 1000) {
            if (touchCoordinateInCameraReper > 0) {
                result = 1000 - focusAreaSize / 2;
            } else {
                result = -1000 + focusAreaSize / 2;
            }
        } else {
            result = touchCoordinateInCameraReper - focusAreaSize / 2;
        }
        return result;
    }

    private Camera.AutoFocusCallback mAutoFocusTakePictureCallback = new Camera.AutoFocusCallback() {
        @Override
        public void onAutoFocus(boolean success, Camera camera) {
            if (success) {
                // do something...
                Logger.i("tap_to_focus success!");
            } else {
                // do something...
                Logger.i("tap_to_focus fail!");
            }
        }
    };


    /**
     * 获取下标,传入的参数为浮点数，就是显示的倍率
     *
     * @param zoomratio
     * @return
     */
    public int getZoomIndexByZoomratio(float zoomratio) {
        List<Integer> allZoomRatios = getAllZoomRatio();
        if (allZoomRatios == null) {
            return -3;
        } else if (allZoomRatios.size() <= 0) {
            return -4;
        }
        if (zoomratio == 1.0f) {
            return 0;
        }
        if (zoomratio == getPictureMaxZoom()) {
            return allZoomRatios.size() - 1;
        }
        for (int i = 1; i < allZoomRatios.size(); i++) {
            if (allZoomRatios.get(i) >= (zoomratio * 100) && allZoomRatios.get(i - 1) <= (zoomratio * 100)) {
                return i;
            }
        }

        return -1;
    }


    /**
     * 获取全部zoomratio
     */
    public List<Integer> getAllZoomRatio() {
        Camera.Parameters parameters = mCamera.getParameters();
        if (parameters.isZoomSupported()) {
            return parameters.getZoomRatios();
        } else {
            return null;
        }
    }

    /**
     * 获取实际意义的最大放大倍数，如4.0，10.0
     * 未完成
     *
     * @return
     */
    public float getPictureMaxZoom() {
        List<Integer> allZoomRatio = getAllZoomRatio();
        if (null == allZoomRatio) {
            return 1.0f;
        } else {
            return Math.round(allZoomRatio.get(allZoomRatio.size() - 1) / 100f);
        }
    }


}
