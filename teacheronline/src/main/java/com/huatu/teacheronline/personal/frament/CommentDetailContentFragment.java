package com.huatu.teacheronline.personal.frament;


import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baijiahulian.common.networkv2.HttpException;
import com.baijiahulian.player.BJPlayerView;
import com.baijiahulian.player.OnPlayerViewListener;
import com.baijiahulian.player.bean.SectionItem;
import com.baijiahulian.player.bean.VideoItem;
import com.baijiahulian.player.playerview.BJCenterViewPresenter;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.CenterInside;
import com.bumptech.glide.request.RequestOptions;
import com.gensee.utils.StringUtil;
import com.huatu.teacheronline.CustomApplication;
import com.huatu.teacheronline.R;
import com.huatu.teacheronline.engine.BJ_CloudApi;
import com.huatu.teacheronline.engine.HTeacherApi;
import com.huatu.teacheronline.engine.ObtainDataFromNetListener;
import com.huatu.teacheronline.personal.InterviewCommentVideoActivity;
import com.huatu.teacheronline.personal.adapter.BaseFragmentPagerAdapter;
import com.huatu.teacheronline.personal.bean.ChangeReadStatusEvent;
import com.huatu.teacheronline.personal.bean.InterviewCommentsDetail;
import com.huatu.teacheronline.personal.bean.QuestionsBean;
import com.huatu.teacheronline.personal.bean.VideoImageInfo;
import com.huatu.teacheronline.utils.CommonUtils;
import com.huatu.teacheronline.utils.FileUtils;
import com.huatu.teacheronline.utils.GsonUtils;
import com.huatu.teacheronline.utils.StringUtils;
import com.huatu.teacheronline.utils.ToastUtils;
import com.huatu.teacheronline.utils.UserInfo;
import com.huatu.teacheronline.widget.bjywidget.BjyBottomViewPresenter;
import com.luck.picture.lib.PictureSelector;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.FileCallback;
import com.lzy.okgo.model.Progress;
import com.lzy.okgo.model.Response;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static android.app.Activity.RESULT_OK;

public class CommentDetailContentFragment extends Fragment {
    public final static String KEY_DATA = "KEY_DATA";
    @BindView(R.id.vp_question_detail)
    ViewPager mVpQuestionDetail;
    @BindView(R.id.tv_comment_detail_upload_time)
    TextView mTvCommentDetailUploadTime;
    @BindView(R.id.img_comment_detail_play)
    ImageView mImgCommentDetailPlay;
    @BindView(R.id.img_comment_detail_preview)
    ImageView mImgCommentDetailPreview;
    @BindView(R.id.tv_comment_detail_teacher_replay_empty)
    TextView mTvCommentDetailTeacherReplayEmpty;
    @BindView(R.id.tv_comment_detail_teacher_replay_time)
    TextView mTvCommentDetailTeacherReplayTime;
    @BindView(R.id.tv_comment_detail_teacher_comment_time)
    TextView mTvCommentDetailTeacherCommentTime;
    @BindView(R.id.ll_comment_detail_teacher_replay)
    LinearLayout mLlCommentDetailTeacherReplay;
    @BindView(R.id.fra_video_container)
    FrameLayout fra_container;


    @BindView(R.id.fra_playback_center)
    FrameLayout mfraCenter;
    @BindView(R.id.ll_comment_top)
    LinearLayout mLLTop;
    @BindView(R.id.ll_comment_back)
    LinearLayout mLLTopBack;
    @BindView(R.id.fra_comment_bottombar)
    FrameLayout mfraBottom;
    @BindView(R.id.view_touch)
    View mViewTouch;

    @BindView(R.id.fra_voice)
    FrameLayout mFraVoice;

    @BindView(R.id.view_comment_detail_tittle_1)
    View mViewCommentDetailTittle1;
    @BindView(R.id.view_comment_detail_tittle_2)
    View mViewCommentDetailTittle2;

    Unbinder unbinder;
    private InterviewCommentsDetail.CommentDetail mInfo;
    private List<Fragment> mFragments;
    private File mTeacherAudio;
    private BJPlayerView mPlayerView;
    private boolean immersive = true;
    private int mPosition = -1;
    private boolean needSeek = false;
    private boolean mDontPauseVideo = false;
    private String mPic;

    public CommentDetailContentFragment() {
    }

    public static CommentDetailContentFragment newInstance(InterviewCommentsDetail.CommentDetail info) {
        CommentDetailContentFragment fragment = new CommentDetailContentFragment();
        Bundle args = new Bundle();
        args.putSerializable(KEY_DATA, info);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mInfo = (InterviewCommentsDetail.CommentDetail) getArguments().getSerializable(KEY_DATA);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_comment_detail_content, container, false);
        unbinder = ButterKnife.bind(this, rootView);


