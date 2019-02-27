package com.huatu.teacheronline.CCVideo;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bokecc.sdk.mobile.exception.ErrorCode;
import com.bokecc.sdk.mobile.play.DWIjkMediaPlayer;
import com.huatu.teacheronline.BaseActivity;
import com.huatu.teacheronline.R;
import com.huatu.teacheronline.engine.ObtainDataFromNetListener;
import com.huatu.teacheronline.engine.SendRequest;
import com.huatu.teacheronline.utils.CommonUtils;
import com.huatu.teacheronline.utils.DebugUtil;
import com.huatu.teacheronline.utils.ToastUtils;
import com.huatu.teacheronline.widget.ChooseDefinitionPopwindows;
import com.huatu.teacheronline.widget.CustomAlertDialog;

import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import tv.danmaku.ijk.media.player.IMediaPlayer;

/**
 * Created by zhxm on 2016/1/11.
 * 面试视频详情页
 */
public class InterviewVideoDetailActivity extends BaseActivity implements SurfaceHolder.Callback, DWIjkMediaPlayer.OnPreparedListener, DWIjkMediaPlayer
        .OnErrorListener {

    private static final String TAG = "InterviewVideoDetailActivity";
    private RelativeLayout rl_main_left;
    private TextView tv_main_title, tv_detail_intro, tv_detail_text, tv_detail_plan;
    private WebView webView;
    private String currentVideoId;
    private InterviewVideoBean interviewVideoBean;
    private CustomAlertDialog mCustomLoadingDialog;
    private RelativeLayout rl_video;
    private SurfaceView surfaceView;
    private SurfaceHolder surfaceHolder;
    private DWIjkMediaPlayer player;
    private boolean isPrepared;
    private boolean isSurfaceDestroy = false;
    // 当player未准备好，并且当前activity经过onPause()生命周期时，此值为true
    private boolean isFreeze = true;
    private Boolean isPlaying;// 是否正在播放
    private Handler playerHandler;
    private Timer timer = new Timer();
    private TimerTask timerTask;
    private SeekBar skbProgress;// 播放进度
    private ImageView iv_play, iv_screen;
    private RelativeLayout rl_play;
//    private LinearLayout  rl_screen;

    private boolean isFullScreen = false;// 当前是否全屏状态
    private RelativeLayout rl_deatil_waiting;
    private int rlVideoWidth = 0, rlVideoheight = 0;
    private TextView videoDuration, playDuration;

    private final static String CLASS_LABEL = "InterviewVideoDetailActivity";
    private PowerManager.WakeLock mWakeLock;// 禁止锁屏
    private ObtainDataListerForLoadingVideoDetailInfo obtainDataListerForLoadingVideoDetailInfo;
    private int currentPosition;
    private AlertDialog errorDialog;
    private LinearLayout rl_play_top;
    private RelativeLayout rl_full_screen_back;
    private TextView tv_full_screen_title;
    private RelativeLayout rl_play_bottom, rl_click_middle;
    private LinearLayout ll_play_detail_bottom;
    private LinearLayout topbar;
    private boolean isDisplay = true;// 播放时的顶部返回和底部进度栏是否显示
    private int definition = DWIjkMediaPlayer.NORMAL_DEFINITION;// 播放时的顶部返回和底部进度栏是否显示
    private ChooseDefinitionPopwindows chooseDefinitionPopwindows;
    private TextView tv_definition;//选择清晰度

    @Override
    public void initView() {
        setContentView(R.layout.activity_interview_video_detail);
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        mWakeLock = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, CLASS_LABEL);
        if ((mWakeLock != null) && !mWakeLock.isHeld()) {
            mWakeLock.acquire();
        }
        currentVideoId = getIntent().getStringExtra("video_id");
        mCustomLoadingDialog = new CustomAlertDialog(InterviewVideoDetailActivity.this, R.layout.dialog_loading_custom);
        player = new DWIjkMediaPlayer();

        rl_main_left = (RelativeLayout) findViewById(R.id.rl_main_left);
        tv_main_title = (TextView) findViewById(R.id.tv_main_title);
        tv_detail_intro = (TextView) findViewById(R.id.tv_detail_intro);
        tv_detail_text = (TextView) findViewById(R.id.tv_detail_text);
        tv_detail_plan = (TextView) findViewById(R.id.tv_detail_plan);
        tv_definition = (TextView) findViewById(R.id.tv_definition);
        webView = (WebView) findViewById(R.id.webView);
        rl_video = (RelativeLayout) findViewById(R.id.rl_video);
        surfaceView = (SurfaceView) findViewById(R.id.playerSurfaceView);

        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.setFormat(PixelFormat.RGBA_8888); //倍速播放器需要设置像素格式
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS); //2.3及以下使用，不然出现只有声音没有图像的问题
        surfaceHolder.addCallback(this);

        skbProgress = (SeekBar) findViewById(R.id.skbProgress);
        iv_play = (ImageView) findViewById(R.id.iv_play);
        iv_screen = (ImageView) findViewById(R.id.iv_screen);
        rl_play = (RelativeLayout) findViewById(R.id.rl_play);
