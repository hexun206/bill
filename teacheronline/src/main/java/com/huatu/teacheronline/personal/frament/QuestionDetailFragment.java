package com.huatu.teacheronline.personal.frament;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.huatu.teacheronline.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class QuestionDetailFragment extends Fragment {
    private static final String KET_CONTENT = "KET_CONTENT";
    private static final String KET_INDEX = "KET_INDEX";
    @BindView(R.id.tv_question_tittle)
    TextView mTvQuestionTittle;
    @BindView(R.id.tv_question_content)
    TextView mTvQuestionContent;
//    @BindView(R.id.view_comment_detail_tittle_1)
//    View mViewCommentDetailTittle1;
//    @BindView(R.id.view_comment_detail_tittle_2)
//    View mViewCommentDetailTittle2;
    Unbinder unbinder;

    private String mContent;
    private String mIndex;


    public QuestionDetailFragment() {
    }

    public static QuestionDetailFragment newInstance(String content, int index) {
        QuestionDetailFragment fragment = new QuestionDetailFragment();
        Bundle args = new Bundle();
        args.putString(KET_CONTENT, content);
        args.putInt(KET_INDEX, index);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mContent = getArguments().getString(KET_CONTENT);
            int index = getArguments().getInt(KET_INDEX, 0);
            mIndex = index == 0 ? "一" : "二";
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_question_detail, container, false);
        unbinder = ButterKnife.bind(this, view);


        mTvQuestionTittle.setText("结构化题目" + mIndex);
        mTvQuestionContent.setText(mContent);
//        if (mIndex.equals("一")) {
//            mViewCommentDetailTittle1.setVisibility(View.VISIBLE);
//            mViewCommentDetailTittle2.setVisibility(View.GONE);
//        } else {
//            mViewCommentDetailTittle1.setVisibility(View.GONE);
//            mViewCommentDetailTittle2.setVisibility(View.VISIBLE);
//        }

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
