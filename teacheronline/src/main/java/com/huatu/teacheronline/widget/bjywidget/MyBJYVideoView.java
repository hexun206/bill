package com.huatu.teacheronline.widget.bjywidget;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.os.Bundle;
import android.os.Build.VERSION;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.baijiayun.BJYPlayerSDK;
import com.baijiayun.constant.VideoDefinition;
import com.baijiayun.glide.Glide;
import com.baijiayun.videoplayer.IBJYVideoPlayer;
import com.baijiayun.videoplayer.event.BundlePool;
import com.baijiayun.videoplayer.event.EventKey;
import com.baijiayun.videoplayer.event.OnPlayerEventListener;
import com.baijiayun.videoplayer.listeners.OnBufferingListener;
import com.baijiayun.videoplayer.log.BJLog;
import com.baijiayun.videoplayer.player.PlayerStatus;
import com.baijiayun.videoplayer.render.AspectRatio;
import com.baijiayun.videoplayer.render.IRender;
import com.baijiayun.videoplayer.ui.R.styleable;
import com.baijiayun.videoplayer.ui.component.ComponentManager;
import com.baijiayun.videoplayer.ui.event.UIEventKey;
import com.baijiayun.videoplayer.ui.utils.NetworkUtils;
import com.baijiayun.videoplayer.ui.widget.BaseVideoView;
import com.baijiayun.videoplayer.ui.widget.ComponentContainer;
import com.baijiayun.videoplayer.widget.BJYPlayerView;
import com.huatu.teacheronline.R;

public class MyBJYVideoView extends BaseVideoView {

    public static final int CUSTOM_CODE_TIMETABLE = -70001;//时间表课表按钮
    public static final int CUSTOM_CODE_WITHDRAW = -70002;//听课返现
    public static final int CUSTOM_CODE_SWITCH = -70003;//是否连续播放
    public static final int CUSTOM_CODE_DEFINITION = -70004;//清晰度
    public static final int CUSTOM_CODE_SPEED = -70005;//播放倍率
    public static final int CUSTOM_CODE_DOWN = -70006;//下载
    private static final String TAG = "BJYVideoView";
    private BJYPlayerView bjyPlayerView;
    private long videoId;
    private String token;
    private boolean encrypted;
    private ImageView audioCoverIv;
    private int mAspectRatio;
    private int mRenderType;
    public MyControllerComponent controllerComponent;

    public MyBJYVideoView(@NonNull Context context) {
        this(context, (AttributeSet) null);
    }

