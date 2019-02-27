package com.huatu.teacheronline.exercise;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bluelinelabs.logansquare.LoganSquare;
import com.gensee.utils.StringUtil;
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
import com.huatu.teacheronline.utils.CommonUtils;
import com.huatu.teacheronline.utils.DebugUtil;
import com.huatu.teacheronline.utils.FileUtils;
import com.huatu.teacheronline.utils.TeacherOnlineUtils;
import com.huatu.teacheronline.utils.ToastUtils;
import com.huatu.teacheronline.utils.UserInfo;
import com.huatu.teacheronline.widget.CustomAlertDialog;
import com.huatu.teacheronline.widget.MindDialog;
import com.huatu.teacheronline.widget.PopwindowDoExercise;
import com.huatu.teacheronline.widget.PopwindowUtil;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
/**
 *  真题演练
 * Created by ply on 2016/1/5.
 */
public class ExerciseDeductionActivity extends BaseActivity implements AdapterView.OnItemClickListener, AbsListView.OnScrollListener {
    public static final String ACTION_NEXT = "action_next";
    private TextView tv_main_title;
    private String mobile;
    private RelativeLayout rl_main_left;
    private RelativeLayout rl_non_exerciseevaluation;
    private RelativeLayout rl_wifi;
    private RelativeLayout mRlTitle;
    private DaoUtils mDaoUtils;
    private DownLoadManagerForExercise mDownLoadManagerForExercise;
    private ListView lv_exam;
    private boolean isLoadEnd;
    private boolean hasMoreData = true;
    private View loadView;
    private View loadIcon;
    private RotateAnimation refreshingAnimation;
    private int currentPage;
    private ExamAdapter mExamAdapter;
    private CustomAlertDialog mCustomLoadingDialog;
    private CustomAlertDialog mCustomProgressDialog;
    private static final String TAG = "ExercisemodetionActivity";
    private CustomAlertDialog mCustomUpdateDialog;
    private DecimalFormat mDecimalFormat;
    private CustomAlertDialog mMindDialog; //做题相关提示
    private String subjectName;
    private ArrayList<CategoryBean> subjects;
    private PopwindowUtil popwindowUtil;
    private String years;//年份
    private int isSelect = 2;//2 真题估分
    private int mPositionChoose;
    private String name;
    private String pattentions;
    private List<PaperItem> paperItems;
    private int onlyOne;
    private ImageView ib_main_right;
    private RelativeLayout rl_main_right;
    private PopwindowDoExercise popwindowDoExercise;


    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void initView() {
        setContentView(R.layout.activity_exercise_deduction);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_NEXT);
        setIntentFilter(intentFilter);
        tv_main_title = (TextView) findViewById(R.id.tv_main_title);
        ib_main_right = (ImageView) findViewById(R.id.ib_main_right);
//        ib_main_right.setImageResource(R.drawable.more_dot);
        ib_main_right.setVisibility(View.GONE);
        TextView tv_main_right= (TextView) findViewById(R.id.tv_main_right);
        tv_main_right.setVisibility(View.VISIBLE);
        tv_main_right.setBackgroundResource(R.drawable.bg_rectangle_frame_green_radius);
        tv_main_right.setText(R.string.analysisAndstore);
        tv_main_right.setTextColor(getResources().getColor(R.color.green004));
        rl_main_right = (RelativeLayout) findViewById(R.id.rl_main_right);
        rl_main_right.setVisibility(View.VISIBLE);
        mobile = CommonUtils.getSharedPreferenceItem(null, UserInfo.KEY_SP_MOBILE, "");
        rl_main_left = (RelativeLayout) findViewById(R.id.rl_main_left);
        rl_non_exerciseevaluation = (RelativeLayout) findViewById(R.id.rl_non_exerciseevaluation);
        rl_wifi = (RelativeLayout) findViewById(R.id.rl_wifi);
        mRlTitle = (RelativeLayout) findViewById(R.id.rl_titleContainer_main);
        mDownLoadManagerForExercise = DownloadService.getDownloadManager_exercise();
        lv_exam = (ListView) findViewById(R.id.lv_exam);
        popwindowDoExercise = new PopwindowDoExercise(this,this);
        //下拉加载更多
        loadView = getLayoutInflater().inflate(R.layout.background_isloading, null);
        loadIcon = loadView.findViewById(R.id.loading_icon);
        refreshingAnimation = (RotateAnimation) AnimationUtils.loadAnimation(
                this, R.anim.pull_to_refresh_and_load_rotating);
        lv_exam.addFooterView(loadView);
        loadIcon.startAnimation(refreshingAnimation);
        mDaoUtils = DaoUtils.getInstance();
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
        mMindDialog = new CustomAlertDialog(this, R.layout.dialog_join_mydirect);
//        loadDatas(true);
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
        TeacherOnlineUtils.setTextViewRightImage(ExerciseDeductionActivity.this, tv_main_title, R.drawable.down_ang, 5);
        tv_main_title.setText(subjectName);
        //多科目
        subjects = UserInfo.getSubjects();
        if (popwindowUtil == null || subjects.size() > 2) {
            popwindowUtil = new PopwindowUtil(this, subjects, this);
        } else {
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
        isLoadEnd = hasMoreData;
    }

