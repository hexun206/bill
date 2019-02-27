package com.huatu.teacheronline.personal;

import android.content.Context;
import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.huatu.teacheronline.BaseActivity;
import com.huatu.teacheronline.R;
import com.huatu.teacheronline.engine.HTeacherApi;
import com.huatu.teacheronline.engine.ObtainDataFromNetListener;
import com.huatu.teacheronline.personal.adapter.InterviewCommentsAdapter;
import com.huatu.teacheronline.personal.bean.CanUploadInterviewVideoBean;
import com.huatu.teacheronline.utils.CommonUtils;
import com.huatu.teacheronline.utils.GsonUtils;
import com.huatu.teacheronline.utils.ToastUtils;
import com.huatu.teacheronline.utils.UserInfo;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class InterviewCommentsActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener {

    @BindView(R.id.rcy_interview_comments)
    RecyclerView mRcyInterviewComments;
    @BindView(R.id.swipe_interview_comments)
    SwipeRefreshLayout mSwipeInterviewComments;
    private InterviewCommentsAdapter mAdapter;
    private String mUid;

    public static void start(Context context) {
        context.startActivity(new Intent(context, InterviewCommentsActivity.class));

    }

    public static void startClearTop(Context context) {
        Intent intent = new Intent(context, InterviewCommentsActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);

    }

    @Override
    public void initView() {
        setContentView(R.layout.activity_interview_comments);
        ButterKnife.bind(this);

        mUid = CommonUtils.getSharedPreferenceItem(null, UserInfo.KEY_SP_USERID, null);

        View emptyView = getLayoutInflater().inflate(R.layout.empty_interview_vcomments, null);
        mRcyInterviewComments.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new InterviewCommentsAdapter();
        mAdapter.bindToRecyclerView(mRcyInterviewComments);
        mAdapter.setEmptyView(emptyView);
        mSwipeInterviewComments.setOnRefreshListener(this);

    }

    @Override
    protected void onStart() {
        super.onStart();
        loadData();
    }

    private void loadData() {

        HTeacherApi.getCommentList(mUid, new ObtainDataFromNetListener<String, Throwable>() {

            @Override
            public void onStart() {
                mSwipeInterviewComments.setRefreshing(true);

            }

            @Override
            public void onSuccess(String res) {

                mSwipeInterviewComments.setRefreshing(false);

                CanUploadInterviewVideoBean result = GsonUtils.parseJSON(res, CanUploadInterviewVideoBean.class);

                if (result == null) {
                    ToastUtils.showToast(R.string.server_error);
                } else if (result.success()) {
                    List<CanUploadInterviewVideoBean.ClasslistBean> classlist = result.getData();
                    mAdapter.setNewData(classlist);
                } else {
                    ToastUtils.showToast(result.getMessage());

                }


            }

            @Override
            public void onFailure(Throwable res) {
                mSwipeInterviewComments.setRefreshing(false);
                ToastUtils.showToast(R.string.network);


            }
        });


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        HTeacherApi.cancel();
    }

    @Override
    public void setListener() {
        mAdapter.setOnItemChildClickListener((adapter, view, position) -> {
            switch (view.getId()) {
                case R.id.tv_inteview_comments_result:
                    InterviewCommentDetailActivity.start(InterviewCommentsActivity.this, mAdapter.getData().get(position).getNetclassid());
                    break;
                case R.id.tv_inteview_comments_upload:
                    InterviewVideoUploadActivity.start(InterviewCommentsActivity.this, mAdapter.getData().get(position).getNetclassid());

                    break;
            }
        });
    }

    @Override
    public void onClick(View v) {

    }

    @OnClick(R.id.img_back)
    public void onViewClicked() {
        finish();
    }

    @Override
    public void onRefresh() {
        loadData();
    }
}
