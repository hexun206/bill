package com.huatu.teacheronline.exercise;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bluelinelabs.logansquare.LoganSquare;
import com.google.gson.Gson;
import com.greendao.ChapterTree;
import com.greendao.DaoUtils;
import com.greendao.StudyRecords;
import com.huatu.teacheronline.BaseActivity;
import com.huatu.teacheronline.R;
import com.huatu.teacheronline.engine.AsyncHttpClientHelper;
import com.huatu.teacheronline.exercise.bean.CategoryBean;
import com.huatu.teacheronline.utils.DebugUtil;
import com.huatu.teacheronline.utils.ToastUtils;
import com.huatu.teacheronline.widget.CustomAlertDialog;
import com.ta.utdid2.android.utils.StringUtils;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by ljzyuhenda on 16/2/17.
 * 模块练习 知识点做题页面
 */
public class ModuleExerciseDetailActivity extends BaseActivity implements AdapterView.OnItemClickListener {
    public static final String ACTION_NEXT_GROUP = "action_next_group";
    private DaoUtils mDaoUtils;
    private List<CategoryBean> mCategoryBeanList;
    private CategoryBean mParentCategoryBean;
    private int mChoosePosition;
    private CategoryItemAdapter mCategoryItemAdapter;
    private ListView mLv_category;
    private CustomAlertDialog mLoadingDialog;
    private static final int exerciseNum = 10;
    private RelativeLayout rl_quickExercise;
    private TextView tv_main_title;
    private RelativeLayout rl_main_left;
    private SendRequestUtilsForExercise mSendRequestUtilsForExercise;
    private int currenPosition = 0;

    @Override
    public void initView() {
        setContentView(R.layout.activity_moduleexercisedetail);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_NEXT_GROUP);
        intentFilter.addAction(ACTION_REFRESH);
        setIntentFilter(intentFilter);
        mLv_category = (ListView) findViewById(R.id.lv_category);

        tv_main_title = (TextView) findViewById(R.id.tv_main_title);
        rl_main_left = (RelativeLayout) findViewById(R.id.rl_main_left);

        rl_quickExercise = (RelativeLayout) findViewById(R.id.rl_quickExercise);
        mLoadingDialog = new CustomAlertDialog(this, R.layout.dialog_loading_custom);
        mLoadingDialog.setTitle(getString(R.string.prepareexercise_ing));
        mLoadingDialog.setCancelable(false);

        mDaoUtils = DaoUtils.getInstance();
        mChoosePosition = getIntent().getIntExtra("position", -1);
        String id = SendRequestUtilsForExercise.getKeyForExercisePackageDownload();
        ChapterTree chapterTree = mDaoUtils.queryChapterTree(id);
        try {
            mParentCategoryBean = LoganSquare.parseList(chapterTree.getChaptertree(), CategoryBean.class).get(0).children.get(mChoosePosition);
        } catch (IOException e) {
            e.printStackTrace();
        }
        tv_main_title.setText(mParentCategoryBean.name);

        if (mParentCategoryBean != null) {
            mCategoryBeanList = mParentCategoryBean.children;
            for (int i = 0; i < mCategoryBeanList.size(); i++) {
                String count = mCategoryBeanList.get(i).getCount();
                DebugUtil.e("ModuleExerciseDetailActivity: "+mCategoryBeanList.get(i).qids.toString());
//                DebugUtil.e("ModuleExerciseDetailActivity: "+mCategoryBeanList.get(i).deleteqids.toString());
//                DebugUtil.e("ModuleExerciseDetailActivity: "+mCategoryBeanList.get(i).updateqids.toString());
            }
        }
        mCategoryItemAdapter = new CategoryItemAdapter();
        mLv_category.setAdapter(mCategoryItemAdapter);

