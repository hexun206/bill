package com.huatu.teacheronline.exercise;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bluelinelabs.logansquare.LoganSquare;
import com.greendao.DaoUtils;
import com.greendao.ExerciseDownloadPackage;
import com.greendao.QuestionDetail;
import com.greendao.StudyRecords;
import com.huatu.teacheronline.BaseActivity;
import com.huatu.teacheronline.R;
import com.huatu.teacheronline.engine.AsyncHttpClientHelper;
import com.huatu.teacheronline.engine.ObtainDataFromNetListener;
import com.huatu.teacheronline.engine.SendRequest;
import com.huatu.teacheronline.exercise.bean.CategoryBean;
import com.huatu.teacheronline.exercise.bean.PaperItem;
import com.huatu.teacheronline.exercise.download.DownLoadCallBackForExercise;
import com.huatu.teacheronline.exercise.download.DownLoadInfo;
import com.huatu.teacheronline.exercise.download.DownLoadManagerForExercise;
import com.huatu.teacheronline.exercise.download.DownloadService;
import com.huatu.teacheronline.login.ExamSubjectChooseActivity;
import com.huatu.teacheronline.login.ExamSubjectChooseNewActivity;
import com.huatu.teacheronline.login.ProvinceChooseActivity;
import com.huatu.teacheronline.utils.CommonUtils;
import com.huatu.teacheronline.utils.DebugUtil;
import com.huatu.teacheronline.utils.FileUtils;
import com.huatu.teacheronline.utils.TeacherOnlineUtils;
import com.huatu.teacheronline.utils.ToastUtils;
import com.huatu.teacheronline.utils.UserInfo;
import com.huatu.teacheronline.widget.CustomAlertDialog;
import com.huatu.teacheronline.widget.MindDialog;
import com.huatu.teacheronline.widget.PopwindowUtil;
import com.umeng.analytics.MobclickAgent;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by ljzyuhenda on 16/2/19.
 * 真题测评
 */
public class ExerciseEvaluationActivity extends BaseActivity implements AdapterView.OnItemClickListener, AbsListView.OnScrollListener {
    public static final String ACTION_NEXT = "action_next";
    private ListView lv_exam;
    private ExamAdapter mExamAdapter;
    private List<PaperItem> paperItems;
    private CustomAlertDialog mCustomLoadingDialog;
    private DaoUtils mDaoUtils;
    private DownLoadManagerForExercise mDownLoadManagerForExercise;
    private CustomAlertDialog mCustomProgressDialog;
    private static final String TAG = "ExerciseEvaluationActivity";
    private CustomAlertDialog mCustomUpdateDialog;
    private int mPositionChoose;
    private DecimalFormat mDecimalFormat;
    private boolean isLoadEnd;
    private boolean hasMoreData = true;
    private View loadView;
    private View loadIcon;
    private RotateAnimation refreshingAnimation;
    private int currentPage;
    private TextView tv_main_title, tv_default_sort, tv_select;
    private ScrollView sl_select;
    private RelativeLayout rl_main_left;
    private int isSelect = 1;//1 默认 2 筛选
    private RadioGroup gv_eva_years, gv_eva_area;
    private String[] eva_years;//年份
    private String sparea;//本地保存的城市
    int pad0 = CommonUtils.dip2px(3);
    int pad1 = CommonUtils.dip2px(10);
    int pad2 = CommonUtils.dip2px(20);
    private LinearLayout ll_eva_years/*,ll_eva_area*/;
    private String area;
    private ImageView img_eva_area, img_eva_years;
    private String years;//年份
    private RelativeLayout rl_non_exerciseevaluation;//无数据
    //多科目相关
    private String subjectName;//当前选中的科目
    private ArrayList<CategoryBean> subjects;
    private PopwindowUtil popwindowUtil;

