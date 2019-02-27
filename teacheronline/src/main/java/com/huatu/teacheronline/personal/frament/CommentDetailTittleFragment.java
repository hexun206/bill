package com.huatu.teacheronline.personal.frament;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.huatu.teacheronline.R;
import com.huatu.teacheronline.personal.bean.ChangeReadStatusEvent;
import com.huatu.teacheronline.personal.bean.InterviewCommentsDetail;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class CommentDetailTittleFragment extends Fragment {


    @BindView(R.id.tv_comment_detail_classtitle)
    TextView mTvCommentDetailClasstitle;
    @BindView(R.id.tv_comment_detail_phase_subject)
    TextView mTvCommentDetailPhaseSubject;
    @BindView(R.id.tv_comment_detail_version)
    TextView mTvCommentDetailVersion;
    @BindView(R.id.tv_comment_detail_status)
    TextView mTvCommentDetailStatus;
    @BindView(R.id.img_detail_example_status)
    ImageView mImgDetailExampleStatus;
    @BindView(R.id.view_comment_detail_msg)
    View mViewCommentDetailMsg;
    private boolean mNoExample;
    Unbinder unbinder;

    public CommentDetailTittleFragment() {
    }

    public final static String KEY_DATA = "KEY_DATA";
    private InterviewCommentsDetail.CommentDetail mInfo;

    public static CommentDetailTittleFragment newInstance(InterviewCommentsDetail.CommentDetail info) {
        CommentDetailTittleFragment fragment = new CommentDetailTittleFragment();
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
        EventBus.getDefault().register(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = null;
//        if (mNoExample) {
//            view = inflater.inflate(R.layout.fragment_comment_detail_tittle_no_example, container, false);
//        } else {
        view = inflater.inflate(R.layout.fragment_comment_detail_tittle, container, false);
//        }


        unbinder = ButterKnife.bind(this, view);
        initUI();
        return view;
    }

    private void initUI() {

        mTvCommentDetailClasstitle.setText(mInfo.getClasstitle());
        mTvCommentDetailPhaseSubject.setText(mInfo.getClassphase() + "·" + mInfo.getClasssubject());
        mTvCommentDetailVersion.setText(mInfo.getVersions());

        mImgDetailExampleStatus.setVisibility(mInfo.isExample() ? View.VISIBLE : View.INVISIBLE);
        mViewCommentDetailMsg.setVisibility(mInfo.isUnRead() ? View.VISIBLE : View.INVISIBLE);
        mTvCommentDetailStatus.setText(mInfo.haveComments() ? "已点评" : "点评中");


    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReadStatusChangeEvent(ChangeReadStatusEvent event) {
        if (!isAdded()) {
            return;
        }

        if (event.getOrderId().equals(mInfo.getId())) {
            mInfo.setReadstatus("2");
            mViewCommentDetailMsg.setVisibility(mInfo.isUnRead() ? View.VISIBLE : View.INVISIBLE);
        }

    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
        unbinder.unbind();
    }
}
