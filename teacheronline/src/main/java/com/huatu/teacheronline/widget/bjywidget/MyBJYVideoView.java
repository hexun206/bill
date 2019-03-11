package com.huatu.teacheronline.widget.bjywidget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.os.Build.VERSION;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView.ScaleType;
import com.baijiayun.BJYPlayerSDK;
import com.baijiayun.constant.VideoDefinition;
import com.baijiayun.glide.Glide;
import com.baijiayun.videoplayer.IBJYVideoPlayer;
import com.baijiayun.videoplayer.event.BundlePool;
import com.baijiayun.videoplayer.listeners.OnBufferedUpdateListener;
import com.baijiayun.videoplayer.listeners.OnBufferingListener;
import com.baijiayun.videoplayer.listeners.OnPlayerErrorListener;
import com.baijiayun.videoplayer.listeners.OnPlayerStatusChangeListener;
import com.baijiayun.videoplayer.listeners.OnPlayingTimeChangeListener;
import com.baijiayun.videoplayer.log.BJLog;
import com.baijiayun.videoplayer.player.PlayerStatus;
import com.baijiayun.videoplayer.render.AspectRatio;
import com.baijiayun.videoplayer.ui.R.styleable;
import com.baijiayun.videoplayer.ui.component.ComponentManager;
import com.baijiayun.videoplayer.ui.event.UIEventKey;
import com.baijiayun.videoplayer.ui.utils.NetworkUtils;
import com.baijiayun.videoplayer.ui.widget.BJYVideoView;
import com.baijiayun.videoplayer.ui.widget.BaseVideoView;
import com.baijiayun.videoplayer.ui.widget.ComponentContainer;
import com.baijiayun.videoplayer.widget.BJYPlayerView;

public class MyBJYVideoView extends BaseVideoView {
    private static final String TAG = "BJYVideoView";
    private BJYPlayerView bjyPlayerView;
    private long videoId;
    private String token;
    private boolean encrypted;
    private ImageView audioCoverIv;
    private int mAspectRatio;
    private int mRenderType;

    public MyBJYVideoView(@NonNull Context context) {
        this(context, (AttributeSet)null);
    }

