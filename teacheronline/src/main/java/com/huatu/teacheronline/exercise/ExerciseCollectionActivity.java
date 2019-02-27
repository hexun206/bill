package com.huatu.teacheronline.exercise;

import android.app.Activity;
import android.content.Intent;
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
import com.greendao.ExerciseStore;
import com.greendao.StudyRecords;
import com.huatu.teacheronline.BaseActivity;
import com.huatu.teacheronline.R;
import com.huatu.teacheronline.engine.AsyncHttpClientHelper;
import com.huatu.teacheronline.engine.ObtainDataFromNetListener;
import com.huatu.teacheronline.engine.SendRequest;
import com.huatu.teacheronline.exercise.adapter.ChapterTreeAdapter;
import com.huatu.teacheronline.exercise.bean.CategoryBean;
import com.huatu.teacheronline.utils.ToastUtils;
import com.huatu.teacheronline.widget.CustomAlertDialog;
import com.umeng.analytics.MobclickAgent;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by ljzyuhenda on 16/2/22.
 * 模块题海 我的收藏
 */
public class ExerciseCollectionActivity extends BaseActivity implements AdapterView.OnItemClickListener {
    private List<CategoryBean> mCategoryBeanList;
    private ListView lv_collections;
    private CustomAlertDialog mCustomLoadingDialog;
    private CollectionAdapter mCollectionAdapter;
    private DaoUtils mDaoUtils;
    private boolean[] isAlreadyUpdateArr;
    private TextView tv_main_title;
    private RelativeLayout rl_main_left;
//    private ExpandableListView lv_collections_category;
//    private ChapterTreeAdapter adapter;

    @Override
    public void initView() {
        setContentView(R.layout.activity_exercisecollection);

        mCustomLoadingDialog = new CustomAlertDialog(this, R.layout.dialog_loading_custom);
        mCustomLoadingDialog.setCancelable(false);

        lv_collections = (ListView) findViewById(R.id.lv_collections);
//        lv_collections_category = (ExpandableListView) findViewById(R.id.lv_collections_category);
        mCollectionAdapter = new CollectionAdapter();
        lv_collections.setAdapter(mCollectionAdapter);
        mDaoUtils = DaoUtils.getInstance();
        tv_main_title = (TextView) findViewById(R.id.tv_main_title);
        tv_main_title.setText(R.string.store_mine);
        rl_main_left = (RelativeLayout) findViewById(R.id.rl_main_left);

//        adapter = new ChapterTreeAdapter(this,lv_collections_category);
//        lv_collections_category.setAdapter(adapter);

    }

    @Override
    public void setListener() {
        lv_collections.setOnItemClickListener(this);
        rl_main_left.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_main_left:
                back();
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        SendRequest.getCollectionQuestions(new ObtainDataListenerForCollectionExerciseChapterTree(this));
    }

    public static void newIntent(Activity context) {
        Intent intent = new Intent(context, ExerciseCollectionActivity.class);
        context.startActivity(intent);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
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

    private class CollectionAdapter extends BaseAdapter {

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
            ViewHolderCategory viewHolderCategory;
            if (convertView == null) {
                convertView = View.inflate(ExerciseCollectionActivity.this, R.layout.item_exercisecollectionerro, null);
                viewHolderCategory = new ViewHolderCategory();
                viewHolderCategory.tv_categoryName = (TextView) convertView.findViewById(R.id.tv_categoryName);
                viewHolderCategory.tv_exerciseNum = (TextView) convertView.findViewById(R.id.tv_exerciseNum);

                convertView.setTag(viewHolderCategory);
            }

            viewHolderCategory = (ViewHolderCategory) convertView.getTag();
            viewHolderCategory.tv_categoryName.setText(mCategoryBeanList.get(position).name);
            viewHolderCategory.tv_exerciseNum.setText(mCategoryBeanList.get(position).qids.size() + "题");

            return convertView;
        }
    }

    private class ViewHolderCategory {
        TextView tv_categoryName;
        TextView tv_exerciseNum;
    }

    private static class ObtainDataListenerForCollectionExerciseChapterTree extends ObtainDataFromNetListener<CategoryBean, String> {
        private ExerciseCollectionActivity activity;

