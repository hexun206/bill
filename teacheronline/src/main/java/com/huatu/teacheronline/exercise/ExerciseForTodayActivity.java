package com.huatu.teacheronline.exercise;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.greendao.DaoUtils;
import com.greendao.StudyRecords;
import com.huatu.teacheronline.BaseActivity;
import com.huatu.teacheronline.R;
import com.huatu.teacheronline.engine.AsyncHttpClientHelper;
import com.huatu.teacheronline.utils.DebugUtil;
import com.huatu.teacheronline.utils.ToastUtils;
import com.huatu.teacheronline.widget.CustomAlertDialog;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * Created by ljzyuhenda on 16/1/25.
 * 今日特训
 */
public class ExerciseForTodayActivity extends BaseActivity implements AdapterView.OnItemClickListener {
    private ListView mLv_category;
    private CategoryItemAdapter mCategoryItemAdapter;
    private DaoUtils mDaoutils;
    private SendRequestUtilsForExercise mSendRequestUtilsForExercise;
    private List<StudyRecords> studyRecordsList;
    private RelativeLayout rl_main_right;
    private TextView tv_main_right;
    private TextView tv_title;
    private CustomAlertDialog mLoadingDialog;
    private TextView tv_main_title;
    private RelativeLayout rl_main_left;

    @Override
    public void initView() {
        setContentView(R.layout.activity_exercisefortoday_layout);

        tv_main_title = (TextView) findViewById(R.id.tv_main_title);
        tv_main_title.setText(R.string.training_today);
        rl_main_left = (RelativeLayout) findViewById(R.id.rl_main_left);
        tv_main_title.setText(R.string.training_today);

        rl_main_right = (RelativeLayout) findViewById(R.id.rl_main_right);
        rl_main_right.setVisibility(View.VISIBLE);
        tv_main_right = (TextView) findViewById(R.id.tv_main_right);
        tv_main_right.setText(R.string.alter);
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText(getString(R.string.training_today));

        mLv_category = (ListView) findViewById(R.id.lv_category);

        mDaoutils = DaoUtils.getInstance();
        mSendRequestUtilsForExercise = SendRequestUtilsForExercise.getInstance();
        mSendRequestUtilsForExercise.assignDatas();
        studyRecordsList = mDaoutils.queryStudyRecordsForToday("1", SendRequestUtilsForExercise.getKeyForExercisePackageDownload(),
                mSendRequestUtilsForExercise.userId);
        for (int i = 0; i < studyRecordsList.size(); i++) {
            DebugUtil.e("studyRecordsList:" + studyRecordsList.get(i).toString());
            if (studyRecordsList.get(i).getExercisenum() < 6) {
                studyRecordsList.remove(i);
            }
        }
        mCategoryItemAdapter = new CategoryItemAdapter();
        mLv_category.setAdapter(mCategoryItemAdapter);
        mLoadingDialog = new CustomAlertDialog(this, R.layout.dialog_loading_custom);
        mLoadingDialog.setTitle(getString(R.string.prepareexercise_ing));
        mLoadingDialog.setCancelable(false);
    }

    @Override
    public void setListener() {
        mLv_category.setOnItemClickListener(this);
        rl_main_right.setOnClickListener(this);
        rl_main_left.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_main_right:
                updateSelfMakeDbInfos();
                ExerciseSelfMakeActivity.newIntent(this);
                finish();
                break;
            case R.id.rl_main_left:
                finish();
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        mLoadingDialog.show();
        AsyncHttpClientHelper.createInstance().getThreadPool().execute(new RunnableForPrepareExerciseDatas(this, studyRecordsList.get(position).getId()));
    }

    private class CategoryItemAdapter extends BaseAdapter {

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
            StudyRecords studyRecordsToday = studyRecordsList.get(position);
            ViewHolderForExerciseTodayList viewHolder;
            if (convertView == null) {
                convertView = View.inflate(ExerciseForTodayActivity.this, R.layout.item_exercisefortoday, null);

                viewHolder = new ViewHolderForExerciseTodayList();
                viewHolder.tv_categoryName = (TextView) convertView.findViewById(R.id.tv_categoryName);
                viewHolder.tv_exerciseNum = (TextView) convertView.findViewById(R.id.tv_exerciseNum);
                viewHolder.progressBar = (ProgressBar) convertView.findViewById(R.id.progressBar);

                convertView.setTag(viewHolder);
            }

