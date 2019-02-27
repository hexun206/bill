package com.huatu.teacheronline.personal;

import android.content.Context;
import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.dd.ShadowLayout;
import com.huatu.teacheronline.BaseActivity;
import com.huatu.teacheronline.R;
import com.huatu.teacheronline.utils.CommonUtils;
import com.huatu.teacheronline.utils.UserInfo;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class InterviewCommentDetailActivityCopy extends BaseActivity {

    @BindView(R.id.tv_comment_detail_classtitle)
    TextView mTvCommentDetailClasstitle;
    @BindView(R.id.tv_comment_detail_phase_subject)
    TextView mTvCommentDetailPhaseSubject;
    @BindView(R.id.tv_comment_detail_version)
    TextView mTvCommentDetailVersion;
    @BindView(R.id.tv_comment_detail_status)
    TextView mTvCommentDetailStatus;
    @BindView(R.id.sl_classtittle)
    ShadowLayout mSlClasstittle;
    @BindView(R.id.vp_question_detail)
    ViewPager mVpQuestionDetail;
    @BindView(R.id.view_comment_detail_tittle_1)
    View mViewCommentDetailTittle1;
    @BindView(R.id.view_comment_detail_tittle_2)
    View mViewCommentDetailTittle2;
    @BindView(R.id.fra_class_question)
    FrameLayout mFraClassQuestion;
    @BindView(R.id.tv_comment_detail_upload_time)
    TextView mTvCommentDetailUploadTime;
    @BindView(R.id.img_comment_detail_preview)
    ImageView mImgCommentDetailPreview;
    @BindView(R.id.img_comment_detail_play)
    ImageView mImgCommentDetailPlay;
    @BindView(R.id.sl_video)
    ShadowLayout mSlVideo;
    @BindView(R.id.tv_comment_detail_teacher_replay_time)
    TextView mTvCommentDetailTeacherReplayTime;
    @BindView(R.id.img_voice)
    ImageView mImgVoice;
    @BindView(R.id.tv_comment_detail_teacher_comment_time)
    TextView mTvCommentDetailTeacherCommentTime;
    @BindView(R.id.sl_teacher_comment)
    ShadowLayout mSlTeacherComment;
    private String mUid;
    private String mNetClassId;

    public final static String KEY_ID = "KEY_ID";

    public static void start(Context context, String netClassId) {
        Intent intent = new Intent(context, InterviewCommentDetailActivityCopy.class);
        intent.putExtra(KEY_ID, netClassId);
        context.startActivity(intent);

    }

    @Override
    public void initView() {
        setContentView(R.layout.activity_interview_comment_detail);
        ButterKnife.bind(this);

        if (getIntent() != null) {
            mNetClassId = getIntent().getStringExtra(KEY_ID);
        }

        mUid = CommonUtils.getSharedPreferenceItem(null, UserInfo.KEY_SP_USERID, null);


        getDetail();


    }

    /**
     * 获取点评详情
     */
    private void getDetail() {

    }

    @Override
    public void setListener() {

    }

    @Override
    public void onClick(View v) {

    }


    @OnClick({R.id.img_back, R.id.img_comment_detail_play, R.id.img_voice})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.img_back:
                finish();
                break;
            case R.id.img_comment_detail_play:
                break;
            case R.id.img_voice:
                break;
        }
    }
}
