package com.huatu.teacheronline.personal;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.huatu.teacheronline.BaseActivity;
import com.huatu.teacheronline.R;
import com.huatu.teacheronline.personal.adapter.InterviewUploadChooseSubjectAdapter;
import com.huatu.teacheronline.personal.adapter.InterviewUploadChooseVersionAdapter;
import com.huatu.teacheronline.personal.bean.UploadParamBean;
import com.huatu.teacheronline.widget.LinearLayoutColorDivider;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 上传面试视频的参数选择
 */
public class ChooseParamActivity extends BaseActivity implements BaseQuickAdapter.OnItemClickListener {

    @BindView(R.id.tv_tittle)
    TextView mTvTittle;
    @BindView(R.id.rcv_choose_param)
    RecyclerView mRcvChooseParam;

    private boolean isChooseSubject;


    public final static String KEY_SUBJECTS = "key_subjects";
    public final static String KEY_VERSIONS = "key_versions";
    public final static String KEY_DATA = "key_data";

    public final static int CODE_SUBJECTS = 0x01;
    public final static int CODE_VERSIONS = 0x02;

    private List<UploadParamBean.PhasesBean.SubjectBean> mSubjects = new ArrayList<>();

    private List<UploadParamBean.PhasesBean.SubjectBean.VersionBean> mVersions = new ArrayList<>();
    private InterviewUploadChooseSubjectAdapter mSubjectAdapter;
    private InterviewUploadChooseVersionAdapter mVersionAdapter;


    public static void startForSubjectResult(Activity activity, ArrayList<UploadParamBean.PhasesBean.SubjectBean> infos) {
        Intent intent = new Intent(activity, ChooseParamActivity.class);
        intent.putExtra(KEY_SUBJECTS, infos);
        activity.startActivityForResult(intent, CODE_SUBJECTS);
    }

    public static void startForVersionResult(Activity activity, ArrayList<UploadParamBean.PhasesBean.SubjectBean.VersionBean> infos) {
        Intent intent = new Intent(activity, ChooseParamActivity.class);
        intent.putExtra(KEY_VERSIONS, infos);
        activity.startActivityForResult(intent, CODE_VERSIONS);
    }


    @Override
    public void initView() {
        setContentView(R.layout.activity_choose_param);
        ButterKnife.bind(this);

        if (getIntent() != null) {
            ArrayList<UploadParamBean.PhasesBean.SubjectBean> subjects =
                    (ArrayList<UploadParamBean.PhasesBean.SubjectBean>) getIntent().getSerializableExtra(KEY_SUBJECTS);

            isChooseSubject = subjects != null;
            if (isChooseSubject) {
                mSubjects.addAll(subjects);
                mTvTittle.setText("选择学科");
            } else {
                mTvTittle.setText("选择教材版本");
                ArrayList<UploadParamBean.PhasesBean.SubjectBean.VersionBean> versions =
                        (ArrayList<UploadParamBean.PhasesBean.SubjectBean.VersionBean>) getIntent().getSerializableExtra(KEY_VERSIONS);

                mVersions.addAll(versions);

            }


        }

        mRcvChooseParam.setLayoutManager(new LinearLayoutManager(this));
        LinearLayoutColorDivider linearLayoutColorDivider = new LinearLayoutColorDivider(getResources(), R.color.gray007, R.dimen.width_1, LinearLayout.VERTICAL);
        mRcvChooseParam.addItemDecoration(linearLayoutColorDivider);


        mSubjectAdapter = new InterviewUploadChooseSubjectAdapter(mSubjects);
        mSubjectAdapter.setOnItemClickListener(this);

        mVersionAdapter = new InterviewUploadChooseVersionAdapter(mVersions);
        mVersionAdapter.setOnItemClickListener(this);
        if (isChooseSubject) {
            mRcvChooseParam.setAdapter(mSubjectAdapter);
        } else {
            mRcvChooseParam.setAdapter(mVersionAdapter);
        }


    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        Intent intent = new Intent();

        if (isChooseSubject) {
            mSubjectAdapter.select(position);
            intent.putExtra(KEY_DATA, mSubjectAdapter.getData().get(position));
        } else {
            mVersionAdapter.select(position);
            intent.putExtra(KEY_DATA, mVersionAdapter.getData().get(position));
        }
        setResult(RESULT_OK, intent);
        finish();
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
