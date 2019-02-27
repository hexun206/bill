package com.huatu.teacheronline.personal;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.aohanyao.transformer.library.CardPageTransformer;
import com.aohanyao.transformer.library.conf.OnPageTransformerListener;
import com.aohanyao.transformer.library.conf.PageTransformerConfig;
import com.huatu.teacheronline.BaseActivity;
import com.huatu.teacheronline.R;
import com.huatu.teacheronline.engine.HTeacherApi;
import com.huatu.teacheronline.engine.ObtainDataFromNetListener;
import com.huatu.teacheronline.personal.adapter.BaseFragmentPagerAdapter;
import com.huatu.teacheronline.personal.bean.InterviewCommentsDetail;
import com.huatu.teacheronline.personal.frament.CommentDetailContentFragment;
import com.huatu.teacheronline.personal.frament.CommentDetailTittleFragment;
import com.huatu.teacheronline.utils.CommonUtils;
import com.huatu.teacheronline.utils.GsonUtils;
import com.huatu.teacheronline.utils.ToastUtils;
import com.huatu.teacheronline.utils.UserInfo;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class InterviewCommentDetailActivity extends BaseActivity {

    @BindView(R.id.vp_comment_tittle_info)
    ViewPager mVpCommentTittleInfo;
    private String mUid;
    private String mNetClassId;

    public final static String KEY_ID = "KEY_ID";
    private ArrayList<InterviewCommentsDetail.CommentDetail> mCommentDetails;
        private BaseFragmentPagerAdapter mTittleAdapter;
    private List<Fragment> mContentFragments;
    private int mPrePosition;

    public static void start(Context context, String netClassId) {
        Intent intent = new Intent(context, InterviewCommentDetailActivity.class);
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
        HTeacherApi.getCommentDetail(mUid, mNetClassId, new ObtainDataFromNetListener<String, Throwable>() {
            @Override
            public void onStart() {
                showLoadingDialog();
            }

            @Override
            public void onSuccess(String res) {
                dismissLoadingDialog();

                InterviewCommentsDetail interviewCommentsDetail = GsonUtils.parseJSON(res, InterviewCommentsDetail.class);


                if (interviewCommentsDetail == null) {
                    ToastUtils.showToast(R.string.server_error);
                    finish();
                } else if (interviewCommentsDetail.success()) {
                    mCommentDetails = interviewCommentsDetail.getData();
                    initTitlePager();
                    initContentFragment();

                } else {
                    ToastUtils.showToast(interviewCommentsDetail.getMessage());
                    finish();
                }


            }

            @Override
            public void onFailure(Throwable res) {
                ToastUtils.showToast(R.string.network);
                dismissLoadingDialog();
                finish();
            }
        });


    }

    private void initContentFragment() {
        mContentFragments = new ArrayList<>();

        for (InterviewCommentsDetail.CommentDetail commentDetail : mCommentDetails) {
            if (commentDetail.isUnmodifiable()) {
                mContentFragments.add(CommentDetailContentFragment.newInstance(commentDetail));
            }
        }


        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        for (int i = 0; i < mContentFragments.size(); i++) {

            fragmentTransaction
                    .add(R.id.fra_comment_detail, mContentFragments.get(i));
            if (i != 0) {
                fragmentTransaction
                        .hide(mContentFragments.get(i));
            }

        }

        fragmentTransaction.commit();


    }

    private void initTitlePager() {
        List<Fragment> tittleFragments = new ArrayList<>();

        for (InterviewCommentsDetail.CommentDetail commentDetail : mCommentDetails) {
            if (commentDetail.isUnmodifiable()) {
                tittleFragments.add(CommentDetailTittleFragment.newInstance(commentDetail));
            }
        }

        mVpCommentTittleInfo.setOffscreenPageLimit(3);
        mVpCommentTittleInfo.setPageTransformer(true, CardPageTransformer.getBuild()//建造者模式
                .addAnimationType(PageTransformerConfig.ROTATION)//默认动画 default animation rotation  旋转  当然 也可以一次性添加两个  后续会增加更多动画
                .setRotation(-45)//旋转角度
                .addAnimationType(PageTransformerConfig.ALPHA)//默认动画 透明度 暂时还有问题
                .setViewType(PageTransformerConfig.BOTTOM)
                .setOnPageTransformerListener(new OnPageTransformerListener() {
                    @Override
                    public void onPageTransformerListener(View page, float position) {
                        //你也可以在这里对 page 实行自定义动画 cust anim
                    }
                })
                .setTranslationOffset(15)
                .setScaleOffset(30)
                .create(mVpCommentTittleInfo));
        mTittleAdapter = new BaseFragmentPagerAdapter(getSupportFragmentManager(), tittleFragments, null);
//        mCommentTittleAdapter = new InterviewCommentTittleAdapter(this, mCommentDetails, mVpCommentTittleInfo, noExample);
//        mCommentTittleCardAdapter = new InterviewCommentTittleCardAdapter(this, mCommentDetails, noExample);

        mVpCommentTittleInfo.setAdapter(mTittleAdapter);

        mVpCommentTittleInfo.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {


                int index = position /*% mCommentTittleCardAdapter.getDatas().size()*/;
                if (mPrePosition != index) {
                    showFragment(index);
                }


                mPrePosition = index;


            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void showFragment(int position) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction
                .hide(mContentFragments.get(mPrePosition))
                .show(mContentFragments.get(position))
                .commit();
    }

    @Override
    public void setListener() {

    }

    @Override
    public void onClick(View v) {

    }


    @OnClick(R.id.img_back)
    public void onViewClicked() {
        finish();
    }
}
