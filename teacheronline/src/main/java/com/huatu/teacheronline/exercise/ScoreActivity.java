package com.huatu.teacheronline.exercise;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.greendao.DaoUtils;
import com.greendao.QuestionDetail;
import com.greendao.StudyRecords;
import com.huatu.teacheronline.BaseActivity;
import com.huatu.teacheronline.R;
import com.huatu.teacheronline.engine.ObtainDataFromNetListener;
import com.huatu.teacheronline.engine.SendRequest;
import com.huatu.teacheronline.exercise.adapter.AnswercardGridAdapter;
import com.huatu.teacheronline.utils.ShareUtils;
import com.huatu.teacheronline.utils.ToastUtils;
import com.huatu.teacheronline.widget.CircleView;
import com.huatu.teacheronline.widget.CustomAlertDialog;
import com.huatu.teacheronline.widget.CustomGridView;
import com.huatu.teacheronline.widget.PopwindowDoExercise;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ljzyuhenda on 16/2/15.
 */
public class ScoreActivity extends BaseActivity {
    private static final String TAG = "ScoreActivity_Exercise";
    private TextView tv_main_title;
    private RelativeLayout rl_main_left;
    private TextView tv_examType_section1, tv_answerRightNum_section1, tv_answerSumNum_section1,
            tv_accuracy, tv_recommand_time, tv_realTime;
//    private CircleView circle;
    private GridView gv_all_answer;

    private int caseNum = 0;
    private int rightNum = 0;
    private String titleName;
    private int realTime;
    private String[] answersFlag;
    private AnswercardGridAdapter answercardGridAdapter;
    // 错误题目数据
    private List<QuestionDetail> wrongExerciseList;
    private String type;// 0随机联系  1顺序练习  2模拟题  3真题  4错题  5收藏
    private TextView tv_share_result;//分享本次做题
    public static boolean isContinue = false;
    private long mId;
    private StudyRecords mStudyRecord;
    private DaoUtils mDaoUtils;
    private PopwindowDoExercise popwindowDoExercise;
    private ImageView ib_main_right;
    private RelativeLayout rl_main_right;
    private String accuracy = "";
    private CustomAlertDialog mCustomLoadingDialog;

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void initView() {
        setContentView(R.layout.activity_score_exercise_new);
        tv_main_title = (TextView) findViewById(R.id.tv_main_title);
        tv_main_title.setText("成绩");
        rl_main_right = (RelativeLayout) findViewById(R.id.rl_main_right);
        rl_main_right.setVisibility(View.VISIBLE);
        TextView tv_main_right= (TextView) findViewById(R.id.tv_main_right);
        tv_main_right.setVisibility(View.VISIBLE);
        tv_main_right.setBackgroundResource(R.drawable.bg_rectangle_frame_green_radius);
        tv_main_right.setText(R.string.analysis);
        tv_main_right.setTextColor(getResources().getColor(R.color.green004));
        ib_main_right = (ImageView) findViewById(R.id.ib_main_right);
//        ib_main_right.setImageResource(R.drawable.more_dot);
        ib_main_right.setVisibility(View.GONE);
        rl_main_left = (RelativeLayout) findViewById(R.id.rl_main_left);
        tv_examType_section1 = (TextView) findViewById(R.id.tv_examType_section1);
        tv_answerRightNum_section1 = (TextView) findViewById(R.id.tv_answerRightNum_section1);
        tv_answerSumNum_section1 = (TextView) findViewById(R.id.tv_answerSumNum_section1);
        tv_accuracy = (TextView) findViewById(R.id.tv_accuracy);
        mCustomLoadingDialog = new CustomAlertDialog(this, R.layout.dialog_loading_custom);
//        tv_wrong_explain = (TextView) findViewById(R.id.tv_wrong_explain);
//        tv_all_explain = (TextView) findViewById(R.id.tv_all_explain);
        tv_recommand_time = (TextView) findViewById(R.id.tv_recommand_time);
        tv_realTime = (TextView) findViewById(R.id.tv_realTime);
//        circle = (CircleView) findViewById(R.id.circle);
        gv_all_answer = (GridView) findViewById(R.id.gv_all_answer);
        tv_share_result = (TextView) findViewById(R.id.tv_share_result);

        initData();
    }