            viewHolder = (ViewHolderForExerciseTodayList) convertView.getTag();
            viewHolder.tv_categoryName.setText(studyRecordsToday.getName());
            int totalExerciseNum;
            int currentProgress;
            if (studyRecordsToday.getExercisenum() == null) {
                totalExerciseNum = 0;
            } else {
                totalExerciseNum = Integer.valueOf(studyRecordsToday.getExercisenum());
            }

            if (studyRecordsToday.getCurrentprogress() == null) {
                currentProgress = 0;
            } else {
                currentProgress = Integer.valueOf(studyRecordsToday.getCurrentprogress()) + 1;
            }

            if ("1".equals(studyRecordsToday.getCompleted())) {
                if (totalExerciseNum == 0) {
                    viewHolder.progressBar.setProgress(0);
                } else {
                    viewHolder.progressBar.setProgress(100);
                }

                viewHolder.tv_exerciseNum.setText(totalExerciseNum + getString(R.string.divide) + totalExerciseNum);
            } else {
                if (totalExerciseNum == 0) {
                    viewHolder.progressBar.setProgress(0);
                    viewHolder.tv_exerciseNum.setText(currentProgress + getString(R.string.divide) + totalExerciseNum);
                } else {
                    viewHolder.progressBar.setProgress(currentProgress * 100 / totalExerciseNum);
                    viewHolder.tv_exerciseNum.setText(currentProgress + getString(R.string.divide) + totalExerciseNum);
                }
            }

            return convertView;
        }
    }

    public static void newIntent(Activity context) {
        Intent intent = new Intent(context, ExerciseForTodayActivity.class);
        context.startActivity(intent);
    }

    private class ViewHolderForExerciseTodayList {
        TextView tv_categoryName;
        TextView tv_exerciseNum;
        ProgressBar progressBar;
    }

    /**
     * 表示今日特训today 0,表示无今日特训
     */
    private void updateSelfMakeDbInfos() {
        mDaoutils.getDaoSession().runInTx(new Runnable() {

            @Override
            public void run() {
                List<StudyRecords> studyRecordses = mDaoutils.queryStudyRecordsForToday("1", SendRequestUtilsForExercise.getKeyForExercisePackageDownload(),
                        mSendRequestUtilsForExercise.userId);
                for (StudyRecords studyRecordsEve : studyRecordses) {
                    studyRecordsEve.setToday("0");

                    mDaoutils.updateStudyRecord(studyRecordsEve);
                }
            }
        });
    }

    private class RunnableForPrepareExerciseDatas implements Runnable {
        private ExerciseForTodayActivity exerciseForTodayActivity;
        private long id;

        public RunnableForPrepareExerciseDatas(ExerciseForTodayActivity exerciseForTodayActivity, long id) {
            this.exerciseForTodayActivity = new WeakReference<>(exerciseForTodayActivity).get();
            this.id = id;
        }

        @Override
        public void run() {
            //重置数据，防止上次数据影响
            DataStore_ExamLibrary.resetDatas();

            DaoUtils daoutils = DaoUtils.getInstance();
            StudyRecords studyRecords = daoutils.queryStudyRecords(id);
            //更新学习记录为未删除
            studyRecords.setIsdelete("0");
            daoutils.updateStudyRecord(studyRecords);

            DoVipExerciseActivity.initExerciseData(studyRecords);
            DoVipExerciseActivity.initSelectChoiceList(studyRecords);

            if (exerciseForTodayActivity != null) {

                exerciseForTodayActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        exerciseForTodayActivity.mLoadingDialog.dismiss();
                        if (DataStore_ExamLibrary.questionDetailList == null || DataStore_ExamLibrary.questionDetailList.size() == 0) {
                            //该模块无题
                            ToastUtils.showToast(R.string.noexericse);
                            return;
                        }

                        DoVipExerciseActivity.newIntent(exerciseForTodayActivity, id, 0);
                    }
                });
            }
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();

        mCategoryItemAdapter.notifyDataSetChanged();
    }
}
