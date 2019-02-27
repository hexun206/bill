package com.huatu.teacheronline.exercise;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
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
import com.huatu.teacheronline.engine.AsyncHttpClientHelper;
import com.huatu.teacheronline.engine.ObtainDataFromNetListener;
import com.huatu.teacheronline.engine.SendRequest;
import com.huatu.teacheronline.exercise.adapter.ChapterTreeAdapter;
import com.huatu.teacheronline.exercise.bean.CategoryBean;
import com.huatu.teacheronline.exercise.download.DownLoadCallBackForExercise;
import com.huatu.teacheronline.exercise.download.DownLoadInfo;
import com.huatu.teacheronline.exercise.download.DownLoadManagerForExercise;
import com.huatu.teacheronline.exercise.download.DownloadService;
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

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Created by ljzyuhenda on 16/2/17.
 * 模块训练
 */
public class ModuleExerciseActivity extends BaseActivity implements AdapterView.OnItemClickListener {
    public static final String ACTION_NEXT_GROUP = "action_next_group";
    private static String userId;
    private ExpandableListView lv_category;
    private CustomAlertDialog mCustomLoadingDialog;
    private List<CategoryBean> mChapterTreeDatas;
//    private CategoryAdapter mCategoryAdapter;
    private DownLoadManagerForExercise mDownLoadManagerForExercise;
    private DownLoadInfo downLoadInfo;
    private DaoUtils mDaoUtils;
    private SendRequestUtilsForExercise mSendRequestUtilsForExercise;
    private CustomAlertDialog mCustomUpdateDialog;
    private RelativeLayout rl_bottom;
    private TextView tv_commit;
    private ProgressBar progressBar;
    private TextView tv_loading;
    private TextView tv_main_title;
    private RelativeLayout rl_main_left;
    private String mVersion;
    private PopwindowUtil popwindowUtil;
    private ArrayList<CategoryBean> subjects;
    private String subjectName;
    private boolean isDownLoading = false; //是否在下载或者更新题包，主要用于下载时不让切换科目
    public static String[] deleteQids; //删除的题目qid
    public static String[] updateQids;//修改的题目qid
    private static boolean isFirstDownExercise = false; //是否第一次下载题目
    private RelativeLayout mRlTitle;
    private ChapterTreeAdapter adapter;
    private static final int exerciseNum = 10;
    private RelativeLayout rl_main_right;
    private ImageView ib_main_right;
    private TextView tv_main_right;
    private PopwindowDoExercise popwindowDoExercise;//模块题海页面popwindow
    private CategoryBean childCategoryBean;//当前做题的二级知识点
    private CategoryBean groupCategoryBean;//随机做题当前父及知识点
    public static int updateType = 0;//是否增量包 0完整包 1 增量包
    private static List<CategoryBean> localres;
    private static List<CategoryBean> netres;
    private boolean isLcalData = true;//本地是否有数据

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void initView() {
        setContentView(R.layout.activity_moduleexercise);
        updateType = 0;
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_NEXT_GROUP);
        intentFilter.addAction(ACTION_REFRESH);
        setIntentFilter(intentFilter);
        tv_main_title = (TextView) findViewById(R.id.tv_main_title);
        ib_main_right = (ImageView) findViewById(R.id.ib_main_right);
        tv_main_title.setText(R.string.module_examocean);
        tv_main_title.setOnClickListener(this);
        rl_main_left = (RelativeLayout) findViewById(R.id.rl_main_left);
        rl_main_right = (RelativeLayout) findViewById(R.id.rl_main_right);
        rl_main_right.setVisibility(View.VISIBLE);
//        ib_main_right.setImageResource(R.drawable.more_dot);
        ib_main_right.setVisibility(View.GONE);
        tv_main_right = (TextView) findViewById(R.id.tv_main_right);
        tv_main_right.setVisibility(View.VISIBLE);
        tv_main_right.setBackgroundResource(R.drawable.bg_rectangle_frame_green_radius);
        tv_main_right.setText(R.string.analysisAndstore);
//        tv_main_right.setTextColor(getColor(R.color.green004));
        tv_main_right.setTextColor(getResources().getColor(R.color.green004));
        mCustomLoadingDialog = new CustomAlertDialog(this, R.layout.dialog_loading_custom);
        popwindowDoExercise = new PopwindowDoExercise(this, this);
//        mCustomLoadingDialog.setCancelable(false);
        userId = CommonUtils.getSharedPreferenceItem(null, UserInfo.KEY_SP_USERID, "null");
        lv_category = (ExpandableListView) findViewById(R.id.lv_category);
//        mCategoryAdapter = new CategoryAdapter();
        adapter = new ChapterTreeAdapter(this, lv_category);
        lv_category.setAdapter(adapter);
//        adapter.setData(items);
        mRlTitle = (RelativeLayout) findViewById(R.id.rl_titleContainer_main);
        rl_bottom = (RelativeLayout) findViewById(R.id.rl_bottom);
        tv_commit = (TextView) findViewById(R.id.tv_commit);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        tv_loading = (TextView) findViewById(R.id.tv_loading);