    private void initData() {
        mId = getIntent().getLongExtra("id", -1);
        mDaoUtils = DaoUtils.getInstance();
        mStudyRecord = mDaoUtils.queryStudyRecords(mId);

        caseNum = mStudyRecord.getExercisenum();
        rightNum = mStudyRecord.getRightnum();
        titleName = mStudyRecord.getName();
        realTime = mStudyRecord.getUsedtime();
        answersFlag = DataStore_ExamLibrary.answerFlag;
        wrongExerciseList = DataStore_ExamLibrary.wrongExerciseList;

        type = mStudyRecord.getType();

        tv_realTime.setText(getString(R.string.time_used) +" "+ formatTimes(realTime));
        tv_examType_section1.setText("练习类型: " + titleName);
        tv_answerRightNum_section1.setText(rightNum + "");
        tv_answerSumNum_section1.setText("道/" + caseNum + "道");
        accuracy = "0";
        if (caseNum == 0) {
            accuracy = "0";
            tv_accuracy.setText(0 + "");
//            circle.setProgress(0);
        } else {
            accuracy = (rightNum * 100 / caseNum) + "";
            tv_accuracy.setText((rightNum * 100 / caseNum) + "");
//            circle.setProgress(rightNum * 100 / caseNum);
        }

        List<Integer> codeList = new ArrayList<>();// 题号集合
        Map<Integer, String> gridMap = new HashMap<>();// <题号，答题情况>
        if (answersFlag != null) {
            for (int i = 0; i < answersFlag.length; i++) {
                codeList.add(i + 1);
                gridMap.put(i + 1, answersFlag[i]);
            }
        }
        answercardGridAdapter = new AnswercardGridAdapter(this, codeList, gridMap);
        gv_all_answer.setAdapter(answercardGridAdapter);

        //暂时 用 casenum * 0.6 分 来计算用时，应该用 服务器返回数据
        long recommandTimes = caseNum * 36;//(s)
        tv_recommand_time.setText(getString(R.string.time_recommand) +" "+ formatTimes(recommandTimes));
    }