    @Override
    protected void onResume() {
        super.onResume();
        defaultSort();
    }

    @Override
    public void setListener() {
        lv_exam.setOnItemClickListener(this);
        mCustomUpdateDialog.setOkOnClickListener(this);
        mCustomUpdateDialog.setCancelOnClickListener(this);
        lv_exam.setOnScrollListener(this);
        rl_main_left.setOnClickListener(this);
        tv_main_title.setOnClickListener(this);
        mMindDialog.setOkOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMindDialog.dismiss();
            }
        });
        mMindDialog.setCancelOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMindDialog.dismiss();
            }
        });
        rl_main_right.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_dialog_cancel:
                MobclickAgent.onEvent(this, "zhentiUpdateClickCancel");
                mCustomUpdateDialog.dismiss();
                //取消更新,直接做题
                AsyncHttpClientHelper.createInstance().getThreadPool().execute(new RunnableForPrepareExerciseDatas(this, mPositionChoose, name, pattentions,
                        isSelect));
                break;
            case R.id.tv_dialog_ok:
                MobclickAgent.onEvent(this, "zhentiUpdateClickOK");
                //进行更新
                PaperItem paperItem = paperItems.get(mPositionChoose);
                //开始下载该题包
//                if(isSelect == 5){//如果为模考大赛，则调用模块大赛试题包接口、
//                    mDownLoadManagerForExercise.startDownLoadTask(paperItem.pid, isSelect, new DownLoadCallBackExerciseWithDialog(DownLoadCallBackForExercise
//                            .DownLoadExerciseType.ExerciseEvaluationActivity, this));
//                }else{
                mDownLoadManagerForExercise.startDownLoadTask(paperItem.pid, isSelect, new DownLoadCallBackExerciseWithDialog(DownLoadCallBackForExercise
                        .DownLoadExerciseType.ExerciseEvaluationActivity, this));
//                }
                mCustomUpdateDialog.dismiss();
                break;
            case R.id.rl_main_left:
                finish();
                break;
            case R.id.tv_main_title:
                popwindowUtil.show(mRlTitle, 2);
                break;
            case R.id.rl_creat_sub:
                Intent intent = new Intent();
                intent.setClass(ExerciseDeductionActivity.this, ExamSubjectChooseNewActivity.class);
                intent.putExtra("whereFrom", 1);
                UserInfo.setTempSubjectsIdName();
                startActivityForResult(intent, UserInfo.CHOOSE_SUBJECT_RESULT_MODULE);
                popwindowUtil.dismiss();
                break;
            case R.id.rl_main_right:
                popwindowDoExercise.setData(4);
                popwindowDoExercise.show(rl_main_right);
                break;
            case R.id.tv_collection_exercise://我的收藏
                popwindowDoExercise.dismiss();
                MobclickAgent.onEvent(this, "myCollection");
//                ExerciseCollectionActivity.newIntent(this);
                ExercisePaperErrorCollectionActivity.newIntent(this, "zhenti", "collect");

                break;
            case R.id.tv_share_exercise://我的错题
                popwindowDoExercise.dismiss();
                MobclickAgent.onEvent(this, "centerError");
                //错题中心
                ExercisePaperErrorCollectionActivity.newIntent(this, "zhenti", "error");
