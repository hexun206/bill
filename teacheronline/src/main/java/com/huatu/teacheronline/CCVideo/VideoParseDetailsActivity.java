package com.huatu.teacheronline.CCVideo;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bokecc.sdk.mobile.exception.ErrorCode;
import com.bokecc.sdk.mobile.play.DWMediaPlayer;
import com.huatu.teacheronline.BaseActivity;
import com.huatu.teacheronline.R;
import com.huatu.teacheronline.utils.CommonUtils;
import com.huatu.teacheronline.utils.ToastUtils;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by zhxm on 2016/1/11.
 * 面试视频详情页
 */
public class VideoParseDetailsActivity extends BaseActivity implements SurfaceHolder.Callback, DWMediaPlayer.OnPreparedListener, DWMediaPlayer.OnErrorListener {
    private SurfaceView surfaceView;
    private SurfaceHolder surfaceHolder;
    private DWMediaPlayer player;
    private boolean isPrepared;
    private boolean isSurfaceDestroy = false;
    // 当player未准备好，并且当前activity经过onPause()生命周期时，此值为true
    private boolean isFreeze = true;
    private Boolean isPlaying;// 是否正在播放
    private Handler playerHandler;
    private Timer timer = new Timer();
    private TimerTask timerTask;
    private SeekBar skbProgress;// 播放进度
    private ImageView iv_play;
    private RelativeLayout rl_play;
    private RelativeLayout rl_deatil_waiting;
    private TextView videoDuration, playDuration;

    private final static String CLASS_LABEL = "InterviewVideoDetailActivity";
    private PowerManager.WakeLock mWakeLock;// 禁止锁屏
    private int currentPosition;
    private AlertDialog errorDialog;
    private LinearLayout rl_play_top;
    private RelativeLayout rl_full_screen_back;
    private TextView tv_full_screen_title;
    private RelativeLayout rl_play_bottom, rl_click_middle;
    private LinearLayout topbar;
    private boolean isDisplay = true;// 播放时的顶部返回和底部进度栏是否显示
    private String videoId, video_uid, video_key;

    @Override
    public void initView() {
        setContentView(R.layout.activity_videoparse);
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        mWakeLock = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, CLASS_LABEL);
        if ((mWakeLock != null) && !mWakeLock.isHeld()) {
            mWakeLock.acquire();
        }
        videoId = getIntent().getStringExtra("videoId");
        video_uid = getIntent().getStringExtra("video_uid");
        video_key = getIntent().getStringExtra("video_key");
        player = new DWMediaPlayer();

        surfaceView = (SurfaceView) findViewById(R.id.playerSurfaceView);
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS); //2.3及以下使用，不然出现只有声音没有图像的问题
        surfaceHolder.addCallback(this);
        skbProgress = (SeekBar) findViewById(R.id.skbProgress);
        iv_play = (ImageView) findViewById(R.id.iv_play);
        rl_play = (RelativeLayout) findViewById(R.id.rl_play);
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
        rl_click_middle = (RelativeLayout) findViewById(R.id.rl_click_middle);

        tv_full_screen_title.setText("错题解析");
        skbProgress.setOnSeekBarChangeListener(onSeekBarChangeListener);

        if (TextUtils.isEmpty(videoId) || TextUtils.isEmpty(video_uid) || TextUtils.isEmpty(video_key)) {
            ToastUtils.showToast("视频无法播放");
            return;
        }

        initPlayHander();
        initPlayInfo();
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        // 视频播放失败的错误信息
        Message msg = new Message();
        msg.what = what;
        if (alertHandler != null) {
            alertHandler.sendMessage(msg);
        }
        return false;
    }

    private void initPlayHander() {
        playerHandler = new Handler() {
            public void handleMessage(Message msg) {
                if (player == null) {
                    return;
                }

                // 更新播放进度
                int position = player.getCurrentPosition();
                int duration = player.getDuration();

                if (duration > 0) {
                    long pos = skbProgress.getMax() * position / duration;
                    playDuration.setText(CommonUtils.millsecondsToStr(player.getCurrentPosition()));
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
        timer.schedule(timerTask, 0, 1000);
        isPrepared = false;
        player.reset();
        player.setOnErrorListener(this);

        try {
            player.setVideoPlayInfo(videoId, video_uid, video_key, this);
            player.setDefaultDefinition(DWMediaPlayer.NORMAL_DEFINITION);
            player.prepareAsync();
        } catch (IllegalArgumentException e) {
            Log.e("player error", e.getMessage());
        } catch (IllegalStateException e) {
            Log.e("player error", e + "");
        }
    }

    @Override
    public void setListener() {
        rl_full_screen_back.setOnClickListener(this);
        rl_play.setOnClickListener(this);
        rl_click_middle.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_full_screen_back:
                // 全屏时的返回键
                back();
                break;
            case R.id.rl_play:
                // 播放暂停按钮
                if (!isPrepared) {
                    return;
                }
                changePlayStatus();
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

        if (mWakeLock != null) {
            mWakeLock.release();
            mWakeLock = null;
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        try {
            player.setAudioStreamType(AudioManager.STREAM_MUSIC);
            player.setOnPreparedListener(this);
            player.setDisplay(holder);
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
            currentPosition = player.getCurrentPosition();
        }

        isPrepared = false;
        isSurfaceDestroy = true;

        player.stop();
        player.reset();
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        isPrepared = true;
        if (!isFreeze) {
            if (isPlaying == null || isPlaying) {

                player.start();
                iv_play.setImageResource(R.drawable.icon_play_play);
            }
        }
        rl_deatil_waiting.setVisibility(View.GONE);
        videoDuration.setText("/" + CommonUtils.millsecondsToStr(player.getDuration()));
        if (currentPosition > 0) {
            player.seekTo(currentPosition);
        }
    }

    SeekBar.OnSeekBarChangeListener onSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        int progress = 0;

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            this.progress = progress * player.getDuration() / seekBar.getMax();
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
                builder = new AlertDialog.Builder(VideoParseDetailsActivity.this);
                errorDialog = builder.setTitle("提示").setMessage(message)
                        .setPositiveButton("OK", onClickListener)
                        .setCancelable(false).show();
            }

            super.handleMessage(msg);
        }
    };
}