    @Override
    public void initView() {
        setContentView(R.layout.activity_exerciseevaluation);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_NEXT);
        setIntentFilter(intentFilter);
        tv_main_title = (TextView) findViewById(R.id.tv_main_title);
        tv_default_sort = (TextView) findViewById(R.id.tv_default_sort);
        tv_select = (TextView) findViewById(R.id.tv_select);
//        tv_main_title.setText(R.string.exam_test);
        rl_main_left = (RelativeLayout) findViewById(R.id.rl_main_left);
        rl_non_exerciseevaluation = (RelativeLayout)findViewById(R.id.rl_non_exerciseevaluation);
        gv_eva_years = (RadioGroup) findViewById(R.id.gv_eva_years);
        gv_eva_area = (RadioGroup) findViewById(R.id.gv_eva_area);
        ll_eva_years = (LinearLayout) findViewById(R.id.ll_eva_years);
//        ll_eva_area = (LinearLayout)findViewById(R.id.ll_eva_area);
        img_eva_area = (ImageView) findViewById(R.id.img_eva_area);
        img_eva_years = (ImageView) findViewById(R.id.img_eva_years);
        findViewById(R.id.rl_default_sort).setOnClickListener(this);
        findViewById(R.id.rl_eva_years).setOnClickListener(this);
        findViewById(R.id.rl_eva_area).setOnClickListener(this);
        findViewById(R.id.rl_select).setOnClickListener(this);
        findViewById(R.id.btn_commit_select).setOnClickListener(this);
        sl_select = (ScrollView) findViewById(R.id.sv_select);
        mDaoUtils = DaoUtils.getInstance();
        mDownLoadManagerForExercise = DownloadService.getDownloadManager_exercise();
        lv_exam = (ListView) findViewById(R.id.lv_exam);
        //下拉加载更多
        loadView = getLayoutInflater().inflate(R.layout.background_isloading, null);
        loadIcon = loadView.findViewById(R.id.loading_icon);
        refreshingAnimation = (RotateAnimation) AnimationUtils.loadAnimation(
                this, R.anim.pull_to_refresh_and_load_rotating);
        lv_exam.addFooterView(loadView);
        loadIcon.startAnimation(refreshingAnimation);
        mExamAdapter = new ExamAdapter();
        lv_exam.setAdapter(mExamAdapter);

        mDecimalFormat = new DecimalFormat("###.##");

        mCustomLoadingDialog = new CustomAlertDialog(this, R.layout.dialog_loading_custom);
        mCustomLoadingDialog.setCancelable(false);

        mCustomProgressDialog = new CustomAlertDialog(this, R.layout.dialog_showprogress);
        mCustomProgressDialog.setTitle(getString(R.string.startdownloadexercisepackage_info));
        mCustomProgressDialog.setCancelable(false);

        mCustomUpdateDialog = new CustomAlertDialog(this, R.layout.dialog_join_mydirect);
        mCustomUpdateDialog.setTitle(getString(R.string.updateinfoforexercise));
        mCustomUpdateDialog.setCancelable(false);

        loadDatas(true);
        eva_years = getResources().getStringArray(R.array.eva_years);

        iniSelectData(gv_eva_years, eva_years);

        iniSelectData(gv_eva_area, new String[]{"+"});
        gv_eva_years.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton tempButton = (RadioButton) findViewById(checkedId);
                years = tempButton.getText().toString();
//                ll_eva_area.setVisibility(View.VISIBLE);
            }
        });
        gv_eva_area.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton tempButton = (RadioButton) findViewById(checkedId);
                area = tempButton.getText().toString();
                if ("+".equals(area)) {
                    tempButton.setChecked(false);
                    ProvinceChooseActivity.newIntent(ExerciseEvaluationActivity.this, 2);
                }