//                ExerciseErrorCenterActivity.newIntent(this);
                break;
        }
    }
    /**
     * 默认排序
     */
    private void defaultSort() {
        lv_exam.setVisibility(View.VISIBLE);
//        sl_select.setVisibility(View.GONE);
//        if (paperItems == null) {
//            return;
//        }
        MobclickAgent.onEvent(this, "zhentiDefaultSort");
        years = null;
        if (paperItems != null) {
            paperItems.clear();
        }
        lv_exam.addFooterView(loadView);
        loadIcon.startAnimation(refreshingAnimation);
        mExamAdapter.notifyDataSetChanged();
        loadDatas(true);
    }

    public static void newIntent(Activity context) {
        Intent intent = new Intent(context, ExerciseDeductionActivity.class);
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
        //是否活动卷
//        if (paperItem.onlyOne == 1) {
//            if (StringUtil.isEmpty(mobile.trim().toString())) {
//                Binding_iphone(view);//如果没绑定手机号则弹出绑定手机框
//                return;
//            }
//            //活动状态 0：未开始，1正在进行，2结束
//            if (paperItem.userone == 1) {
//                //0可做 1次数超过 2未开始 3活动结束不可做
//                mMindDialog.setTitle("试卷做题次数超出");
//                mMindDialog.show();
//                mMindDialog.setCancelGone();
//                return;
//            } else if (paperItem.userone == 2) {
//                mMindDialog.setTitle("活动未开始");
//                mMindDialog.show();
//                mMindDialog.setCancelGone();
//                return;
//            } else if (paperItem.userone == 3) {
//                //学员是否可做
//                mMindDialog.setTitle("活动已结束");
//                mMindDialog.show();
//                mMindDialog.setCancelGone();
//                return;
//            }
//        }
        MobclickAgent.onEvent(this, "zhentiOnItemClick");
        String key = paperItem.pid;
        pattentions = paperItem.pattentions;
        onlyOne = paperItem.onlyOne;
        name = paperItem.name;
        ExerciseDownloadPackage exerciseDownloadPackage = mDaoUtils.queryExerciseDownloadPackageInfo(key);
        DownLoadInfo downLoadInfo = initDownLoadInfo(key, paperItem.versions);
        DebugUtil.i("tv_commit:" + (exerciseDownloadPackage != null && !downLoadInfo.isUpdate) + ",downloadstatus:" + downLoadInfo.status);
//        if (exerciseDownloadPackage != null && !downLoadInfo.isUpdate) {
//            //保存数据库成功
//            if (!exerciseDownloadPackage.getVersions().equals(paperItem.versions)) {
//                //有新版本数据
//                showUpdateDialog();
//            } else {
//                //保存数据库成功且非更新状态(亦无新版本数据),则可以做题
//                mCustomLoadingDialog.setTitle(getString(R.string.prepareexercise_ing));
//                mCustomLoadingDialog.show();
//                AsyncHttpClientHelper.createInstance().getThreadPool().execute(new RunnableForPrepareExerciseDatas(ExerciseDeductionActivity.this, position, name, pattentions,
//                        isSelect));
//            }
//        } else if (DownLoadCallBackForExercise.DownLoadExerciseStatus.DownLoading == downLoadInfo.status || DownLoadCallBackForExercise
//                .DownLoadExerciseStatus.Success == downLoadInfo.status) {
//            //第一次下载中或者更新中 或者下载成功尚未数据库插入完成
////                    ((DownLoadCallBackForExercise) downLoadInfo.listener).refreshItems();
//        } else {
            //开始下载该题包
//            if(isSelect==5){//如果为模考大赛，则调用模块大赛试题包接口、
            mDownLoadManagerForExercise.startDownLoadTask(paperItem.pid, isSelect, new DownLoadCallBackExerciseWithDialog(DownLoadCallBackForExercise
                    .DownLoadExerciseType.ExerciseEvaluationActivity, ExerciseDeductionActivity.this));
//            }else{
//                mDownLoadManagerForExercise.startDownLoadTask(paperItem.pid, 1, new DownLoadCallBackExerciseWithDialog(DownLoadCallBackForExercise
//                        .DownLoadExerciseType.ExerciseEvaluationActivity, this));
//            }
//        }
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
                paperItem.ptimelimit = paperItem.count * 0.6f;
            }
            ViewHolderExam viewHolder;
            if (convertView == null) {
                convertView = View.inflate(ExerciseDeductionActivity.this, R.layout.exermode_tim, null);
                viewHolder = new ViewHolderExam();
                viewHolder.tv_examname = (TextView) convertView.findViewById(R.id.tv_examname);
                viewHolder.tv_fraction = (TextView) convertView.findViewById(R.id.Fraction);
                viewHolder.tv_raik = (TextView) convertView.findViewById(R.id.raik);
                viewHolder.iv_bs= (ImageView) convertView.findViewById(R.id.iv_bs);
                viewHolder.iv_new= (ImageView) convertView.findViewById(R.id.iv_new);
                convertView.setTag(viewHolder);
            }
            viewHolder = (ViewHolderExam) convertView.getTag();
            viewHolder.tv_examname.setText(paperItem.name);
            viewHolder.iv_bs.setImageResource(R.drawable.more_arrow);
            if (paperItem.rank.equals("-1") || paperItem.score.equals("-1")) {
                viewHolder.tv_fraction.setText("试试手，马上开始做题");
                viewHolder.tv_raik.setVisibility(View.GONE);
            }else{
                viewHolder.tv_raik.setVisibility(View.VISIBLE);
                viewHolder.tv_raik.setText("排名：" + paperItem.rank);
                viewHolder.tv_fraction.setText("分数：" + paperItem.score);
            }

            if (paperItem.onlyOne == 1){
              if (paperItem.userone == 2){
                  viewHolder.tv_fraction.setText("模考尚未开始");
                  viewHolder.tv_raik.setVisibility(View.GONE);
              }else if (paperItem.userone == 3&&paperItem.rank.equals("-1") || paperItem.score.equals("-1")){
                  viewHolder.tv_fraction.setText("模考已结束");
                  viewHolder.tv_raik.setVisibility(View.GONE);
              }
            }

            if (paperItem.isnew==0){
                viewHolder.iv_new.setVisibility(View.GONE);
            }else{
                viewHolder.iv_new.setVisibility(View.VISIBLE);
            }
            return convertView;
        }
    }

    private class ViewHolderExam {
        TextView tv_examname;
        TextView tv_fraction;
        TextView tv_raik;
        ImageView iv_bs,iv_new;
    }

    private static class ObtainDataFromListenerForExerciseEvaluation extends ObtainDataFromNetListener<List<PaperItem>, String> {
        private ExerciseDeductionActivity ExercisemodetionActivity;
        private boolean isReset;

        public ObtainDataFromListenerForExerciseEvaluation(ExerciseDeductionActivity exerciseEvaluationActivity, boolean isReset) {
            this.ExercisemodetionActivity = new WeakReference<>(exerciseEvaluationActivity).get();
            this.isReset = isReset;
        }

        @Override
        public void onSuccess(final List<PaperItem> res) {
            if (ExercisemodetionActivity != null) {
                ExercisemodetionActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ExercisemodetionActivity.rl_wifi.setVisibility(View.GONE);
                        if (isReset && (res == null || res.size() == 0)) {
                            ExercisemodetionActivity.hasMoreData = false;
                            ToastUtils.showToast(R.string.no_data);
                            ExercisemodetionActivity.lv_exam.setVisibility(View.GONE);
                            if (ExercisemodetionActivity.isSelect == 5 ){
                                ExercisemodetionActivity.rl_non_exerciseevaluation.setVisibility(View.GONE);
                            }else{
                                ExercisemodetionActivity.rl_non_exerciseevaluation.setVisibility(View.VISIBLE);
                            }
                        } else if (!isReset && (res == null || res.size() == 0)) {
                            ExercisemodetionActivity.hasMoreData = false;
                            ToastUtils.showToast(R.string.no_more);
                        } else {
                            ExercisemodetionActivity.lv_exam.setVisibility(View.VISIBLE);
                            ExercisemodetionActivity.rl_non_exerciseevaluation.setVisibility(View.GONE);
                            ExercisemodetionActivity.hasMoreData = true;
                            if (ExercisemodetionActivity.paperItems == null) {
                                ExercisemodetionActivity.paperItems = new ArrayList<>();
                            }
                            if(isReset){
                                ExercisemodetionActivity.paperItems.clear();
                            }
                            ExercisemodetionActivity.paperItems.addAll(res);
                        }
                        ExercisemodetionActivity.completeRefresh();
                    }
                });
            }
        }

        @Override
        public void onStart() {
            if (ExercisemodetionActivity != null) {
                ExercisemodetionActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ExercisemodetionActivity.isLoadEnd = false;
                    }
                });
            }
        }

        @Override
        public void onFailure(final String res) {
            if (ExercisemodetionActivity != null) {
                ExercisemodetionActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (SendRequest.ERROR_NETWORK.equals(res)) {
                            ToastUtils.showToast(R.string.network);
                            if (ExercisemodetionActivity.paperItems==null){
                                ExercisemodetionActivity.rl_wifi.setVisibility(View.VISIBLE);
                            }
                        }
                        ExercisemodetionActivity.isLoadEnd = true;
                        ExercisemodetionActivity.completeRefresh();
                        ExercisemodetionActivity.rl_non_exerciseevaluation.setVisibility(View.GONE);
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
        private ExerciseDeductionActivity ExercisemodetionActivity;
        private long id;//本次练习对应id
        private int position;//点击的位置
        private String name;
        private String pattentions;
        private int isSelect;

        /**
         * @param position 点击的位置
         */
        public RunnableForPrepareExerciseDatas(ExerciseDeductionActivity ExercisemodetionActivity, int position, String name, String pattentions, int isSelect) {
            this.ExercisemodetionActivity = new WeakReference<>(ExercisemodetionActivity).get();
            this.position = position;
            this.name = name;
            this.pattentions = pattentions;
            this.isSelect = isSelect;
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
            DoExerciseActivity.initExerciseDataForExerciseEvaluation(studyRecords);
            if (DataStore_ExamLibrary.questionDetailList != null) {
                //更新题目数据
                studyRecords.setExercisenum(DataStore_ExamLibrary.questionDetailList.size());
            }
            daoutils.updateStudyRecord(studyRecords);
            DoExerciseActivity.initSelectChoiceList(studyRecords);
            if (ExercisemodetionActivity != null) {
                ExercisemodetionActivity.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        ExercisemodetionActivity.mCustomLoadingDialog.dismiss();
                        if (DataStore_ExamLibrary.questionDetailList == null || DataStore_ExamLibrary.questionDetailList.size() == 0) {
                            //该模块无题
                            ToastUtils.showToast(R.string.noexericse);
                            return;
                        }
                        DataStore_ExamLibrary.paperItem = ExercisemodetionActivity.paperItems.get(position);
//                            DoExerciseActivity.newIntent(ExercisemodetionActivity, id, 0,isSelect);
                            DoVipExerciseActivity.newIntent(ExercisemodetionActivity, id, 0,isSelect);

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
                    PaperItem paperItem = ExercisemodetionActivity.paperItems.get(position);
//                    StudyRecords studyRecords = daoUtils.queryStudyRecordsByType(isSelect + "", paperItem.pid, SendRequestUtilsForExercise
//                            .getKeyForExercisePackageDownload(), sendRequestUtilsForExercise.userId);
                    StudyRecords studyRecords = null;
                    boolean isFirstimeDoExercise = false;
                    if (studyRecords == null) {
                        //表明第一次做该真题
                        studyRecords = new StudyRecords();
                        studyRecords.setDate(new Date());
                        studyRecords.setType(isSelect + "");//0今日特训 1模块题海 2真题模考 3错题中心 4收藏 5 模考大赛
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
                    //注：这边是为了每次做题重新设置用户时间、做到哪一题、用户选项
                    studyRecords.setUsedtime(null);
                    studyRecords.setCurrentprogress(null);
                    studyRecords.setChoicesforuser(null);
                    if ("1".equals(studyRecords.getCompleted()) || isFirstimeDoExercise) {
                        //表明已完成或者第一次,重置进度、时间等信息

                        if (isFirstimeDoExercise) {
                            //第一次,则插入学习记录
                            id = daoUtils.insertStudyRecord(studyRecords);
                        } else {
                            //第二次,则更新学习记录
                            id = studyRecords.getId();
                            studyRecords.setName(paperItem.name);
                            daoUtils.updateStudyRecord(studyRecords);
                        }
                    } else {
                        //上次未完成,直接去上次记录继续做题
                        id = studyRecords.getId();
                        studyRecords.setName(paperItem.name);
                        daoUtils.updateStudyRecord(studyRecords);
                    }
                    RunnableForPrepareExerciseDatas.this.id = id;
                }
            });
        }
    }

    private static class DownLoadCallBackExerciseWithDialog extends DownLoadCallBackForExercise {
        private ExerciseDeductionActivity ExercisemodetionActivity;

        public DownLoadCallBackExerciseWithDialog(DownLoadExerciseType type, ExerciseDeductionActivity ExercisemodetionActivity) {
            super(type);

            this.ExercisemodetionActivity = ExercisemodetionActivity;
        }

        @Override
        public void onStart(final DownLoadExerciseStatus status) {
            if (ExercisemodetionActivity != null && !ExercisemodetionActivity.isDestoryed) {
                ExercisemodetionActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        DownLoadCallBackExerciseWithDialog.super.onStart(status);
                        ExercisemodetionActivity.mCustomProgressDialog.show();
                        ExercisemodetionActivity.mCustomProgressDialog.setProgress(0, 0);
                    }
                });
            }
        }

        @Override
        public void onFailure(final DownLoadExerciseStatus status) {
            if (ExercisemodetionActivity != null && !ExercisemodetionActivity.isDestoryed) {
                ExercisemodetionActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        DownLoadCallBackExerciseWithDialog.super.onFailure(status);
                        ExercisemodetionActivity.mCustomProgressDialog.dismiss();
                        ToastUtils.showToast(R.string.network);
                    }
                });
            }
        }

        @Override
        public void onProcess(final long bytesWritten, final long totalSize, final DownLoadExerciseStatus status) {
            if (ExercisemodetionActivity != null && !ExercisemodetionActivity.isDestoryed) {
                ExercisemodetionActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        DownLoadCallBackExerciseWithDialog.super.onProcess(bytesWritten, totalSize, status);
                        ExercisemodetionActivity.mCustomProgressDialog.setProgress(bytesWritten, totalSize);
                    }
                });
            }
        }

        @Override
        public void onSuccess(final byte[] responseBody, final String key, final DownLoadExerciseStatus status) {
            initDatasIntoDb(responseBody, key);
            DebugUtil.i("init db->onSuccess");
            if (ExercisemodetionActivity != null && !ExercisemodetionActivity.isDestoryed) {
                ExercisemodetionActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        DebugUtil.i("refreshItems->onSuccess");
                        DownLoadCallBackExerciseWithDialog.super.onSuccess(responseBody, key, status);
                        ExercisemodetionActivity.mCustomProgressDialog.dismiss();
                        //更新完毕开始做题
                        ExercisemodetionActivity.mCustomLoadingDialog.setTitle(ExercisemodetionActivity.getString(R.string.prepareexercise_ing));
                        ExercisemodetionActivity.mCustomLoadingDialog.show();
                        AsyncHttpClientHelper.createInstance().getThreadPool().execute(new RunnableForPrepareExerciseDatas(ExercisemodetionActivity,
                                ExercisemodetionActivity.mPositionChoose, ExercisemodetionActivity.name, ExercisemodetionActivity.pattentions,
                                ExercisemodetionActivity.isSelect));
                    }
                });
            }
        }
    }

    private static void initDatasIntoDb(byte[] responseBody, final String key) {
        DateFormat dateFormat = new SimpleDateFormat("HH-mm-ss-SSSS");
        String strBuilder = new String(responseBody);
        DebugUtil.i(TAG, "readFromFile-end:" + dateFormat.format(new Date()));
        String qids = "";
        final DaoUtils daoUtils = DaoUtils.getInstance();
        if (strBuilder == null) {
            updateDownloadPckageInfoForNullQues(daoUtils, key);
            return;
        }
        String res = FileUtils.gunzip(strBuilder);
        if (res == null || StringUtil.isEmpty(res)) {
            updateDownloadPckageInfoForNullQues(daoUtils, key);
            return;
        }
        DebugUtil.i(TAG, "gunZip-end:" + dateFormat.format(new Date()));
        //本地题包全部习题
        final List<QuestionDetail> questions;
        try {
            JSONArray JSONArray = new JSONArray(res);
            String s = JSONArray.get(0).toString();
            JSONObject JSONObject = new JSONObject(s);
            //真题返回的数据分为questions和qids
            String questions1 = JSONObject.get("questions").toString();
            questions = LoganSquare.parseList(questions1, QuestionDetail.class);
            qids = JSONObject.get("qids").toString();
            DebugUtil.e("questions++" + questions.toString());
        } catch (IOException e) {
            e.printStackTrace();
            updateDownloadPckageInfoForNullQues(daoUtils, key);
            return;
        } catch (JSONException e) {
            e.printStackTrace();
            updateDownloadPckageInfoForNullQues(daoUtils, key);
            return;
        }
        DebugUtil.i(TAG, "parseJsonArr-end:" + dateFormat.format(new Date()));
        final String finalQids = qids;
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
                    DebugUtil.e("插入数据库：" + questionDetailEve.toString());
                    questionDetailEve.setId(id);
                    daoUtils.insertQuestionDetail(questionDetailEve);
                }
                //更新题包下载信息-》downloadstatus以及versions
                daoUtils.inserOrUpdateExerciseDownloadPackageInfo(id, downloadInfoRes.versions);
                //把qids存在本地，查找根据这个qids
                CommonUtils.putSharedPreferenceItem(null, id, finalQids);
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