    public MyBJYVideoView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MyBJYVideoView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.BJVideoView, 0, 0);
        if (a.hasValue(R.styleable.BJVideoView_aspect_ratio)) {
            mAspectRatio = a.getInt(R.styleable.BJVideoView_aspect_ratio, AspectRatio.AspectRatio_16_9.ordinal());
        }
        if (a.hasValue(R.styleable.BJVideoView_render_type)) {
            mRenderType = a.getInt(R.styleable.BJVideoView_render_type, IRender.RENDER_TYPE_SURFACE_VIEW);
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                mRenderType = IRender.RENDER_TYPE_SURFACE_VIEW;
            }
        }
        a.recycle();
    }


    @Override
    protected void init(Context context, AttributeSet attrs, int defStyleAttr) {
        bjyPlayerView = new BJYPlayerView(context);
        addView(bjyPlayerView);

        audioCoverIv = new ImageView(context);
        audioCoverIv.setScaleType(ImageView.ScaleType.FIT_XY);
        audioCoverIv.setVisibility(View.GONE);
        audioCoverIv.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        addView(audioCoverIv);
    }

    public void initPlayer(IBJYVideoPlayer videoPlayer, boolean shouldRenderCustomComponent) {
        bjyVideoPlayer = videoPlayer;
        bjyVideoPlayer.bindPlayerView(bjyPlayerView);

        //初始化videoplayer之后才能设置宽高比
        bjyPlayerView.setAspectRatio(AspectRatio.values()[mAspectRatio]);
        bjyPlayerView.setRenderType(mRenderType);

        if (shouldRenderCustomComponent) {
            initComponentContainer();

            bjyVideoPlayer.addOnPlayerErrorListener(error -> {
                Bundle bundle = BundlePool.obtain();
                bundle.putString(EventKey.STRING_DATA, error.getMessage());
                componentContainer.dispatchErrorEvent(error.getCode(), bundle);
            });

            bjyVideoPlayer.addOnPlayingTimeChangeListener((currentTime, duration) -> {
                //只通知到controller component
                Bundle bundle = BundlePool.obtainPrivate(UIEventKey.KEY_CONTROLLER_COMPONENT, currentTime);
                componentContainer.dispatchPlayEvent(OnPlayerEventListener.PLAYER_EVENT_ON_TIMER_UPDATE, bundle);
            });

            bjyVideoPlayer.addOnBufferUpdateListener(bufferedPercentage -> {
                //只通知到controller component
                Bundle bundle = BundlePool.obtainPrivate(UIEventKey.KEY_CONTROLLER_COMPONENT, bufferedPercentage);
                componentContainer.dispatchPlayEvent(OnPlayerEventListener.PLAYER_EVENT_ON_BUFFERING_UPDATE, bundle);
            });

            bjyVideoPlayer.addOnBufferingListener(new OnBufferingListener() {
                @Override
                public void onBufferingStart() {
                    BJLog.d("bjy", "onBufferingStart invoke");
                    componentContainer.dispatchPlayEvent(UIEventKey.PLAYER_CODE_BUFFERING_START, null);
                }

                @Override
                public void onBufferingEnd() {
                    BJLog.d("bjy", "onBufferingEnd invoke");
                    componentContainer.dispatchPlayEvent(UIEventKey.PLAYER_CODE_BUFFERING_END, null);
                }
            });
        } else {
            //回放模式下不监听网络
            useDefaultNetworkListener = false;
        }

        bjyVideoPlayer.addOnPlayerStatusChangeListener(status -> {
            if (status == PlayerStatus.STATE_PREPARED) {
                updateAudioCoverStatus(bjyVideoPlayer.getVideoInfo() != null && bjyVideoPlayer.getVideoInfo().getDefinition() == VideoDefinition.Audio);
            }
            if (componentContainer != null) {
                Bundle bundle = BundlePool.obtain(status);
                componentContainer.dispatchPlayEvent(OnPlayerEventListener.PLAYER_EVENT_ON_STATUS_CHANGE, bundle);
            }
        });
    }

    public void initPlayer(IBJYVideoPlayer videoPlayer) {
        this.initPlayer(videoPlayer, true);
    }

    private void initComponentContainer() {
        controllerComponent = new MyControllerComponent(this.getContext());
        ComponentManager componentManager = new ComponentManager(this.getContext());
        componentManager.removeComponent(UIEventKey.KEY_CONTROLLER_COMPONENT);
        componentManager.addComponent(UIEventKey.KEY_CONTROLLER_COMPONENT, controllerComponent);
        this.componentContainer = new ComponentContainer(this.getContext());
        this.componentContainer.init(this, componentManager);
        this.componentContainer.setOnComponentEventListener(this.internalComponentEventListener);
        this.addView(this.componentContainer, new android.view.ViewGroup.LayoutParams(-1, -1));
    }

    @Override
    protected void requestPlayAction() {
        super.requestPlayAction();
        //视频未初始化成功则请求视频地址
        if (!bjyVideoPlayer.isPlayLocalVideo() && (getVideoInfo() == null || getVideoInfo().getVideoId() == 0)) {
            setupOnlineVideoWithId(videoId, token, encrypted);
            sendCustomEvent(UIEventKey.CUSTOM_CODE_REQUEST_VIDEO_INFO, null);
        } else {
            play();
        }
    }

    /**
     * 设置播放百家云在线视频
     *
     * @param videoId 视频id
     * @param token   需要集成方后端调用百家云后端的API获取
     */
    public void setupOnlineVideoWithId(long videoId, String token) {
        setupOnlineVideoWithId(videoId, token, true);
    }

    /**
     * 设置播放百家云在线视频
     *
     * @param videoId   视频id
     * @param token     需要集成方后端调用百家云后端的API获取
     * @param encrypted 是否加密
     */
    public void setupOnlineVideoWithId(long videoId, String token, boolean encrypted) {
        this.videoId = videoId;
        this.token = token;
        this.encrypted = encrypted;
        if (useDefaultNetworkListener) {
            registerNetChangeReceiver();
        }
        if (!enablePlayWithMobileNetwork && NetworkUtils.isMobile(NetworkUtils.getNetworkState(getContext()))) {
            sendCustomEvent(UIEventKey.CUSTOM_CODE_NETWORK_CHANGE_TO_MOBILE, null);
        } else {
            bjyVideoPlayer.setupOnlineVideoWithId(videoId, token);
        }
    }

    /**
     * 设置播放本地文件路径
     *
     * @param path 视频文件绝对路径
     */
    public void setupLocalVideoWithFilePath(String path) {
        bjyVideoPlayer.setupLocalVideoWithFilePath(path);
    }


    /**
     * 更新纯音频占位图状态
     */
    private void updateAudioCoverStatus(boolean isAudio) {
        if (isAudio) {
            audioCoverIv.setVisibility(View.VISIBLE);
            Glide.with(this)
                    .load(BJYPlayerSDK.AUDIO_ON_PICTURE)
                    .into(audioCoverIv);
        } else {
            audioCoverIv.setVisibility(View.GONE);
        }
    }
}