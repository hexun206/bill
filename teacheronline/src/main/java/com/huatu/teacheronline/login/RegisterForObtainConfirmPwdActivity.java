package com.huatu.teacheronline.login;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.huatu.teacheronline.BaseActivity;
import com.huatu.teacheronline.HomeActivity;
import com.huatu.teacheronline.R;
import com.huatu.teacheronline.engine.ObtainDataFromNetListener;
import com.huatu.teacheronline.engine.SendRequest;
import com.huatu.teacheronline.exercise.bean.CategoryBean;
import com.huatu.teacheronline.sensorsdata.TrackUtil;
import com.huatu.teacheronline.utils.CommonUtils;
import com.huatu.teacheronline.utils.ToastUtils;
import com.huatu.teacheronline.utils.UserInfo;
import com.huatu.teacheronline.widget.CustomAlertDialog;
import com.umeng.analytics.MobclickAgent;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by ljzyuhenda on 16/1/14.
 * 手机号获取验证码页面
 */
public class RegisterForObtainConfirmPwdActivity extends BaseActivity {
    private RelativeLayout rl_main_left;
    private TextView tv_main_title;
    private EditText tv_inputPhoneNum;
    private EditText et_moblie_login;
    private TextView tv_next_step;
    private TextView tv_get_verificationCode;
    private TextView tv_old_mobile;
    //    private String randomNumber = "";//随机数
    private String code = "";//验证码
    private Subscription mSubscription_timer;
    /**
     * flag_mobile 0表示注册 1表示绑定手机号 2 表示更换绑定手机号
     */
    private int flag_mobile = -1;//
    private CustomAlertDialog mCustomLoadingDialog;
    private int type = 1;//发送验证码的类型 2-注册、更换手机号、绑定手机号 1-忘记密码
    private int bindType = 1;//绑定手机号类型 1、更换手机号 2-绑定手机号
    private String mobile = "";//当前手机号码
    private TextView tv_skip;
    private String selectedCatetoryId;//考试类型
    private String[] categoryKeyList;
    private String selectedStageName;//学段名称
    private String selectedSubjectId;//学科id
    private String selectedCityXzqh;//城市id
    private int isRegistered;// 是否注册跳过来的绑定手机 0-不是 1-是
    private int first_pass;//0设置密码  1 修改密码

    @Override
    public void initView() {
        setContentView(R.layout.activity_registerforobtaincomfirmpwd_layout);
        // 在当前的activity中注册广播
        IntentFilter filter = new IntentFilter();
        filter.addAction(getResources().getString(R.string.action_name_exit_activity));
        this.registerReceiver(this.broadcastReceiver, filter);
        flag_mobile = getIntent().getIntExtra("flag_mobile", -1);
        isRegistered = getIntent().getIntExtra("isRegistered", 0);
        mCustomLoadingDialog = new CustomAlertDialog(RegisterForObtainConfirmPwdActivity.this, R.layout.dialog_loading_custom);

        categoryKeyList = getResources().getStringArray(R.array.key_category);
        selectedCatetoryId = CommonUtils.getSharedPreferenceItem(null, UserInfo.KEY_SP_EXAMCATEGORY_ID, "");
        selectedStageName = CommonUtils.getSharedPreferenceItem(null, UserInfo.KEY_SP_EXAMSTAGE_NAME, "");
        selectedSubjectId = CommonUtils.getSharedPreferenceItem(null, UserInfo.KEY_SP_EXAMSUBJECT_ID, "");
        selectedCityXzqh = CommonUtils.getSharedPreferenceItem(null, UserInfo.KEY_SP_CITY_ID, "");
        first_pass = Integer.parseInt(CommonUtils.getSharedPreferenceItem(null, UserInfo.KEY_SP_FIRST_PASS, "0"));

        rl_main_left = (RelativeLayout) findViewById(R.id.rl_main_left);
        tv_main_title = (TextView) findViewById(R.id.tv_main_title);
        tv_inputPhoneNum = (EditText) findViewById(R.id.tv_inputPhoneNum);
        tv_inputPhoneNum.setInputType(EditorInfo.TYPE_CLASS_PHONE);
        tv_old_mobile = (TextView) findViewById(R.id.tv_old_mobile);
        tv_skip = (TextView) findViewById(R.id.tv_skip);
        et_moblie_login = (EditText) findViewById(R.id.et_moblie_login);
        tv_get_verificationCode = (TextView) findViewById(R.id.tv_get_verificationCode);
        tv_next_step = (TextView) findViewById(R.id.tv_next_step);
        if (flag_mobile == 0) {
            type = 2;
            tv_main_title.setText(R.string.register);
        } else if (flag_mobile == 1) {
            type = 2;
            bindType = 2;
            tv_main_title.setText(R.string.bind_mobile);
            tv_next_step.setText(R.string.complete);
        } else if (flag_mobile == 2) {
            mobile = CommonUtils.getSharedPreferenceItem(null, UserInfo.KEY_SP_MOBILE, "");
            tv_old_mobile.setVisibility(View.VISIBLE);
            tv_old_mobile.setText(getResources().getString(R.string.old_mobile_mind) + "\n" + mobile);
            type = 2;
            tv_main_title.setText(R.string.change_mobile);
            bindType = 1;
            tv_next_step.setText(R.string.complete);
        }


    }

