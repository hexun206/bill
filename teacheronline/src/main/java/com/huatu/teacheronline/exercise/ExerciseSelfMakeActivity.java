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

import com.bluelinelabs.logansquare.LoganSquare;
import com.google.gson.Gson;
import com.greendao.ChapterTree;
import com.greendao.DaoUtils;
import com.greendao.ExerciseDownloadPackage;
import com.greendao.QuestionDetail;
import com.greendao.StudyRecords;
import com.huatu.teacheronline.BaseActivity;
import com.huatu.teacheronline.R;
import com.huatu.teacheronline.engine.ObtainDataFromNetListener;
import com.huatu.teacheronline.engine.SendRequest;
import com.huatu.teacheronline.exercise.bean.CategoryBean;
import com.huatu.teacheronline.exercise.download.DownLoadCallBackForExercise;
import com.huatu.teacheronline.exercise.download.DownLoadInfo;
import com.huatu.teacheronline.exercise.download.DownLoadManagerForExercise;
import com.huatu.teacheronline.exercise.download.DownloadService;
import com.huatu.teacheronline.utils.DebugUtil;
import com.huatu.teacheronline.utils.FileUtils;
import com.huatu.teacheronline.utils.ToastUtils;
import com.huatu.teacheronline.widget.CustomAlertDialog;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by ljzyuhenda on 16/1/22.
 * 今日定制
 */
public class ExerciseSelfMakeActivity extends BaseActivity implements AdapterView.OnItemClickListener {
    private ListView mLv_category_1;
    private ListView mLv_category_2;
    private Category1Adapter mCategory1Adapter;
    private Category2Adapter mCategory2Adapter;
    private TextView tv_commit;
    private WeakReference<ExerciseSelfMakeActivity> mContextWeakRefernce;
    private CustomAlertDialog mCustomLoadingDialog;
    private ObtainDataFromNetListenerGetChapterTree mObtainDataFromNetListenerGetChapterTree;
    private List<CategoryBean> mChapterTreeDatas;
    private int positionFor1Selected = 0;
    private ProgressBar progressBar;
    private TextView tv_loading;
    private SendRequestUtilsForExercise mSendRequestUtilsForExercise;
    private DaoUtils mDaoUtils;
    private DownLoadManagerForExercise mDownLoadManagerForExercise;
    private static String TAG = "ExerciseSelfMakeActivity";
    private CustomAlertDialog mCustomUpdateDialog;
    private DownLoadInfo downLoadInfo;
    private List<String> mCidsSelected;
    private int mNumExercise = 5;
    private TextView tv_main_title;
    private RelativeLayout rl_main_left;
    private String mVersion;
    private TextView tv_num_5;
    private TextView tv_num_10;
    private TextView tv_num_15;

    @Override
    public void initView() {
        setContentView(R.layout.activity_exercise_selfmake_layout);

        tv_main_title = (TextView) findViewById(R.id.tv_main_title);
        tv_main_title.setText(R.string.training_today);
        rl_main_left = (RelativeLayout) findViewById(R.id.rl_main_left);

        mLv_category_1 = (ListView) findViewById(R.id.lv_category_1);
        mCategory1Adapter = new Category1Adapter();
        mLv_category_1.setAdapter(mCategory1Adapter);

        mLv_category_2 = (ListView) findViewById(R.id.lv_category_2);
        mCategory2Adapter = new Category2Adapter();
        mLv_category_2.setAdapter(mCategory2Adapter);
        tv_commit = (TextView) findViewById(R.id.tv_commit);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        tv_loading = (TextView) findViewById(R.id.tv_loading);

        tv_num_5 = (TextView) findViewById(R.id.tv_num_5);
        tv_num_10 = (TextView) findViewById(R.id.tv_num_10);
        tv_num_15 = (TextView) findViewById(R.id.tv_num_15);

        mContextWeakRefernce = new WeakReference<>(this);
        mCustomLoadingDialog = new CustomAlertDialog(this, R.layout.dialog_loading_custom);
        mCustomLoadingDialog.setCancelable(false);

        mCustomUpdateDialog = new CustomAlertDialog(this, R.layout.dialog_join_mydirect);
        mCustomUpdateDialog.setTitle(getString(R.string.updateinfoforexercise));
        mCustomUpdateDialog.setCancelable(false);

        mObtainDataFromNetListenerGetChapterTree = new ObtainDataFromNetListenerGetChapterTree(mContextWeakRefernce);
        SendRequest.getChapterTree(mObtainDataFromNetListenerGetChapterTree);

        mSendRequestUtilsForExercise = SendRequestUtilsForExercise.getInstance();
        mSendRequestUtilsForExercise.assignDatas();

        mDaoUtils = DaoUtils.getInstance();
        mDownLoadManagerForExercise = DownloadService.getDownloadManager_exercise();

        mCidsSelected = new ArrayList<>();
    }

