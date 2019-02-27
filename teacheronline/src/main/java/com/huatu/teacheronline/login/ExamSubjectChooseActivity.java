package com.huatu.teacheronline.login;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.greendao.DaoUtils;
import com.greendao.ExamSubject;
import com.huatu.teacheronline.BaseActivity;
import com.huatu.teacheronline.HomeActivity;
import com.huatu.teacheronline.R;
import com.huatu.teacheronline.engine.ObtainDataFromNetListener;
import com.huatu.teacheronline.engine.SendRequest;
import com.huatu.teacheronline.exercise.bean.CategoryBean;
import com.huatu.teacheronline.utils.CommonUtils;
import com.huatu.teacheronline.utils.DebugUtil;
import com.huatu.teacheronline.utils.StringUtils;
import com.huatu.teacheronline.utils.ToastUtils;
import com.huatu.teacheronline.utils.UserInfo;
import com.huatu.teacheronline.widget.CustomAlertDialog;
import com.umeng.analytics.MobclickAgent;

import java.lang.ref.WeakReference;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * Created by ljzyuhenda on 16/1/18.
 * 选择考试科目
 */
public class ExamSubjectChooseActivity extends BaseActivity {
    private RelativeLayout rl_main_left;
    private TextView tv_main_title;
    private ListView mLv_examsubject;
    private ExamSubjectListViewAdapter mAdapter;
    private ObtainDataFromNetListenerGetCategoryList mObtainDataFromNetListenerGetCategoryList;
    private CustomAlertDialog mCustomLoadingDialog;
    private WeakReference<ExamSubjectChooseActivity> mContextWeakRefernce;
    private TextView tv_next_step;
    private int selectedPosition = -1;
    private String currentVideoSubjectId;
    private StringBuffer currentSubjectIdNames = new StringBuffer();
    private int checkCount = 0;//当前选中了几个科目
    private ArrayList<CategoryBean> subjects;
    private int whereFrom = 0;//0代表首页进去的 1代表其他页面（错题，真题，模块题海进来的）

    @Override
    public void initView() {
        setContentView(R.layout.activity_examsubjectchoose_layout);
        // 在当前的activity中注册广播
        IntentFilter filter = new IntentFilter();
        filter.addAction(getResources().getString(R.string.action_name_exit_activity));
        whereFrom = getIntent().getIntExtra("whereFrom",0);
        this.registerReceiver(this.broadcastReceiver, filter);
        rl_main_left = (RelativeLayout) findViewById(R.id.rl_main_left);
        tv_main_title = (TextView) findViewById(R.id.tv_main_title);
        tv_main_title.setText(R.string.subject_exam_choose);
        mLv_examsubject = (ListView) findViewById(R.id.lv_examsubject);
        tv_next_step = (TextView) findViewById(R.id.tv_next_step);
        tv_next_step.setVisibility(View.GONE);
        mAdapter = new ExamSubjectListViewAdapter();
        mLv_examsubject.setAdapter(mAdapter);
        mCustomLoadingDialog = new CustomAlertDialog(this, R.layout.dialog_loading_custom);
        // 获取当前科目列表的id
        getCurrentVideoSubjectsId();
        //获取当前多选的科目
        subjects = UserInfo.getSubjects();
        mContextWeakRefernce = new WeakReference<>(this);
        mObtainDataFromNetListenerGetCategoryList = new ObtainDataFromNetListenerGetCategoryList(mContextWeakRefernce);
        SendRequest.getCategoryList(currentVideoSubjectId, mObtainDataFromNetListenerGetCategoryList);
        if (!CommonUtils.getSharedPreferenceItemForBoolean(null, UserInfo.KEY_SP_ISFRIST_SUBJECTCHOOSE)) {
            ToastUtils.showToastMoreChoose(this);
            CommonUtils.putSharedPreferenceItemForBoolean(null, UserInfo.KEY_SP_ISFRIST_SUBJECTCHOOSE, true);
        }
    }
    private static class ObtainDataFromNetListenerGetCategoryList extends ObtainDataFromNetListener<List<CategoryBean>, String> {
        private ExamSubjectChooseActivity examSubjectChooseActivity;
        //创建两个容器用来对写死的前端显示做保存。
        List<CategoryBean> category1 = new ArrayList<CategoryBean>();
        List<CategoryBean> category2 = new ArrayList<CategoryBean>();

