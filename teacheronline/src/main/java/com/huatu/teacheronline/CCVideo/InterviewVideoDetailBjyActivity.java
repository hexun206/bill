package com.huatu.teacheronline.CCVideo;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.PowerManager;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baijiahulian.common.networkv2.HttpException;
import com.baijiayun.videoplayer.VideoPlayerFactory;
import com.baijiayun.videoplayer.bean.SectionItem;
import com.baijiayun.videoplayer.bean.VideoItem;
import com.baijiayun.videoplayer.ui.event.UIEventKey;
import com.baijiayun.videoplayer.util.Utils;
import com.huatu.teacheronline.BaseActivity;
import com.huatu.teacheronline.CustomApplication;
import com.huatu.teacheronline.R;
import com.huatu.teacheronline.engine.ObtainDataFromNetListener;
import com.huatu.teacheronline.engine.SendRequest;
import com.huatu.teacheronline.utils.CommonUtils;
import com.huatu.teacheronline.utils.DebugUtil;
import com.huatu.teacheronline.utils.ToastUtils;
import com.huatu.teacheronline.widget.ChooseSpeedPopwindows;
import com.huatu.teacheronline.widget.CustomAlertDialog;
import com.huatu.teacheronline.widget.bjywidget.BjyBottomViewPresenter;
import com.huatu.teacheronline.widget.bjywidget.BjyTopViewPresenter;
import com.huatu.teacheronline.widget.bjywidget.MyBJYVideoView;

import java.lang.ref.WeakReference;

/**
 * Created by zhxm on 2016/1/11.
 * 面试视频详情页
 */
public class InterviewVideoDetailBjyActivity extends BaseActivity  {//implements OnPlayerViewListener

    private static final String TAG = "InterviewVideoDetailBjyActivity";
    private RelativeLayout rl_main_left;
    private TextView tv_main_title, tv_detail_intro, tv_detail_text, tv_detail_plan;
    private WebView webView;
    private String currentVideoId;
    private InterviewVideoBean interviewVideoBean;
    private CustomAlertDialog mCustomLoadingDialog;

    private final static String CLASS_LABEL = "InterviewVideoDetailActivity";
    private PowerManager.WakeLock mWakeLock;// 禁止锁屏
    private ObtainDataListerForLoadingVideoDetailInfo obtainDataListerForLoadingVideoDetailInfo;
    private MyBJYVideoView player;
    private LinearLayout topbar;
    //    private ImageView ib_main_right_player;
//    private RelativeLayout rl_main_left_player;
    private String bjyid;//百家云视频id
    private String token;//百家云视频token
    private int rlVideoWidth;
    private int rlVideoheight;
    private RelativeLayout rl_video;
    int screenWidth = CommonUtils.getScreenWidth();
    int screenHeight = CommonUtils.getScreenHeight();
    private BjyTopViewPresenter bjyTopViewPresenter;
    //    private RelativeLayout rl_main_left_player;
    private ChooseSpeedPopwindows chooseSpeedPopwindows;//倍速播放弹出框
    private TextView tv_speed;//倍速播放
    private int speedType = 0;
    private BjyBottomViewPresenter mBjyBottomViewPresenter;

    private ImageView img_bottom_screen;

