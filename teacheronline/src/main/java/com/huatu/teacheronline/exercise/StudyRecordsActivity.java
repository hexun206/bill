package com.huatu.teacheronline.exercise;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.greendao.DaoUtils;
import com.greendao.StudyRecords;
import com.huatu.teacheronline.BaseActivity;
import com.huatu.teacheronline.R;
import com.huatu.teacheronline.engine.AsyncHttpClientHelper;
import com.huatu.teacheronline.engine.ObtainDataFromNetListener;
import com.huatu.teacheronline.engine.SendRequest;
import com.huatu.teacheronline.exercise.bean.PaperItem;
import com.huatu.teacheronline.utils.CommonUtils;
import com.huatu.teacheronline.utils.DebugUtil;
import com.huatu.teacheronline.utils.ToastUtils;
import com.huatu.teacheronline.utils.UserInfo;
import com.huatu.teacheronline.widget.CustomAlertDialog;
import com.umeng.analytics.MobclickAgent;
import com.zhy.android.percent.support.PercentRelativeLayout;

import java.lang.ref.WeakReference;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * 做题记录
 * Created by ljzyuhenda on 16/2/22.
 */
public class StudyRecordsActivity extends BaseActivity implements AdapterView.OnItemLongClickListener, AdapterView.OnItemClickListener {

    private ListView lv_studyrecords;
    private CustomAlertDialog mCustomLoadingDialog;
    private CustomAlertDialog mDeleteDialog;
    private int selectedPosition;
    private List<StudyRecords> studyRecordsList;
    private StudyRecordsAdapter mStudyRecordAdapter;
    private SimpleDateFormat mDateFormat;
    private DecimalFormat mDecimalFormat;
    private TextView tv_main_right;
    private DaoUtils mDaoutils;
    private TextView tv_main_title;
    private RelativeLayout rl_main_left;
    private PercentRelativeLayout rl_nodata;
    private CustomAlertDialog mMindDialog; //真题不能做提示
    private PaperItem paperItem;//真题当前试卷信息
    private String userId;

    @Override
    public void initView() {
        setContentView(R.layout.activity_studyrecords);
        userId = CommonUtils.getSharedPreferenceItem(null, UserInfo.KEY_SP_USERID, "");
        tv_main_title = (TextView) findViewById(R.id.tv_main_title);
        tv_main_title.setText(R.string.studyRecord);
        rl_main_left = (RelativeLayout) findViewById(R.id.rl_main_left);

        mDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        mDecimalFormat = new DecimalFormat("###.##");
        lv_studyrecords = (ListView) findViewById(R.id.lv_studyrecords);
        mCustomLoadingDialog = new CustomAlertDialog(this, R.layout.dialog_loading_custom);
        mCustomLoadingDialog.setCancelable(false);

        mDeleteDialog = new CustomAlertDialog(this, R.layout.dialog_join_mydirect);
        mDeleteDialog.setTitle(getString(R.string.deletestudyrecords_info));

        mMindDialog = new CustomAlertDialog(this, R.layout.dialog_join_mydirect);

        mStudyRecordAdapter = new StudyRecordsAdapter();
        lv_studyrecords.setAdapter(mStudyRecordAdapter);
        tv_main_right = (TextView) findViewById(R.id.tv_main_right);
        tv_main_right.setVisibility(View.GONE);
        mDaoutils = DaoUtils.getInstance();

        rl_nodata = (PercentRelativeLayout) findViewById(R.id.rl_nodata);
        mCustomLoadingDialog.show();
        AsyncHttpClientHelper.createInstance().getThreadPool().execute(new RunnableForPrepareDatasFromDb(this));
    }