//    public void Binding_iphone(View v) {
//        View inflate = getLayoutInflater().inflate(R.layout.pop_bound_phone, null);
//        final PopupWindow mPopupWindow = new PopupWindow(inflate, ViewGroup.LayoutParams.MATCH_PARENT,
//                ViewGroup.LayoutParams.MATCH_PARENT, true);
//        mPopupWindow.setOutsideTouchable(false);
//        WindowManager.LayoutParams lp = getWindow().getAttributes();
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);//此行代码主要是解决在华为手机上半透明效果无效的bug
//        lp.alpha = 0.7f;
//        getWindow().setAttributes(lp);
//        mPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
//            @Override
//            public void onDismiss() {
//                WindowManager.LayoutParams lp = getWindow().getAttributes();
//                lp.alpha = 1f;
//                getWindow().setAttributes(lp);
//            }
//        });
//        inflate.findViewById(R.id.canle).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mPopupWindow.dismiss();
//            }
//        });
//        inflate.findViewById(R.id.bound_phone).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(ExerciseDeductionActivity.this, RegisterForObtainConfirmPwdActivity.class);
//                intent.putExtra("flag_mobile", flag_mobile);
//                startActivityForResult(intent, 101);
//                mPopupWindow.dismiss();
//            }
//        });
//        BitmapDrawable bitmapDrawable = new BitmapDrawable();
//        mPopupWindow.setBackgroundDrawable(bitmapDrawable);
//        mPopupWindow.showAtLocation(v, Gravity.CENTER, 0, 0);
//    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        if (resultCode == 101) {
//            mobile = CommonUtils.getSharedPreferenceItem(null, UserInfo.KEY_SP_MOBILE, "");
//        }
        if (UserInfo.CHOOSE_SUBJECT_RESULT_MODULE == resultCode) {
            defaultSort();
        }
    }

    @Override
    public void onReceiveBroadCast(Context context, Intent intent) {
        super.onReceiveBroadCast(context, intent);
        AsyncHttpClientHelper.createInstance().getThreadPool().execute(new RunnableForPrepareExerciseDatas(ExerciseDeductionActivity.this, mPositionChoose, name, pattentions, isSelect));
    }

}