        iniData();
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
     * 各类数据初始化
     */
    private void iniData() {
        subjectName = CommonUtils.getSharedPreferenceItem(null, UserInfo.KEY_SP_EXAMSUBJECT_NAME, "");
        SendRequest.getChapterTree2(new ObtainDataFromNetListenerGetChapterTree(this));
        TeacherOnlineUtils.setTextViewRightImage(ModuleExerciseActivity.this, tv_main_title, R.drawable.down_ang, 5);
        tv_main_title.setText(subjectName);
        initDatas();
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
                if (isDownLoading) {
                    ToastUtils.showToast(R.string.exercisepackagedowning_info);
                    popwindowUtil.dismiss();
                    return;
                }
                CommonUtils.putSharedPreferenceItems(null,
                        new String[]{UserInfo.KEY_SP_EXAMSUBJECT_ID, UserInfo.KEY_SP_EXAMSUBJECT_NAME},
                        new String[]{cid, name});
                popwindowUtil.dismiss();
                subjectName = item.name;
                iniData();
            }
        });
    }

    private void initDatas() {
        mDownLoadManagerForExercise = DownloadService.getDownloadManager_exercise();
        mSendRequestUtilsForExercise = SendRequestUtilsForExercise.getInstance();
        mSendRequestUtilsForExercise.assignDatas();

        if (mCustomUpdateDialog == null) {
            mCustomUpdateDialog = new CustomAlertDialog(this, R.layout.dialog_join_mydirect);
        }

        mCustomUpdateDialog.setTitle(getString(R.string.updateinfoforexercise));
        mCustomUpdateDialog.setCancelable(false);
        mDaoUtils = DaoUtils.getInstance();
    }

    @Override
    public void setListener() {
        mCustomUpdateDialog.setOkOnClickListener(this);
        mCustomUpdateDialog.setCancelOnClickListener(this);
        lv_category.setOnItemClickListener(this);
        tv_commit.setOnClickListener(this);
        rl_main_left.setOnClickListener(this);
        rl_main_right.setOnClickListener(this);
        lv_category.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                mCustomLoadingDialog.show();
                groupCategoryBean = null;
                childCategoryBean = mChapterTreeDatas.get(groupPosition).children.get(childPosition);
                AsyncHttpClientHelper.createInstance().getThreadPool().execute(new RunnableForPrepareExerciseDatas(ModuleExerciseActivity.this, -1,
                        childCategoryBean, 1));
                return false;
            }
        });
        lv_category.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {

            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                // We call collapseGroupWithAnimation(int) and
                // expandGroupWithAnimation(int) to animate group
                // expansion/collapse.
                mCustomLoadingDialog.show();
                childCategoryBean = null;
                groupCategoryBean = mChapterTreeDatas.get(groupPosition);
                AsyncHttpClientHelper.createInstance().getThreadPool().execute(new RunnableForPrepareExerciseDatas(ModuleExerciseActivity.this, -1,
                        groupCategoryBean, 0));
                return true;
            }

        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_commit:
                ExerciseDownloadPackage exerciseDownloadPackage = mDaoUtils.queryExerciseDownloadPackageInfo(SendRequestUtilsForExercise
                        .getKeyForExercisePackageDownload());
                String key = SendRequestUtilsForExercise.getKeyForExercisePackageDownload();
                ChapterTree chapterTree = mDaoUtils.queryChapterTree(key);
                DownLoadInfo downLoadInfo = initDownLoadInfo(key, mVersion);
                DebugUtil.i("tv_commit:" + (exerciseDownloadPackage != null && !downLoadInfo.isUpdate) + ",downloadstatus:" + downLoadInfo.status);
                if (exerciseDownloadPackage != null && !downLoadInfo.isUpdate) {
                    //保存数据库成功且非更新状态,则可以做题
//                    ModuleExerciseDetailActivity.newIntent(this);
                } else if (DownLoadCallBackForExercise.DownLoadExerciseStatus.DownLoading == downLoadInfo.status || DownLoadCallBackForExercise
                        .DownLoadExerciseStatus.Success == downLoadInfo.status) {
                    //第一次下载中或者更新中 或者下载成功尚未数据库插入完成
//                    ((DownLoadCallBackForExercise) downLoadInfo.listener).refreshItems();
                } else {
                    //开始下载该题包
                    mDownLoadManagerForExercise.startDownLoadTask(key, 0, updateType);
                    isDownLoading = true;
                    //没有数据,则插入数据库
                    if (chapterTree == null && netres != null) {
                        inSertChapterTree(netres);
                    }
                }

                break;
            case R.id.tv_dialog_cancel:
                //取消更新,直接做题
                if (mCustomUpdateDialog != null && mCustomUpdateDialog.isShow()) {
                    mCustomUpdateDialog.dismiss();
                }
                break;
            case R.id.tv_dialog_ok:
                //进行更新
                key = SendRequestUtilsForExercise.getKeyForExercisePackageDownload();
                mDownLoadManagerForExercise.startDownLoadTask(key, 0, updateType);
                if (ModuleExerciseActivity.this.downLoadInfo != null) {
                    ModuleExerciseActivity.this.downLoadInfo.isUpdate = true;//更新
                }
                if (mCustomUpdateDialog != null && mCustomUpdateDialog.isShow()) {
                    mCustomUpdateDialog.dismiss();
                }
                if (netres != null) {
                    inSertChapterTree(netres);
                    setAdapterData(netres);
                }
                break;
            case R.id.rl_main_left:
                finish();

                break;
            case R.id.rl_main_right:
                popwindowDoExercise.setData(4);
                popwindowDoExercise.show(rl_main_right);
                break;
            case R.id.tv_main_title:
                popwindowUtil.show(mRlTitle, 2);
                break;
            case R.id.rl_creat_sub:
                Intent intent = new Intent();
                intent.setClass(ModuleExerciseActivity.this, ExamSubjectChooseNewActivity.class);
                intent.putExtra("whereFrom", 1);
                UserInfo.setTempSubjectsIdName();
                startActivityForResult(intent, UserInfo.CHOOSE_SUBJECT_RESULT_MODULE);
                popwindowUtil.dismiss();
                break;
            case R.id.tv_collection_exercise://我的收藏
                popwindowDoExercise.dismiss();
                MobclickAgent.onEvent(this, "myCollection");
                ExerciseCollectionActivity.newIntent(this);
                break;
            case R.id.tv_share_exercise://我的错题
                popwindowDoExercise.dismiss();
                MobclickAgent.onEvent(this, "centerError");
                //错题中心
                ExerciseErrorCenterActivity.newIntent(this);
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        ExerciseDownloadPackage exerciseDownloadPackage = mDaoUtils.queryExerciseDownloadPackageInfo(SendRequestUtilsForExercise
                .getKeyForExercisePackageDownload());
        String key = SendRequestUtilsForExercise.getKeyForExercisePackageDownload();
        DownLoadInfo downLoadInfo = initDownLoadInfo(key, mVersion);
        DebugUtil.i("tv_commit:" + (exerciseDownloadPackage != null && !downLoadInfo.isUpdate) + ",downloadstatus:" + downLoadInfo.status);
        if (exerciseDownloadPackage != null && !downLoadInfo.isUpdate) {
            //保存数据库成功且非更新状态,则可以做题
            ModuleExerciseDetailActivity.newIntent(this, position);
        } else if (DownLoadCallBackForExercise.DownLoadExerciseStatus.DownLoading == downLoadInfo.status || DownLoadCallBackForExercise
                .DownLoadExerciseStatus.Success == downLoadInfo.status) {
            //第一次下载中或者更新中 或者下载成功尚未数据库插入完成
//                    ((DownLoadCallBackForExercise) downLoadInfo.listener).refreshItems();
            ToastUtils.showToast(R.string.exercisepackagedowning_info);
        } else {
            //开始下载该题包
            mDownLoadManagerForExercise.startDownLoadTask(key, 0, updateType);
            ToastUtils.showToast(R.string.startdownloadexercisepackage_info);
            isDownLoading = true;
        }
    }

    private class CategoryAdapter extends BaseAdapter {
        private List<CategoryBean> chapterTreeDatas;

        @Override
        public int getCount() {
            if (chapterTreeDatas == null) {
                return 0;
            }
            return chapterTreeDatas.size();
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
            CategoryBean bean = mChapterTreeDatas.get(position);
            ViewHolderCategory viewHolderCategory;
            if (convertView == null) {
                convertView = View.inflate(ModuleExerciseActivity.this, R.layout.item_moduleexercise, null);
                viewHolderCategory = new ViewHolderCategory();
                viewHolderCategory.tv_categoryName = (TextView) convertView.findViewById(R.id.tv_categoryName);
                convertView.setTag(viewHolderCategory);
            }

            viewHolderCategory = (ViewHolderCategory) convertView.getTag();
            viewHolderCategory.tv_categoryName.setText(bean.name);

            return convertView;
        }

        public void setChapterTreeDatas(List<CategoryBean> chapterTreeDatas) {
            this.chapterTreeDatas = chapterTreeDatas;
        }
    }

    private class ViewHolderCategory {
        TextView tv_categoryName;
    }

    private static class ObtainDataFromNetListenerGetChapterTree extends ObtainDataFromNetListener<List<CategoryBean>, String> {
        private ModuleExerciseActivity moduleExerciseActivity;

        public ObtainDataFromNetListenerGetChapterTree(ModuleExerciseActivity exerciseActivity) {
            this.moduleExerciseActivity = new WeakReference<>(exerciseActivity).get();
        }

        @Override
        public void onSuccess(List<CategoryBean> res) {
            DaoUtils daoUtils = DaoUtils.getInstance();
            String id = SendRequestUtilsForExercise.getKeyForExercisePackageDownload();
            ChapterTree chapterTree = daoUtils.queryChapterTree(id);
            ExerciseDownloadPackage exerciseDownloadPackage = moduleExerciseActivity.mDaoUtils.queryExerciseDownloadPackageInfo(id);

            if (res != null && res.size() > 0 && res.get(0) != null) {
                //此方法主要是弹窗
                moduleExerciseActivity.mVersion = res.get(0).versions;
                //点击更新确定时,isupdate为true;本次更新成功时,isupdate 为false
                if (exerciseDownloadPackage != null) {
                    //保存数据库成功
                    DebugUtil.e("exerciseDownloadPackage.getVersions():" + exerciseDownloadPackage.getVersions() + "  mVersion:" + res.get(0).versions);
                    if (!exerciseDownloadPackage.getVersions().equals(res.get(0).versions)) {
                        //有新版本数据
                        moduleExerciseActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                moduleExerciseActivity.showUpdateDialog();
                            }
                        });
                    } else {
                        //无新版本数据,不需要显示
                        updateType = 0;
                    }
                }
            }
            if (chapterTree != null) {
                //数据库有数据直接用数据库数据
                try {
                    localres = LoganSquare.parseList(chapterTree.getChaptertree(), CategoryBean.class);
                    moduleExerciseActivity.isLcalData = true;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }else {
                moduleExerciseActivity.isLcalData = false;
            }
            //数据库没数据 网络有数据 第一次版本
            if (res != null && res.size() > 0 && res.get(0) != null) {
                netres = res;
                //如果一级知识点下没有二级知识点,则将自己加入二级知识点
                for (CategoryBean categoryBean : netres) {
                    if (categoryBean == null) {
                        break;
                    }
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
            }

//            if (res != null && res.size() > 0 && res.get(0) != null) {
//                netres = res;
//                //如果一级知识点下没有二级知识点,则将自己加入二级知识点
//                for (CategoryBean categoryBean : res) {
//                    if (categoryBean == null) {
//                        break;
//                    }
//
//                    for (CategoryBean categoryBean1 : categoryBean.children) {
//                        if (categoryBean1.children == null || categoryBean1.children.size() == 0) {
//                            //如果一级知识点下没有二级知识点,则将构建一个新的自己加入二级知识点
//                            CategoryBean categoryBeanNew = new CategoryBean();
//                            categoryBeanNew.qids = categoryBean1.qids;
//                            categoryBeanNew.name = categoryBean1.name;
//                            categoryBeanNew.cid = categoryBean1.cid;
//                            categoryBeanNew.versions = categoryBean1.versions;
//
//                            categoryBean1.children = new ArrayList<>();
//                            categoryBean1.children.add(categoryBeanNew);
//                        }
//                    }
//                }
//                //有数据,则更新数据库
//                String id = SendRequestUtilsForExercise.getKeyForExercisePackageDownload();
//                String chapterTreeInfo = null;
//                try {
//                    chapterTreeInfo = LoganSquare.serialize(res, CategoryBean.class);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//
////                String version = res.get(0).versions;
////                ChapterTree chapterTree = new ChapterTree(id, chapterTreeInfo, version);
////                daoUtils.insertOrUpdateChapterTree(chapterTree);
//
//            } else {
//                //无最新数据,从数据库读取
//                String id = SendRequestUtilsForExercise.getKeyForExercisePackageDownload();
//                ChapterTree chapterTree = daoUtils.queryChapterTree(id);
//                if (chapterTree != null) {
//                    try {
//                        localres = LoganSquare.parseList(chapterTree.getChaptertree(), CategoryBean.class);
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
            moduleExerciseActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    moduleExerciseActivity.mCustomLoadingDialog.dismiss();
                    if (moduleExerciseActivity.isLcalData) {
                        moduleExerciseActivity.setAdapterData(localres);
                    } else {
//                        moduleExerciseActivity.inSertChapterTree(netres);
                        moduleExerciseActivity.setAdapterData(netres);
                    }
                }
            });

        }

        @Override
        public void onFailure(String res) {
            //失败从数据库中读取
            DaoUtils daoUtils = DaoUtils.getInstance();
            List<CategoryBean> categoryBeanList = null;
            String id = SendRequestUtilsForExercise.getKeyForExercisePackageDownload();
            ChapterTree chapterTree = daoUtils.queryChapterTree(id);
            if (chapterTree == null) {
//                moduleExerciseActivity.mCategoryAdapter.setChapterTreeDatas(categoryBeanList);
                moduleExerciseActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //知识树没有内容的时候 提示获取失败
                        moduleExerciseActivity.mCustomLoadingDialog.dismiss();
//                        moduleExerciseActivity.mCategoryAdapter.notifyDataSetChanged();
                        ToastUtils.showToast(moduleExerciseActivity.getString(R.string.getChapterTreeFail));
                    }
                });
                return;
            }
            try {
                categoryBeanList = LoganSquare.parseList(chapterTree.getChaptertree(), CategoryBean.class);
            } catch (IOException e) {
                e.printStackTrace();
            }

            final List<CategoryBean> resFinal = categoryBeanList;
            if (moduleExerciseActivity != null) {
                moduleExerciseActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        moduleExerciseActivity.mCustomLoadingDialog.dismiss();
                        if (resFinal == null || resFinal.size() == 0) {
                            return;
                        }

                        moduleExerciseActivity.mChapterTreeDatas = resFinal.get(0).children;
                        moduleExerciseActivity.mVersion = resFinal.get(0).versions;
//                        moduleExerciseActivity.mCategoryAdapter.setChapterTreeDatas(resFinal.get(0).children);
//                        moduleExerciseActivity.mCategoryAdapter.notifyDataSetChanged();
                        moduleExerciseActivity.adapter.setData(resFinal.get(0).children);
                        moduleExerciseActivity.adapter.notifyDataSetChanged();
                        if (moduleExerciseActivity.mDownLoadManagerForExercise != null) {
                            moduleExerciseActivity.initDownload();
                        }
                    }
                });
            }
        }

        @Override
        public void onStart() {
            if (moduleExerciseActivity != null) {
                moduleExerciseActivity.mCustomLoadingDialog.show();
            }
        }
    }

    /**
     * 设置adapter数据
     *
     * @param res
     */
    private void setAdapterData(List<CategoryBean> res) {
        final List<CategoryBean> resFinal = res;
        if (resFinal == null || resFinal.size() == 0 || resFinal.get(0) == null) {
            return;
        }
//                        mVersion = resFinal.get(0).versions;
        mChapterTreeDatas = resFinal.get(0).children;

//                        mCategoryAdapter.setChapterTreeDatas(resFinal.get(0).children);
//                        DebugUtil.e("setChapterTreeDatas:" + resFinal.get(0).toString());
//                        mCategoryAdapter.notifyDataSetChanged();
        adapter.setData(resFinal.get(0).children);
        adapter.notifyDataSetChanged();
        initDownload();
    }

    /**
     * 将知识树数据插入数据库
     *
     * @param res
     */
    private void inSertChapterTree(List<CategoryBean> res) {
        String i2d = SendRequestUtilsForExercise.getKeyForExercisePackageDownload();
        String chapterTreeInfo = null;
        try {
            chapterTreeInfo = LoganSquare.serialize(res, CategoryBean.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String version = res.get(0).versions;
        ChapterTree c2hapterTree = new ChapterTree(i2d, chapterTreeInfo, version);
        mDaoUtils.insertOrUpdateChapterTree(c2hapterTree);
    }

    public static void newIntent(Activity context) {
        Intent intent = new Intent(context, ModuleExerciseActivity.class);
        context.startActivity(intent);
    }

    private void initDownload() {
        String key = SendRequestUtilsForExercise.getKeyForExercisePackageDownload();
        downLoadInfo = initDownLoadInfo(key, mVersion);
        DownLoadCallBackExerciseWithDialog listener = new DownLoadCallBackExerciseWithDialog(DownLoadCallBackForExercise.DownLoadExerciseType
                .ModuleExerciseActivity, this);
        listener.setKey(key);
        listener.setViewFirst(new WeakReference<View>(tv_commit));
        listener.setViewSecond(new WeakReference<View>(tv_loading));
        listener.setViewThird(new WeakReference<View>(progressBar));
        listener.setViewFourth(new WeakReference<View>(rl_bottom));

        downLoadInfo.listener = listener;

        ExerciseDownloadPackage exerciseDownloadPackage = mDaoUtils.queryExerciseDownloadPackageInfo(key);

        //点击更新确定时,isupdate为true;本次更新成功时,isupdate 为false
        if (exerciseDownloadPackage != null && !downLoadInfo.isUpdate) {
            rl_bottom.setVisibility(View.GONE);
            //保存数据库成功
            DebugUtil.e("exerciseDownloadPackage.getVersions():" + exerciseDownloadPackage.getVersions() + "  mVersion:" + mVersion);
//            if (!exerciseDownloadPackage.getVersions().equals(mVersion)) {
//                //有新版本数据
//                showUpdateDialog();
//            } else {
//                //无新版本数据,不需要显示
//                updateType = 0;
//            }
        } else if (DownLoadCallBackForExercise.DownLoadExerciseStatus.DownLoading == downLoadInfo.status || DownLoadCallBackForExercise
                .DownLoadExerciseStatus.Success == downLoadInfo.status) {
            //第一次下载中或者更新中 或者下载成功尚未数据库插入完成
            listener.refreshItems(downLoadInfo.status);
        } else {
            //开始在下该题包 第一次下载
            isFirstDownExercise = true;
            rl_bottom.setVisibility(View.VISIBLE);
            tv_commit.setText(R.string.download);
            tv_commit.setVisibility(View.VISIBLE);
            tv_loading.setVisibility(View.GONE);
            progressBar.setVisibility(View.GONE);
        }
    }

    public DownLoadInfo initDownLoadInfo(String key, String version) {
        DownLoadInfo downloadInfoRes = mDownLoadManagerForExercise.getDownLoadInfo(key);

        if (downloadInfoRes == null) {
            downloadInfoRes = new DownLoadInfo();
            downloadInfoRes.versions = version;

            mDownLoadManagerForExercise.downloadInfoList.put(key, downloadInfoRes);
        }

        //防止version有变动且该downInfo已存在->更新为最新version
        downloadInfoRes.versions = version;

        return downloadInfoRes;
    }

    private static class DownLoadCallBackExerciseWithDialog extends DownLoadCallBackForExercise {
        private ModuleExerciseActivity moduleExerciseActivity;

        public DownLoadCallBackExerciseWithDialog(DownLoadExerciseType type, ModuleExerciseActivity moduleExerciseActivity) {
            super(type);

            this.moduleExerciseActivity = new WeakReference<>(moduleExerciseActivity).get();
        }

        @Override
        public void onStart(final DownLoadExerciseStatus status) {
            if (moduleExerciseActivity != null && !moduleExerciseActivity.isDestoryed) {
                moduleExerciseActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        DownLoadCallBackExerciseWithDialog.super.onStart(status);
                    }
                });
            }
        }

        @Override
        public void onFailure(final DownLoadExerciseStatus status) {
            if (moduleExerciseActivity != null && !moduleExerciseActivity.isDestoryed) {
                moduleExerciseActivity.runOnUiThread(new Runnable() {
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
            if (moduleExerciseActivity != null && !moduleExerciseActivity.isDestoryed) {
                moduleExerciseActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        DownLoadCallBackExerciseWithDialog.super.onProcess(bytesWritten, totalSize, status);
                    }
                });
            }
        }

        @Override
        public void onSuccess(final byte[] responseBody, final String key, final DownLoadExerciseStatus status) {
            DebugUtil.i("init db->onSuccess");
            if (moduleExerciseActivity != null && !moduleExerciseActivity.isDestoryed) {
                moduleExerciseActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        moduleExerciseActivity.initDatasIntoDb(responseBody, key);
                        DebugUtil.i("refreshItems->onSuccess");
                        DownLoadCallBackExerciseWithDialog.super.onSuccess(responseBody, key, status);
                        moduleExerciseActivity.isDownLoading = false;
                    }
                });
            }
        }
    }

    private void initDatasIntoDb(byte[] responseBody, final String key) {
        String strBuilder = new String(responseBody);
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
        //本地题包全部习题
        final List<QuestionDetail> questions;
        try {
            questions = LoganSquare.parseList(res, QuestionDetail.class);
        } catch (IOException e) {
            e.printStackTrace();
            updateDownloadPckageInfoForNullQues(daoUtils, key);

            return;
        }

        daoUtils.getDaoSession().runInTx(new Runnable() {
            @Override
            public void run() {
                DownLoadInfo downloadInfoRes = DownloadService.getDownloadManager_exercise().getDownLoadInfo(key);
                if (downloadInfoRes == null) {
                    return;
                }

                //插入习题数据
                String id = key;
//                daoUtils.deleteQuestionDetail(id);
                StringBuilder deleteqids = new StringBuilder();
                StringBuilder updateqids = new StringBuilder();
                for (QuestionDetail questionDetailEve : questions) {
                    questionDetailEve.setId(id);
                    questionDetailEve.setStateQuestion("0");
                    if (!isFirstDownExercise) {
                        for (String updateQid : updateQids) {
                            updateqids.append(updateQid + ",");
                            DebugUtil.e("updateQid : updateQids" + updateQids);
                            if (updateQid.equals(questionDetailEve.getQid())) {
                                questionDetailEve.setStateQuestion("1");
                            }
                        }
                    }
//                    daoUtils.insertQuestionDetail(questionDetailEve);
//                    daoUtils.insertOrUpdateQuestionDetail(questionDetailEve);
                    daoUtils.insertOrUpdateQuestionDetail2(questionDetailEve);
                }
                for (String deleteQid : deleteQids) {
                    QuestionDetail questionDetail = daoUtils.queryQuestionDetail(deleteQid, id);
                    if (questionDetail != null) {
                        questionDetail.setStateQuestion("-1");
                        DebugUtil.e("删除你啦: deleteQid" + deleteQid);
                        daoUtils.insertOrUpdateQuestionDetail2(questionDetail);
                    }
                }
                if (updateQids.length > 0) {
                    DebugUtil.e("initDatasIntoDb" + "updateQids:" + Arrays.toString(updateQids));
                    CommonUtils.putSharedPreferenceItem(null, UserInfo.KEY_SP_UPDATEQIDS_KEY + key, Arrays.toString(updateQids));
                } else {
                    CommonUtils.putSharedPreferenceItem(null, UserInfo.KEY_SP_UPDATEQIDS_KEY + key, "");
                }
                if (deleteQids.length > 0) {
                    CommonUtils.putSharedPreferenceItem(null, UserInfo.KEY_SP_DELETEQIDS_KEY + key, Arrays.toString(deleteQids));
                } else {
                    CommonUtils.putSharedPreferenceItem(null, UserInfo.KEY_SP_DELETEQIDS_KEY + key, "");
                }

                //更新题包下载信息-》downloadstatus以及versions
                daoUtils.inserOrUpdateExerciseDownloadPackageInfo(id, downloadInfoRes.versions);
                DebugUtil.i("updateExerciseDb->onSuccess");
                DebugUtil.e("deleteStudyRecordv:key" + key);
                //删除学习记录
//                List<StudyRecords> studyRecordses = daoUtils.queryStudyRecordsByChooseCategory("1", key, userId);
//                for (int i = 0; i < studyRecordses.size(); i++) {
////                    DebugUtil.e("deleteStudyRecordv:"+studyRecordses.get(i).toString());
//                    daoUtils.deleteStudyRecord(studyRecordses.get(i));
//                }

            }
        });

    }

    private void showUpdateDialog() {
        mCustomUpdateDialog.show();
    }

    //当前题包无题,更新题包下载状态
    private void updateDownloadPckageInfoForNullQues(DaoUtils daoUtils, String key) {
        //更新题包下载信息-》downloadstatus以及versions
        String id = key;
        DownLoadInfo downloadInfoRes = DownloadService.getDownloadManager_exercise().getDownLoadInfo(key);
        if (downloadInfoRes == null) {
            return;
        }
        daoUtils.inserOrUpdateExerciseDownloadPackageInfo(id, downloadInfoRes.versions);
//        inSertChapterTree(localres);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (UserInfo.CHOOSE_SUBJECT_RESULT_MODULE == resultCode) {
            iniData();
        }

    }

    private class RunnableForPrepareExerciseDatas implements Runnable {
        private ModuleExerciseActivity moduleExerciseActivity;
        private long id;
        private int position;
        private int type;//0 表明是快速组卷;1 表明是知识点训练
        private CategoryBean mBean;

        /**
         * @param moduleExerciseActivity
         * @param position               选择知识点训练时,该字段有效,表示当前选择的知识点位置
         * @param type                   0 表明是快速组卷;1 表明是知识点训练
         */
        public RunnableForPrepareExerciseDatas(ModuleExerciseActivity moduleExerciseActivity, int position, CategoryBean bean, int type) {
            this.moduleExerciseActivity = new WeakReference<>(moduleExerciseActivity).get();
            this.mBean = bean;
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

            if (moduleExerciseActivity != null) {

                moduleExerciseActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        moduleExerciseActivity.mCustomLoadingDialog.dismiss();
                        if (DataStore_ExamLibrary.questionDetailList == null || DataStore_ExamLibrary.questionDetailList.size() == 0) {
                            //该模块无题
                            ToastUtils.showToast(R.string.noexericse);
                            return;
                        }

                        DoVipExerciseActivity.newIntent(moduleExerciseActivity, id, 0, 1);
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
                    CategoryBean bean = mBean;
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
                    CategoryBean bean = mBean;
                    //快速组卷 cid -> 为知识点id + quick
                    StudyRecords studyRecords = daoUtils.queryStudyRecordsByType("1", bean.cid , SendRequestUtilsForExercise
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
    public void onReceiveBroadCast(Context context, Intent intent) {
        super.onReceiveBroadCast(context, intent);
        if (intent.getAction().equals(ACTION_NEXT_GROUP)) {
            if (childCategoryBean != null) {
//                AsyncHttpClientHelper.createInstance().getThreadPool().execute(new RunnableForPrepareExerciseDatas(this, currenPosition, 1));
                AsyncHttpClientHelper.createInstance().getThreadPool().execute(new RunnableForPrepareExerciseDatas(ModuleExerciseActivity.this, -1,
                        childCategoryBean, 1));
            }
            if (groupCategoryBean != null) {
//                AsyncHttpClientHelper.createInstance().getThreadPool().execute(new RunnableForPrepareExerciseDatas(this, currenPosition, 1));
                AsyncHttpClientHelper.createInstance().getThreadPool().execute(new RunnableForPrepareExerciseDatas(ModuleExerciseActivity.this, -1,
                        groupCategoryBean, 0));
            }
        } else {
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        adapter.notifyDataSetChanged();
    }
}