        public ObtainDataFromNetListenerGetCategoryList(WeakReference<ExamSubjectChooseActivity> contextWeakReference) {
            this.examSubjectChooseActivity = contextWeakReference.get();
        }
        @Override
        public void onSuccess(final List<CategoryBean> res) {
            if (examSubjectChooseActivity != null) {
                examSubjectChooseActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Gson gson = new Gson();
                        if (null != res && res.size() > 0) {
                            //首先插入数据库
                            ExamSubject examSubject = new ExamSubject(examSubjectChooseActivity.currentVideoSubjectId, gson.toJson(res), res.get(0).versions);
                            DaoUtils.getInstance().insertExamSubject(examSubject);
                            // 有值说明有更新，要更新数据库
                            if (UserInfo.tempCategoryTypeName.equals(examSubjectChooseActivity.getResources().getStringArray(R.array.name_category)[0])||UserInfo.tempCategoryTypeName.equals("国家教师资格证")){
                                //判断如果是从国家教師資格證進入的話，使用Separate容器保存数据
                                Separate(res);
                            }else{
                                examSubjectChooseActivity.mAdapter.categoryBeanList = res;
                            }

                        } else {
                            // 没有值说明没有更新，从数据库中读取数据
                            ExamSubject examSubject = DaoUtils.getInstance().queryExamSubject(examSubjectChooseActivity.currentVideoSubjectId);
                            if (examSubject == null) {
                                examSubjectChooseActivity.tv_next_step.setVisibility(View.GONE);
                                examSubjectChooseActivity.mCustomLoadingDialog.dismiss();
                                ToastUtils.showToast(R.string.no_data);
                                return;
                            }
                            String jsonforsubject = examSubject.getJsonforsubject();
                            if (UserInfo.tempCategoryTypeName.equals(examSubjectChooseActivity.getResources().getStringArray(R.array.name_category)[0])||UserInfo.tempCategoryTypeName.equals("国家教师资格证")){
                                //判断如果是从国家教師資格證進入的話，使用Separate容器保存数据
                                category2 = gson.fromJson(jsonforsubject, new TypeToken<List<CategoryBean>>() {
                                }.getType());
                                DebugUtil.e("Separate:之前"+ category2.toString());
                                Separate(category2);
                            }else{
                                examSubjectChooseActivity.mAdapter.categoryBeanList = gson.fromJson(jsonforsubject, new TypeToken<List<CategoryBean>>() {
                                }.getType());
                            }
                        }
                        if(examSubjectChooseActivity.whereFrom == 1){
                            //两个for循环检测科目是否被选中
                            for (int i = 0; i < examSubjectChooseActivity.subjects.size(); i++) {
                                CategoryBean categoryBean = examSubjectChooseActivity.subjects.get(i);
                                for (int i1 = 0; i1 < examSubjectChooseActivity.mAdapter.categoryBeanList.size(); i1++) {
                                    if(examSubjectChooseActivity.mAdapter.categoryBeanList.get(i1).cid.equals(categoryBean.cid)){
                                        examSubjectChooseActivity.mAdapter.categoryBeanList.get(i1).setIsCheck(true);
                                    }
                                }
                            }
                        }

                        examSubjectChooseActivity.mAdapter.notifyDataSetChanged();
                        examSubjectChooseActivity.tv_next_step.setVisibility(View.VISIBLE);
                        examSubjectChooseActivity.mCustomLoadingDialog.dismiss();
                    }
                });
            }
        }
        public void Separate( List<CategoryBean> gory){
            //方法在读取数据成功的时候调用，用来判断和排序前端写死的
            if(category1!=null){ //每次清空容器，再重新读取对应的列表
                category1.clear();
            }
            for (CategoryBean category2 :gory){
                if(UserInfo.tempStageName.equals("幼教")){
                    if(category2.name.equals("综合素质")|| category2.name.equals("保教知识与能力")){
                        category1.add(category2);
                    }
                }else if(UserInfo.tempStageName.equals("小学")) {
                    if (category2.name.equals("综合素质") || category2.name.equals("教育教学知识与能力")) {
                        category1.add(category2);
                    }
                }
                else {//幼教 教育综合知识 保教知识与能力 教育教学知识与能力
                    if(category2.name.contains("综合素质")|| category2.name.contains("教育知识与能力")||category2.name.contains("音乐")||
                            category2.name.contains("美术")|| category2.name.contains("英语")|| category2.name.contains("数学")
                            || category2.name.contains("体育")|| category2.name.contains("语文")) {
                        if (category2.name.contains("体育")) {
                            category2.setName("体育专业知识");
                        }
                        if (category2.name.contains("音乐")) {
                            category2.setName("音乐专业知识");
                        }
                        if (category2.name.contains("英语")) {
                            category2.setName("英语专业知识");
                        }
                        if (category2.name.contains("数学")) {
                            category2.setName("数学专业知识");
                        }
                        if (category2.name.contains("语文")) {
                            category2.setName("语文专业知识");
                        }
                        if (category2.name.contains("美术")) {
                            category2.setName("美术专业知识");
                        }
                        category1.add(category2);
                    }
                }
            }
            CategoryBean data= null;
            for (int i = 0; i < category1.size(); i++) {//判断如何是综合素质的话移除再add
                if(category1.get(i).name.equals("综合素质")){
                    data = category1.get(i);
                    category1.remove(i);
                }
            }
            category1.add(0,data);
            examSubjectChooseActivity.mAdapter.categoryBeanList=category1;
            DebugUtil.e("Separate:"+examSubjectChooseActivity.mAdapter.categoryBeanList.toString());
        }
        @Override
        public void onFailure(String res) {
            if (examSubjectChooseActivity != null) {
                examSubjectChooseActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        examSubjectChooseActivity.mCustomLoadingDialog.dismiss();
                    }
                });
            }
        }

        @Override
        public void onStart() {
            if (examSubjectChooseActivity != null) {
                examSubjectChooseActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        examSubjectChooseActivity.mCustomLoadingDialog.show();
                    }
                });
            }
        }
    }

    private void getCurrentVideoSubjectsId() {
        //id保存为类型+地域+学段:选择国家后，id只保存类型；选择地区后，id保存类型+地区+学段
        if (getResources().getString(R.string.key_stage_government_confirm).equals(UserInfo.tempCategoryTypeId)) {
            currentVideoSubjectId = UserInfo.tempCategoryTypeId;
        } else {
            if(!StringUtils.isEmpty(UserInfo.tempCityId)){
                currentVideoSubjectId = UserInfo.tempCategoryTypeId + UserInfo.tempCityId + UserInfo.tempStageId;
                //有可能市为空，只有省
            }else {
                currentVideoSubjectId = UserInfo.tempCategoryTypeId + UserInfo.tempProvinceId + UserInfo.tempStageId;
            }
        }
    }

    @Override
    public void setListener() {
        tv_next_step.setOnClickListener(this);
        rl_main_left.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_next_step:
                MobclickAgent.onEvent(ExamSubjectChooseActivity.this, "subjectscompleted");//完成
                setUserTemp();
                if (checkCount > 0 ) {
                    UserInfo.tempSubjectsIdName = currentSubjectIdNames.substring(0,currentSubjectIdNames.length()-1);
                    updateInfo2Server();
                } else {
                    ToastUtils.showToast(getResources().getString(R.string.subject_exam_choose));
                }
                break;
            case R.id.rl_main_left:
                finish();
                break;
        }
    }

    /**
     * 设置用户的临时数据
     */
    private void setUserTemp() {
        for (int i = 0; i < mAdapter.categoryBeanList.size(); i++) {
            if(mAdapter.categoryBeanList.get(i).isCheck()){
                if(checkCount == 0){
                UserInfo.tempSubjectId = mAdapter.categoryBeanList.get(i).cid;
                UserInfo.tempSubjectName = mAdapter.categoryBeanList.get(i).name;
                }
                currentSubjectIdNames.append(mAdapter.categoryBeanList.get(i).cid+"_"+mAdapter.categoryBeanList.get(i).name+",");
                checkCount++;
            }
        }
    }

    private void updateInfo2Server() {
        ObtainDataFromNetListenerUpdateInfo mObtainDataFromNetListenerUpdateInfo = new ObtainDataFromNetListenerUpdateInfo(this);
        SendRequest.updateInfoAfterRegister(mObtainDataFromNetListenerUpdateInfo);
    }

    private static class ObtainDataFromNetListenerUpdateInfo extends ObtainDataFromNetListener<String, String> {
        private ExamSubjectChooseActivity weak_activity;

        public ObtainDataFromNetListenerUpdateInfo(ExamSubjectChooseActivity contextWeakReference) {
            weak_activity = new WeakReference<>(contextWeakReference).get();
        }

        @Override
        public void onStart() {
            if (weak_activity != null) {
                weak_activity.mCustomLoadingDialog.show();
            }
        }

        @Override
        public void onSuccess(String res) {
            if (weak_activity != null) {
                weak_activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        weak_activity.mCustomLoadingDialog.dismiss();
                        if(weak_activity.whereFrom == 0){
                            CommonUtils.putSharedPreferenceItems(null,
                                    new String[]{UserInfo.KEY_SP_EXAMCATEGORY_ID, UserInfo.KEY_SP_EXAMCATEGORY_NAME,
                                            UserInfo.KEY_SP_CITY_ID, UserInfo.KEY_SP_CITY_NAME, UserInfo.KEY_SP_PROVINCE_ID,
                                            UserInfo.KEY_SP_PROVINCE_NAME, UserInfo.KEY_SP_EXAMSTAGE_ID, UserInfo.KEY_SP_EXAMSTAGE_NAME,
                                            UserInfo.KEY_SP_EXAMSUBJECT_ID, UserInfo.KEY_SP_EXAMSUBJECT_NAME, UserInfo.KEY_SP_EXAMSUBJECTS_ID_NAME},
                                    new String[]{UserInfo.tempCategoryTypeId, UserInfo.tempCategoryTypeName,
                                            UserInfo.tempCityId, UserInfo.tempCityName, UserInfo.tempProvinceId,
                                            UserInfo.tempProvinceName, UserInfo.tempStageId, UserInfo.tempStageName,
                                            UserInfo.tempSubjectId, UserInfo.tempSubjectName, UserInfo.tempSubjectsIdName});

                            //当第一次进入程序，选择考试类型，学段等项目时，进到主页面之后要finish掉之前的页面
                            Intent intent = new Intent();
                            intent.setAction(weak_activity.getResources().getString(R.string.action_name_exit_activity));
                            weak_activity.sendBroadcast(intent);
//                            MainActivity.newIntent(weak_activity);
                            HomeActivity.newIntent(weak_activity);
                        }else {
                            CommonUtils.putSharedPreferenceItems(null,
                                    new String[]{UserInfo.KEY_SP_EXAMSUBJECT_ID, UserInfo.KEY_SP_EXAMSUBJECT_NAME, UserInfo.KEY_SP_EXAMSUBJECTS_ID_NAME},
                                    new String[]{UserInfo.tempSubjectId, UserInfo.tempSubjectName, UserInfo.tempSubjectsIdName});
                            //添加课程_返回原来页面
                            weak_activity.setResult(UserInfo.CHOOSE_SUBJECT_RESULT_MODULE);
                            weak_activity.finish();
                        }

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
                        if (SendRequest.ERROR_SERVER.equals(res)) {
                            ToastUtils.showToast(weak_activity.getResources().getString(R.string.server_error));
                        } else if (SendRequest.ERROR_NETWORK.equals(res)) {
                            ToastUtils.showToast(weak_activity.getResources().getString(R.string.network));
                        }
                    }
                });
            }
        }
    }

    public static void newIntent(Activity context) {
        Intent intent = new Intent(context, ExamSubjectChooseActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        UserInfo.tempSubjectId = null;
        UserInfo.tempSubjectName = null;
        mCustomLoadingDialog.dismiss();
        this.unregisterReceiver(this.broadcastReceiver);
    }

    private class ExamSubjectListViewAdapter extends BaseAdapter {
        public List<CategoryBean> categoryBeanList = new ArrayList<CategoryBean>();

        @Override
        public int getCount() {
            if (categoryBeanList == null) {
                return 0;
            }

            return categoryBeanList.size();
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
        public View getView(final int position, View convertView, ViewGroup parent) {
            ExamSubjectViewHolder viewHolder;
            if (convertView == null) {
                convertView = View.inflate(ExamSubjectChooseActivity.this, R.layout.item_examstagechooselist, null);
                viewHolder = new ExamSubjectViewHolder();
                viewHolder.iv_choose = (ImageView) convertView.findViewById(R.id.iv_choose);
                viewHolder.tv_name_subject = (TextView) convertView.findViewById(R.id.tv_name_stage);
                convertView.setTag(viewHolder);
            }

            viewHolder = (ExamSubjectViewHolder) convertView.getTag();
            viewHolder.tv_name_subject.setText(categoryBeanList.get(position).name);
            if (categoryBeanList.get(position).isCheck()) {
                viewHolder.iv_choose.setImageResource(R.drawable.icon_choice_selected);
            } else {
                viewHolder.iv_choose.setImageResource(R.drawable.icon_choice_unselected);
            }

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MobclickAgent.onEvent(ExamSubjectChooseActivity.this, "Selectsubjects");//选择考试科目
                    if(categoryBeanList.get(position).isCheck()){
                        categoryBeanList.get(position).setIsCheck(false);
                    }else {
                        categoryBeanList.get(position).setIsCheck(true);
                    }
//                    selectedPosition = position;
//                    UserInfo.tempSubjectId = categoryBeanList.get(position).cid;
                    notifyDataSetChanged();
//                    UserInfo.tempSubjectName = categoryBeanList.get(position).name;
                }
            });

            return convertView;
        }
    }

    private class ExamSubjectViewHolder {
        private ImageView iv_choose;
        private TextView tv_name_subject;
    }

    /**
     * 注册完登录到首页时，关掉之前所有打开的activity
     */
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            finish();
        }
    };
}
