package com.huatu.teacheronline.exercise;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.greendao.DaoUtils;
import com.greendao.ExerciseDownloadPackage;
import com.greendao.StudyRecords;
import com.huatu.teacheronline.BaseActivity;
import com.huatu.teacheronline.R;
import com.huatu.teacheronline.engine.AsyncHttpClientHelper;
import com.huatu.teacheronline.engine.ObtainDataFromNetListener;
import com.huatu.teacheronline.engine.SendRequest;
import com.huatu.teacheronline.exercise.adapter.ChapterTreeAdapter;
import com.huatu.teacheronline.exercise.bean.CategoryBean;
import com.huatu.teacheronline.login.ExamSubjectChooseActivity;
import com.huatu.teacheronline.utils.CommonUtils;
import com.huatu.teacheronline.utils.TeacherOnlineUtils;
import com.huatu.teacheronline.utils.ToastUtils;
import com.huatu.teacheronline.utils.UserInfo;
import com.huatu.teacheronline.widget.CustomAlertDialog;
import com.huatu.teacheronline.widget.MindDialog;
import com.huatu.teacheronline.widget.PopwindowUtil;
import com.umeng.analytics.MobclickAgent;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 * Created by ljzyuhenda on 16/2/20.
 * 模块题海 我的错题
 */
public class ExerciseErrorCenterActivity extends BaseActivity implements AdapterView.OnItemClickListener {
    public static final String ACTION_REPETITION_GROUP = "action_repetition_group";
    private ListView lv_errorCategory;
    private ErrorAdapter mErrorAdapter;
    private List<CategoryBean> mCategoryBeanList;
    private CustomAlertDialog mCustomLoadingDialog;
    private DaoUtils mDaoUtils;
    private TextView tv_main_title;
    private RelativeLayout rl_main_left;
    //-----多科目相关
    private RelativeLayout rl_wifi;
    private RelativeLayout mRlTitle;
    private int currentPosition = 0;

    @Override
    public void initView() {
        setContentView(R.layout.activity_exercise_errorcenter);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_REPETITION_GROUP);
        setIntentFilter(intentFilter);
        tv_main_title = (TextView) findViewById(R.id.tv_main_title);
        tv_main_title.setText(R.string.my_error_exercise);
        rl_main_left = (RelativeLayout) findViewById(R.id.rl_main_left);
        mRlTitle= (RelativeLayout) findViewById(R.id.rl_titleContainer_main);

        lv_errorCategory = (ListView) findViewById(R.id.lv_errorCategory);
        rl_wifi = (RelativeLayout) findViewById(R.id.rl_wifi);
        mErrorAdapter = new ErrorAdapter();
        lv_errorCategory.setAdapter(mErrorAdapter);
        mDaoUtils = DaoUtils.getInstance();