    private void initDownload() {
        String key = SendRequestUtilsForExercise.getKeyForExercisePackageDownload();
        downLoadInfo = initDownLoadInfo(key, mVersion);

        DownLoadCallBackExerciseWithDialog listener = new DownLoadCallBackExerciseWithDialog(DownLoadCallBackForExercise.DownLoadExerciseType
                .ExerciseSelfMakeActivity, mContextWeakRefernce);
        listener.setKey(key);
        listener.setViewFirst(new WeakReference<View>(tv_commit));
        listener.setViewSecond(new WeakReference<View>(tv_loading));
        listener.setViewThird(new WeakReference<View>(progressBar));

        downLoadInfo.listener = listener;

        ExerciseDownloadPackage exerciseDownloadPackage = mDaoUtils.queryExerciseDownloadPackageInfo(key);

        //点击更新确定时,isupdate为true;本次更新成功时,isupdate 为false
        if (exerciseDownloadPackage != null && !downLoadInfo.isUpdate) {
            //保存数据库成功
            if (!exerciseDownloadPackage.getVersions().equals(mVersion)) {
                //有新版本数据
                showUpdateDialog();
            } else {
                //无新版本数据
                tv_commit.setText(R.string.commit);
                tv_commit.setVisibility(View.VISIBLE);
                tv_loading.setVisibility(View.GONE);
                progressBar.setVisibility(View.GONE);
            }
        } else if (DownLoadCallBackForExercise.DownLoadExerciseStatus.DownLoading == downLoadInfo.status || DownLoadCallBackForExercise
                .DownLoadExerciseStatus.Success == downLoadInfo.status) {
            //第一次下载中或者更新中 或者下载成功尚未数据库插入完成
            listener.refreshItems(downLoadInfo.status);
        } else {
            //开始在下该题包
            tv_commit.setText(R.string.download);
            tv_commit.setVisibility(View.VISIBLE);
            tv_loading.setVisibility(View.GONE);
            progressBar.setVisibility(View.GONE);
        }
    }