    @SuppressLint("InvalidWakeLockTag")
    @Override
    public void initView() {
        setContentView(R.layout.activity_interview_video_detail_bjy);
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        mWakeLock = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, CLASS_LABEL);
        if ((mWakeLock != null) && !mWakeLock.isHeld()) {
            mWakeLock.acquire();
        }

//         findViewById(R.id.rl_play_bottom).setOnTouchListener(new View.OnTouchListener() {
//             @Override
//             public boolean onTouch(View v, MotionEvent event) {
//                 return true;
//             }
//         });

//        tv_speed = (TextView) findViewById(R.id.tv_speed);
//        tv_speed.setOnClickListener(this);
        currentVideoId = getIntent().getStringExtra("video_id");
        bjyid = getIntent().getStringExtra("bjyid");
        token = getIntent().getStringExtra("token");
        mCustomLoadingDialog = new CustomAlertDialog(InterviewVideoDetailBjyActivity.this, R.layout.dialog_loading_custom);
        topbar = (LinearLayout) findViewById(R.id.topbar);
        rl_main_left = (RelativeLayout) findViewById(R.id.rl_main_left);
        rl_video = (RelativeLayout) findViewById(R.id.rl_video);
        tv_main_title = (TextView) findViewById(R.id.tv_main_title);
        tv_detail_intro = (TextView) findViewById(R.id.tv_detail_intro);
        tv_detail_text = (TextView) findViewById(R.id.tv_detail_text);
        tv_detail_plan = (TextView) findViewById(R.id.tv_detail_plan);
        webView = (WebView) findViewById(R.id.webView);
//        ib_main_right_player = (ImageView) findViewById(R.id.ib_main_right_player);
//        rl_main_left_player = (RelativeLayout) findViewById(R.id.rl_main_left_player);
        loadData();
//        ib_main_right_player.setVisibility(View.GONE);
//        rl_main_left_player.setVisibility(View.GONE);
        player = (MyBJYVideoView) findViewById(R.id.videoView);
        player.initPlayer(new VideoPlayerFactory.Builder()
                //后台暂停播放
                .setSupportBackgroundAudio(false)
                //开启循环播放
                .setSupportLooping(true)
                //开启记忆播放
                .setSupportBreakPointPlay(true, this)
                //绑定activity生命周期
                .setLifecycle(getLifecycle()).build());
        player.setComponentEventListener((eventCode, bundle) -> {
            switch (eventCode) {
//                case UIEventKey.CUSTOM_CODE_REQUEST_BACK://播放器返回按钮 已经自定义隐藏
//                    if (isLandscape) {
//                        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//                    } else {
//                        finish();
//                    }
//                    break;
                case UIEventKey.CUSTOM_CODE_REQUEST_TOGGLE_SCREEN://播放器全屏按钮
                    setRequestedOrientation(isLandscape ?
                            ActivityInfo.SCREEN_ORIENTATION_PORTRAIT :
                            ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                    break;
                default:
                    break;
            }
        });
        requestLayout(false);
        //以下三个方法分别设置底部、顶部和中部界面
//        mBjyBottomViewPresenter = new BjyBottomViewPresenter(player.getBottomView(), this);
//        player.setBottomPresenter(mBjyBottomViewPresenter);
//        bjyTopViewPresenter = new BjyTopViewPresenter(player.getTopView());
//        player.setTopPresenter(bjyTopViewPresenter);
//        player.setCenterPresenter(new BJCenterViewPresenter(player.getCenterView()));
//        //初始化partnerId，第一个参数换成您的partnerId
//        player.initPartner(CustomApplication.BJPlayerView_partnerId, BJPlayerView.PLAYER_DEPLOY_ONLINE);
        chooseSpeedPopwindows = new ChooseSpeedPopwindows(this);

//        img_bottom_screen = (ImageView) findViewById(R.id.iv_screen);
//        img_bottom_screen.setOnClickListener(this);
    }

    private void loadData() {
        // 根据id请求全部数据，防止内存泄漏
        obtainDataListerForLoadingVideoDetailInfo = new ObtainDataListerForLoadingVideoDetailInfo(this);
        SendRequest.getInterviewVideoDetail(currentVideoId, obtainDataListerForLoadingVideoDetailInfo);
    }

//    @Override
//    public void onVideoInfoInitialized(BJPlayerView bjPlayerView, HttpException exception) {
//        DebugUtil.e(TAG, "onVideoInfoInitialized:");
//        //TODO: 视频信息初始化结束
//        if (exception != null) {
//            // 视频信息初始化成功
//            VideoItem videoItem = player.getVideoItem();
//        }
//    }
//
//    @Override
//    public void onPause(BJPlayerView bjPlayerView) {
//        //TODO: video暂停
//        DebugUtil.e(TAG, "onPause:");
//    }
//
//    @Override
//    public void onPlay(BJPlayerView bjPlayerView) {
//        //TODO: 开始播放
//        DebugUtil.e(TAG, "onPlay:");
//    }
//
//    @Override
//    public void onError(BJPlayerView bjPlayerView, int i) {
//        //TODO: 播放出错
//        DebugUtil.e(TAG, "onError:" + " onError:" + i);
//    }
//
//    @Override
//    public void onUpdatePosition(BJPlayerView bjPlayerView, int i) {
//        //TODO: 播放过程中更新播放位置
//        DebugUtil.e(TAG, "onUpdatePosition:" + i);
//    }
//
//    @Override
//    public void onSeekComplete(BJPlayerView bjPlayerView, int i) {
//        //TODO: 拖动进度条
//        DebugUtil.e(TAG, "onSeekComplete:" + i);
//    }
//
//    @Override
//    public void onSpeedUp(BJPlayerView bjPlayerView, float v) {
//        //TODO: 设置倍速播放
//        DebugUtil.e(TAG, "onSpeedUp:" + v);
//    }
//
//    @Override
//    public void onVideoDefinition(BJPlayerView bjPlayerView, int i) {
//        //TODO: 设置清晰度完成
//        DebugUtil.e(TAG, "onVideoDefinition:" + i);
//    }
//
//    @Override
//    public void onPlayCompleted(BJPlayerView bjPlayerView, VideoItem videoItem, SectionItem nextSection) {
//        //TODO: 当前视频播放完成 [nextSection已被废弃，请勿使用]
//        DebugUtil.e(TAG, "onPlayCompleted:" + videoItem.videoId);
//    }
//
//    @Override
//    public void onVideoPrepared(BJPlayerView bjPlayerView) {
//        DebugUtil.e(TAG, "onVideoPrepared" + bjPlayerView.getVideoItem().videoId);
//        //TODO: 准备好了，马上要播放
//        // 可以在这时获取视频时长
//        player.getDuration();
//    }
//
//    @Override
//    public void onCaton(BJPlayerView bjPlayerView) {
//
//    }
//
//    @Override
//    public String getVideoTokenWhenInvalid() {
//        return null;
//    }