        initUI();
        return rootView;
    }


    protected void initUI() {

        mfraBottom.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });

        initTittlePager();

        mTvCommentDetailUploadTime.setText("上传时间：" + mInfo.getSubmittime());

        initVideoPlayer();

        initTeacherComment();

        initVideoImage();


    }

    private void initVideoImage() {
        BJ_CloudApi.getVideoImage(mInfo.getBjyvideoid(), new ObtainDataFromNetListener<String, Throwable>() {
            @Override
            public void onSuccess(String res) {
                if (!isAdded()) {
                    return;
                }
                VideoImageInfo info = GsonUtils.parseJSON(res, VideoImageInfo.class);
                if (info != null && info.success()) {
                    VideoImageInfo.Img data = info.getData();
                    if (data.getImgs() != null && data.getImgs().size() != 0) {
                        mPic = data.getImgs().get(0);
                        if (isAdded() && !StringUtils.isEmpty(mPic)) {
                            RequestOptions options = new RequestOptions()
                                    .transforms(new CenterInside())
                                    .placeholder(R.color.gray007)
                                    .diskCacheStrategy(DiskCacheStrategy.ALL);
                            Glide.with(getActivity())
                                    .load(mPic)
                                    .apply(options)
                                    .into(mImgCommentDetailPreview);
                        }
                    }


                }
            }

            @Override
            public void onFailure(Throwable res) {

            }
        });


    }

    /**
     * in viewPager
     *
     * @param isVisibleToUser
     */

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if (getUserVisibleHint()) {
            onVisible();
        } else {
            onInvisible();
        }
    }


    /**
     * in show hide
     *
     * @param hidden
     */
    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            onVisible();
        } else {
            onInvisible();
        }
    }

    private void initTeacherComment() {

        if (StringUtils.isEmpty(mInfo.getTeaaudio())) {
            mTvCommentDetailTeacherReplayEmpty.setVisibility(View.VISIBLE);
            mLlCommentDetailTeacherReplay.setVisibility(View.GONE);
            mTvCommentDetailTeacherReplayTime.setVisibility(View.GONE);
        } else {
            mTvCommentDetailTeacherReplayEmpty.setVisibility(View.GONE);
            mLlCommentDetailTeacherReplay.setVisibility(View.VISIBLE);
            mTvCommentDetailTeacherReplayTime.setVisibility(View.VISIBLE);
            mTvCommentDetailTeacherReplayTime.setText("点评时间：" + mInfo.getCommenttime());
            downLoadTeacherAudio();
        }


    }

    private void downLoadTeacherAudio() {

        String[] split = mInfo.getTeaaudio().split("/");
        if (split.length == 0) {
            return;
        }


        String fileName = split[split.length - 1];

        File audioFile = new File(FileUtils.getInterviewTeacherAudioFolder(), fileName);

        if (audioFile.exists()) {
            mTeacherAudio = audioFile;
            getMediaDuring();
        } else {

            OkGo.<File>get(mInfo.getTeaaudio())
                    .tag(this)
                    .execute(new FileCallback(FileUtils.getInterviewTeacherAudioFolder(), fileName) {
                        @Override
                        public void onSuccess(Response<File> response) {
                            mTeacherAudio = response.body();

                            getMediaDuring();


                        }

                        @Override
                        public void downloadProgress(Progress progress) {


                        }
                    });


        }


    }

    /**
     * 获取音频时长
     */
    private void getMediaDuring() {

        MediaPlayer player = new MediaPlayer();
        try {
            player.setDataSource(mTeacherAudio.getAbsolutePath());
            player.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        double duration = player.getDuration() / 1000;//获取音频的时间 s
        int minutes = (int) (duration / 60);
        int seconds = (int) (duration % 60);
        mTvCommentDetailTeacherCommentTime.setText(minutes + "'" + seconds + "''");
        player.release();//释放资源


    }

    @Override
    public void onDetach() {
        super.onDetach();
        OkGo.getInstance().cancelTag(this);
    }


    private void initVideoPlayer() {
        mViewTouch.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                immersive = !immersive;
                changeBarStatus();


                return false;
            }
        });


        mPlayerView = new BJPlayerView(getActivity());
        mPlayerView.removeView(mPlayerView.getTopView());
        mPlayerView.removeView(mPlayerView.getBottomView());
        mPlayerView.removeView(mPlayerView.getCenterView());