    @Override
    public void setListener() {
        tv_commit.setOnClickListener(this);
        mLv_category_1.setOnItemClickListener(this);
        mCustomUpdateDialog.setOkOnClickListener(this);
        mCustomUpdateDialog.setCancelOnClickListener(this);
        mLv_category_2.setOnItemClickListener(this);
        rl_main_left.setOnClickListener(this);
        tv_num_5.setOnClickListener(this);
        tv_num_10.setOnClickListener(this);
        tv_num_15.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        String key;
        switch (v.getId()) {
            case R.id.tv_commit:
                key = SendRequestUtilsForExercise.getKeyForExercisePackageDownload();
                ExerciseDownloadPackage exerciseDownloadPackage = mDaoUtils.queryExerciseDownloadPackageInfo(key);

                DownLoadInfo downLoadInfo = initDownLoadInfo(key, mVersion);
                if (exerciseDownloadPackage != null && !downLoadInfo.isUpdate) {
                    //保存数据库成功且非更新状态,则可以做题
                    if (mCidsSelected.size() == 0) {
                        ToastUtils.showToast(R.string.choosecategory);
                    } else {
                        initSelfMakeExerciseDbInfos();
                        ExerciseForTodayActivity.newIntent(this);
                        finish();
                    }
                } else if (DownLoadCallBackForExercise.DownLoadExerciseStatus.DownLoading == downLoadInfo.status || DownLoadCallBackForExercise
                        .DownLoadExerciseStatus.Success == downLoadInfo.status) {
                    //第一次下载中或者更新中 或者下载成功尚未数据库插入完成
//                    ((DownLoadCallBackForExercise) downLoadInfo.listener).refreshItems();
                } else {
                    //开始下载该题包
                    mDownLoadManagerForExercise.startDownLoadTask(key, 0,0);
                }

                break;
            case R.id.tv_dialog_cancel:
                //取消更新,直接做题
                tv_commit.setText(R.string.commit);
                tv_commit.setVisibility(View.VISIBLE);
                tv_loading.setVisibility(View.GONE);
                progressBar.setVisibility(View.GONE);
                if (mCustomUpdateDialog != null && mCustomUpdateDialog.isShow()) {
                    mCustomUpdateDialog.dismiss();
                }
                break;
            case R.id.tv_dialog_ok:
                //进行更新
                key = SendRequestUtilsForExercise.getKeyForExercisePackageDownload();
                mDownLoadManagerForExercise.startDownLoadTask(key, 0,0);
                if (ExerciseSelfMakeActivity.this.downLoadInfo != null) {
                    ExerciseSelfMakeActivity.this.downLoadInfo.isUpdate = true;//更新
                }
                if (mCustomUpdateDialog != null && mCustomUpdateDialog.isShow()) {
                    mCustomUpdateDialog.dismiss();
                }

                break;
            case R.id.rl_main_left:
                finish();
                break;
            case R.id.tv_num_5:
                mNumExercise = 5;

                tv_num_5.setTextColor(getResources().getColor(R.color.green005));
                tv_num_10.setTextColor(getResources().getColor(R.color.black));
                tv_num_15.setTextColor(getResources().getColor(R.color.black));
                break;
            case R.id.tv_num_10:
                mNumExercise = 10;

                tv_num_5.setTextColor(getResources().getColor(R.color.black));
                tv_num_10.setTextColor(getResources().getColor(R.color.green005));
                tv_num_15.setTextColor(getResources().getColor(R.color.black));
                break;
            case R.id.tv_num_15:
                mNumExercise = 15;

                tv_num_5.setTextColor(getResources().getColor(R.color.black));
                tv_num_10.setTextColor(getResources().getColor(R.color.black));
                tv_num_15.setTextColor(getResources().getColor(R.color.green005));
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (parent.getId() == R.id.lv_category_1) {
            init2Adapter(position);
        } else {
            if (position == 0) {
                if (mCidsSelected.contains(mChapterTreeDatas.get(positionFor1Selected).cid + "0")) {
                    //取消全选
                    mCidsSelected.remove(mChapterTreeDatas.get(positionFor1Selected).cid + "0");
                    for (CategoryBean categoryBean : mChapterTreeDatas.get(positionFor1Selected).children) {
                        if (mCidsSelected.contains(categoryBean.cid)) {
                            mCidsSelected.remove(categoryBean.cid);
                        }
                    }
                } else {
                    //全选
                    mCidsSelected.add(mChapterTreeDatas.get(positionFor1Selected).cid + "0");
                    //全选
                    for (CategoryBean categoryBean : mChapterTreeDatas.get(positionFor1Selected).children) {
                        if (!mCidsSelected.contains(categoryBean.cid)) {
                            mCidsSelected.add(categoryBean.cid);
                        }
                    }
                }
            } else {
                String cidForPosition = mChapterTreeDatas.get(positionFor1Selected).children.get(position - 1).cid;
                if (mCidsSelected.contains(cidForPosition)) {
                    mCidsSelected.remove(cidForPosition);

                    mCidsSelected.remove(mChapterTreeDatas.get(positionFor1Selected).cid + "0");//非全选
                } else {
                    mCidsSelected.add(cidForPosition);

                    //判断是否全选
                    boolean isAllChoosed = true;
                    for (CategoryBean categoryBean : mChapterTreeDatas.get(positionFor1Selected).children) {
                        if (!mCidsSelected.contains(categoryBean.cid)) {
                            isAllChoosed = false;

                            break;
                        }
                    }

                    if (isAllChoosed) {
                        mCidsSelected.add(mChapterTreeDatas.get(positionFor1Selected).cid + "0");
                    } else {
                        mCidsSelected.remove(mChapterTreeDatas.get(positionFor1Selected).cid + "0");
                    }
                }
            }

            mCategory2Adapter.notifyDataSetChanged();
        }
    }

    private class Category1Adapter extends BaseAdapter {
        private List<CategoryBean> beanListFor1;

        public void setBeanListFor1(List<CategoryBean> beanListFor1) {
            this.beanListFor1 = beanListFor1;
        }

        @Override
        public int getCount() {
            if (beanListFor1 == null) {
                return 0;
            }

            return beanListFor1.size();
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
            ViewHolder1 viewHolder1;
            if (convertView == null) {
                convertView = View.inflate(ExerciseSelfMakeActivity.this, R.layout.item_category1_exerciseselfmake, null);

                viewHolder1 = new ViewHolder1();
                viewHolder1.v_select = convertView.findViewById(R.id.v_select);
                viewHolder1.tv_content = (TextView) convertView.findViewById(R.id.tv_content);
                convertView.setTag(viewHolder1);
            }

            viewHolder1 = (ViewHolder1) convertView.getTag();
            viewHolder1.tv_content.setText(beanListFor1.get(position).name);
            if (position == positionFor1Selected) {
                viewHolder1.tv_content.setBackgroundColor(getResources().getColor(R.color.white));
                viewHolder1.v_select.setVisibility(View.VISIBLE);
            } else {
                viewHolder1.v_select.setVisibility(View.GONE);
                viewHolder1.tv_content.setBackgroundColor(getResources().getColor(R.color.white001));
            }

            return convertView;
        }
    }

    private class ViewHolder1 {
        View v_select;
        TextView tv_content;
    }

    private class Category2Adapter extends BaseAdapter {
        private List<CategoryBean> beanListFor2;

        public void setBeanListFor2(List<CategoryBean> beanListFor2) {
            this.beanListFor2 = beanListFor2;
        }

        @Override
        public int getCount() {
            if (beanListFor2 == null) {
                return 0;
            }

            return beanListFor2.size() + 1;//多加全选item
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
            ViewHolder2 viewHolder2;
            if (convertView == null) {
                convertView = View.inflate(ExerciseSelfMakeActivity.this, R.layout.item_category2_exerciseselfmake, null);

                viewHolder2 = new ViewHolder2();
                viewHolder2.v_select = convertView.findViewById(R.id.v_select);
                viewHolder2.tv_content = (TextView) convertView.findViewById(R.id.tv_content);
                convertView.setTag(viewHolder2);
            }
            viewHolder2 = (ViewHolder2) convertView.getTag();

            if (position == 0) {
                //cid
                viewHolder2.tv_content.setText(R.string.chooseall);
                if (mCidsSelected.contains(mChapterTreeDatas.get(positionFor1Selected).cid + "0")) {
                    viewHolder2.v_select.setBackgroundResource(R.drawable.icon_circle_select);
                } else {
                    viewHolder2.v_select.setBackgroundResource(R.drawable.icon_circle_unselect);
                }
            } else {
                CategoryBean bean = beanListFor2.get(position - 1);
                viewHolder2.tv_content.setText(bean.name);
                if (mCidsSelected.contains(bean.cid)) {
                    viewHolder2.v_select.setBackgroundResource(R.drawable.icon_circle_select);
                } else {
                    viewHolder2.v_select.setBackgroundResource(R.drawable.icon_circle_unselect);
                }
            }

            return convertView;
        }
    }

    private class ViewHolder2 {
        View v_select;
        TextView tv_content;
    }

    public static void newIntent(Activity context) {
        Intent intent = new Intent(context, ExerciseSelfMakeActivity.class);
        context.startActivity(intent);
    }

    private static class ObtainDataFromNetListenerGetChapterTree extends ObtainDataFromNetListener<List<CategoryBean>, String> {
        private ExerciseSelfMakeActivity exerciseSelfMakeActivity;

        public ObtainDataFromNetListenerGetChapterTree(WeakReference<ExerciseSelfMakeActivity> contextWeakReference) {
            this.exerciseSelfMakeActivity = contextWeakReference.get();
        }

        @Override
        public void onSuccess(List<CategoryBean> res) {
            DaoUtils daoUtils = DaoUtils.getInstance();
            if (res != null && res.size() > 0) {
                //如果一级知识点下没有二级知识点,则将自己加入二级知识点
                for (CategoryBean categoryBean : res) {
                    for (CategoryBean categoryBean1 : categoryBean.children) {
                        if (categoryBean1.children == null || categoryBean1.children.size() == 0) {
                            //如果一级知识点下没有二级知识点,则将构建一个新的自己加入二级知识点
                            CategoryBean categoryBeanNew = new CategoryBean();
                            categoryBeanNew.qids = categoryBean1.qids;
                            categoryBeanNew.name = categoryBean1.name;
                            categoryBeanNew.cid = categoryBean1.cid;
                            categoryBeanNew.versions = categoryBean1.versions;

                            categoryBean1.children = new ArrayList<>();
                            categoryBean1.children.add(categoryBeanNew);
                        }
                    }
                }

                //有数据,则更新数据库
                String id = SendRequestUtilsForExercise.getKeyForExercisePackageDownload();
                String chapterTreeInfo = null;
                try {
                    chapterTreeInfo = LoganSquare.serialize(res, CategoryBean.class);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                String version = res.get(0).versions;
                ChapterTree chapterTree = new ChapterTree(id, chapterTreeInfo, version);
                daoUtils.insertOrUpdateChapterTree(chapterTree);
            } else {
                //无最新数据,从数据库读取
                String id = SendRequestUtilsForExercise.getKeyForExercisePackageDownload();
                ChapterTree chapterTree = daoUtils.queryChapterTree(id);
                try {
                    res = LoganSquare.parseList(chapterTree.getChaptertree(), CategoryBean.class);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            final List<CategoryBean> resFinal = res;

            if (exerciseSelfMakeActivity != null) {
                exerciseSelfMakeActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        exerciseSelfMakeActivity.mCustomLoadingDialog.dismiss();
                        if (resFinal == null || resFinal.size() == 0) {
                            return;
                        }

                        exerciseSelfMakeActivity.mVersion = resFinal.get(0).versions;
                        exerciseSelfMakeActivity.mChapterTreeDatas = resFinal.get(0).children;

                        exerciseSelfMakeActivity.mCategory1Adapter.setBeanListFor1(resFinal.get(0).children);
                        exerciseSelfMakeActivity.mCategory1Adapter.notifyDataSetChanged();
                        exerciseSelfMakeActivity.init2Adapter(0);

                        exerciseSelfMakeActivity.initDownload();
                    }
                });
            }
        }

        @Override
        public void onFailure(String res) {
            //失败从数据库中读取
            DaoUtils daoUtils = DaoUtils.getInstance();
            List<CategoryBean> categoryBeanList = null;
            String id = SendRequestUtilsForExercise.getKeyForExercisePackageDownload();
            ChapterTree chapterTree = daoUtils.queryChapterTree(id);
            if (chapterTree != null) {
                try {
                    categoryBeanList = LoganSquare.parseList(chapterTree.getChaptertree(), CategoryBean.class);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            final List<CategoryBean> resFinal = categoryBeanList;
            if (exerciseSelfMakeActivity != null) {
                exerciseSelfMakeActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        exerciseSelfMakeActivity.mCustomLoadingDialog.dismiss();
                        if (resFinal == null || resFinal.size() == 0) {
                            return;
                        }

                        exerciseSelfMakeActivity.mChapterTreeDatas = resFinal.get(0).children;

                        exerciseSelfMakeActivity.mCategory1Adapter.setBeanListFor1(resFinal.get(0).children);
                        exerciseSelfMakeActivity.mCategory1Adapter.notifyDataSetChanged();
                        exerciseSelfMakeActivity.init2Adapter(0);
                    }
                });
            }
        }

        @Override
        public void onStart() {
            if (exerciseSelfMakeActivity != null) {
                exerciseSelfMakeActivity.mCustomLoadingDialog.show();
            }
        }
    }

