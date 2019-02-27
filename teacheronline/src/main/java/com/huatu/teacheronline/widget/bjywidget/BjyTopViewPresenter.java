package com.huatu.teacheronline.widget.bjywidget;

import android.view.View;

import com.baijiahulian.player.BJPlayerView;
import com.baijiahulian.player.playerview.IPlayerTopContact;
import com.huatu.teacheronline.R;

/**
 * Created by 79937 on 2018/1/29.
 */
public class BjyTopViewPresenter implements IPlayerTopContact.TopView {
    private QueryCopy $;
    private IPlayerTopContact.IPlayer mPlayer;

    public BjyTopViewPresenter(View topView) {
        $ = QueryCopy.with(topView);

        $.id(R.id.rl_main_left_player).clicked(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPlayer != null) {
                    mPlayer.onBackPressed();
                }
            }
        });
        $.id(R.id.rl_main_left_player).gone();
    }

    public void setBjyTopViewVisible(int V){
        if (V == View.VISIBLE) {
            $.visible();
        }else {
            $.gone();
        }
    }

    @Override
    public void onBind(IPlayerTopContact.IPlayer player) {
        mPlayer = player;
        setOrientation(mPlayer.getOrientation());
    }

    @Override
    public void setTitle(String title) {
//        $.id(R.id.bjplayer_top_title_tv).text(title);
    }

    @Override
    public void setOrientation(int orientation) {
        if (orientation == BJPlayerView.VIDEO_ORIENTATION_LANDSCAPE) {
            $.id(R.id.rl_main_left_player).visible();
            $.visible();
//            $.contentView().setBackgroundColor(ContextCompat.getColor($.view().getContext(), com.baijiahulian.player.R.color.bjplayer_controller_bg));
        } else {
            $.id(R.id.rl_main_left_player).gone();
//            $.contentView().setBackgroundColor(ContextCompat.getColor($.view().getContext(), android.R.color.transparent));
            $.gone();
        }
    }

    @Override
    public void setOnBackClickListener(final View.OnClickListener listener) {
        $.id(R.id.rl_main_left_player).clicked(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPlayer != null) {
                    if (!mPlayer.onBackPressed()) {
                        listener.onClick(v);
                    }
                } else {
                    listener.onClick(v);
                }
            }
        });
    }
}
