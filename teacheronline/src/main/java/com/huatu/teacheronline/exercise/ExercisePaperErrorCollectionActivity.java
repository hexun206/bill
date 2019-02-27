package com.huatu.teacheronline.exercise;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bluelinelabs.logansquare.LoganSquare;
import com.gensee.utils.StringUtil;
import com.google.gson.Gson;
import com.greendao.DaoUtils;
import com.greendao.ExerciseDownloadPackage;
import com.greendao.QuestionDetail;
import com.greendao.StudyRecords;
import com.huatu.teacheronline.BaseActivity;
import com.huatu.teacheronline.R;
import com.huatu.teacheronline.engine.AsyncHttpClientHelper;
import com.huatu.teacheronline.engine.ObtainDataFromNetListener;
import com.huatu.teacheronline.engine.SendRequest;
import com.huatu.teacheronline.exercise.bean.PaperItem;
import com.huatu.teacheronline.exercise.download.DownLoadCallBackForExercise;
import com.huatu.teacheronline.exercise.download.DownLoadInfo;
import com.huatu.teacheronline.exercise.download.DownLoadManagerForExercise;
import com.huatu.teacheronline.exercise.download.DownloadService;
import com.huatu.teacheronline.utils.CommonUtils;
import com.huatu.teacheronline.utils.DebugUtil;
import com.huatu.teacheronline.utils.FileUtils;
import com.huatu.teacheronline.utils.ToastUtils;
import com.huatu.teacheronline.utils.UserInfo;
import com.huatu.teacheronline.widget.CustomAlertDialog;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 真题演练-模考 ，错题 和收藏界面
 *
 * @author cwqiang
 * @time 2017-10-11 09:06:36
 */
public class ExercisePaperErrorCollectionActivity extends BaseActivity implements AdapterView.OnItemClickListener {
    public static final String ACTION_REPETITION_GROUP = "action_repetition_group";
    private TextView tv_main_title;
    private RelativeLayout rl_main_left;
    private ListView listView;
    private int mPositionChoose;
    private List<PaperItem> paperItems = new ArrayList<>();
    private CustomAlertDialog mCustomLoadingDialog;
    private ExamAdapter examAdapter;
    private DaoUtils mDaoUtils;
    private int currentPosition = 0;
    private String mode;
    private String type;
    private CustomAlertDialog mCustomUpdateDialog;
    private DownLoadManagerForExercise mDownLoadManagerForExercise;
    public CustomAlertDialog mCustomProgressDialog;
    private int isSelcet = 2;
    private static int exerciseType = 3; //3 错题 4 收藏
    private String userid = "";
    private RelativeLayout rl_non_exerciseevaluation;
    private RelativeLayout rl_wifi;
    private ImageView img_non_exerciseevaluation;
    private TextView tv_non_exerciseevaluation;
    private CustomAlertDialog mMindDialog; //做题相关提示


    @Override
    public void initView() {
        setContentView(R.layout.activity_exercise_errortopic);
        userid = CommonUtils.getSharedPreferenceItem(null, UserInfo.KEY_SP_USERID, "");
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_REPETITION_GROUP);
        setIntentFilter(intentFilter);
        mCustomLoadingDialog = new CustomAlertDialog(this, R.layout.dialog_loading_custom);
        rl_non_exerciseevaluation = (RelativeLayout) findViewById(R.id.rl_non_exerciseevaluation);
        img_non_exerciseevaluation = (ImageView)findViewById(R.id.img_non_exerciseevaluation);
        tv_non_exerciseevaluation = (TextView)findViewById(R.id.tv_non_exerciseevaluation);
        rl_wifi = (RelativeLayout) findViewById(R.id.rl_wifi);
        tv_main_title = (TextView) findViewById(R.id.tv_main_title);
        rl_main_left = (RelativeLayout) findViewById(R.id.rl_main_left);
        mode = getIntent().getStringExtra("mode");
        type = getIntent().getStringExtra("type");
        mDaoUtils = DaoUtils.getInstance();
        listView = (ListView) findViewById(R.id.lv_exam);
        mDownLoadManagerForExercise = DownloadService.getDownloadManager_exercise();
        mCustomUpdateDialog = new CustomAlertDialog(this, R.layout.dialog_join_mydirect);
        mCustomUpdateDialog.setTitle(getString(R.string.updateinfoforexercise));
        mCustomUpdateDialog.setCancelable(false);
        mCustomProgressDialog = new CustomAlertDialog(this, R.layout.dialog_showprogress);
        mCustomProgressDialog.setTitle(getString(R.string.startdownloadexercisepackage_info));
        mCustomProgressDialog.setCancelable(false);