    @Override
    public void setListener() {
        lv_studyrecords.setOnItemLongClickListener(this);
        lv_studyrecords.setOnItemClickListener(this);
        mDeleteDialog.setCancelOnClickListener(this);
        mDeleteDialog.setOkOnClickListener(this);
        rl_main_left.setOnClickListener(this);
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
            case R.id.tv_dialog_cancel:
                MobclickAgent.onEvent(this, "doRecordDeleteCancel");
                mDeleteDialog.dismiss();
                break;
            case R.id.tv_dialog_ok:
                MobclickAgent.onEvent(this, "doRecordDeleteOK");
                mDeleteDialog.dismiss();
                deleteStudyRecord();
                break;
            case R.id.rl_main_left:
                finish();
                break;
        }
    }

    public static void newIntent(Activity context) {
        Intent intent = new Intent(context, StudyRecordsActivity.class);
        context.startActivity(intent);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        mDeleteDialog.show();
        selectedPosition = position;
        MobclickAgent.onEvent(this, "doRecordOnItemLongClick");
        return true;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        StudyRecords studyRecords = studyRecordsList.get(position);
        selectedPosition = position;
        if(Integer.parseInt(studyRecords.getType()) == 2 || Integer.parseInt(studyRecords.getType()) == 5){
            ObtainDataFromListenerEvaluationStudyRecord obtainDataFromListenerEvaluationStudyRecord = new ObtainDataFromListenerEvaluationStudyRecord(this);
            SendRequest.getPaperNum(studyRecords.getCid() + "", obtainDataFromListenerEvaluationStudyRecord);
        }else {
            MobclickAgent.onEvent(this, "doRecordOnItemClick");
            mCustomLoadingDialog.setTitle(getString(R.string.prepareexercise_ing));
            mCustomLoadingDialog.show();
            AsyncHttpClientHelper.createInstance().getThreadPool().execute(new RunnableForPrepareExerciseDatas(this, studyRecordsList.get(position).getId()));
        }

    }

    private static class RunnableForPrepareDatasFromDb implements Runnable {
        private StudyRecordsActivity activity;

        public RunnableForPrepareDatasFromDb(StudyRecordsActivity activity) {
            this.activity = new WeakReference<>(activity).get();
        }

        @Override
        public void run() {
            DaoUtils daoUtils = DaoUtils.getInstance();
            final List<StudyRecords> studyRecordsList = daoUtils.queryStudyRecordsByUserId(activity.userId);

            if (activity != null) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        activity.mCustomLoadingDialog.dismiss();
                        activity.studyRecordsList = studyRecordsList;
                        activity.mStudyRecordAdapter.notifyDataSetChanged();
                        if (studyRecordsList.size()==0){
                            activity.rl_nodata.setVisibility(View.VISIBLE);
                        }
                    }
                });
            }
        }
    }

    private class StudyRecordsAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            if (studyRecordsList == null) {
                return 0;
            }

            return studyRecordsList.size();
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
            StudyRecords studyRecords = studyRecordsList.get(position);
            ViewholderForStudyRecord viewholderForStudyRecord;
            if (convertView == null) {
                convertView = View.inflate(StudyRecordsActivity.this, R.layout.item_studyrecords, null);
                viewholderForStudyRecord = new ViewholderForStudyRecord();
                viewholderForStudyRecord.tv_categoryName = (TextView) convertView.findViewById(R.id.tv_categoryName);
                viewholderForStudyRecord.tv_complete = (TextView) convertView.findViewById(R.id.tv_complete);
                viewholderForStudyRecord.tv_correctpercent = (TextView) convertView.findViewById(R.id.tv_correctpercent);
                viewholderForStudyRecord.tv_date = (TextView) convertView.findViewById(R.id.tv_date);
                convertView.setTag(viewholderForStudyRecord);
            }
            viewholderForStudyRecord = (ViewholderForStudyRecord) convertView.getTag();
            viewholderForStudyRecord.tv_categoryName.setText(studyRecords.getName());
            if ("1".equals(studyRecords.getCompleted())) {
                viewholderForStudyRecord.tv_complete.setText(R.string.complete);
                viewholderForStudyRecord.tv_complete.setTextColor(Color.parseColor("#0DB790"));
            } else {
                viewholderForStudyRecord.tv_complete.setText(R.string.notcomplete);
                viewholderForStudyRecord.tv_complete.setTextColor(Color.RED);
            }

            viewholderForStudyRecord.tv_date.setText(mDateFormat.format(studyRecords.getDate()));
            int rightNum;
            int totalExerciseNum;
            if (studyRecords.getRightnum() == null) {
                rightNum = 0;
            } else {
                rightNum = studyRecords.getRightnum();
            }

            if (studyRecords.getExercisenum() == null) {
                totalExerciseNum = 0;
            } else {
                totalExerciseNum = Integer.valueOf(studyRecords.getExercisenum());
            }

            float rightPercent;
            if (totalExerciseNum == 0) {
                rightPercent = 100;
            } else {
                rightPercent = (float) (rightNum * 100) / studyRecords.getExercisenum();
            }

            viewholderForStudyRecord.tv_correctpercent.setText(getString(R.string.rightpercent) + mDecimalFormat.format(rightPercent) + getString(R.string
                    .percent));

            return convertView;
        }
    }

    private class ViewholderForStudyRecord {
        private TextView tv_categoryName;
        private TextView tv_complete;
        private TextView tv_correctpercent;
        private TextView tv_date;
    }

    private void deleteStudyRecord() {
        StudyRecords studyrecords = studyRecordsList.get(selectedPosition);
        DebugUtil.e("deleteStudyRecord:"+studyrecords.toString());
        if ("0".equals(studyrecords.getType()) && "0".equals(studyrecords.getToday())) {
            //对于今日特训的非当天学习记录,可以直接删除
            mDaoutils.deleteStudyRecord(studyrecords);
        } else {
            //删除标志位设置成1,假删除
            studyrecords.setIsdelete("1");
            mDaoutils.updateStudyRecord(studyrecords);
        }

        //更新ui
        studyRecordsList.remove(studyrecords);
        if (studyRecordsList.size()==0){
            rl_nodata.setVisibility(View.VISIBLE);
        }
        mStudyRecordAdapter.notifyDataSetChanged();
    }

    private class RunnableForPrepareExerciseDatas implements Runnable {
        private StudyRecordsActivity sturecordActivity;
        private long id;

        public RunnableForPrepareExerciseDatas(StudyRecordsActivity sturecordActivity, long id) {
            this.sturecordActivity = new WeakReference<>(sturecordActivity).get();
            this.id = id;
        }

        @Override
        public void run() {
            //重置数据，防止上次数据影响
            DataStore_ExamLibrary.resetDatas();
            DaoUtils daoutils = DaoUtils.getInstance();
            final StudyRecords studyRecords = daoutils.queryStudyRecords(id);
            //是否真题
            int isEvaluation = 1;
            if ("2".equals(studyRecords.getType())) {
                //真题测评
                DoVipExerciseActivity.initExerciseDataForExerciseEvaluation(studyRecords);
                isEvaluation = 2;
            } else if("5".equals(studyRecords.getType())){
                DoVipExerciseActivity.initExerciseDataForExerciseEvaluation(studyRecords);
                isEvaluation = 5;
            }else {
                isEvaluation = 1;
                DoVipExerciseActivity.initExerciseData(studyRecords);
            }

            DoVipExerciseActivity.initSelectChoiceList(studyRecords);

            if (sturecordActivity != null) {

                final int finalIsEvaluation = isEvaluation;
                sturecordActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        sturecordActivity.mCustomLoadingDialog.dismiss();
                        if (DataStore_ExamLibrary.questionDetailList == null || DataStore_ExamLibrary.questionDetailList.size() == 0) {
                            //该模块无题
                            ToastUtils.showToast(R.string.noexericse);
                            return;
                        }
                        //真题
                        if(finalIsEvaluation == 2 || finalIsEvaluation == 5){
                            DataStore_ExamLibrary.paperItem = paperItem;
                            if (paperItem == null) {
                                return;
                            }
//                            if (paperItem.onlyOne==1){
//                                if (studyRecords.getCompleted().equals("1")) {
//                                    DoVipExerciseActivity.newIntent(sturecordActivity, id, 2, -2);
//                                }else {
//                                    DoVipExerciseActivity.newIntentEvaluationRecord(sturecordActivity, id, 0, finalIsEvaluation);
//                                }
//                            }else{
//                                DoVipExerciseActivity.newIntentMoudleRecord(sturecordActivity, id, 0, finalIsEvaluation);
//                            }
                            if (studyRecords.getCompleted()!= null&&studyRecords.getCompleted().equals("1")) {
                                DoVipExerciseActivity.newIntentEvaluationRecord(sturecordActivity, id, 2, -2);
                            }else {
                                DoVipExerciseActivity.newIntentEvaluationRecord(sturecordActivity, id, 0, finalIsEvaluation);
                            }
                        }else {
                            //模块训练
                            DoVipExerciseActivity.newIntentMoudleRecord(sturecordActivity, id, 0, finalIsEvaluation);
                        }
                    }
                });
            }
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();

        //跟新当前做题记录
        StudyRecords studyRecordsSelect = studyRecordsList.get(selectedPosition);
        StudyRecords studyRecordsFinal = mDaoutils.queryStudyRecords(studyRecordsSelect.getId());
        studyRecordsList.set(selectedPosition, studyRecordsFinal);
        mStudyRecordAdapter.notifyDataSetChanged();
    }

    private class  ObtainDataFromListenerEvaluationStudyRecord extends ObtainDataFromNetListener<PaperItem, String>{

        private StudyRecordsActivity context;

        public ObtainDataFromListenerEvaluationStudyRecord(StudyRecordsActivity context){
            this.context = context;
        }
        @Override
        public void onSuccess(final PaperItem res) {

            this.context.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //是否活动卷
                    if (res.onlyOne == 1) {

                        //活动状态 0：未开始，1正在进行，2结束
                        if (res.userone == 1) {
                            //0可做 1次数超过 2未开始 3活动结束不可做
//                            context.mMindDialog.setTitle("试卷做题次数超出");
//                            context.mMindDialog.show();
//                            context.mMindDialog.setCancelGone();

                            //这边判断字段与收藏模考列表不同（所以这边直接判断活动中open为0说明暂未开放解析）
                            if (res.open.equals("0")){
                                context.mMindDialog.setTitle("模考暂未结束，为确保公正，无法查看解析！");
                                context.mMindDialog.show();
                                context.mMindDialog.setCancelGone();
                                return;
                            }

                            context.mCustomLoadingDialog.setTitle(getString(R.string.prepareexercise_ing));
                            context.mCustomLoadingDialog.show();
                            context.paperItem = res;
                            AsyncHttpClientHelper.createInstance().getThreadPool().execute(new RunnableForPrepareExerciseDatas(context, context.studyRecordsList.get
                                    (selectedPosition).getId()));
                            return;
                        } else if (res.userone == 2) {
                            context.mMindDialog.setTitle("活动未开始");
                            context.mMindDialog.show();
                            context.mMindDialog.setCancelGone();
                            return;
                        } else if (res.userone == 3) {
                            //学员是否可做
                            if (res.openHour!=-1){
                            context.mMindDialog.setTitle("模考已结束，暂未开放解析！");
                            context.mMindDialog.show();
                            context.mMindDialog.setCancelGone();
                                return;
                            }
                        }
//                        else if (res.userone==0){
//                            context.mMindDialog.setTitle("活动卷请到真题模考做题");
//                            context.mMindDialog.show();
//                            context.mMindDialog.setCancelGone();
//                            return;
//                        }

                        if (res.userone==0){
                            context.mMindDialog.setTitle("活动卷请到在线模考做题");
                            context.mMindDialog.show();
                            context.mMindDialog.setCancelGone();
                            return;
                        }
                        if (res.isactivitie==1){
                            context.mMindDialog.setTitle("活动卷请到在线模考做题");
                            context.mMindDialog.show();
                            context.mMindDialog.setCancelGone();
                            return;
                        }
                    }
                    context.mCustomLoadingDialog.setTitle(getString(R.string.prepareexercise_ing));
                    context.mCustomLoadingDialog.show();
                    context.paperItem = res;
                    AsyncHttpClientHelper.createInstance().getThreadPool().execute(new RunnableForPrepareExerciseDatas(context, context.studyRecordsList.get(selectedPosition).getId()));
                }
            });
        }

        @Override
        public void onFailure(final String res) {
            this.context.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (res.equals(SendRequest.ERROR_NETWORK)) {
                        ToastUtils.showToast(R.string.network);
                    } else if (res.equals(SendRequest.ERROR_SERVER)) {
                        ToastUtils.showToast(R.string.server_error);
                    } else {
                        ToastUtils.showToast(res);
                    }
                }
            });
        }
    }
}