//                ll_eva_area.setVisibility(View.VISIBLE);
            }
        });
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
     * 获取账单
     *
     * @param
     */
    public void loadDatas(final boolean isReset) {
        subjectName = CommonUtils.getSharedPreferenceItem(null, UserInfo.KEY_SP_EXAMSUBJECT_NAME, "");
        TeacherOnlineUtils.setTextViewRightImage(ExerciseEvaluationActivity.this, tv_main_title, R.drawable.down_ang,5);
        tv_main_title.setText(subjectName);
        //多科目
        subjects = UserInfo.getSubjects();
        if(popwindowUtil == null||subjects.size()>2){
            popwindowUtil = new PopwindowUtil(this,subjects,this);
        }else {
            popwindowUtil.getMyAdapter().setDatas(subjects);
        }
        popwindowUtil.setItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CategoryBean item = (CategoryBean) parent.getAdapter().getItem(position);
                String cid = item.cid;
                String name = item.name;
                if (name.equals(subjectName)) {
                    popwindowUtil.dismiss();
                    return;
                }
                CommonUtils.putSharedPreferenceItems(null,
                        new String[]{UserInfo.KEY_SP_EXAMSUBJECT_ID, UserInfo.KEY_SP_EXAMSUBJECT_NAME},
                        new String[]{cid, name});
                popwindowUtil.dismiss();
                subjectName = item.name;
                defaultSort();
            }
        });
        if (isReset) {
            currentPage = 1;
        } else {
            currentPage++;
        }

        SendRequest.getPaperList(currentPage, years, new ObtainDataFromListenerForExerciseEvaluation(this, isReset));
    }

    /**
     * 上拉加载更多
     */
    private void completeRefresh() {
        if (lv_exam != null && lv_exam.getFooterViewsCount() > 0) {
            lv_exam.removeFooterView(loadView);
        }

        if (mExamAdapter != null) {
            mExamAdapter.notifyDataSetChanged();
        }

        if (hasMoreData) {
            isLoadEnd = true;
        } else {
            isLoadEnd = false;
        }
    }

    @Override
    public void setListener() {
        lv_exam.setOnItemClickListener(this);
        mCustomUpdateDialog.setOkOnClickListener(this);
        mCustomUpdateDialog.setCancelOnClickListener(this);
        lv_exam.setOnScrollListener(this);
        rl_main_left.setOnClickListener(this);
        tv_main_title.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_dialog_cancel:
                MobclickAgent.onEvent(this, "zhentiUpdateClickCancel");
                mCustomUpdateDialog.dismiss();
                //取消更新,直接做题
                AsyncHttpClientHelper.createInstance().getThreadPool().execute(new RunnableForPrepareExerciseDatas(this, mPositionChoose));
                break;
            case R.id.tv_dialog_ok:
                MobclickAgent.onEvent(this, "zhentiUpdateClickOK");
                //进行更新
                PaperItem paperItem = paperItems.get(mPositionChoose);
                //开始下载该题包
                mDownLoadManagerForExercise.startDownLoadTask(paperItem.pid, 1, new DownLoadCallBackExerciseWithDialog(DownLoadCallBackForExercise
                        .DownLoadExerciseType.ExerciseEvaluationActivity, this));
                mCustomUpdateDialog.dismiss();

                break;
            case R.id.rl_main_left:
                finish();
                break;
            case R.id.btn_commit_select:
                sl_select.setVisibility(View.GONE);
                lv_exam.setVisibility(View.VISIBLE);
                isSelect = 2;
                tv_default_sort.setTextColor(getResources().getColor(R.color.black));
                tv_select.setTextColor(getResources().getColor(R.color.green001));
                Drawable drawable1 = getResources().getDrawable(R.drawable.ic_shaixuan_on);
                drawable1.setBounds(0, 0, drawable1.getMinimumWidth(), drawable1.getMinimumHeight());
                tv_select.setCompoundDrawables(null, null, drawable1, null);
                if (paperItems == null) {
                    return;
                }
                MobclickAgent.onEvent(this, "zhentiScreeningSubmission");
                paperItems.clear();
                lv_exam.addFooterView(loadView);
                loadIcon.startAnimation(refreshingAnimation);
                mExamAdapter.notifyDataSetChanged();
                loadDatas(true);

                break;
            case R.id.rl_default_sort:
                defaultSort();
                break;
            case R.id.rl_select:
                MobclickAgent.onEvent(this, "zhentiScreening");
                lv_exam.setVisibility(View.GONE);
                sl_select.setVisibility(View.VISIBLE);
                tv_default_sort.setTextColor(getResources().getColor(R.color.black));
                tv_select.setTextColor(getResources().getColor(R.color.green001));
                Drawable drawable2 = getResources().getDrawable(R.drawable.ic_shaixuan_on);
                drawable2.setBounds(0, 0, drawable2.getMinimumWidth(), drawable2.getMinimumHeight());
                tv_select.setCompoundDrawables(null, null, drawable2, null);
                iniSelectData(gv_eva_years, eva_years);

                break;
            case R.id.rl_eva_years:
                expandGridview(gv_eva_years, img_eva_years);
                break;
            case R.id.rl_eva_area:
                expandGridview(gv_eva_area, img_eva_area);
                break;
            case R.id.tv_main_title:
                popwindowUtil.show(tv_main_title,2);
                break;
            case R.id.rl_creat_sub:
                Intent intent = new Intent();
                intent.setClass(ExerciseEvaluationActivity.this, ExamSubjectChooseNewActivity.class);
                intent.putExtra("whereFrom", 1);
                UserInfo.setTempSubjectsIdName();
                startActivityForResult(intent, UserInfo.CHOOSE_SUBJECT_RESULT_MODULE);
                popwindowUtil.dismiss();
                break;
        }
    }

    /**
     * 默认排序
     */
    private void defaultSort() {

        isSelect = 1;
        lv_exam.setVisibility(View.VISIBLE);
        sl_select.setVisibility(View.GONE);
        tv_default_sort.setTextColor(getResources().getColor(R.color.green001));
        tv_select.setTextColor(getResources().getColor(R.color.black));
        Drawable drawable = getResources().getDrawable(R.drawable.ic_shaixuan);
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        tv_select.setCompoundDrawables(null, null, drawable, null);
//        if (paperItems == null) {
//            return;
//        }
        MobclickAgent.onEvent(this, "zhentiDefaultSort");
        years = null;
        if(paperItems != null){
            paperItems.clear();
        }
        lv_exam.addFooterView(loadView);
        loadIcon.startAnimation(refreshingAnimation);
        mExamAdapter.notifyDataSetChanged();
        loadDatas(true);
    }

    public static void newIntent(Activity context) {
        Intent intent = new Intent(context, ExerciseEvaluationActivity.class);
        context.startActivity(intent);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (view.getId() == R.id.ll_loading) {
            //此时触发的是刷新item

            return;
        }
        MobclickAgent.onEvent(this, "zhentiOnItemClick");
        mPositionChoose = position;
        PaperItem paperItem = paperItems.get(position);
        String key = paperItem.pid;
        ExerciseDownloadPackage exerciseDownloadPackage = mDaoUtils.queryExerciseDownloadPackageInfo(key);
        DownLoadInfo downLoadInfo = initDownLoadInfo(key, paperItem.versions);

        DebugUtil.i("tv_commit:" + (exerciseDownloadPackage != null && !downLoadInfo.isUpdate) + ",downloadstatus:" + downLoadInfo.status);
        if (exerciseDownloadPackage != null && !downLoadInfo.isUpdate) {
            //保存数据库成功
            if (!exerciseDownloadPackage.getVersions().equals(paperItem.versions)) {
                //有新版本数据
                showUpdateDialog();
            } else {
                //保存数据库成功且非更新状态(亦无新版本数据),则可以做题
                mCustomLoadingDialog.setTitle(getString(R.string.prepareexercise_ing));
                mCustomLoadingDialog.show();
                AsyncHttpClientHelper.createInstance().getThreadPool().execute(new RunnableForPrepareExerciseDatas(this, position));
            }
        } else if (DownLoadCallBackForExercise.DownLoadExerciseStatus.DownLoading == downLoadInfo.status || DownLoadCallBackForExercise
                .DownLoadExerciseStatus.Success == downLoadInfo.status) {
            //第一次下载中或者更新中 或者下载成功尚未数据库插入完成
//                    ((DownLoadCallBackForExercise) downLoadInfo.listener).refreshItems();
        } else {
            //开始下载该题包
            mDownLoadManagerForExercise.startDownLoadTask(paperItem.pid, 1, new DownLoadCallBackExerciseWithDialog(DownLoadCallBackForExercise
                    .DownLoadExerciseType.ExerciseEvaluationActivity, this));
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
            if (isLoadEnd) {
                if (view.getLastVisiblePosition() == view.getCount() - 1) {
                    lv_exam.addFooterView(loadView);
                    loadIcon.startAnimation(refreshingAnimation);
                    loadDatas(false);
                }
            }
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

    }

    private class ExamAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            if (paperItems == null) {
                return 0;
            }

            return paperItems.size();
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
            PaperItem paperItem = paperItems.get(position);
            if (paperItem.ptimelimit == 0) {
                paperItem.ptimelimit = paperItem.count * 0.6f;//
            }

            ViewHolderExam viewHolder;
            if (convertView == null) {
                convertView = View.inflate(ExerciseEvaluationActivity.this, R.layout.item_examlist_exerciseevaluation, null);
                viewHolder = new ViewHolderExam();
                viewHolder.tv_examname = (TextView) convertView.findViewById(R.id.tv_examname);

                convertView.setTag(viewHolder);
            }

            viewHolder = (ViewHolderExam) convertView.getTag();
            viewHolder.tv_examname.setText(paperItem.name);

            return convertView;
        }
    }

    private class ViewHolderExam {
        TextView tv_examname;
        TextView tv_totalnum;
        TextView tv_time_recommand;
    }

    private static class ObtainDataFromListenerForExerciseEvaluation extends ObtainDataFromNetListener<List<PaperItem>, String> {
        private ExerciseEvaluationActivity exerciseEvaluationActivity;
        private boolean isReset;

        public ObtainDataFromListenerForExerciseEvaluation(ExerciseEvaluationActivity exerciseEvaluationActivity, boolean isReset) {
            this.exerciseEvaluationActivity = new WeakReference<>(exerciseEvaluationActivity).get();

            this.isReset = isReset;
        }

        @Override
        public void onSuccess(final List<PaperItem> res) {
            if (exerciseEvaluationActivity != null) {
                exerciseEvaluationActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (isReset && (res == null || res.size() == 0)) {
                            exerciseEvaluationActivity.hasMoreData = false;
                            ToastUtils.showToast(R.string.no_data);
                            exerciseEvaluationActivity.lv_exam.setVisibility(View.GONE);
                            exerciseEvaluationActivity.rl_non_exerciseevaluation.setVisibility(View.VISIBLE);
                        } else if (!isReset && (res == null || res.size() == 0)) {
                            exerciseEvaluationActivity.hasMoreData = false;
                            ToastUtils.showToast(R.string.no_more);
                        } else {
                            exerciseEvaluationActivity.lv_exam.setVisibility(View.VISIBLE);
                            exerciseEvaluationActivity.rl_non_exerciseevaluation.setVisibility(View.GONE);
                            exerciseEvaluationActivity.hasMoreData = true;
                            if (exerciseEvaluationActivity.paperItems == null) {
                                exerciseEvaluationActivity.paperItems = new ArrayList<>();
                            }
                            exerciseEvaluationActivity.paperItems.addAll(res);
                        }

                        exerciseEvaluationActivity.completeRefresh();
                    }
                });
            }
        }

        @Override
        public void onStart() {
            if (exerciseEvaluationActivity != null) {
                exerciseEvaluationActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        exerciseEvaluationActivity.isLoadEnd = false;
                    }
                });
            }
        }

        @Override
        public void onFailure(String res) {
            if (exerciseEvaluationActivity != null) {
                exerciseEvaluationActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        exerciseEvaluationActivity.isLoadEnd = true;
                        exerciseEvaluationActivity.completeRefresh();
                    }
                });
            }
        }
    }

    public DownLoadInfo initDownLoadInfo(String key, String version) {
        DownLoadInfo downloadInfoRes = mDownLoadManagerForExercise.getDownLoadInfo(key);

        if (downloadInfoRes == null) {
            downloadInfoRes = new DownLoadInfo();
            downloadInfoRes.versions = version;

            mDownLoadManagerForExercise.downloadInfoList.put(key, downloadInfoRes);
        }

        //防止version有变动->更新为最新version
        downloadInfoRes.versions = version;

        return downloadInfoRes;
    }

    private static class RunnableForPrepareExerciseDatas implements Runnable {
        private ExerciseEvaluationActivity exerciseEvaluationActivity;
        private long id;//本次练习对应id
        private int position;//点击的位置

        /**
         * @param position 点击的位置
         */
        public RunnableForPrepareExerciseDatas(ExerciseEvaluationActivity exerciseEvaluationActivity, int position) {
            this.exerciseEvaluationActivity = new WeakReference<>(exerciseEvaluationActivity).get();
            this.position = position;
        }

        @Override
        public void run() {
            initModuleDetailDatasFromDB();

            //重置数据，防止上次数据影响
            DataStore_ExamLibrary.resetDatas();

            DaoUtils daoutils = DaoUtils.getInstance();
            StudyRecords studyRecords = daoutils.queryStudyRecords(id);
            //更新学习记录为未删除
            studyRecords.setIsdelete("0");

            DoVipExerciseActivity.initExerciseDataForExerciseEvaluation(studyRecords);
            if (DataStore_ExamLibrary.questionDetailList != null) {
                //更新题目数据
                studyRecords.setExercisenum(DataStore_ExamLibrary.questionDetailList.size());
            }
            daoutils.updateStudyRecord(studyRecords);

            DoVipExerciseActivity.initSelectChoiceList(studyRecords);

            if (exerciseEvaluationActivity != null) {

                exerciseEvaluationActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        exerciseEvaluationActivity.mCustomLoadingDialog.dismiss();
                        if (DataStore_ExamLibrary.questionDetailList == null || DataStore_ExamLibrary.questionDetailList.size() == 0) {
                            //该模块无题
                            ToastUtils.showToast(R.string.noexericse);
                            return;
                        }

                        DoVipExerciseActivity.newIntent(exerciseEvaluationActivity, id, 0,2);
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
                    PaperItem paperItem = exerciseEvaluationActivity.paperItems.get(position);
                    StudyRecords studyRecords = daoUtils.queryStudyRecordsByType("2", paperItem.pid, SendRequestUtilsForExercise
                            .getKeyForExercisePackageDownload(), sendRequestUtilsForExercise.userId);
                    boolean isFirstimeDoExercise = false;
                    if (studyRecords == null) {
                        //表明第一次做该真题
                        studyRecords = new StudyRecords();
                        studyRecords.setDate(new Date());
                        studyRecords.setType("2");//0今日特训 1模块题海 2真题测评 3错题中心
                        studyRecords.setUserid(sendRequestUtilsForExercise.userId);
                        studyRecords.setChoosecategory(SendRequestUtilsForExercise.getKeyForExercisePackageDownload());
                        studyRecords.setName(paperItem.name);
                        studyRecords.setIsdelete("0");
                        studyRecords.setCid(paperItem.pid);
                        //真题只需第一次设置count
                        studyRecords.setExercisenum(paperItem.count);

                        isFirstimeDoExercise = true;
                    }

                    long id;
                    if ("1".equals(studyRecords.getCompleted()) || isFirstimeDoExercise) {
                        //表明已完成或者第一次,重置进度、时间等信息
//                        studyRecords.setUsedtime(null);
//                        studyRecords.setCurrentprogress(null);
//                        studyRecords.setChoicesforuser(null);

                        if (isFirstimeDoExercise) {
                            //第一次,则插入学习记录
                            id = daoUtils.insertStudyRecord(studyRecords);
                        } else {
                            //第二次,则更新学习记录
                            id = studyRecords.getId();
                            daoUtils.updateStudyRecord(studyRecords);
                        }
                    } else {
                        //上次未完成,直接去上次记录继续做题
                        id = studyRecords.getId();
                    }

                    RunnableForPrepareExerciseDatas.this.id = id;
                }
            });
        }
    }

    private static class DownLoadCallBackExerciseWithDialog extends DownLoadCallBackForExercise {
        private ExerciseEvaluationActivity exerciseEvaluationActivity;

        public DownLoadCallBackExerciseWithDialog(DownLoadExerciseType type, ExerciseEvaluationActivity exerciseEvaluationActivity) {
            super(type);

            this.exerciseEvaluationActivity = exerciseEvaluationActivity;
        }

        @Override
        public void onStart(final DownLoadExerciseStatus status) {
            if (exerciseEvaluationActivity != null && !exerciseEvaluationActivity.isDestoryed) {
                exerciseEvaluationActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        DownLoadCallBackExerciseWithDialog.super.onStart(status);
                        exerciseEvaluationActivity.mCustomProgressDialog.show();
                        exerciseEvaluationActivity.mCustomProgressDialog.setProgress(0, 0);
                    }
                });
            }
        }

        @Override
        public void onFailure(final DownLoadExerciseStatus status) {
            if (exerciseEvaluationActivity != null && !exerciseEvaluationActivity.isDestoryed) {
                exerciseEvaluationActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        DownLoadCallBackExerciseWithDialog.super.onFailure(status);
                        exerciseEvaluationActivity.mCustomProgressDialog.dismiss();
                        ToastUtils.showToast(R.string.network);
                    }
                });
            }
        }

        @Override
        public void onProcess(final long bytesWritten, final long totalSize, final DownLoadExerciseStatus status) {
            if (exerciseEvaluationActivity != null && !exerciseEvaluationActivity.isDestoryed) {
                exerciseEvaluationActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        DownLoadCallBackExerciseWithDialog.super.onProcess(bytesWritten, totalSize, status);
                        exerciseEvaluationActivity.mCustomProgressDialog.setProgress(bytesWritten, totalSize);
                    }
                });
            }
        }

        @Override
        public void onSuccess(final byte[] responseBody, final String key, final DownLoadExerciseStatus status) {
            initDatasIntoDb(responseBody, key);
            DebugUtil.i("init db->onSuccess");
            if (exerciseEvaluationActivity != null && !exerciseEvaluationActivity.isDestoryed) {
                exerciseEvaluationActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        DebugUtil.i("refreshItems->onSuccess");
                        DownLoadCallBackExerciseWithDialog.super.onSuccess(responseBody, key, status);
                        exerciseEvaluationActivity.mCustomProgressDialog.dismiss();

                        //更新完毕开始做题
                        exerciseEvaluationActivity.mCustomLoadingDialog.setTitle(exerciseEvaluationActivity.getString(R.string.prepareexercise_ing));
                        exerciseEvaluationActivity.mCustomLoadingDialog.show();
                        AsyncHttpClientHelper.createInstance().getThreadPool().execute(new RunnableForPrepareExerciseDatas(exerciseEvaluationActivity,
                                exerciseEvaluationActivity.mPositionChoose));
                    }
                });
            }
        }
    }


    private static void initDatasIntoDb(byte[] responseBody, final String key) {
        DateFormat dateFormat = new SimpleDateFormat("HH-mm-ss-SSSS");
        String strBuilder = new String(responseBody);
        DebugUtil.i(TAG, "readFromFile-end:" + dateFormat.format(new Date()));
        final DaoUtils daoUtils = DaoUtils.getInstance();
        if (strBuilder == null) {
            updateDownloadPckageInfoForNullQues(daoUtils, key);

            return;
        }
        String res = FileUtils.gunzip(strBuilder);
        if (res == null) {
            updateDownloadPckageInfoForNullQues(daoUtils, key);

            return;
        }
        DebugUtil.i(TAG, "gunZip-end:" + dateFormat.format(new Date()));
        //本地题包全部习题
        final List<QuestionDetail> questions;
        try {
            questions = LoganSquare.parseList(res, QuestionDetail.class);
        } catch (IOException e) {
            e.printStackTrace();
            updateDownloadPckageInfoForNullQues(daoUtils, key);

            return;
        }

        DebugUtil.i(TAG, "parseJsonArr-end:" + dateFormat.format(new Date()));

        daoUtils.getDaoSession().runInTx(new Runnable() {
            @Override
            public void run() {
                DownLoadInfo downloadInfoRes = DownloadService.getDownloadManager_exercise().getDownLoadInfo(key);
                if (downloadInfoRes == null) {
                    return;
                }

                //插入习题数据
                String id = key;
                daoUtils.deleteQuestionDetail(id);
                for (QuestionDetail questionDetailEve : questions) {
                    questionDetailEve.setId(id);
                    DebugUtil.e("插入数据库的题目："+questionDetailEve.getQuestion());
                    daoUtils.insertQuestionDetail(questionDetailEve);
                }

                //更新题包下载信息-》downloadstatus以及versions
                daoUtils.inserOrUpdateExerciseDownloadPackageInfo(id, downloadInfoRes.versions);
                DebugUtil.i("updateExerciseDb->onSuccess");
            }
        });
    }

    private void showUpdateDialog() {
        mCustomUpdateDialog.show();
    }

    //当前题包无题,更新题包下载状态
    private static void updateDownloadPckageInfoForNullQues(DaoUtils daoUtils, String key) {
        //更新题包下载信息-》downloadstatus以及versions
        String id = key;
        DownLoadInfo downloadInfoRes = DownloadService.getDownloadManager_exercise().getDownLoadInfo(key);
        if (downloadInfoRes == null) {
            return;
        }
        daoUtils.inserOrUpdateExerciseDownloadPackageInfo(id, downloadInfoRes.versions);
    }

    /**
     * 筛选数据适配
     **/
    public void iniSelectData(RadioGroup contentRg, String... datas) {
        contentRg.removeAllViews();
        for (int i = 0; i < datas.length; i++) {
            RadioButton tempButton = new RadioButton(this);
            tempButton.setTextColor(getResources().getColor(R.color.text_check_staus));
            tempButton.setBackgroundResource(R.drawable.bg_select_gv);   // 设置RadioButton的背景图片
            tempButton.setButtonDrawable(android.R.color.transparent);
//            tempButton.setButtonDrawable(R.drawable.xxx);           // 设置按钮的样式
//            tempButton.setPadding(pad2, pad1, pad2, pad1);                 // 设置文字距离按钮四周的距离

            RadioGroup.LayoutParams lp = new RadioGroup.LayoutParams(RadioGroup.LayoutParams.WRAP_CONTENT, RadioGroup.LayoutParams.WRAP_CONTENT);
            lp.setMargins(pad2, pad1, pad2, pad1);

            if (datas[i].length() == 1 || datas[i].length() == 2 || datas[i].length() == 3 || datas[i].length() == 4) {
                lp.width = CommonUtils.dip2px(75);
                lp.height = CommonUtils.dip2px(36);
            } else if (datas[i].length() == 5 || datas[i].length() == 6) {
                lp.width = CommonUtils.dip2px(100);
                lp.height = CommonUtils.dip2px(36);
            } else if (datas[i].length() > 5) {
                lp.width = CommonUtils.dip2px(120);
                lp.height = CommonUtils.dip2px(36);
            }
            tempButton.setGravity(Gravity.CENTER);
            tempButton.setLayoutParams(lp);
            tempButton.setText(datas[i]);
            contentRg.addView(tempButton);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 2) {
            String dataStringExtra = data.getStringExtra(UserInfo.KEY_SP_CITY_NAME);
            if (dataStringExtra.trim().length() > 0) {
                iniSelectData(gv_eva_area, new String[]{dataStringExtra});
                RadioButton checkedButton = (RadioButton) gv_eva_area.getChildAt(0);
                checkedButton.setChecked(true);
            }
        }
        if(UserInfo.CHOOSE_SUBJECT_RESULT_MODULE == resultCode){
            defaultSort();
        }

    }

    /**
     * 展开筛选 gridview
     */
    private static void expandGridview(RadioGroup gv_content, ImageView img_content) {
        if (gv_content.getVisibility() == View.GONE) {
            gv_content.setVisibility(ViewGroup.VISIBLE);
            img_content.setImageResource(R.drawable.tri_up_more);
        } else {
            gv_content.setVisibility(ViewGroup.GONE);
            img_content.setImageResource(R.drawable.tri_down_hide);
        }
    }

    @Override
    public void onReceiveBroadCast(Context context, Intent intent) {
        super.onReceiveBroadCast(context, intent);
        AsyncHttpClientHelper.createInstance().getThreadPool().execute(new RunnableForPrepareExerciseDatas(this, mPositionChoose));
    }
}
