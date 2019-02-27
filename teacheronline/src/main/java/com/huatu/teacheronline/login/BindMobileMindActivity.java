package com.huatu.teacheronline.login;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import com.huatu.teacheronline.BaseActivity;
import com.huatu.teacheronline.HomeActivity;
import com.huatu.teacheronline.R;
import com.huatu.teacheronline.engine.ObtainDataFromNetListener;
import com.huatu.teacheronline.engine.SendRequest;
import com.huatu.teacheronline.exercise.bean.CategoryBean;
import com.huatu.teacheronline.utils.CommonUtils;
import com.huatu.teacheronline.utils.ToastUtils;
import com.huatu.teacheronline.utils.UserInfo;
import com.huatu.teacheronline.widget.CustomAlertDialog;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * 绑定手机提示页
 *
 * @author ljyu
 * @time 2016-10-28 13:33:02
 */
public class BindMobileMindActivity extends BaseActivity {

    private TextView tv_main_title, tv_bind_mobile, tv_bind_later;
    private String selectedCatetoryId;//考试类型
    private String[] categoryKeyList;
    private String selectedStageName;//学段名称
    private String selectedSubjectId;//学科id
    private String selectedCityXzqh;//城市id
    private CustomAlertDialog mCustomLoadingDialog;

    @Override
    public void initView() {
        setContentView(R.layout.activity_bind_mobile_mind);
        // 在当前的activity中注册广播
        IntentFilter filter = new IntentFilter();
        filter.addAction(getResources().getString(R.string.action_name_exit_activity));
        this.registerReceiver(this.broadcastReceiver, filter);
        mCustomLoadingDialog = new CustomAlertDialog(BindMobileMindActivity.this, R.layout.dialog_loading_custom);
        tv_main_title = (TextView) findViewById(R.id.tv_main_title);
        tv_main_title.setText(R.string.bind_mobile);
        tv_bind_mobile = (TextView) findViewById(R.id.tv_bind_mobile);
        tv_bind_later = (TextView) findViewById(R.id.tv_bind_later);
        categoryKeyList = getResources().getStringArray(R.array.key_category);
        selectedCatetoryId = CommonUtils.getSharedPreferenceItem(null, UserInfo.KEY_SP_EXAMCATEGORY_ID, "");
        selectedStageName = CommonUtils.getSharedPreferenceItem(null, UserInfo.KEY_SP_EXAMSTAGE_NAME, "");
        selectedSubjectId = CommonUtils.getSharedPreferenceItem(null, UserInfo.KEY_SP_EXAMSUBJECT_ID, "");
        selectedCityXzqh = CommonUtils.getSharedPreferenceItem(null, UserInfo.KEY_SP_CITY_ID, "");
    }

    @Override
    public void setListener() {
        findViewById(R.id.rl_main_left).setOnClickListener(this);
        tv_bind_mobile.setOnClickListener(this);
        tv_bind_later.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_main_left:
//                back();
                skip();
                break;
            case R.id.tv_bind_mobile:
                Intent intent = new Intent(this, RegisterForObtainConfirmPwdActivity.class);
                intent.putExtra("flag_mobile", 1);
                intent.putExtra("isRegistered", 1);
                startActivity(intent);
                break;
            case R.id.tv_bind_later:
                skip();
                break;
        }
    }

    /**
     * 考试类型，地区，学段，科目必须全部选择之后才能跳转到首页
     */
    private void skip() {
        if (!TextUtils.isEmpty(selectedCatetoryId)) {
            if (categoryKeyList[0].equals(selectedCatetoryId)) {
                if (!TextUtils.isEmpty(selectedStageName) && !TextUtils.isEmpty(selectedSubjectId)) {
                    getSubjects(selectedSubjectId);
                } else {
                    newIntentToExamCatagoryChooseActivity();
                    finish();
                }
            } else if (categoryKeyList[1].equals(selectedCatetoryId)) {
                // 选择地区时，城市必须不能为空
                if (!TextUtils.isEmpty(selectedCityXzqh) && !TextUtils.isEmpty(selectedStageName) && !TextUtils.isEmpty(selectedSubjectId)) {
                    getSubjects(selectedSubjectId);
                } else {
                    newIntentToExamCatagoryChooseActivity();
                    finish();
                }
            } else {
                newIntentToExamCatagoryChooseActivity();
                finish();
            }
        } else {
            newIntentToExamCatagoryChooseActivity();
            finish();
        }
    }

    /**
     * 进入选择考试类型页面
     */
    private void newIntentToExamCatagoryChooseActivity() {
        ExamCatagoryChooseNewActivity.newIntent(this,true);
    }

    // 根据科目id获取科目名称
    private void getSubjects(String selectedSubjectId) {
        SubjectObtainDataFromNetListener subjectObtainDataFromNetListener = new SubjectObtainDataFromNetListener(this);
        SendRequest.getCategoryBeansByIds(selectedSubjectId, subjectObtainDataFromNetListener);
    }

    private static class SubjectObtainDataFromNetListener extends ObtainDataFromNetListener<List<CategoryBean>, String> {
        private WeakReference<BindMobileMindActivity> weak_activity;

        public SubjectObtainDataFromNetListener(BindMobileMindActivity activity) {
            weak_activity = new WeakReference<>(activity);
        }

        @Override
        public void onSuccess(List<CategoryBean> res) {
            if (weak_activity.get() != null) {
                if (null != res && res.size() > 0) {
                    CommonUtils.putSharedPreferenceItem(null, UserInfo.KEY_SP_EXAMSUBJECT_NAME, res.get(0).name);
                }
                weak_activity.get().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        weak_activity.get().newIntentToMainActivity();
                    }
                });
            }
        }

        @Override
        public void onFailure(final String res) {
            if (weak_activity.get() != null) {
                weak_activity.get().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        weak_activity.get().mCustomLoadingDialog.dismiss();
                        if (SendRequest.ERROR_SERVER.equals(res)) {
                            ToastUtils.showToast(weak_activity.get().getResources().getString(R.string.server_error));
                        } else if (SendRequest.ERROR_NETWORK.equals(res)) {
                            ToastUtils.showToast(weak_activity.get().getResources().getString(R.string.network));
                        } else {
                            ToastUtils.showToast(R.string.login_fail);
                        }
                    }
                });
            }
        }
    }

    private void newIntentToMainActivity() {
        // 当第一次进入程序，选择考试类型，学段等项目时，进到主页面之后要finish掉之前的页面
        Intent intent = new Intent();
        intent.setAction(getResources().getString(R.string.action_name_exit_activity));
        this.sendBroadcast(intent);
//        MainActivity.newIntent(this);
        HomeActivity.newIntent(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.unregisterReceiver(this.broadcastReceiver);
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

    public static void newIntent(Activity context) {
        Intent intent = new Intent(context, BindMobileMindActivity.class);
        context.startActivity(intent);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            skip();
        }
        return false;
    }
}
