package com.huatu.teacheronline.personal;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.google.gson.reflect.TypeToken;
import com.huatu.teacheronline.BaseActivity;
import com.huatu.teacheronline.R;
import com.huatu.teacheronline.engine.HTeacherApi;
import com.huatu.teacheronline.engine.ObtainDataFromNetListener;
import com.huatu.teacheronline.personal.adapter.InterviewUploadChoosePhaseAdapter;
import com.huatu.teacheronline.personal.bean.InterviewCommentsDetail;
import com.huatu.teacheronline.personal.bean.InteviewVideoInfoUploadRequest;
import com.huatu.teacheronline.personal.bean.QuestionsBean;
import com.huatu.teacheronline.personal.bean.UploadParamBean;
import com.huatu.teacheronline.personal.db.VideoUploadInfoDAO;
import com.huatu.teacheronline.personal.db.VideoUploadInfoManager;
import com.huatu.teacheronline.utils.CommonUtils;
import com.huatu.teacheronline.utils.GsonUtils;
import com.huatu.teacheronline.utils.StringUtils;
import com.huatu.teacheronline.utils.ToastUtils;
import com.huatu.teacheronline.utils.UserInfo;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class InterviewVideoUploadActivity extends BaseActivity {

    @BindView(R.id.edt_video_tittle)
    EditText mEdtVideoTittle;
    @BindView(R.id.rcy_video_upload_phases)
    RecyclerView mRcyVideoUploadPhases;
    @BindView(R.id.edt_video_upload_subjects)
    EditText mEdtVideoUploadSubjects;
    @BindView(R.id.rel_video_upload_subjects)
    RelativeLayout mRelVideoUploadSubjects;
    @BindView(R.id.edt_video_upload_version)
    EditText mEdtVideoUploadVersion;
    @BindView(R.id.rel_video_upload_version)
    RelativeLayout mRelVideoUploadVersion;
    @BindView(R.id.tv_video_upload_next)
    TextView mTvVideoUploadNext;
    @BindView(R.id.tv_video_edit_tittle_1)
    TextView mTvVideoTittle1;
    @BindView(R.id.tv_video_edit_tittle_2)
    TextView mTvVideoTittle2;


    public static final String KEY_CLASS_ID = "key_class_id";
    private String mNetClassId;

    private List<QuestionsBean.Question> mQuestions;
    private String mUid;

    private boolean needRefreshData = false;

    /**
     * 选择的学段
     */
    private UploadParamBean.PhasesBean mSelectedPhase;
    /**
     * 选择的学科
     */
    private UploadParamBean.PhasesBean.SubjectBean mSelectedSubject;

    /**
     * 选择的教材版本
     */
    private UploadParamBean.PhasesBean.SubjectBean.VersionBean mSelectedVersion;

    private InterviewUploadChoosePhaseAdapter mChoosePhaseAdapter;
    private InteviewVideoInfoUploadRequest mUploadRequest = new InteviewVideoInfoUploadRequest();
    private UploadParamBean mParamInfo;

    public static void start(Context context, String netclassid) {
        Intent intent = new Intent(context, InterviewVideoUploadActivity.class);
        intent.putExtra(KEY_CLASS_ID, netclassid);
        context.startActivity(intent);

    }

    @Override
    public void initView() {
        setContentView(R.layout.activity_interview_video_upload);
        ButterKnife.bind(this);
        initData();
        mChoosePhaseAdapter = new InterviewUploadChoosePhaseAdapter();
        mChoosePhaseAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                mChoosePhaseAdapter.select(position);
                mSelectedPhase = mChoosePhaseAdapter.getData().get(position);
                mSelectedSubject = null;
                refreshPickState();
            }
        });
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayout.HORIZONTAL);

        mRcyVideoUploadPhases.setLayoutManager(linearLayoutManager);
        mRcyVideoUploadPhases.setAdapter(mChoosePhaseAdapter);


    }

    @Override
    protected void onStart() {
        super.onStart();
//        if (mMesdata != null && needRefreshData) {
//            getDetailFromServer();
//
//        }
//        needRefreshData = false;


    }

    @Override
    protected void onStop() {
        super.onStop();
        needRefreshData = true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        VideoUploadInfoManager.getInstance().destory();
    }

    private void initData() {
        if (getIntent() != null) {
            mNetClassId = getIntent().getStringExtra(KEY_CLASS_ID);
        }

        mUid = CommonUtils.getSharedPreferenceItem(null, UserInfo.KEY_SP_USERID, null);

//        VideoUploadInfoManager.getInstance().init(mUid, mNetClassId);


        HTeacherApi.getParamInfo(mUid, new ObtainDataFromNetListener<String, Throwable>() {

            @Override
            public void onStart() {
                showLoadingDialog();
            }

            @Override
            public void onSuccess(String res) {
                dismissLoadingDialog();
                boolean success = "1".equals(GsonUtils.getJson(res, "code"));
                String message = GsonUtils.getJson(res, "message");

                String data = GsonUtils.getJson(res, "data");
                if (data == null) {
                    ToastUtils.showToast(R.string.server_error);
                    finish();
                } else if (success) {

                    String subjects = GsonUtils.getJson(data, "subjects");
                    String versions = GsonUtils.getJson(data, "versions");


                    mParamInfo = GsonUtils.parseJSON(data, UploadParamBean.class);
                    for (UploadParamBean.PhasesBean phasesBean : mParamInfo.getPhases()) {

                        String json = GsonUtils.getJson(subjects, phasesBean.getId());

                        Type type = new TypeToken<ArrayList<UploadParamBean.PhasesBean.SubjectBean>>() {
                        }.getType();

                        List<UploadParamBean.PhasesBean.SubjectBean> subjectBeans = GsonUtils.parseJSONArray(json, type);

                        for (UploadParamBean.PhasesBean.SubjectBean subjectBean : subjectBeans) {

                            String versionJson = GsonUtils.getJson(versions, subjectBean.getId());

                            Type versionType = new TypeToken<ArrayList<UploadParamBean.PhasesBean.SubjectBean.VersionBean>>() {
                            }.getType();
                            List<UploadParamBean.PhasesBean.SubjectBean.VersionBean> versionBeans = GsonUtils.parseJSONArray(versionJson, versionType);

                            subjectBean.setVersions(versionBeans);

                        }


                        phasesBean.setSubjects(subjectBeans);


                    }
                    mChoosePhaseAdapter.setNewData(mParamInfo.getPhases());
//                    initParamByDB();
                    getDetailFromServer();

                } else {
                    ToastUtils.showToast(message);
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


    /**
     * 加载结构化试题信息
     */
    public void loadQuestionInfo() {
        HTeacherApi.getQuestionInfo(mUid, new ObtainDataFromNetListener<String, Throwable>() {
            @Override
            public void onStart() {
                showLoadingDialog();
            }

            @Override
            public void onSuccess(String res) {
                dismissLoadingDialog();
                QuestionsBean result = GsonUtils.parseJSON(res, QuestionsBean.class);

                if (result == null) {
                    ToastUtils.showToast(R.string.server_error);
                    finish();
                } else if (result.success()) {
                    mQuestions = result.getData().getQuestions();
                    mTvVideoTittle1.setText(mQuestions.get(0).getQuestionname());
                    mTvVideoTittle2.setText(mQuestions.get(1).getQuestionname());


                } else {
                    ToastUtils.showToast(result.getMessage());
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


    /**
     * todo
     * 加载服务端已存在的数据
     */
    private void getDetailFromServer() {
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

                    for (InterviewCommentsDetail.CommentDetail commentDetail : interviewCommentsDetail.getData()) {
                        if (!commentDetail.isUnmodifiable()) {

                            initParamByServer(commentDetail);
                            break;
                        }


                    }
                    if (mQuestions == null || mQuestions.size() == 0) {
                        loadQuestionInfo();
                        VideoUploadInfoDAO.getInstance().delete(mUid, mNetClassId);

                    } else {
                        mTvVideoTittle1.setText(mQuestions.get(0).getQuestionname());
                        mTvVideoTittle2.setText(mQuestions.get(1).getQuestionname());

                    }

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

    private void initParamByServer(InterviewCommentsDetail.CommentDetail commentDetail) {
        mUploadRequest.setClassphase(commentDetail.getClassphase());
        mUploadRequest.setClasssubject(commentDetail.getClasssubject());
        mUploadRequest.setClasstitle(commentDetail.getClasstitle());
        mUploadRequest.setOrderId(Integer.valueOf(commentDetail.getId()));
        mUploadRequest.setVersions(commentDetail.getVersions());
        mQuestions = commentDetail.getQuestions();


        for (int i = 0; i < mParamInfo.getPhases().size(); i++) {
            UploadParamBean.PhasesBean phasesBean = mParamInfo.getPhases().get(i);
            if (phasesBean.getPhasename().equals(mUploadRequest.getClassphase())) {
                mSelectedPhase = phasesBean;
                break;
            }
        }
        if (mSelectedPhase != null) {
            for (UploadParamBean.PhasesBean.SubjectBean subjectBean : mSelectedPhase.getSubjects()) {
                if (subjectBean.getSubjectname().equals(mUploadRequest.getClasssubject())) {
                    mSelectedSubject = subjectBean;
                    break;
                }
            }
        }


        if (mSelectedSubject != null) {
            for (UploadParamBean.PhasesBean.SubjectBean.VersionBean versionBean : mSelectedSubject.getVersions()) {
                if (versionBean.getVersionname().equals(mUploadRequest.getVersions())) {
                    mSelectedVersion = versionBean;
                    break;
                }
            }

        }

        mEdtVideoTittle.setText(mUploadRequest.getClasstitle());
        if (mSelectedPhase != null) {
            mChoosePhaseAdapter.select(mChoosePhaseAdapter.getData().indexOf(mSelectedPhase));
        }
        if (mSelectedSubject != null) {
            mEdtVideoUploadSubjects.setText(mSelectedSubject.getSubjectname());
        }
        if (mSelectedVersion != null) {
            mEdtVideoUploadVersion.setText(mSelectedVersion.getVersionname());
        }


    }

//    private void initParamByDB() {
//
//        new AsyncTask<Void, Void, Boolean>() {
//            @Override
//            protected Boolean doInBackground(Void... voids) {
//                mUploadRequest = VideoUploadInfoManager.getInstance().getInfo();
//                if (mUploadRequest != null) {
//
//                    for (int i = 0; i < mMesdata.getPhases().size(); i++) {
//                        UploadParamBean.MesdataBean.PhasesBean phasesBean = mMesdata.getPhases().get(i);
//                        if (phasesBean.getPhasename().equals(mUploadRequest.getClassphase())) {
//                            mSelectedPhase = phasesBean;
//                            break;
//                        }
//                    }
//                    if (mSelectedPhase == null) {
//                        return true;
//
//                    }
//
//                    for (UploadParamBean.MesdataBean.SubjectsBean.SubjectBean subjectBean : getSubjectsByPhaseId(mSelectedPhase.getId())) {
//                        if (subjectBean.getSubjectname().equals(mUploadRequest.getClasssubject())) {
//                            mSelectedSubject = subjectBean;
//                            break;
//                        }
//                    }
//                    if (mSelectedSubject == null) {
//                        return true;
//
//                    }
//
//
//                    for (UploadParamBean.MesdataBean.VersionsBean.VersionBean versionBean : getVersionsBySubjectId(mSelectedSubject.getId())) {
//                        if (versionBean.getVersionname().equals(mUploadRequest.getVersions())) {
//                            mSelectedVersion = versionBean;
//                            break;
//                        }
//                    }
//
//
//                }
//
//
//                return mUploadRequest != null;
//            }
//
//            @Override
//            protected void onPostExecute(Boolean aBoolean) {
//                if (aBoolean && !isDestroyed()) {
//                    mEdtVideoTittle.setText(mUploadRequest.getClasstitle());
//                    if (mSelectedPhase != null) {
//                        mChoosePhaseAdapter.select(mChoosePhaseAdapter.getData().indexOf(mSelectedPhase));
//                    }
//                    if (mSelectedSubject != null) {
//                        mEdtVideoUploadSubjects.setText(mSelectedSubject.getSubjectname());
//                    }
//                    if (mSelectedVersion != null) {
//                        mEdtVideoUploadVersion.setText(mSelectedVersion.getVersionname());
//                    }
//
//
//                }
//
//
//            }
//        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
//
//
//    }


    @Override
    public void setListener() {

    }


    /**
     * 上一选项重新选择时重置
     */
    private void refreshPickState() {
        if (mSelectedSubject == null) {
            mSelectedVersion = null;
            mEdtVideoUploadSubjects.setText("");
            mEdtVideoUploadVersion.setText("");

        }

        if (mSelectedVersion == null) {
            mEdtVideoUploadVersion.setText("");
        }


    }

    @Override
    public void onClick(View v) {

    }

    @OnClick({R.id.img_back, R.id.rel_video_upload_subjects, R.id.rel_video_upload_version, R.id.tv_video_upload_next})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.img_back:
                finish();
                break;
            case R.id.rel_video_upload_subjects:

                if (mSelectedPhase == null) {
                    ToastUtils.showToast("请先选择学段");
                    return;
                }

                ArrayList<UploadParamBean.PhasesBean.SubjectBean> subjectsByPhaseId =
                        (ArrayList<UploadParamBean.PhasesBean.SubjectBean>) mSelectedPhase.getSubjects();

                ChooseParamActivity.startForSubjectResult(this, subjectsByPhaseId);
                break;
            case R.id.rel_video_upload_version:
                if (mSelectedSubject == null) {
                    ToastUtils.showToast("请先选择学科");
                    return;
                }

                ArrayList<UploadParamBean.PhasesBean.SubjectBean.VersionBean> versionsBySubjectId =
                        (ArrayList<UploadParamBean.PhasesBean.SubjectBean.VersionBean>) mSelectedSubject.getVersions();

                ChooseParamActivity.startForVersionResult(this, versionsBySubjectId);

                break;
            case R.id.tv_video_upload_next:
                String tittle = mEdtVideoTittle.getText().toString().trim();
                if (StringUtils.isEmpty(tittle)) {
                    ToastUtils.showToast("请填写课文标题");
                    return;
                }

                if (mSelectedPhase == null) {
                    ToastUtils.showToast("请选择学段");
                    return;
                }

                if (mSelectedSubject == null) {
                    ToastUtils.showToast("请选择学科");
                    return;
                }
                if (mSelectedVersion == null) {
                    ToastUtils.showToast("请选择教材版本");
                    return;
                }

                mUploadRequest.setClasstitle(tittle);
                mUploadRequest.setUserid(mUid);
                mUploadRequest.setNetclassid(mNetClassId);
                String qusetions = "";
                String qusetionsDetail = "";
                for (QuestionsBean.Question questionsBean : mQuestions) {
                    qusetions = qusetions + "," + questionsBean.getId();
                    qusetionsDetail = qusetionsDetail + "," + questionsBean.getQuestionname();
                }
                if (!StringUtils.isEmpty(qusetions)) {
                    qusetions = qusetions.substring(1, qusetions.length());
                }
                if (!StringUtils.isEmpty(qusetionsDetail)) {
                    qusetionsDetail = qusetionsDetail.substring(1, qusetionsDetail.length());
                }
                mUploadRequest.setQuestions(qusetions);
                mUploadRequest.setQuestionsDetail(qusetionsDetail);
                mUploadRequest.setClassphase(mSelectedPhase.getPhasename());
                mUploadRequest.setClasssubject(mSelectedSubject.getSubjectname());
                mUploadRequest.setVersions(mSelectedVersion.getVersionname());

                HTeacherApi.updateVideoInfo(mUploadRequest, new ObtainDataFromNetListener<String, Throwable>() {

                    @Override
                    public void onStart() {
                        showLoadingDialog();
                    }

                    @Override
                    public void onSuccess(String res) {
                        dismissLoadingDialog();
                        String code = GsonUtils.getJson(res, "code");

                        if ("1".equals(code)) {

                            String orderId = GsonUtils.getJson(res, "id");
                            if (!StringUtils.isEmpty(orderId)) {
                                try {
                                    mUploadRequest.setOrderId(Integer.valueOf(orderId));
                                } catch (NumberFormatException e) {
                                    e.printStackTrace();
                                }

                            }

                            InterviewVideoUploadEditActivity.start(InterviewVideoUploadActivity.this, mUploadRequest);

                        } else {
                            ToastUtils.showToast("保存失败!" + GsonUtils.getJson(res, "message"));

                        }


                    }

                    @Override
                    public void onFailure(Throwable res) {
                        dismissLoadingDialog();
                        ToastUtils.showToast(R.string.network);
                    }
                });


                break;
        }
    }

//    private ArrayList<UploadParamBean.MesdataBean.VersionsBean.VersionBean> getVersionsBySubjectId(String id) {
//        ArrayList<UploadParamBean.MesdataBean.VersionsBean.VersionBean> versionList = null;
//        UploadParamBean.MesdataBean.VersionsBean versions = mMesdata.getVersions();
//        Method[] methods = versions.getClass().getMethods();
//        for (Method method : methods) {
//            if (method.getName().contains(id)) {
//                try {
//                    versionList = new ArrayList<>();
//                    List<UploadParamBean.MesdataBean.VersionsBean.VersionBean> invoke =
//                            (List<UploadParamBean.MesdataBean.VersionsBean.VersionBean>) method.invoke(versions);
//                    versionList.addAll(invoke);
//                    break;
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//
//            }
//        }
//        return versionList;
//
//    }
//
//    /**
//     * 获取学科列表
//     *
//     * @param id
//     */
//    private ArrayList<UploadParamBean.MesdataBean.SubjectsBean.SubjectBean> getSubjectsByPhaseId(String id) {
//        ArrayList<UploadParamBean.MesdataBean.SubjectsBean.SubjectBean> subjectList = null;
//        UploadParamBean.MesdataBean.SubjectsBean subjects = mMesdata.getSubjects();
//        Method[] methods = subjects.getClass().getMethods();
//        for (Method method : methods) {
//            if (method.getName().contains(id)) {
//                try {
//                    subjectList = new ArrayList<>();
//                    Object invoke = method.invoke(subjects);
//                    subjectList.addAll((List<UploadParamBean.MesdataBean.SubjectsBean.SubjectBean>) invoke);
//                    break;
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//
//            }
//        }
//        return subjectList;
//
//    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && data != null) {
            switch (requestCode) {
                case ChooseParamActivity.CODE_SUBJECTS:
                    mSelectedSubject = (UploadParamBean.PhasesBean.SubjectBean) data.getSerializableExtra(ChooseParamActivity.KEY_DATA);
                    mSelectedVersion = null;
                    mEdtVideoUploadSubjects.setText(mSelectedSubject.getSubjectname());

                    break;
                case ChooseParamActivity.CODE_VERSIONS:
                    mSelectedVersion = (UploadParamBean.PhasesBean.SubjectBean.VersionBean) data.getSerializableExtra(ChooseParamActivity.KEY_DATA);
                    mEdtVideoUploadVersion.setText(mSelectedVersion.getVersionname());
                    break;
            }
            refreshPickState();

        }


    }
}