        mSendRequestUtilsForExercise = SendRequestUtilsForExercise.getInstance();
    }

    @Override
    public void setListener() {
        mLv_category.setOnItemClickListener(this);
        rl_quickExercise.setOnClickListener(this);
        rl_main_left.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_quickExercise:
                mLoadingDialog.show();
                AsyncHttpClientHelper.createInstance().getThreadPool().execute(new RunnableForPrepareExerciseDatas(this, -1, 0));
                break;
            case R.id.rl_main_left:
                finish();
                break;
        }
    }

    /**
     * 跳转模块训练页面
     *
     * @param context
     * @param position 选择的大知识点位置
     */
    public static void newIntent(Activity context, int position) {
        Intent intent = new Intent(context, ModuleExerciseDetailActivity.class);
        intent.putExtra("position", position);

        context.startActivity(intent);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        mLoadingDialog.show();
        currenPosition = position;
        AsyncHttpClientHelper.createInstance().getThreadPool().execute(new RunnableForPrepareExerciseDatas(this, position, 1));
    }

    private class CategoryItemAdapter extends BaseAdapter {

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
            StudyRecords studyRecords = mDaoUtils.queryStudyRecordsByType("1", categoryBean.cid, SendRequestUtilsForExercise.getKeyForExercisePackageDownload
                    (), mSendRequestUtilsForExercise.userId);
            if (studyRecords != null && studyRecords.getLastprogress() > categoryBean.qids.size()) {
                mDaoUtils.deleteStudyRecord(studyRecords);
                studyRecords = null;
            }
            ViewHolderForModuleExercise viewHolder;
            if (convertView == null) {
                convertView = View.inflate(ModuleExerciseDetailActivity.this, R.layout.item_exercisefortoday, null);

                viewHolder = new ViewHolderForModuleExercise();
                viewHolder.tv_categoryName = (TextView) convertView.findViewById(R.id.tv_categoryName);
                viewHolder.tv_exerciseNum = (TextView) convertView.findViewById(R.id.tv_exerciseNum);
                viewHolder.progressBar = (ProgressBar) convertView.findViewById(R.id.progressBar);

                convertView.setTag(viewHolder);
            }

            viewHolder = (ViewHolderForModuleExercise) convertView.getTag();
            viewHolder.tv_categoryName.setText(categoryBean.name);
            viewHolder.tv_exerciseNum.setText(String.valueOf(categoryBean.qids.size()));

            if (studyRecords == null) {
                viewHolder.progressBar.setProgress(0);
                viewHolder.tv_exerciseNum.setText(0 + getString(R.string.divide) + categoryBean.qids.size());
            } else if ("1".equals(studyRecords.getCompleted())) {
                int totalProgress = studyRecords.getLastprogress();
                int totalExerciseNum = categoryBean.qids.size();
                if (totalExerciseNum == 0) {
                    viewHolder.progressBar.setProgress(100);
                    viewHolder.tv_exerciseNum.setText(totalProgress + getString(R.string.divide) + totalExerciseNum);
                } else {
                    viewHolder.progressBar.setProgress(totalProgress * 100 / totalExerciseNum);
                    viewHolder.tv_exerciseNum.setText(totalProgress + getString(R.string.divide) + totalExerciseNum);
                }
            } else {
                int totalProgress;
                if (studyRecords.getCurrentprogress() == null) {
                    totalProgress = studyRecords.getLastprogress() - studyRecords.getExercisenum();
                } else {
                    totalProgress = studyRecords.getLastprogress() + Integer.valueOf(studyRecords.getCurrentprogress()) + 1 - studyRecords.getExercisenum();
                }

                int totalExerciseNum = categoryBean.qids.size();

                if (totalExerciseNum == 0) {
                    viewHolder.progressBar.setProgress(100);
                    viewHolder.tv_exerciseNum.setText(totalProgress + getString(R.string.divide) + totalExerciseNum);
                } else {
                    viewHolder.progressBar.setProgress(totalProgress * 100 / totalExerciseNum);
                    viewHolder.tv_exerciseNum.setText(totalProgress + getString(R.string.divide) + totalExerciseNum);
                }
            }

            return convertView;
        }
    }

    private class ViewHolderForModuleExercise {
        TextView tv_categoryName;
        TextView tv_exerciseNum;
        ProgressBar progressBar;
    }

    private class RunnableForPrepareExerciseDatas implements Runnable {
        private ModuleExerciseDetailActivity moduleExerciseDetailActivity;
        private long id;
        private int position;
        private int type;//0 表明是快速组卷;1 表明是知识点训练

        /**
         * @param moduleExerciseDetailActivity
         * @param position                     选择知识点训练时,该字段有效,表示当前选择的知识点位置
         * @param type                         0 表明是快速组卷;1 表明是知识点训练
         */
        public RunnableForPrepareExerciseDatas(ModuleExerciseDetailActivity moduleExerciseDetailActivity, int position, int type) {
            this.moduleExerciseDetailActivity = new WeakReference<>(moduleExerciseDetailActivity).get();
            this.position = position;
            this.type = type;
        }

        @Override
        public void run() {
            if (type == 0) {
                initModuleDetailQuickExerciseDatasFromDB();
            } else {
                initModuleDetailDatasFromDB();
            }

            //重置数据，防止上次数据影响
            DataStore_ExamLibrary.resetDatas();
            DaoUtils daoutils = DaoUtils.getInstance();
            StudyRecords studyRecords = daoutils.queryStudyRecords(id);
            //更新学习记录为未删除
            studyRecords.setIsdelete("0");
            daoutils.updateStudyRecord(studyRecords);

            DoVipExerciseActivity.initExerciseData(studyRecords);
            DoVipExerciseActivity.initSelectChoiceList(studyRecords);

            if (moduleExerciseDetailActivity != null) {

                moduleExerciseDetailActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        moduleExerciseDetailActivity.mLoadingDialog.dismiss();
                        if (DataStore_ExamLibrary.questionDetailList == null || DataStore_ExamLibrary.questionDetailList.size() == 0) {
                            //该模块无题
                            ToastUtils.showToast(R.string.noexericse);
                            return;
                        }

                        DoVipExerciseActivity.newIntent(moduleExerciseDetailActivity, id, 0,1);
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
                    CategoryBean bean = moduleExerciseDetailActivity.mCategoryBeanList.get(position);
                    StudyRecords studyRecords = daoUtils.queryStudyRecordsByType("1", bean.cid, SendRequestUtilsForExercise.getKeyForExercisePackageDownload
                            (), sendRequestUtilsForExercise.userId);
                    int lastProgress = 0;
                    boolean isFirstimeDoExercise = false;//表明是第一次做该知识点模块训练
                    if (studyRecords == null) {
                        //表明第一次做该知识点的模块训练
                        studyRecords = new StudyRecords();
                        studyRecords.setDate(new Date());
                        studyRecords.setType("1");//0今日特训 1模块题海 2真题测评
                        studyRecords.setUserid(sendRequestUtilsForExercise.userId);
                        studyRecords.setChoosecategory(SendRequestUtilsForExercise.getKeyForExercisePackageDownload());
                        studyRecords.setName(bean.name);
                        studyRecords.setIsdelete("0");
                        studyRecords.setCid(bean.cid);

                        isFirstimeDoExercise = true;
                    } else {
                        lastProgress = studyRecords.getLastprogress();
                    }

                    if (lastProgress >= bean.qids.size()) {
                        //表明所有题已完成一遍,重置为0,重新开始
                        lastProgress = 0;
                    }

                    long id;
                    if ("1".equals(studyRecords.getCompleted()) || isFirstimeDoExercise) {
                        //表明已完成或者第一次,则抽取10道题
                        List<String> selectList = new ArrayList<>();//本次抽选的10道题
                        Gson gson = new Gson();

                        //挑选指定数目的习题
                        int length = bean.qids.size();
                        if (lastProgress + exerciseNum > length) {
                            //没有更多习题
                            selectList.addAll(bean.qids.subList(lastProgress, length));
                            lastProgress = length;
                        } else {
                            selectList.addAll(bean.qids.subList(lastProgress, lastProgress + exerciseNum));//0-9不包括10
                            lastProgress = lastProgress + exerciseNum;
                        }

                        studyRecords.setLastprogress(lastProgress);

                        if (selectList.size() == 0) {
                            studyRecords.setCompleted("1");//无习题,直接设置成完成状态
                        } else {
                            studyRecords.setCompleted("0");
                        }

                        studyRecords.setEids(gson.toJson(selectList, ArrayList.class));
                        studyRecords.setExercisenum(selectList.size());
                        studyRecords.setUsedtime(null);
                        studyRecords.setCurrentprogress(null);
                        studyRecords.setChoicesforuser(null);

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

        private void initModuleDetailQuickExerciseDatasFromDB() {
            final DaoUtils daoUtils = DaoUtils.getInstance();
            final SendRequestUtilsForExercise sendRequestUtilsForExercise = SendRequestUtilsForExercise.getInstance();
            daoUtils.getDaoSession().runInTx(new Runnable() {
                @Override
                public void run() {
                    //快速组卷,只保留一份学习记录,从父知识点中取题
                    CategoryBean bean = moduleExerciseDetailActivity.mParentCategoryBean;
                    //快速组卷 cid -> 为知识点id + quick
                    StudyRecords studyRecords = daoUtils.queryStudyRecordsByType("1", bean.cid + "quick", SendRequestUtilsForExercise
                            .getKeyForExercisePackageDownload(), sendRequestUtilsForExercise.userId);
                    boolean isFirstimeDoExercise = false;//表明是第一次做快速组卷
                    if (studyRecords == null) {
                        //表明第一次做该知识点的模块训练
                        studyRecords = new StudyRecords();
                        studyRecords.setDate(new Date());
                        studyRecords.setType("1");//0今日特训 1模块题海 2真题测评
                        studyRecords.setUserid(sendRequestUtilsForExercise.userId);
                        studyRecords.setChoosecategory(SendRequestUtilsForExercise.getKeyForExercisePackageDownload());
                        studyRecords.setName(bean.name);
                        studyRecords.setIsdelete("0");
                        studyRecords.setCid(bean.cid + "quick");
                        isFirstimeDoExercise = true;
                    }

                    long id;
                    //挑选指定数目的习题
                    List<String> selectList = new ArrayList<>();//本次抽选的10道题
                    List<Integer> selectRandoms = new ArrayList<>();//本次抽选的num
                    Gson gson = new Gson();
                    int length = bean.qids.size();
                    int index = 0;
                    if (length > exerciseNum) {
                        while (index < exerciseNum) {
                            int selectNum = (int) (Math.random() * length);// 10 0 1 2
                            if (!selectRandoms.contains(selectNum)) {
                                selectList.add(bean.qids.get(selectNum));
                                selectRandoms.add(selectNum);

                                index++;
                            }
                        }
                    } else {
                        selectList.addAll(bean.qids);
                    }

                    if (selectList.size() == 0) {
                        studyRecords.setCompleted("1");//无习题,直接设置成完成状态
                    } else {
                        studyRecords.setCompleted("0");
                    }

                    studyRecords.setEids(gson.toJson(selectList, ArrayList.class));
                    studyRecords.setExercisenum(selectList.size());
                    studyRecords.setUsedtime(null);
                    studyRecords.setCurrentprogress(null);
                    studyRecords.setChoicesforuser(null);

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
    protected void onRestart() {
        super.onRestart();

        mCategoryItemAdapter.notifyDataSetChanged();
    }

    @Override
    public void onReceiveBroadCast(Context context, Intent intent) {
        super.onReceiveBroadCast(context, intent);
        if (intent.getAction().equals(ACTION_NEXT_GROUP)) {
            AsyncHttpClientHelper.createInstance().getThreadPool().execute(new RunnableForPrepareExerciseDatas(this, currenPosition, 1));
        }else {
            mCategoryItemAdapter.notifyDataSetChanged();
        }
    }
}
