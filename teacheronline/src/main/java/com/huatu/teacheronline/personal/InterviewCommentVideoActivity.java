package com.huatu.teacheronline.personal;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.baijiahulian.common.networkv2.HttpException;
import com.baijiahulian.player.BJPlayerView;
import com.baijiahulian.player.OnPlayerViewListener;
import com.baijiahulian.player.bean.SectionItem;
import com.baijiahulian.player.bean.VideoItem;
import com.baijiahulian.player.playerview.BJCenterViewPresenter;
import com.huatu.teacheronline.BaseActivity;
import com.huatu.teacheronline.CustomApplication;
import com.huatu.teacheronline.R;
import com.huatu.teacheronline.widget.bjywidget.BjyBottomViewPresenter;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class InterviewCommentVideoActivity extends BaseActivity {


    public final static String KEY_ID = "key_id";
    public final static String KEY_TOKEN = "key_token";
    @BindView(R.id.view_touch)
    View mViewTouch;
    @BindView(R.id.ll_comment_top)
    LinearLayout mLlComentTop;
    @BindView(R.id.fra_comment_bottombar)
    FrameLayout mFraComentBottom;
    @BindView(R.id.fra_playback_center)
    FrameLayout mfraCenter;
    @BindView(R.id.iv_screen)
    ImageView mIvScreen;
    @BindView(R.id.fra_container)
    FrameLayout mFraContainer;

    private long videoId;
    private String token;
    private boolean immersive;
    private BJPlayerView mPlayerView;

    public final static String KEY_PROGRESS = "key_progress";
    public final static int CODE_REQUEST = 0xf0;
    private int mUpdatePosition;

    public static void start(Context context, long videoId, String token) {

        Intent intent = new Intent(context, InterviewCommentVideoActivity.class);
        intent.putExtra(KEY_ID, videoId);
        intent.putExtra(KEY_TOKEN, token);

        context.startActivity(intent);


    }

    public static void startForResult(Fragment context, long videoId, String token) {

        Intent intent = new Intent(context.getActivity(), InterviewCommentVideoActivity.class);
        intent.putExtra(KEY_ID, videoId);
        intent.putExtra(KEY_TOKEN, token);

        context.startActivityForResult(intent, CODE_REQUEST);


    }


    @Override
    public void initView() {
        setContentView(R.layout.activity_interver_comment_video);
        ButterKnife.bind(this);
        videoId = getIntent().getLongExtra(KEY_ID, -1);
        token = getIntent().getStringExtra(KEY_TOKEN);

        mViewTouch.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                immersive = !immersive;
                changeBarStatus();


                return false;
            }
        });

        mFraComentBottom.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });

        mIvScreen.setImageResource(R.drawable.ic_fullsc_land);


        mPlayerView = new BJPlayerView(this);
        mPlayerView.removeView(mPlayerView.getTopView());
        mPlayerView.removeView(mPlayerView.getBottomView());
        mPlayerView.removeView(mPlayerView.getCenterView());

        mPlayerView.setBottomPresenter(new BjyBottomViewPresenter(mFraComentBottom, null));
        mPlayerView.setCenterPresenter(new BJCenterViewPresenter(mfraCenter));
        mPlayerView.initPartner(CustomApplication.BJPlayerView_partnerId, BJPlayerView.PLAYER_DEPLOY_ONLINE);
        mPlayerView.setMemoryPlayEnable(true);
        mPlayerView.setOnPlayerViewListener(new OnPlayerViewListener() {
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
                mUpdatePosition = i;
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
        });

        mFraContainer.addView(mPlayerView, 0);

        changeBarStatus();
        mPlayerView.setVideoId(videoId, token);

    }

    private void changeBarStatus() {
        mFraComentBottom.setVisibility(immersive ? View.GONE : View.VISIBLE);
        mLlComentTop.setVisibility(immersive ? View.GONE : View.VISIBLE);


    }

    @Override
    protected void onStop() {
        super.onStop();

        mPlayerView.pauseVideo();
    }

    @Override
    protected void onStart() {
        super.onStart();

        mPlayerView.playVideo(0);
    }

    @Override
    public void setListener() {

    }

    @Override
    public void onClick(View v) {

    }


    @Override
    public boolean back() {
        Intent intent = new Intent();
        intent.putExtra(KEY_PROGRESS, mUpdatePosition);
        setResult(RESULT_OK, intent);
        return super.back();
    }

    @OnClick({R.id.img_top_back, R.id.iv_screen})
    public void onViewClicked() {

        back();
    }
}