        mCustomLoadingDialog = new CustomAlertDialog(this, R.layout.dialog_loading_custom);
        iniTitle();
        showMindDialog();

    }

    /**
     * 第一次进入需要提示
     */
    private void showMindDialog() {
        //是否第一次进入模块题海、真题测评、错题中心中的一个
        if (!CommonUtils.getSharedPreferenceItemForBoolean(null, UserInfo.KEY_SP_ISFRIST_OPEN_MORE_SUBJECT)) {
            MindDialog.showMoreSubDialog(this);
            CommonUtils.putSharedPreferenceItemForBoolean(null, UserInfo.KEY_SP_ISFRIST_OPEN_MORE_SUBJECT, true);
        }
    }

    /**
     * 初始化标题
     */
    private void iniTitle() {
        SendRequest.getWrongQuestions(new ObtainDataListenerForErrorExerciseChapterTree(this));
    }

    @Override
    public void setListener() {
        lv_errorCategory.setOnItemClickListener(this);
        rl_main_left.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        SendRequest.getWrongQuestions(new ObtainDataListenerForErrorExerciseChapterTree(this));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_main_left:
                finish();
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        currentPosition = position;
        exerciseLoading(position);
    }

    /**
     * 加载进入做题页面
     * @param position
     */
    private void exerciseLoading(int position) {
        MobclickAgent.onEvent(this, "centerErrorOnItemClik");
        ExerciseDownloadPackage exerciseDownloadPackage = mDaoUtils.queryExerciseDownloadPackageInfo(SendRequestUtilsForExercise
                .getKeyForExercisePackageDownload());
        if (exerciseDownloadPackage == null) {
            ToastUtils.showToast(R.string.downloadfirstinfo);
            return;
        }

        //根据题号从本地取试题
        mCustomLoadingDialog.setTitle(getString(R.string.prepareexercise_ing));
        mCustomLoadingDialog.show();

        AsyncHttpClientHelper.createInstance().getThreadPool().execute(new RunnableForPrepareExerciseDatas(this, position));
    }

    private class ErrorAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            if (mCategoryBeanList == null) {
                return 0;
            }

            return mCategoryBeanList.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            CategoryBean categoryBean = mCategoryBeanList.get(position);
            ViewholderError viewHolderCategory;
            if (convertView == null) {
                convertView = View.inflate(ExerciseErrorCenterActivity.this, R.layout.item_exercisecollectionerro, null);
                viewHolderCategory = new ViewholderError();
                viewHolderCategory.tv_categoryName = (TextView) convertView.findViewById(R.id.tv_categoryName);
                viewHolderCategory.tv_exerciseNum = (TextView) convertView.findViewById(R.id.tv_exerciseNum);

                convertView.setTag(viewHolderCategory);
            }

            viewHolderCategory = (ViewholderError) convertView.getTag();
            viewHolderCategory.tv_categoryName.setText(mCategoryBeanList.get(position).name);
            viewHolderCategory.tv_exerciseNum.setText(mCategoryBeanList.get(position).qids.size() + "题");

            return convertView;
        }
    }

    private class ViewholderError {
        TextView tv_categoryName;
        TextView tv_exerciseNum;
    }

    public static void newIntent(Activity context) {
        Intent intent = new Intent(context, ExerciseErrorCenterActivity.class);
        context.startActivity(intent);
    }

    private static class ObtainDataListenerForErrorExerciseChapterTree extends ObtainDataFromNetListener<CategoryBean, String> {
        private ExerciseErrorCenterActivity activity;

        public ObtainDataListenerForErrorExerciseChapterTree(ExerciseErrorCenterActivity activity) {
            this.activity = new WeakReference<>(activity).get();
        }

        @Override
        public void onSuccess(final CategoryBean res) {
            if (activity != null) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        activity.rl_wifi.setVisibility(View.GONE);
                        activity.mCategoryBeanList = res.children;
                        activity.mCustomLoadingDialog.dismiss();
                        activity.mErrorAdapter.notifyDataSetChanged();

//                        activity.adapter.setData(activity.mCategoryBeanList);
//                        activity.adapter.notifyDataSetChanged();
                    }
                });
            }
        }

        @Override
        public void onStart() {
            if (activity != null) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        activity.mCustomLoadingDialog.setTitle(activity.getString(R.string.loading));
                        activity.mCustomLoadingDialog.show();
                    }
                });
            }
        }

        @Override
        public void onFailure(final String res) {
            if (activity != null) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        activity.mCustomLoadingDialog.dismiss();
                        if (SendRequest.ERROR_NETWORK.equals(res)) {
                            if (activity.mCategoryBeanList == null || activity.mCategoryBeanList.size() == 0) {
                                activity.rl_wifi.setVisibility(View.VISIBLE);
                            }
                            ToastUtils.showToast(R.string.network);
                        } else if (SendRequest.ERROR_SERVER.equals(res)) {
                            ToastUtils.showToast(R.string.server_error);
                        }
                    }
                });
            }
        }
    }

    private static class RunnableForPrepareExerciseDatas implements Runnable {
        private ExerciseErrorCenterActivity errorCenterActivity;
        private long id;//本次练习对应id
        private int position;//点击的位置

        /**
         * @param position 点击的位置
         */
        public RunnableForPrepareExerciseDatas(ExerciseErrorCenterActivity errorCenterActivity, int position) {
            this.errorCenterActivity = new WeakReference<>(errorCenterActivity).get();
            this.position = position;
        }

        @Override
        public void run() {
            initModuleDetailDatasFromDB();

            //重置数据，防止上次数据影响
            DataStore_ExamLibrary.resetDatas();

            DaoUtils daoutils = DaoUtils.getInstance();
            StudyRecords studyRecords = daoutils.queryStudyRecords(id);
            DoVipExerciseActivity.initExerciseData(studyRecords);
            DoVipExerciseActivity.initSelectChoiceList(studyRecords);

            if (errorCenterActivity != null) {
                errorCenterActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        errorCenterActivity.mCustomLoadingDialog.dismiss();
                        if (DataStore_ExamLibrary.questionDetailList == null || DataStore_ExamLibrary.questionDetailList.size() == 0) {
                            //该模块无题
                            ToastUtils.showToast("暂无习题");
                            return;
                        }

                        DoVipExerciseActivity.newIntent(errorCenterActivity, id, 0, 3);
                    }
                });
            }
        }

        private void initModuleDetailDatasFromDB() {
            final DaoUtils daoUtils = DaoUtils.getInstance();
            final SendRequestUtilsForExercise sendRequestUtilsForExercise = SendRequestUtilsForExercise.getInstance();
            daoUtils.getDaoSession().runInTx(new Runnable() {
                @Override
                public void run() {
                    CategoryBean categoryBean = errorCenterActivity.mCategoryBeanList.get(position);
                    StudyRecords studyRecords = daoUtils.queryStudyRecordsByType("3", categoryBean.cid, SendRequestUtilsForExercise
                            .getKeyForExercisePackageDownload(), sendRequestUtilsForExercise.userId);
                    boolean isFirstimeDoExercise = false;
                    if (studyRecords == null) {
                        //表明第一次做该真题
                        studyRecords = new StudyRecords();
                        studyRecords.setDate(new Date());
                        studyRecords.setType("3");//0今日特训 1模块题海 2真题测评 3错题中心
                        studyRecords.setUserid(sendRequestUtilsForExercise.userId);
                        studyRecords.setChoosecategory(SendRequestUtilsForExercise.getKeyForExercisePackageDownload());
                        studyRecords.setName(categoryBean.name);
                        studyRecords.setIsdelete("1");
                        studyRecords.setCid(categoryBean.cid);
                        //真题只需第一次设置count
                        studyRecords.setExercisenum(categoryBean.qids.size());

                        isFirstimeDoExercise = true;
                    }

                    long id;

                    Gson gson = new Gson();
                    studyRecords.setUsedtime(null);
                    studyRecords.setCurrentprogress(null);
                    studyRecords.setChoicesforuser(null);
                    studyRecords.setEids(gson.toJson(categoryBean.qids, ArrayList.class));

                    if (isFirstimeDoExercise) {
                        //第一次,则插入学习记录
                        id = daoUtils.insertStudyRecord(studyRecords);
                    } else {
                        //第二次,则更新学习记录
                        id = studyRecords.getId();
                        daoUtils.updateStudyRecord(studyRecords);
                    }

                    RunnableForPrepareExerciseDatas.this.id = id;
                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(UserInfo.CHOOSE_SUBJECT_RESULT_MODULE == resultCode){
            iniTitle();
        }
    }

    @Override
    public void onReceiveBroadCast(Context context, Intent intent) {
        super.onReceiveBroadCast(context, intent);
        if (intent.getAction().equals(ACTION_REPETITION_GROUP)) {
            exerciseLoading(currentPosition);
        }
    }
}