    private static class ObtainDataListerForLoadingVideoDetailInfo extends ObtainDataFromNetListener<InterviewVideoBean, String> {

        private WeakReference<InterviewVideoDetailBjyActivity> weak_activity;

        public ObtainDataListerForLoadingVideoDetailInfo(InterviewVideoDetailBjyActivity activity) {
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
        loadWebData(interviewVideoBean.getText());
        DebugUtil.e(TAG, interviewVideoBean.toString());
        initPlayInfo();
    }

    private void initPlayInfo() {
        try {
            //第一个参数为百家云后台配置的视频id，第二个参数为视频token
            player.setupOnlineVideoWithId(Long.parseLong(bjyid), token);
            DebugUtil.e(TAG, "initPlayInfo: " + bjyid + "  token" + token);
            //播放
            player.play(0);
//            player.setOnPlayerViewListener(this);
        } catch (IllegalArgumentException e) {
            DebugUtil.e("player error", e.getMessage());
        } catch (IllegalStateException e) {
            DebugUtil.e("player error", e + "");
        } /*catch (IOException e) {
            e.printStackTrace();
            DebugUtil.e("player error", e + "");
        }*/
        rlVideoWidth = rl_video.getWidth();
        rlVideoheight = rl_video.getHeight();
    }

    @Override
    public void setListener() {
        rl_main_left.setOnClickListener(this);
        tv_detail_intro.setOnClickListener(this);
        tv_detail_text.setOnClickListener(this);
        tv_detail_plan.setOnClickListener(this);
//        rl_main_left_player.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_main_left:
                // 竖屏的返回键
                back();
                break;

            case R.id.iv_screen:
                if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//竖屏
                } else {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);//横屏
                }
                break;
            case R.id.rl_main_left_player:
                // 全屏的返回键
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                break;
            case R.id.rl_full_screen_back:
                // 全屏时的返回键
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
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
            case R.id.tv_speed:
                chooseSpeedPopwindows.showInterViewPopUp(v, this, speedType);
                break;
            case R.id.tv_speed_original:
                speedType = 0;
//                chooseSpeedPopwindows.setClickViewGone(v);
                chooseSpeedPopwindows.dissmiss();
                tv_speed.setText(getString(R.string.speed_original));
                if (player != null) {
                    player.setPlayRate(1.0f);
                }
                break;
            case R.id.tv_speed_quick1:
                speedType = 1;
//                chooseSpeedPopwindows.setClickViewGone(v);
                chooseSpeedPopwindows.dissmiss();
                tv_speed.setText(getString(R.string.speed_quick1));
                if (player != null) {
                    player.setPlayRate(1.25f);
                }
                break;
            case R.id.tv_speed_quick2:
                speedType = 2;
//                chooseSpeedPopwindows.setClickViewGone(v);
                chooseSpeedPopwindows.dissmiss();
                tv_speed.setText(getString(R.string.speed_quick2));
                if (player != null) {
                    player.setPlayRate(1.5f);
                }
                break;
            case R.id.tv_speed_quick3:
                speedType = 3;
//                chooseSpeedPopwindows.setClickViewGone(v);
                chooseSpeedPopwindows.dissmiss();
                tv_speed.setText(getString(R.string.speed_quick3));
                if (player != null) {
                    player.setPlayRate(2.0f);
                }
                break;
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
    }

    @Override
    protected void onResume() {
        super.onResume();
//        if (player != null) {
//            player.onResume();
//        }
    }

    @Override
    protected void onPause() {
        super.onPause();
//        if (player != null) {
//            player.onPause();
//        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        if (player != null) {
//            player.onDestroy();
//        }
    }

    @Override
    protected void requestLayout(boolean isLandscape) {
        super.requestLayout(isLandscape);
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) player.getLayoutParams();
        if (isLandscape) {
//            img_bottom_screen.setImageResource(R.drawable.ic_fullsc_land);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);//隐藏状态栏
            topbar.setVisibility(View.GONE);// 全屏
            layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
            layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
        } else {
//            img_bottom_screen.setImageResource(R.drawable.ic_fullsc);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);//显示状态栏
            topbar.setVisibility(View.VISIBLE);
            layoutParams.width = Utils.getScreenWidthPixels(this);
            layoutParams.height = layoutParams.width * 9 / 16;
        }
        player.setLayoutParams(layoutParams);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // 对返回键的处理，横屏时返回后变竖屏，竖屏时返回后退出
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            } else if (getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT || getRequestedOrientation() == ActivityInfo
                    .SCREEN_ORIENTATION_UNSPECIFIED) {
                finish();
            }
        }
        return false;
    }
}