    public MyBJYVideoView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MyBJYVideoView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mAspectRatio = AspectRatio.AspectRatio_16_9.ordinal();
        this.mRenderType = 1;
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, styleable.BJVideoView, 0, 0);
        if (a.hasValue(styleable.BJVideoView_aspect_ratio)) {
            this.mAspectRatio = a.getInt(styleable.BJVideoView_aspect_ratio, AspectRatio.AspectRatio_16_9.ordinal());
        }

        if (a.hasValue(styleable.BJVideoView_render_type)) {
            this.mRenderType = a.getInt(styleable.BJVideoView_render_type, 1);
            if (VERSION.SDK_INT < 21) {
                this.mRenderType = 1;
            }
        }

        a.recycle();
    }

    protected void init(Context context, AttributeSet attrs, int defStyleAttr) {
        this.bjyPlayerView = new BJYPlayerView(context);
        this.addView(this.bjyPlayerView);
        this.audioCoverIv = new ImageView(context);
        this.audioCoverIv.setScaleType(ImageView.ScaleType.FIT_XY);
        this.audioCoverIv.setVisibility(View.GONE);
        this.audioCoverIv.setLayoutParams(new FrameLayout.LayoutParams(-1, -1));
        this.addView(this.audioCoverIv);
    }

    public void initPlayer(IBJYVideoPlayer videoPlayer, boolean shouldRenderCustomComponent) {
        this.bjyVideoPlayer = videoPlayer;
        this.bjyVideoPlayer.bindPlayerView(this.bjyPlayerView);
        this.bjyPlayerView.setAspectRatio(AspectRatio.values()[this.mAspectRatio]);
        this.bjyPlayerView.setRenderType(this.mRenderType);
        if (shouldRenderCustomComponent) {
            this.initComponentContainer();
            this.bjyVideoPlayer.addOnPlayerErrorListener((error) -> {
                Bundle bundle = BundlePool.obtain();
                bundle.putString("string_data", error.getMessage());
                this.componentContainer.dispatchErrorEvent(error.getCode(), bundle);
            });
            this.bjyVideoPlayer.addOnPlayingTimeChangeListener((currentTime, duration) -> {
                Bundle bundle = BundlePool.obtainPrivate("controller_component", currentTime);
                this.componentContainer.dispatchPlayEvent(-99019, bundle);
            });
            this.bjyVideoPlayer.addOnBufferUpdateListener((bufferedPercentage) -> {
                Bundle bundle = BundlePool.obtainPrivate("controller_component", bufferedPercentage);
                this.componentContainer.dispatchPlayEvent(-99012, bundle);
            });
            this.bjyVideoPlayer.addOnBufferingListener(new OnBufferingListener() {
                public void onBufferingStart() {
                    BJLog.d("bjy", "onBufferingStart invoke");
                    MyBJYVideoView.this.componentContainer.dispatchPlayEvent(-80010, (Bundle)null);
                }

                public void onBufferingEnd() {
                    BJLog.d("bjy", "onBufferingEnd invoke");
                    MyBJYVideoView.this.componentContainer.dispatchPlayEvent(-80011, (Bundle)null);
                }
            });
        } else {
            this.useDefaultNetworkListener = false;
        }

        this.bjyVideoPlayer.addOnPlayerStatusChangeListener((status) -> {
            if (status == PlayerStatus.STATE_PREPARED) {
                this.updateAudioCoverStatus(this.bjyVideoPlayer.getVideoInfo() != null && this.bjyVideoPlayer.getVideoInfo().getDefinition() == VideoDefinition.Audio);
            }

            if (this.componentContainer != null) {
                Bundle bundle = BundlePool.obtain(status);
                this.componentContainer.dispatchPlayEvent(-99031, bundle);
            }

        });
    }

    public void initPlayer(IBJYVideoPlayer videoPlayer) {
        this.initPlayer(videoPlayer, true);
    }

    private void initComponentContainer() {
        ComponentManager componentManager = new ComponentManager(this.getContext());
        componentManager.removeComponent(UIEventKey.KEY_CONTROLLER_COMPONENT);
        componentManager.addComponent(UIEventKey.KEY_CONTROLLER_COMPONENT,new MyControllerComponent(this.getContext()));
        this.componentContainer = new ComponentContainer(this.getContext());
        this.componentContainer.init(this,componentManager );
        this.componentContainer.setOnComponentEventListener(this.internalComponentEventListener);
        this.addView(this.componentContainer, new android.view.ViewGroup.LayoutParams(-1, -1));
    }

    protected void requestPlayAction() {
        super.requestPlayAction();
        if (this.getVideoInfo() != null && this.getVideoInfo().getVideoId() != 0L) {
            this.play();
        } else {
            this.setupOnlineVideoWithId(this.videoId, this.token, this.encrypted);
            this.sendCustomEvent(-80017, (Bundle)null);
        }

    }

    public void setupOnlineVideoWithId(long videoId, String token) {
        this.setupOnlineVideoWithId(videoId, token, true);
    }

    public void setupOnlineVideoWithId(long videoId, String token, boolean encrypted) {
        this.videoId = videoId;
        this.token = token;
        this.encrypted = encrypted;
        if (this.useDefaultNetworkListener) {
            this.registerNetChangeReceiver();
        }

        if (!this.enablePlayWithMobileNetwork && NetworkUtils.isMobile(NetworkUtils.getNetworkState(this.getContext()))) {
            this.sendCustomEvent(-80012, (Bundle)null);
        } else {
            this.bjyVideoPlayer.setupOnlineVideoWithId(videoId, token);
        }

    }

    public void setupLocalVideoWithFilePath(String path) {
        this.bjyVideoPlayer.setupLocalVideoWithFilePath(path);
    }

    private void updateAudioCoverStatus(boolean isAudio) {
        if (isAudio) {
            this.audioCoverIv.setVisibility(View.VISIBLE);
            Glide.with(this).load(BJYPlayerSDK.AUDIO_ON_PICTURE).into(this.audioCoverIv);
        } else {
            this.audioCoverIv.setVisibility(View.GONE);
        }

    }
}