    @Override
    public void setListener() {
        popwindowDoExercise = new PopwindowDoExercise(this, this);
        popwindowDoExercise.setData(3);
        rl_main_left.setOnClickListener(this);
        rl_main_right.setOnClickListener(this);
//        tv_wrong_explain.setOnClickListener(this);
//        tv_all_explain.setOnClickListener(this);
        tv_share_result.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_main_left:
                back();
                break;
            case R.id.tv_wrong_explain:
            case R.id.tv_collection_exercise:
                popwindowDoExercise.dismiss();
                if (null != wrongExerciseList && wrongExerciseList.size() > 0) {
//                    DoExerciseActivity.newIntent(this, mId, 1,-1);
                    DoVipExerciseActivity.newIntent(this, mId, 1,-1);
                } else {
                    ToastUtils.showToast(R.string.nowrongexercise_info);
                }

                break;
            case R.id.tv_all_explain:
            case R.id.tv_correction_exercise:
                popwindowDoExercise.dismiss();
//                DoExerciseActivity.newIntent(this, mId, 2,-1);
                DoVipExerciseActivity.newIntent(this, mId, 2,-1);
                break;
            case R.id.tv_continue_exercise:
            case R.id.tv_share_exercise:
                popwindowDoExercise.dismiss();
                updateStudyRecord();
                DataStore_ExamLibrary.wrongChoiceList = null;
                DataStore_ExamLibrary.wrongExerciseList = null;
                DataStore_ExamLibrary.selectedChoiceList = null;
                Intent intent = new Intent();
                if ("2".equals(mStudyRecord.getType())){
                intent.setAction(ExerciseEvaluationActivity.ACTION_NEXT);
                }else{
                intent.setAction(ModuleExerciseDetailActivity.ACTION_NEXT_GROUP);
                }
            sendBroadcast(intent);
//              DoExerciseActivity.newIntent(this, mId, 0);
            finish();
            break;

            case R.id.tv_share_result:
                popwindowDoExercise.dismiss();
//                ShareUtils.popShare(this, ShareUtils.url_appdownload_qq, ShareUtils.content_share, ShareUtils.title_share, false);
                SendRequest.getShareResultUrl(null, null, null, accuracy, new
                        ObtainDataListener(this));
                break;
            case R.id.rl_main_right:
                popwindowDoExercise.show(rl_main_right);
                break;

        }
    }

    private static String formatTimes(long miss) {
        StringBuilder stringBuilder = new StringBuilder();
        String hh = miss / 3600 > 9 ? miss / 3600 + "" : "" + miss / 3600;
//        if (miss / 3600 > 0) {
        if(Integer.parseInt(hh) == 0){
            stringBuilder.append("0"+hh + ":");
        }else if(Integer.parseInt(hh) <= 9){
            stringBuilder.append("0"+hh + ":");
        }else {
            stringBuilder.append(hh + ":");
        }
//        }
        String mm = (miss % 3600) / 60 > 9 ? (miss % 3600) / 60 + "" : "" + (miss % 3600) / 60;
//        if ((miss % 3600) / 60 > 0) {
//            stringBuilder.append(mm + ":");
        if(Integer.parseInt(mm) == 0){
            stringBuilder.append("0"+mm + ":");
        }else if(Integer.parseInt(mm) <= 9){
            stringBuilder.append("0"+mm + ":");
        }else {
            stringBuilder.append(mm + ":");
        }
//        }
        String ss = (miss % 3600) % 60 > 9 ? (miss % 3600) % 60 + "" : "" + (miss % 3600) % 60;
//        if ((miss % 3600) % 60 > 0) {

        if(Integer.parseInt(ss) == 0){
            stringBuilder.append("0"+ss );
        }else if(Integer.parseInt(ss) <= 9){
            stringBuilder.append("0"+ss );
        }else {
            stringBuilder.append(ss);
        }
//        stringBuilder.append(ss);
//        }
        return stringBuilder.toString();
    }

    public static void newIntent(Activity context, long id) {
        Intent intent = new Intent(context, ScoreActivity.class);
        intent.putExtra("id", id);
        context.startActivity(intent);
    }

    /**
     * 更新学习记录
     */
    private void updateStudyRecord() {
        //未交卷 如果是真题不更新
        if (mStudyRecord == null || "2".equals(mStudyRecord.getType())|| "5".equals(mStudyRecord.getType())){
            return;
        }
        mStudyRecord.setChoicesforuser(null);
        mStudyRecord.setUsedtime(0);
        mStudyRecord.setCurrentprogress(null);
        mDaoUtils.updateStudyRecord(mStudyRecord);
    }

    private static class ObtainDataListener extends ObtainDataFromNetListener<String, String> {

        private ScoreActivity weak_activity;

        public ObtainDataListener(ScoreActivity activity) {
            weak_activity = new WeakReference<>(activity).get();
        }

        @Override
        public void onSuccess(final String res) {
            if (weak_activity != null) {
                weak_activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        weak_activity.mCustomLoadingDialog.dismiss();
                        ShareUtils.popShare(weak_activity, SendRequest.ipForExercise+res, ShareUtils.content_share_evaluation_score, ShareUtils.title_share, false);
                    }
                });
            }

        }

        @Override
        public void onStart() {
            if (weak_activity != null) {
                weak_activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        weak_activity.mCustomLoadingDialog.setTitle(weak_activity.getString(R.string.loading));
                        weak_activity.mCustomLoadingDialog.show();
                    }
                });
            }
        }

        @Override
        public void onFailure(final String res) {
            if (weak_activity != null) {
                weak_activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        weak_activity.mCustomLoadingDialog.dismiss();
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