    @Override
    public void setListener() {
        rl_main_left.setOnClickListener(this);
        tv_get_verificationCode.setOnClickListener(this);
        tv_next_step.setOnClickListener(this);
        tv_skip.setOnClickListener(this);
    }

    private void changeVerificationCodeColor(int flag) {
        switch (flag) {
            case 0:
                // 不可点击，灰色
                tv_get_verificationCode.setClickable(false);
                tv_get_verificationCode.setBackgroundColor(getResources().getColor(R.color.gray011));
                tv_get_verificationCode.setTextColor(getResources().getColor(R.color.gray003));
                break;
            case 1:
                // 可点击，绿色
                tv_get_verificationCode.setClickable(true);
                tv_get_verificationCode.setBackgroundColor(getResources().getColor(R.color.green001));
                tv_get_verificationCode.setTextColor(getResources().getColor(R.color.white));
                break;
        }
    }

    @Override
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.rl_main_left:
                back();
                break;
            case R.id.tv_get_verificationCode:
                MobclickAgent.onEvent(this, "getVerificationCode");
                if (!CommonUtils.isMobileNO(tv_inputPhoneNum.getText().toString())) {
                    ToastUtils.showToast(getResources().getString(R.string.valid_phonenumber));
                    return;
                }


                // 获取验证码
                final Observable observable = Observable.timer(0, 1, TimeUnit.SECONDS);
                Subscriber<Long> subscriber = new Subscriber<Long>() {
                    @Override
                    public void onCompleted() {
                        changeVerificationCodeColor(1);
                        tv_get_verificationCode.setText(R.string.confirm_pwd_obtain);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(Long aLong) {
                        changeVerificationCodeColor(0);
                        tv_get_verificationCode.setText((60 - aLong) + "s");
                        if (aLong == 60) {
                            unsubscribe();
                            onCompleted();
                        }
                    }
                };

                mSubscription_timer = observable.observeOn(AndroidSchedulers.mainThread()).subscribe(subscriber);

                sendVerification();
                break;
            case R.id.tv_next_step:
                if (TextUtils.isEmpty(et_moblie_login.getText().toString())) {
                    Toast.makeText(RegisterForObtainConfirmPwdActivity.this, R.string.confirm_pwd_input, Toast.LENGTH_SHORT).show();
                    return;
                }
//                if (!randomNumber.equals(et_moblie_login.getText().toString())) {
//                    Toast.makeText(RegisterForObtainConfirmPwdActivity.this, R.string.verification_code_wrong, Toast.LENGTH_SHORT).show();
//                    return;
//                }
                MobclickAgent.onEvent(this, "getVerificationCodeNext");


//                TrackUtil.trackBindMobile(tv_inputPhoneNum.getText().toString(),
//                        CommonUtils.getSharedPreferenceItem(null, UserInfo.KEY_SP_ACCOUNT, ""));

                if (flag_mobile == 0) {
//                    intent = new Intent(this, CompleteDatasActivity.class);
//                    startActivity(intent);
                    code = et_moblie_login.getText().toString();
                    TestCodeObtainDataFromNetListener testCodeObtainDataFromNetListener = new TestCodeObtainDataFromNetListener(this);
                    mobile = tv_inputPhoneNum.getText().toString();
                    SendRequest.registerTestCode(tv_inputPhoneNum.getText().toString(), et_moblie_login.getText().toString(), testCodeObtainDataFromNetListener);
                } else if (flag_mobile == 1) {
                    // 绑定手机号
                    code = et_moblie_login.getText().toString();
                    BindMobileObtainDataFromNetListener bindMobileObtainDataFromNetListener = new BindMobileObtainDataFromNetListener(this);
                    SendRequest.bindMobile(CommonUtils.getSharedPreferenceItem(null, UserInfo.KEY_SP_USERID, null), tv_inputPhoneNum.getText().toString(),
                            null, et_moblie_login.getText().toString(), bindType + "", bindMobileObtainDataFromNetListener);
                } else if (flag_mobile == 2) {
                    if (tv_inputPhoneNum.getText().toString().equals(mobile)) {
                        ToastUtils.showToast(getString(R.string.change_mobile_existing));
                    }
                    BindMobileObtainDataFromNetListener bindMobileObtainDataFromNetListener = new BindMobileObtainDataFromNetListener(this);
                    SendRequest.bindMobile(CommonUtils.getSharedPreferenceItem(null, UserInfo.KEY_SP_USERID, null), tv_inputPhoneNum.getText().toString(),
                            mobile, et_moblie_login.getText().toString(), bindType + "", bindMobileObtainDataFromNetListener);
                }
                break;
            case R.id.tv_skip:
//                skip();
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
                }
            } else if (categoryKeyList[1].equals(selectedCatetoryId)) {
                // 选择地区时，城市必须不能为空
                if (!TextUtils.isEmpty(selectedCityXzqh) && !TextUtils.isEmpty(selectedStageName) && !TextUtils.isEmpty(selectedSubjectId)) {
                    getSubjects(selectedSubjectId);
                } else {
                    newIntentToExamCatagoryChooseActivity();
                }
            } else {
                newIntentToExamCatagoryChooseActivity();
            }
        } else {
            newIntentToExamCatagoryChooseActivity();
        }
    }

    private static class BindMobileObtainDataFromNetListener extends ObtainDataFromNetListener<String, String> {
        private RegisterForObtainConfirmPwdActivity weak_activity;

        public BindMobileObtainDataFromNetListener(RegisterForObtainConfirmPwdActivity activity) {
            weak_activity = new WeakReference<>(activity).get();
        }

        @Override
        public void onSuccess(String res) {
            if (weak_activity != null) {
                weak_activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        weak_activity.mCustomLoadingDialog.dismiss();
                        if (weak_activity.flag_mobile == 0) {
                            CommonUtils.putSharedPreferenceItem(null, UserInfo.KEY_SP_MOBILE, weak_activity.tv_inputPhoneNum.getText().toString());
                            ExamCatagoryChooseActivity.newIntent(weak_activity, true);
                        } else if (weak_activity.flag_mobile == 1) {
                            CommonUtils.putSharedPreferenceItem(null, UserInfo.KEY_SP_MOBILE, weak_activity.tv_inputPhoneNum.getText().toString());
                            if (weak_activity.isRegistered == 0) {
                                //个人资料进来的绑定手机
                                weak_activity.setResult(101);
                                weak_activity.finish();
                                if (weak_activity.first_pass == 0) {
                                    ToastUtils.showToast(weak_activity.getString(R.string.bind_success_set_pwd));
                                } else {
                                    ToastUtils.showToast(weak_activity.getString(R.string.change_mobile_success));
                                }
                            } else {
                                //注册进来的绑定手机
                                weak_activity.skip();
                                ToastUtils.showToast(weak_activity.getString(R.string.bind_success_set_pwd));
                            }
                        } else if (weak_activity.flag_mobile == 2) {
                            //更换手机
                            if (weak_activity.first_pass == 0) {
                                ToastUtils.showToast(weak_activity.getString(R.string.bind_success_set_pwd));
                            } else {
                                ToastUtils.showToast(weak_activity.getString(R.string.change_mobile_success));
                            }
                            CommonUtils.putSharedPreferenceItem(null, UserInfo.KEY_SP_MOBILE, weak_activity.tv_inputPhoneNum.getText().toString());
                            weak_activity.setResult(101);
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

        @Override
        public void onStart() {
            super.onStart();
            if (weak_activity != null) {
                weak_activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        weak_activity.mCustomLoadingDialog.show();
                        weak_activity.mCustomLoadingDialog.setTitle(weak_activity.getResources().getString(R.string.binding));
                    }
                });
            }
        }
    }

    private static class TestCodeObtainDataFromNetListener extends ObtainDataFromNetListener<String, String> {
        private RegisterForObtainConfirmPwdActivity weak_activity;

        public TestCodeObtainDataFromNetListener(RegisterForObtainConfirmPwdActivity activity) {
            weak_activity = new WeakReference<>(activity).get();
        }

        @Override
        public void onSuccess(String res) {
            if (weak_activity != null) {
                weak_activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        weak_activity.mCustomLoadingDialog.dismiss();
                        Intent intent = new Intent(weak_activity, CompleteDatasActivity.class);
                        UserInfo.registerNumber = weak_activity.mobile;
                        intent.putExtra("Code", weak_activity.code);
                        weak_activity.startActivity(intent);
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
                        } else {
                            ToastUtils.showToast(res);
                        }
                    }
                });
            }
        }

        @Override
        public void onStart() {
            super.onStart();
            if (weak_activity != null) {
                weak_activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        weak_activity.mCustomLoadingDialog.show();
                        weak_activity.mCustomLoadingDialog.setTitle(weak_activity.getResources().getString(R.string.waiting));
                    }
                });
            }
        }
    }

    //调用接口发送短信验证码
    private void sendVerification() {


        TrackUtil.trackSignUpGetCheckCode(tv_inputPhoneNum.getText().toString());

        //生成六位数随机数字
        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 0; i < 6; i++) {
            int num = (int) (Math.random() * 10);
            stringBuffer.append(num);
        }