//        rl_screen = (LinearLayout) findViewById(R.id.rl_screen);
        rl_deatil_waiting = (RelativeLayout) findViewById(R.id.rl_deatil_waiting);
        playDuration = (TextView) findViewById(R.id.playDuration);
        videoDuration = (TextView) findViewById(R.id.videoDuration);
        playDuration.setText(CommonUtils.millsecondsToStr(0));
        videoDuration.setText("/" + CommonUtils.millsecondsToStr(0));
        rl_play_top = (LinearLayout) findViewById(R.id.rl_play_top);
        rl_full_screen_back = (RelativeLayout) findViewById(R.id.rl_full_screen_back);
        tv_full_screen_title = (TextView) findViewById(R.id.tv_full_screen_title);
        rl_play_bottom = (RelativeLayout) findViewById(R.id.rl_play_bottom);
        topbar = (LinearLayout) findViewById(R.id.topbar);
        ll_play_detail_bottom = (LinearLayout) findViewById(R.id.ll_play_detail_bottom);
        rl_click_middle = (RelativeLayout) findViewById(R.id.rl_click_middle);
        chooseDefinitionPopwindows = new ChooseDefinitionPopwindows(this);
        tv_definition.setText(getResources().getString(R.string.generalDefinition));

        loadData();
    }

    private void loadData() {
        // 根据id请求全部数据，防止内存泄漏
        obtainDataListerForLoadingVideoDetailInfo = new ObtainDataListerForLoadingVideoDetailInfo(this);
        SendRequest.getInterviewVideoDetail(currentVideoId, obtainDataListerForLoadingVideoDetailInfo);
    }

    @Override
    public boolean onError(IMediaPlayer mp, int what, int extra) {
        // 视频播放失败的错误信息
        Message msg = new Message();
        msg.what = what;
        if (alertHandler != null) {
            alertHandler.sendMessage(msg);
        }
        return false;
    }

    private static class ObtainDataListerForLoadingVideoDetailInfo extends ObtainDataFromNetListener<InterviewVideoBean, String> {

        private WeakReference<InterviewVideoDetailActivity> weak_activity;

        public ObtainDataListerForLoadingVideoDetailInfo(InterviewVideoDetailActivity activity) {
            weak_activity = new WeakReference<>(activity);
        }

        @Override
        public void onStart() {
            super.onStart();
            if (weak_activity.get() != null) {
                weak_activity.get().mCustomLoadingDialog.show();
            }
        }

        @Override
        public void onSuccess(InterviewVideoBean res) {
            if (weak_activity.get() != null) {
                weak_activity.get().mCustomLoadingDialog.dismiss();
                if (res != null) {
                    weak_activity.get().interviewVideoBean = res;
                    weak_activity.get().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            weak_activity.get().initData();
                        }
                    });
                }
            }
        }

        @Override
        public void onFailure(final String res) {
            if (weak_activity.get() != null) {
                weak_activity.get().mCustomLoadingDialog.dismiss();
                weak_activity.get().runOnUiThread(new Runnable() {
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

    private void initData() {
        tv_main_title.setText(interviewVideoBean.getTitle());
        tv_full_screen_title.setText(interviewVideoBean.getTitle());
        loadWebData(interviewVideoBean.getText());
        skbProgress.setOnSeekBarChangeListener(onSeekBarChangeListener);
        DebugUtil.e(TAG,interviewVideoBean.toString());
        initPlayHander();
        initPlayInfo();
    }

    private void initPlayHander() {
        playerHandler = new Handler() {
            public void handleMessage(Message msg) {
                if (player == null) {
                    return;
                }

                // 更新播放进度
                int position = (int)player.getCurrentPosition();
                int duration = (int)player.getDuration();

                if (duration > 0) {
                    long pos = skbProgress.getMax() * position / duration;
                    playDuration.setText(CommonUtils.millsecondsToStr((int)player.getCurrentPosition()));
                    skbProgress.setProgress((int) pos);
                }
            }
        };

        // 通过定时器和Handler来更新进度
        timerTask = new TimerTask() {
            @Override
            public void run() {
                if (!isPrepared) {
                    return;
                }
                playerHandler.sendEmptyMessage(0);
            }
        };
    }

    private void initPlayInfo() {
        if(timer == null){
            return;
        }
        timer.schedule(timerTask, 0, 1000);
        isPrepared = false;
        player.reset();
        player.setOnErrorListener(this);
        // 获取清晰度列表
        Map<String, Integer> definitions = player.getDefinitions();
        // 获取某种清晰度对应的状态码
        // 设置播放清晰度
        //测试数据
//        uid = @"4F21A251DAE61656";
//        keys = @"aLEa1ASWHT3SJEVP4btnirMgdcfOdQU4";
//        courses_id = @"943FD73F7AED8D039C33DC5901307461";
        try {
            player.setVideoPlayInfo(interviewVideoBean.getCourses_id(), interviewVideoBean.getUid(), interviewVideoBean.getApi_key(), this);
            player.setDefaultDefinition(definition);
            player.setDisplay(surfaceHolder);
            player.prepareAsync();
        } catch (IllegalArgumentException e) {
            DebugUtil.e("player error", e.getMessage());
        } catch (IllegalStateException e) {
            DebugUtil.e("player error", e + "");
        } /*catch (IOException e) {
            e.printStackTrace();
            DebugUtil.e("player error", e + "");
        }*/
    }

    @Override
    public void setListener() {
        rl_main_left.setOnClickListener(this);
        rl_full_screen_back.setOnClickListener(this);
        tv_detail_intro.setOnClickListener(this);
        tv_detail_text.setOnClickListener(this);
        tv_detail_plan.setOnClickListener(this);
        rl_play.setOnClickListener(this);
        iv_screen.setOnClickListener(this);
        rl_click_middle.setOnClickListener(this);
        tv_definition.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_main_left:
                // 竖屏的返回键
                back();
                break;
            case R.id.rl_full_screen_back:
                // 全屏时的返回键
                isFullScreen = false;
                iv_screen.setImageResource(R.drawable.icon_play_halfscreen);
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                tv_definition.setVisibility(View.GONE);
                break;
            case R.id.tv_detail_intro:
                // 简介
                reloadData(1);
                break;
            case R.id.tv_detail_text:
                // 课文
                reloadData(2);
                break;
            case R.id.tv_detail_plan:
                // 教案
                reloadData(3);
                break;
            case R.id.tv_definition:
                chooseDefinitionPopwindows.showPopUp(tv_definition, InterviewVideoDetailActivity.this);
                break;
            case R.id.rl_play:
                // 播放暂停按钮
                if (!isPrepared) {
                    return;
                }
                changePlayStatus();
                break;
            case R.id.iv_screen:
                if (isFullScreen) {// 是全屏 点击后取消全屏
                    isFullScreen = false;
                    iv_screen.setImageResource(R.drawable.icon_play_halfscreen);
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    tv_definition.setVisibility(View.GONE);
                } else {// 不是全屏  点击后设置成全屏
                    isFullScreen = true;
                    iv_screen.setImageResource(R.drawable.icon_play_fullscreen);
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                    tv_definition.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.rl_click_middle:
                // 点击空白区域，显示隐藏顶部标题和底部进度栏
                if (isDisplay) {
                    rl_play_top.setVisibility(View.GONE);
                    rl_play_bottom.setVisibility(View.GONE);
                    isDisplay = false;
                } else {
                    if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                        rl_play_top.setVisibility(View.GONE);
                    } else {
                        rl_play_top.setVisibility(View.VISIBLE);
                    }
                    rl_play_bottom.setVisibility(View.VISIBLE);
                    isDisplay = true;
                }
                break;
            case R.id.tv_general_definition:
//                player.setSpeed(1.0f);
                chooseDefination(DWIjkMediaPlayer.NORMAL_DEFINITION, getResources().getString(R.string.generalDefinition));
                break;
            case R.id.tv_hd_definition:
                chooseDefination(DWIjkMediaPlayer.HIGH_DEFINITION,getResources().getString(R.string.hdDefinition));
//                player.setSpeed(2.0f);
                break;
        }
    }

    /**
     * 选择清晰度
     * @param den
     * @param text
     */
    private void chooseDefination(int den,String text) {
        if(player != null){
            if(definition ==  den){
                chooseDefinitionPopwindows.dissmiss();
                return;
            }
            definition = den;
            initPlayHander();
            initPlayInfo();
            tv_definition.setText(text);
            chooseDefinitionPopwindows.dissmiss();
        }
    }

    private void changePlayStatus() {
        if (player.isPlaying()) {
            player.pause();
            iv_play.setImageResource(R.drawable.icon_play_pause);
        } else {
            player.start();
            iv_play.setImageResource(R.drawable.icon_play_play);
        }
    }

    private void reloadData(int index) {
        switch (index) {
            case 1:// 简介
                tv_detail_intro.setTextColor(getResources().getColor(R.color.black));
                tv_detail_text.setTextColor(getResources().getColor(R.color.black004));
                tv_detail_plan.setTextColor(getResources().getColor(R.color.black004));
                DebugUtil.i("interviewVideoDetail" + index, interviewVideoBean.getText());
                loadWebData(interviewVideoBean.getText());
                break;
            case 2:// 课文
                tv_detail_intro.setTextColor(getResources().getColor(R.color.black004));
                tv_detail_text.setTextColor(getResources().getColor(R.color.black));
                tv_detail_plan.setTextColor(getResources().getColor(R.color.black004));
                DebugUtil.i("interviewVideoDetail" + index, interviewVideoBean.getInfomation());
                loadWebData(interviewVideoBean.getInfomation());
                break;
            case 3:// 教案
                tv_detail_intro.setTextColor(getResources().getColor(R.color.black004));
                tv_detail_text.setTextColor(getResources().getColor(R.color.black004));
                tv_detail_plan.setTextColor(getResources().getColor(R.color.black));
                DebugUtil.i("interviewVideoDetail" + index, interviewVideoBean.getTeach_plan());
                loadWebData(interviewVideoBean.getTeach_plan());
                break;
        }
    }

    private void loadWebData(String webData) {
        webView.loadDataWithBaseURL(null, webData, "text/html", "UTF-8", null);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        // 锁屏再解锁后，显示视频播放的loading
        rl_deatil_waiting.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mWakeLock == null) {
            // 获取唤醒锁,保持屏幕常亮
            PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
            mWakeLock = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, CLASS_LABEL);
            if ((mWakeLock != null) && !mWakeLock.isHeld()) {
                mWakeLock.acquire();
            }
        }
        if (isFreeze) {
            isFreeze = false;
            if (isPrepared) {
                player.start();
            }
        } else {
            if (isPlaying != null && isPlaying && isPrepared) {
                player.start();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mWakeLock != null) {
            mWakeLock.release();
            mWakeLock = null;
        }
        if (isPrepared) {
            // 如果播放器prepare完成，则对播放器进行暂停操作，并记录状态
            if (player.isPlaying()) {
                isPlaying = true;
            } else {
                isPlaying = false;
            }
            player.pause();
        } else {
            // 如果播放器没有prepare完成，则设置isFreeze为true
            isFreeze = true;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != timer) {
            timer.cancel();
            timer = null;
        }
        if (null != timerTask) {
            timerTask.cancel();
            timerTask = null;
        }

        if (player != null) {
            player.reset();
            player.release();
            player = null;
        }

        alertHandler.removeCallbacksAndMessages(null);
        alertHandler = null;
        if (null != errorDialog) {
            errorDialog.dismiss();
        }

        mCustomLoadingDialog.dismiss();

        if (mWakeLock != null) {
            mWakeLock.release();
            mWakeLock = null;
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        try {
//            player.setAudioStreamType(AudioManager.STREAM_MUSIC);
//            player.setOnPreparedListener(this);
//            player.setDisplay(holder);
//            player.setScreenOnWhilePlaying(true);

            player.setOnPreparedListener(this);
            player.setDisplay(surfaceHolder);
            player.setScreenOnWhilePlaying(true);

            if (isSurfaceDestroy) {
                player.prepareAsync();
            }
        } catch (Exception e) {
            Log.e("videoPlayer", "error", e);
        }
        Log.i("videoPlayer", "surface created");
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        holder.setFixedSize(width, height);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if (player == null) {
            return;
        }
        if (isPrepared) {
            currentPosition = (int)player.getCurrentPosition();
        }

        isPrepared = false;
        isSurfaceDestroy = true;

        player.stop();
        player.reset();
    }

    @Override
    public void onPrepared(IMediaPlayer mp) {
        isPrepared = true;
        if (!isFreeze) {
            if (isPlaying == null || isPlaying) {
                player.start();
                iv_play.setImageResource(R.drawable.icon_play_play);
            }
        }
        rl_deatil_waiting.setVisibility(View.GONE);
        videoDuration.setText("/" + CommonUtils.millsecondsToStr((int)player.getDuration()));
        if (currentPosition > 0) {
            player.seekTo(currentPosition);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        if (newConfig.orientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE || newConfig.orientation == ActivityInfo.SCREEN_ORIENTATION_USER) {
            // 全屏
            rlVideoWidth = rl_video.getWidth();
            rlVideoheight = rl_video.getHeight();

            int screenWidth = CommonUtils.getScreenWidth();
            int screenHeight = CommonUtils.getScreenHeight();

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(screenWidth, screenHeight);
            rl_video.setLayoutParams(params);
            RelativeLayout.LayoutParams rParams = new RelativeLayout.LayoutParams(screenWidth, screenHeight);
            surfaceView.setLayoutParams(rParams);
            surfaceHolder.setFixedSize(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
            player.setDisplay(surfaceHolder);

            topbar.setVisibility(View.GONE);
            ll_play_detail_bottom.setVisibility(View.GONE);
        } else if (newConfig.orientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
            // 取消全屏后变竖屏
            try {
                ViewGroup.LayoutParams lp = rl_video.getLayoutParams();
                lp.width = rlVideoWidth;
                lp.height = rlVideoheight;
                rl_video.setLayoutParams(lp);

                surfaceHolder.setFixedSize(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                player.setDisplay(surfaceHolder);

                rl_play_top.setVisibility(View.GONE);
                topbar.setVisibility(View.VISIBLE);
                ll_play_detail_bottom.setVisibility(View.VISIBLE);
            } catch (Exception e) {
                e.printStackTrace();
                rl_deatil_waiting.setVisibility(View.VISIBLE);
                surfaceView.setVisibility(View.GONE);
                surfaceView.setVisibility(View.VISIBLE);

                player.prepareAsync();
                iv_play.setImageResource(R.drawable.icon_play_play);
            }
        }

        super.onConfigurationChanged(newConfig);
    }

    SeekBar.OnSeekBarChangeListener onSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        int progress = 0;

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            this.progress = progress * (int)player.getDuration() / seekBar.getMax();
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            player.seekTo(progress);
        }
    };

    private Handler alertHandler = new Handler() {
        AlertDialog.Builder builder;
        AlertDialog.OnClickListener onClickListener = new AlertDialog.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        };

        @Override
        public void handleMessage(Message msg) {
            String message = "";
            boolean isSystemError = false;
            if (ErrorCode.INVALID_REQUEST.Value() == msg.what) {
                message = getResources().getString(R.string.video_error_state);
            } else if (ErrorCode.NETWORK_ERROR.Value() == msg.what) {
                message = getResources().getString(R.string.video_error_network);
            } else if (ErrorCode.PROCESS_FAIL.Value() == msg.what) {
                message = getResources().getString(R.string.video_error_account);
            } else {
                isSystemError = true;
            }

            if (!isSystemError) {
                // 视频不能播放的时候，显示非系统错误的dialog
                builder = new AlertDialog.Builder(InterviewVideoDetailActivity.this);
                errorDialog = builder.setTitle("提示").setMessage(message)
                        .setPositiveButton("OK", onClickListener)
                        .setCancelable(false).show();
            }

            super.handleMessage(msg);
        }
    };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // 对返回键的处理，横屏时返回后变竖屏，竖屏时返回后退出
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
                isFullScreen = false;
                iv_screen.setImageResource(R.drawable.icon_play_halfscreen);
                tv_definition.setVisibility(View.GONE);
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            } else if (getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT || getRequestedOrientation() == ActivityInfo
                    .SCREEN_ORIENTATION_UNSPECIFIED) {
                finish();
            }
        }
        return false;
    }
}
