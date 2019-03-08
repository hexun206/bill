package com.huatu.teacheronline.widget.bjywidget;

import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;

import com.baijiayun.videoplayer.util.Utils;
import com.huatu.teacheronline.R;
import com.orhanobut.logger.Logger;

/**
 * Created by 79937 on 2018/1/29.
 */
public class BjyBottomViewPresenter  {//implements IPlayerBottomContact.BottomView
    private ImageView iv_screen;
    private ImageView iv_play;
    private QueryCopy $;
//    private IPlayerBottomContact.IPlayer mPlayer;

    private int mDuration = 0;
    private int mCurrentPosition = 0;
    private SeekBar mSeekBar;
    private boolean isSeekBarDraggable = true;


    public BjyBottomViewPresenter(View bottomView,View.OnClickListener speedOnClick) {
        $ = QueryCopy.with(bottomView);

        mSeekBar = (SeekBar) $.id(R.id.skbProgress).view();

        iv_screen = (ImageView)$.id(R.id.iv_screen).view();


        iv_play = (ImageView)$.id(R.id.iv_play).view();

        updateVideoProgress();

        $.id(R.id.iv_play).clicked(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (mPlayer != null) {
//                    if (mPlayer.isPlaying()) {
//                        mPlayer.pauseVideo();
//                    } else {
//                        mPlayer.playVideo();
//                    }
//                }
            }
        });

        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            boolean userTouch;

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(mDuration == 0||!fromUser){
                    return;
                }

                String durationText = Utils.formatDuration(mDuration);
                String positionText = Utils.formatDuration((int)((double)progress/100*mDuration), mDuration >= 3600);
                $.id(R.id.playDuration).text(positionText);
                $.id(R.id.videoDuration).text(durationText);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                userTouch = true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (userTouch) {
                    int pos = seekBar.getProgress() * mDuration / 100;
//                    mPlayer.seekVideo(pos);
                }
                userTouch = false;
            }
        });

        mSeekBar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return !isSeekBarDraggable;
            }
        });

//        $.id(R.id.iv_screen).clicked(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
////                if (mPlayer != null) {
////                    mPlayer.switchOrientation();
////                }
//            }
//        });
//        $.id(R.id.tv_speed).clicked(speedOnClick);
    }

//    @Override
//    public void onBind(IPlayerBottomContact.IPlayer player) {
//        mPlayer = player;
//        setOrientation(mPlayer.getOrientation());
//        setIsPlaying(mPlayer.isPlaying());
//    }
//
//    @Override
//    public void setDuration(int duration) {
//        mDuration = duration;
//        Logger.d("playback progress"+duration);
//        updateVideoProgress();
//    }
//
//    @Override
//    public void setCurrentPosition(int position) {
//        mCurrentPosition = position;
//        updateVideoProgress();
//    }
//
//    @Override
//    public void setIsPlaying(boolean isPlaying) {
//        if (isPlaying) {
//            $.id(R.id.iv_play)
//                    .image(com.baijiahulian.player.R.drawable.bjplayer_ic_pause);
//        } else {
//            $.id(R.id.iv_play)
//                    .image(com.baijiahulian.player.R.drawable.bjplayer_ic_play);
//        }
//
////        $.id(R.id.bjplayer_video_next_btn).enable(mPlayer.hasNext());
//    }
//
//    @Override
//    public void setOrientation(int orientation) {
//        if (orientation == BJPlayerView.VIDEO_ORIENTATION_PORTRAIT) {
////            $.id(R.id.bjplayer_current_pos_tx).gone();
////            $.id(R.id.bjplayer_duration_tx).gone();
////
////            $.id(R.id.bjplayer_current_pos_duration_tx).visible();
////            iv_screen.setBackgroundResource(R.drawable.ic_fullsc);
//        } else {
////            $.id(R.id.bjplayer_current_pos_tx).visible();
////            $.id(R.id.bjplayer_duration_tx).visible();
////
////            $.id(R.id.bjplayer_current_pos_duration_tx).gone();
////            iv_screen.setBackgroundResource(R.drawable.ic_fullsc_land);
//        }
//    }
//
//    @Override
//    public void onBufferingUpdate(int percent) {
//        // 只有 100ms 的 buf, ui 上根本看不出来
////        mSeekBar.setSecondaryProgress(mDuration == 0 ? 0 : mSeekBar.getProgress() + percent * 100 / mDuration);
//    }
//
//    @Override
//    public void setSeekBarDraggable(boolean canDrag) {
//        this.isSeekBarDraggable = canDrag;
//    }

    private void updateVideoProgress() {


        String durationText = Utils.formatDuration(mDuration);
        String positionText = Utils.formatDuration(mCurrentPosition, mDuration >= 3600);
        $.id(R.id.playDuration).text(positionText);
        $.id(R.id.videoDuration).text(durationText);
//        $.id(R.id.bjplayer_current_pos_duration_tx).text(String.format("%s/%s", positionText, durationText));

        mSeekBar.setProgress(mDuration == 0 ? 0 : mCurrentPosition * 100 / mDuration);
    }

    public void setVideoSpeedText(String text) {

    }

}