//        randomNumber = stringBuffer.toString();
        SmsObtainDataFromNetListener smsObtainDataFromNetListener = new SmsObtainDataFromNetListener(this);
        SendRequest.sendSmsVerification(tv_inputPhoneNum.getText().toString().trim(), type + "", smsObtainDataFromNetListener);
    }

    private static class SmsObtainDataFromNetListener extends ObtainDataFromNetListener<String, String> {
        private RegisterForObtainConfirmPwdActivity weak_activity;

        public SmsObtainDataFromNetListener(RegisterForObtainConfirmPwdActivity activity) {
            weak_activity = new WeakReference<>(activity).get();
        }

        @Override
        public void onSuccess(String res) {
            if (weak_activity != null) {
                weak_activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtils.showToast(R.string.sms_success);
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
                        if (weak_activity.mSubscription_timer != null) {
                            weak_activity.changeVerificationCodeColor(1);
                            weak_activity.tv_get_verificationCode.setText(R.string.confirm_pwd_obtain);
                            weak_activity.mSubscription_timer.unsubscribe();
                        }
                        ToastUtils.showToast(res);
                    }
                });
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mCustomLoadingDialog.dismiss();
        if (mSubscription_timer != null) {
            mSubscription_timer.unsubscribe();
        }
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

    public static void newIntent(Activity activity, int flag) {
        // flag  0-注册手机号，1-绑定手机号
        Intent intent = new Intent(activity, RegisterForObtainConfirmPwdActivity.class);
        intent.putExtra("flag_mobile", flag);
        activity.startActivity(intent);
    }

    /**
     * 进入选择考试类型页面
     */
    private void newIntentToExamCatagoryChooseActivity() {
        if (null != mCustomLoadingDialog && mCustomLoadingDialog.isShow()) {
            mCustomLoadingDialog.dismiss();
        }
        ExamCatagoryChooseNewActivity.newIntent(this, true);
    }

    // 根据科目id获取科目名称
    private void getSubjects(String selectedSubjectId) {
        SubjectObtainDataFromNetListener subjectObtainDataFromNetListener = new SubjectObtainDataFromNetListener(this);
        SendRequest.getCategoryBeansByIds(selectedSubjectId, subjectObtainDataFromNetListener);
    }

    private static class SubjectObtainDataFromNetListener extends ObtainDataFromNetListener<List<CategoryBean>, String> {
        private WeakReference<RegisterForObtainConfirmPwdActivity> weak_activity;

        public SubjectObtainDataFromNetListener(RegisterForObtainConfirmPwdActivity activity) {
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
}
