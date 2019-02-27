package com.huatu.teacheronline.direct;

import com.baijia.player.playback.mocklive.OnPlayerListener;
import com.baijiahulian.common.networkv2.HttpException;
import com.baijiahulian.player.BJPlayerView;
import com.baijiahulian.player.OnPlayerViewListener;
import com.baijiahulian.player.bean.SectionItem;
import com.baijiahulian.player.bean.VideoItem;

/**
 * Created by kinndann on 2018/8/17.
 * description:
 */
public abstract class PlayerListener implements OnPlayerViewListener,OnPlayerListener{


    /**
     * 回放的加载成功回调
     * @param bjPlayerView
     * @param l
     * @param e
     */
    @Override
    public void onVideoInfoInitialized(BJPlayerView bjPlayerView, long l, HttpException e) {

    }

    /**
     * 点播的加载成功回调
     * @param bjPlayerView
     * @param e
     */
    @Override
    public void onVideoInfoInitialized(BJPlayerView bjPlayerView, HttpException e) {

    }

    @Override
    public void onPause(BJPlayerView bjPlayerView) {

    }

    @Override
    public void onPlay(BJPlayerView bjPlayerView) {

    }

    @Override
    public void onError(BJPlayerView bjPlayerView, int i) {

    }

    @Override
    public void onUpdatePosition(BJPlayerView bjPlayerView, int i) {

    }

    @Override
    public void onSeekComplete(BJPlayerView bjPlayerView, int i) {

    }

    @Override
    public void onSpeedUp(BJPlayerView bjPlayerView, float v) {

    }

    @Override
    public void onVideoDefinition(BJPlayerView bjPlayerView, int i) {

    }

    @Override
    public void onPlayCompleted(BJPlayerView bjPlayerView, VideoItem videoItem, SectionItem sectionItem) {

    }

    @Override
    public void onVideoPrepared(BJPlayerView bjPlayerView) {

    }

    @Override
    public void onCaton(BJPlayerView bjPlayerView) {

    }

    @Override
    public String getVideoTokenWhenInvalid() {
        return null;
    }
}