        public ObtainDataListenerForCollectionExerciseChapterTree(ExerciseCollectionActivity activity) {
            this.activity = new WeakReference<>(activity).get();
        }

        @Override
        public void onSuccess(final CategoryBean res) {
            if (activity != null) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        activity.isAlreadyUpdateArr = new boolean[res.children.size()];
                        activity.mCategoryBeanList = res.children;
                        activity.mCustomLoadingDialog.dismiss();
                        activity.mCollectionAdapter.notifyDataSetChanged();
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
        public void onFailure(String res) {
            if (activity != null) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        activity.mCustomLoadingDialog.dismiss();
                        ToastUtils.showToast(R.string.server_error);
                    }
                });
            }
        }
    }

    private static class RunnableForPrepareExerciseDatas implements Runnable {
        private ExerciseCollectionActivity collectionActivity;
        private long id;//本次练习对应id
        private int position;//点击的位置

        /**
         * @param position 点击的位置
         */
        public RunnableForPrepareExerciseDatas(ExerciseCollectionActivity collectionActivity, int position) {
            this.collectionActivity = new WeakReference<>(collectionActivity).get();
            this.position = position;
        }

        @Override
        public void run() {
            initCollectionDatasFromDb();

            //重置数据，防止上次数据影响
            DataStore_ExamLibrary.resetDatas();

            DaoUtils daoutils = DaoUtils.getInstance();
            StudyRecords studyRecords = daoutils.queryStudyRecords(id);
            DoVipExerciseActivity.initExerciseData(studyRecords);
            DoVipExerciseActivity.initSelectChoiceList(studyRecords);

            if (collectionActivity != null) {

                collectionActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        collectionActivity.mCustomLoadingDialog.dismiss();
                        if (DataStore_ExamLibrary.questionDetailList == null || DataStore_ExamLibrary.questionDetailList.size() == 0) {
                            //该模块无题
                            ToastUtils.showToast(R.string.noexericse);
                            return;
                        }

                        DoVipExerciseActivity.newIntent(collectionActivity, id, 0, 4);
                    }
                });
            }
        }

        private void initCollectionDatasFromDb() {
            final DaoUtils daoUtils = DaoUtils.getInstance();
            final SendRequestUtilsForExercise sendRequestUtilsForExercise = SendRequestUtilsForExercise.getInstance();
            daoUtils.getDaoSession().runInTx(new Runnable() {
                @Override
                public void run() {
                    CategoryBean categoryBean = collectionActivity.mCategoryBeanList.get(position);
                    if (!collectionActivity.isAlreadyUpdateArr[position]) {
                        //第一次进入页面点击item时,更新收藏数据
                        ExerciseStore exerciseStore = new ExerciseStore();
                        exerciseStore.setUserid(sendRequestUtilsForExercise.userId);
                        for (String qid : categoryBean.qids) {
                            //TODO 更新收藏数据 最好是按choosecategory先删,再插入以保证本地收藏数据跟服务器同步
                            exerciseStore.setQid(qid);
                            if (!daoUtils.queryExerciseStore(qid, sendRequestUtilsForExercise.userId)) {
                                daoUtils.insertExerciseStore(exerciseStore);
                            }
                        }

                        collectionActivity.isAlreadyUpdateArr[position] = true;
                    }


                    StudyRecords studyRecords = daoUtils.queryStudyRecordsByType("4", categoryBean.cid, SendRequestUtilsForExercise
                            .getKeyForExercisePackageDownload(), sendRequestUtilsForExercise.userId);
                    boolean isFirstimeDoExercise = false;
                    if (studyRecords == null) {
                        //表明第一次做该真题
                        studyRecords = new StudyRecords();
                        studyRecords.setDate(new Date());
                        studyRecords.setType("4");//0今日特训 1模块题海 2真题测评 3错题中心 4收藏
                        studyRecords.setUserid(sendRequestUtilsForExercise.userId);
                        studyRecords.setChoosecategory(SendRequestUtilsForExercise.getKeyForExercisePackageDownload());
                        studyRecords.setName(categoryBean.name);
                        studyRecords.setIsdelete("0");
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
}