    private void init2Adapter(int positionFor1) {
        positionFor1Selected = positionFor1;
        mCategory1Adapter.notifyDataSetChanged();
        if (mCategory2Adapter != null) {
            if (mChapterTreeDatas != null && mChapterTreeDatas.get(positionFor1) != null) {
                mCategory2Adapter.setBeanListFor2(mChapterTreeDatas.get(positionFor1).children);
                mCategory2Adapter.notifyDataSetChanged();
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

    private static class DownLoadCallBackExerciseWithDialog extends DownLoadCallBackForExercise {
        private WeakReference<ExerciseSelfMakeActivity> activityWeakReference;

        public DownLoadCallBackExerciseWithDialog(DownLoadExerciseType type, WeakReference<ExerciseSelfMakeActivity> activityWeakReference) {
            super(type);

            this.activityWeakReference = activityWeakReference;
        }

        @Override
        public void onStart(final DownLoadExerciseStatus status) {
            if (activityWeakReference.get() != null && !activityWeakReference.get().isDestoryed) {
                activityWeakReference.get().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        DownLoadCallBackExerciseWithDialog.super.onStart(status);
                    }
                });
            }
        }

        @Override
        public void onFailure(final DownLoadExerciseStatus status) {
            if (activityWeakReference.get() != null && !activityWeakReference.get().isDestoryed) {
                activityWeakReference.get().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        DownLoadCallBackExerciseWithDialog.super.onFailure(status);
                        ToastUtils.showToast(R.string.network);
                    }
                });
            }
        }

        @Override
        public void onProcess(final long bytesWritten, final long totalSize, final DownLoadExerciseStatus status) {
            if (activityWeakReference.get() != null && !activityWeakReference.get().isDestoryed) {
                activityWeakReference.get().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        DownLoadCallBackExerciseWithDialog.super.onProcess(bytesWritten, totalSize, status);
                    }
                });
            }
        }

        @Override
        public void onSuccess(final byte[] responseBody, final String key, final DownLoadExerciseStatus status) {
            initDatasIntoDb(responseBody, key);
            DebugUtil.i("init db->onSuccess");
            if (activityWeakReference.get() != null && !activityWeakReference.get().isDestoryed) {
                activityWeakReference.get().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        DebugUtil.i("refreshItems->onSuccess");
                        DownLoadCallBackExerciseWithDialog.super.onSuccess(responseBody, key, status);
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
                    daoUtils.insertQuestionDetail(questionDetailEve);
                }

                //更新题包下载信息-》downloadstatus以及versions
                daoUtils.inserOrUpdateExerciseDownloadPackageInfo(id, downloadInfoRes.versions);
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

    private void showUpdateDialog() {
        mCustomUpdateDialog.show();
    }

    private void initSelfMakeExerciseDbInfos() {
        mDaoUtils.getDaoSession().runInTx(new Runnable() {
            @Override
            public void run() {
                for (CategoryBean bean1 : mChapterTreeDatas) {
                    for (CategoryBean bean2 : bean1.children) {
                        if (mCidsSelected.contains(bean2.cid)) {
                            List<String> selectList = new ArrayList<>();//本次抽选的10道题
                            List<Integer> selectRandoms = new ArrayList<>();//本次抽选的num
                            Gson gson = new Gson();

                            StudyRecords studyRecords = new StudyRecords();
                            studyRecords.setToday("1");
                            studyRecords.setDate(new Date());
                            studyRecords.setType("0");//0今日特训 1模块题海 2真题测评
                            studyRecords.setUserid(mSendRequestUtilsForExercise.userId);
                            studyRecords.setChoosecategory(SendRequestUtilsForExercise.getKeyForExercisePackageDownload());
                            studyRecords.setName(bean2.name);
                            studyRecords.setIsdelete("0");
                            studyRecords.setCid(bean2.cid);

                            //挑选指定数目的习题
                            int length = bean2.qids.size();
                            int index = 0;
                            if (length > mNumExercise) {
                                while (index < mNumExercise) {
                                    int selectNum = (int) (Math.random() * length);// 10 0 1 2
                                    if (!selectRandoms.contains(selectNum)) {
                                        selectList.add(bean2.qids.get(selectNum));
                                        selectRandoms.add(selectNum);

                                        index++;
                                    }
                                }
                            } else {
                                selectList.addAll(bean2.qids);
                            }
                            if (selectList.size() == 0) {
                                studyRecords.setCompleted("1");//无习题,直接设置成完成状态
                            } else {
                                studyRecords.setCompleted("0");
                            }

                            studyRecords.setEids(gson.toJson(selectList, ArrayList.class));
                            studyRecords.setExercisenum(selectList.size());

                            mDaoUtils.insertStudyRecord(studyRecords);
                        }
                    }
                }
            }
        });
    }
}