//


        mPlayerView.setBottomPresenter(new BjyBottomViewPresenter(mfraBottom, null));
        mPlayerView.setCenterPresenter(new BJCenterViewPresenter(mfraCenter));
        mPlayerView.initPartner(CustomApplication.BJPlayerView_partnerId, BJPlayerView.PLAYER_DEPLOY_ONLINE);
        mPlayerView.setMemoryPlayEnable(true);


        fra_container.addView(mPlayerView, 0);

        changeBarStatus();

        if (!StringUtil.isEmpty(mInfo.getBjyvideoid()) && !StringUtil.isEmpty(mInfo.getToken())) {
            mPlayerView.setVideoId(Long.parseLong(mInfo.getBjyvideoid()), mInfo.getToken());
        } else {
            ToastUtils.showToast("视频播放参数错误！");

        }
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
                if (needSeek && mPosition != -1) {
                    mPlayerView.seekVideo(mPosition);
                    needSeek = false;
                }
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

//                if (needSeek && mPosition != -1) {
//                    bjPlayerView.seekVideo(mPosition);
//                    needSeek = false;
//                }


            }

            @Override
            public void onCaton(BJPlayerView bjPlayerView) {

            }

            @Override
            public String getVideoTokenWhenInvalid() {
                return null;
            }
        });


    }

    private void changeBarStatus() {
        mfraBottom.setVisibility(immersive ? View.GONE : View.VISIBLE);


    }

    private void initTittlePager() {
        mFragments = new ArrayList<>();
        List<QuestionsBean.Question> questions = mInfo.getQuestions();
        for (int i = 0; i < questions.size(); i++) {

            QuestionDetailFragment questionDetailFragment = QuestionDetailFragment.newInstance(questions.get(i).getQuestionname(), i);
            mFragments.add(questionDetailFragment);

        }

        mVpQuestionDetail.setAdapter(new BaseFragmentPagerAdapter(getChildFragmentManager(), mFragments, null));
        mVpQuestionDetail.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    mViewCommentDetailTittle1.setVisibility(View.VISIBLE);
                    mViewCommentDetailTittle2.setVisibility(View.GONE);
                } else {
                    mViewCommentDetailTittle1.setVisibility(View.GONE);
                    mViewCommentDetailTittle2.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == InterviewCommentVideoActivity.CODE_REQUEST && resultCode == RESULT_OK) {

            int position = data.getIntExtra(InterviewCommentVideoActivity.KEY_PROGRESS, -1);
            mPosition = position;

        }


        super.onActivityResult(requestCode, resultCode, data);
    }

    protected void onVisible() {

        View firtView = fra_container.getChildAt(0);
        if (firtView instanceof BJPlayerView) {

        } else {
            fra_container.addView(mPlayerView, 0);

        }

        if (mPlayerView != null) {
            mPlayerView.onResume();
        }


    }

    @Override
    public void onResume() {
        super.onResume();
        if (isVisible()) {
            if (mPlayerView != null) {
                mPlayerView.onResume();

            }
        }

    }

    @Override
    public void onPause() {
        super.onPause();

        if (mPlayerView != null) {
            if (!mDontPauseVideo) {
                mPlayerView.pauseVideo();
                mDontPauseVideo = false;
            }
            mPlayerView.onPause();

        }
    }

    protected void onInvisible() {
        View firtView = fra_container.getChildAt(0);
        if (firtView instanceof BJPlayerView) {
            fra_container.removeView(mPlayerView);
        }


        if (mPlayerView != null) {
            mPlayerView.pauseVideo();
            mPlayerView.onPause();

        }
    }


    @OnClick({R.id.img_comment_detail_play, R.id.fra_voice, R.id.iv_screen})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.img_comment_detail_play:
                if (mPlayerView != null) {
                    mPlayerView.playVideo(0);
                }
                mImgCommentDetailPlay.setVisibility(View.GONE);
                mImgCommentDetailPreview.setVisibility(View.GONE);

                break;
            case R.id.fra_voice:

                if (mTeacherAudio == null) {

                    ToastUtils.showToast("老师点评获取中,请稍后尝试!");

                } else {

                    if (mInfo.isUnRead()) {

                        ChangeReadStatusEvent changeReadStatusEvent = new ChangeReadStatusEvent();
                        changeReadStatusEvent.setOrderId(mInfo.getId());
                        EventBus.getDefault().post(changeReadStatusEvent);

                        readRequest();
                    }
                    PictureSelector.create(this).externalPictureVideo(mTeacherAudio.getAbsolutePath());
                }


                break;

            case R.id.iv_screen:
                needSeek = true;
                mDontPauseVideo = true;
                InterviewCommentVideoActivity.startForResult(this, Long.parseLong(mInfo.getBjyvideoid()), mInfo.getToken());
                break;
        }
    }


    /**
     * 已读
     */
    private void readRequest() {
        String uid = CommonUtils.getSharedPreferenceItem(null, UserInfo.KEY_SP_USERID, null);
        HTeacherApi.postReadStatus(uid, mInfo.getId(), new ObtainDataFromNetListener<String, Throwable>() {
            @Override
            public void onSuccess(String res) {

            }

            @Override
            public void onFailure(Throwable res) {

            }
        });

    }


}