        mMindDialog = new CustomAlertDialog(this, R.layout.dialog_join_mydirect);

        if (type.equals("error")) {
            tv_main_title.setText(R.string.my_error_exercise);
            exerciseType = 3;
            img_non_exerciseevaluation.setBackgroundResource(R.drawable.pic_zwct);
            tv_non_exerciseevaluation.setText(R.string.no_error_exercise);
        } else {
            img_non_exerciseevaluation.setBackgroundResource(R.drawable.pic_zwsc);
            tv_non_exerciseevaluation.setText(R.string.no_collect_exercise);
            tv_main_title.setText(R.string.my_collection_exercise);
            exerciseType = 4;
        }
        if (mode.equals("zhenti")) {
            isSelcet = 2;
        } else {
            isSelcet = 5;
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        SendRequest.getPaperWrongOrColectionQuestions(mode, type, new ObtainDataListenerForErrorExerciseChapterTree(this));
    }

    @Override
    public void setListener() {
        rl_main_left.setOnClickListener(this);
        listView.setOnItemClickListener(this);
        mCustomUpdateDialog.setOkOnClickListener(this);
        mCustomUpdateDialog.setCancelOnClickListener(this);
        mMindDialog.setOkOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMindDialog.dismiss();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_main_left:
                back();
                break;
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
//                if(isSelect == 5){//如果为模考大赛，则调用模块大赛试题包接口、
//                    mDownLoadManagerForExercise.startDownLoadTask(paperItem.pid, isSelect, new DownLoadCallBackExerciseWithDialog(DownLoadCallBackForExercise
//                            .DownLoadExerciseType.ExerciseEvaluationActivity, this));
//                }else{
                mDownLoadManagerForExercise.startDownLoadTask(paperItem.pid, isSelcet, new DownLoadCallBackExerciseWithDialog(DownLoadCallBackForExercise
                        .DownLoadExerciseType.ExerciseEvaluationActivity, this));
//                }
                mCustomUpdateDialog.dismiss();
                break;

        }
    }


    public static void newIntent(Activity context, String mode, String type) {
        Intent intent = new Intent(context, ExercisePaperErrorCollectionActivity.class);
        intent.putExtra("mode", mode);
        intent.putExtra("type", type);
        context.startActivity(intent);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (view.getId() == R.id.ll_loading) {
            //此时触发的是刷新item
            return;
        }
        mPositionChoose = position;
        onitemClick(position);
    }

    /**
     * 点击进入题目
     * @param position
     */
    private void onitemClick(int position) {
        currentPosition = position;
        PaperItem paperItem = paperItems.get(position);
        String key = paperItem.pid;

        if (paperItem.down.equals("0")) {
            //已下线
            ToastUtils.showToast("此试卷已经下线");
            SendRequest.delDownPaper(userid, key, type, new ObtainDataListener(this));
            return;
        }

             //是否是模考卷列表
        if(mode.equals("moni")){
            //是否活动卷
            if (paperItem.onlyOne == 1){
                //isactivitie  1 活动中是否开放解析 2 活动结束  0 未开始
          if (paperItem.isactivitie == 1){
              //活动中 open = 1  立即开放 0 不放开
             if (paperItem.open.equals("0")){
                 mMindDialog.setTitle("模考未结束，暂未开放解析！");
                 mMindDialog.show();
                 mMindDialog.setCancelGone();
                 return;
             }
            //活动结束的卷子
          }else if (paperItem.isactivitie == 2){
              if (paperItem.openHour!=-1){
                  mMindDialog.setTitle("模考已结束，暂未开放解析！");
                  mMindDialog.show();
                  mMindDialog.setCancelGone();
                  return;
              }
          }
            }
        }

        Gson Gson = new Gson();
        String s = Gson.toJson(paperItem.qids);
        CommonUtils.putSharedPreferenceItem(null, paperItem.pid + exerciseType, s);
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
            if (paperItem.down.equals("0")) {
                //已下线
                ToastUtils.showToast("此试卷已经下线！");
            }else if (paperItem.down.equals("1")){
                //在线 下载完整的这套试卷
                mDownLoadManagerForExercise.startDownLoadTask(paperItem.pid, isSelcet, new DownLoadCallBackExerciseWithDialog(DownLoadCallBackForExercise
                        .DownLoadExerciseType.ExerciseEvaluationActivity, this));
            }
        }
    }


    private class ExamAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return paperItems == null?0:paperItems.size();
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
            ViewHolderExam viewHolder;
            if (convertView == null) {
                convertView = View.inflate(ExercisePaperErrorCollectionActivity.this, R.layout.exermode_tim, null);
                viewHolder = new ViewHolderExam();
                viewHolder.tv_examname = (TextView) convertView.findViewById(R.id.tv_examname);
                viewHolder.tv_fraction = (TextView) convertView.findViewById(R.id.Fraction);
                viewHolder.tv_raik = (TextView) convertView.findViewById(R.id.raik);
                viewHolder.iv_bs = (ImageView) convertView.findViewById(R.id.iv_bs);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolderExam) convertView.getTag();
            }
            viewHolder.iv_bs.setImageResource(R.drawable.more_arrow);
            viewHolder.tv_raik.setVisibility(View.GONE);
            viewHolder.tv_examname.setText(paperItems.get(position).name);
            viewHolder.tv_fraction.setText(paperItems.get(position).count + "题");
            return convertView;
        }
    }

    private class ViewHolderExam {
        TextView tv_examname;
        TextView tv_fraction;
        TextView tv_raik;
        ImageView iv_bs;
    }


    private static class ObtainDataListenerForErrorExerciseChapterTree extends ObtainDataFromNetListener<List<PaperItem>, String> {
        private ExercisePaperErrorCollectionActivity activity;

        public ObtainDataListenerForErrorExerciseChapterTree(ExercisePaperErrorCollectionActivity activity) {
            this.activity = new WeakReference<>(activity).get();
        }

        @Override
        public void onSuccess(final List<PaperItem> res) {
            if (activity != null) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        activity.paperItems.clear();
                        activity.paperItems = res;
                        activity.mCustomLoadingDialog.dismiss();
                        if (activity.paperItems == null || activity.paperItems.size() == 0) {
                            activity.rl_non_exerciseevaluation.setVisibility(View.VISIBLE);
                        }else {
                            activity.rl_non_exerciseevaluation.setVisibility(View.GONE);
                            activity.listView.setVisibility(View.VISIBLE);
                            activity.setAdapter();
                        }
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
                            if (activity.paperItems == null || activity.paperItems.size() == 0) {
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

    public void setAdapter() {
        if (examAdapter == null) {
            examAdapter = new ExamAdapter();
            listView.setAdapter(examAdapter);
        } else {
            examAdapter.notifyDataSetChanged();
        }
    }

//    /**
//     * 加载进入做题页面
//     *
//     * @param position
//     */
//    private void exerciseLoading(int position) {
//        MobclickAgent.onEvent(this, "centerErrorOnItemClik");
//        ExerciseDownloadPackage exerciseDownloadPackage = mDaoUtils.queryExerciseDownloadPackageInfo(SendRequestUtilsForExercise
//                .getKeyForExercisePackageDownload());
//        if (exerciseDownloadPackage == null) {
//            ToastUtils.showToast(R.string.downloadfirstinfo);
//            return;
//        }
//
//        //根据题号从本地取试题
//        mCustomLoadingDialog.setTitle(getString(R.string.prepareexercise_ing));
//        mCustomLoadingDialog.show();
//
//        AsyncHttpClientHelper.createInstance().getThreadPool().execute(new RunnableForPrepareExerciseDatas(this, position));
//    }


    private static class RunnableForPrepareExerciseDatas implements Runnable {
        private ExercisePaperErrorCollectionActivity errorCenterActivity;
        private long id;//本次练习对应id
        private int position;//点击的位置

        /**
         * @param position 点击的位置
         */
        public RunnableForPrepareExerciseDatas(ExercisePaperErrorCollectionActivity errorCenterActivity, int position) {
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
            DoVipExerciseActivity.initExerciseDataForExerciseEvaluation(studyRecords);
            DoVipExerciseActivity.initSelectChoiceList(studyRecords);
            boolean isEvaluationError = false;
            boolean isEvaluationCollect = false;
            if (studyRecords.getType().equals("3")) {
                //试卷的错题
                isEvaluationError = true;
            }else if (studyRecords.getType().equals("4")){
                //试卷的收藏
                isEvaluationCollect = true;
            }
            if (errorCenterActivity != null) {
                final boolean finalIsEvaluationError = isEvaluationError;
                final boolean finalIsEvaluationCollect = isEvaluationCollect;
                errorCenterActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        errorCenterActivity.mCustomLoadingDialog.dismiss();
                        DataStore_ExamLibrary.paperItem = errorCenterActivity.paperItems.get(position);
                        if (DataStore_ExamLibrary.questionDetailList == null || DataStore_ExamLibrary.questionDetailList.size() == 0) {
                            //该模块无题
                            ToastUtils.showToast("暂无习题");
                            return;
                        }
                        DoVipExerciseActivity.newIntent(errorCenterActivity, id, 0,errorCenterActivity.exerciseType, finalIsEvaluationError, finalIsEvaluationCollect);
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
                    PaperItem paperItem = errorCenterActivity.paperItems.get(position);
                    StudyRecords studyRecords = daoUtils.queryStudyRecordsByType(exerciseType + "", paperItem.pid, SendRequestUtilsForExercise
                            .getKeyForExercisePackageDownload(), sendRequestUtilsForExercise.userId);
                    boolean isFirstimeDoExercise = false;
                    if (studyRecords == null) {
                        //表明第一次做该真题
                        studyRecords = new StudyRecords();
                        studyRecords.setDate(new Date());
                        studyRecords.setType(exerciseType + "");//0今日特训 1模块题海 2真题模考 3错题中心 4收藏 5 模考大赛
                        studyRecords.setUserid(sendRequestUtilsForExercise.userId);
                        studyRecords.setChoosecategory(SendRequestUtilsForExercise.getKeyForExercisePackageDownload());
                        studyRecords.setName(paperItem.name);
                        studyRecords.setIsdelete("1");
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


    @Override
    public void onReceiveBroadCast(Context context, Intent intent) {
        super.onReceiveBroadCast(context, intent);
        if (intent.getAction().equals(ACTION_REPETITION_GROUP)) {
            onitemClick(currentPosition);
        }
    }

    private void showUpdateDialog() {
        mCustomUpdateDialog.show();
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

    private static class DownLoadCallBackExerciseWithDialog extends DownLoadCallBackForExercise {
        private ExercisePaperErrorCollectionActivity ExercisemodetionActivity;

        public DownLoadCallBackExerciseWithDialog(DownLoadExerciseType type, ExercisePaperErrorCollectionActivity ExercisemodetionActivity) {
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
                                ExercisemodetionActivity.currentPosition));
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
//                //把qids存在本地，查找根据这个qids
//                CommonUtils.putSharedPreferenceItem(null, id+exerciseType, finalQids);
                DebugUtil.i("updateExerciseDb->onSuccess");
            }
        });
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

    private static class ObtainDataListener extends ObtainDataFromNetListener<String, String> {

        private ExercisePaperErrorCollectionActivity weak_activity;

        public ObtainDataListener(ExercisePaperErrorCollectionActivity activity) {
            weak_activity = new WeakReference<>(activity).get();
        }

        @Override
        public void onSuccess(final String res) {
            if (weak_activity != null) {
                weak_activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtils.showToast(res);
                    }
                });
            }

        }

        @Override
        public void onFailure(final String res) {
            if (weak_activity != null) {
                weak_activity.mCustomLoadingDialog.dismiss();
                weak_activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (res.equals(SendRequest.ERROR_NETWORK)) {
                            ToastUtils.showToast(R.string.network);
                        } else if (res.equals(SendRequest.ERROR_SERVER)) {
                            ToastUtils.showToast(R.string.server_error);
                        }
                    }
                });
            }
        }
    }
}
