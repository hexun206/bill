package com.huatu.teacheronline.login;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.huatu.teacheronline.BaseActivity;
import com.huatu.teacheronline.R;
import com.huatu.teacheronline.engine.ObtainDataFromNetListener;
import com.huatu.teacheronline.engine.SendRequest;
import com.huatu.teacheronline.utils.CommonUtils;
import com.huatu.teacheronline.utils.ToastUtils;
import com.huatu.teacheronline.utils.UserInfo;
import com.huatu.teacheronline.widget.CustomAlertDialog;
import com.umeng.analytics.MobclickAgent;

import java.lang.ref.WeakReference;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by zhxm on 2016/2/24.
 * 找回密码输入验证码
 */
public class ForgetPwdForConfirmPwdActivity extends BaseActivity {
    private RelativeLayout rl_main_left;
    private TextView tv_main_title;
    private EditText tv_inputPhoneNum;
    private EditText et_moblie_login;
    private TextView tv_next_step;
    private TextView tv_get_verificationCode;
//    private String randomNumber = "";//随机数
    private Subscription mSubscription_timer;
    private CustomAlertDialog mCustomLoadingDialog;
    private String code;//验证码
    private String mobile;//手机号

    @Override
    public void initView() {
        setContentView(R.layout.activity_registerforobtaincomfirmpwd_layout);
        mCustomLoadingDialog = new CustomAlertDialog(ForgetPwdForConfirmPwdActivity.this, R.layout.dialog_loading_custom);

        rl_main_left = (RelativeLayout) findViewById(R.id.rl_main_left);
        tv_main_title = (TextView) findViewById(R.id.tv_main_title);
        tv_main_title.setText(R.string.find_pwd);

        tv_inputPhoneNum = (EditText) findViewById(R.id.tv_inputPhoneNum);
//        tv_inputPhoneNum.setText(UserInfo.registerNumber);
        et_moblie_login = (EditText) findViewById(R.id.et_moblie_login);
        tv_inputPhoneNum.setInputType(EditorInfo.TYPE_CLASS_PHONE);
        tv_get_verificationCode = (TextView) findViewById(R.id.tv_get_verificationCode);
        tv_next_step = (TextView) findViewById(R.id.tv_next_step);
    }

    @Override
    public void setListener() {
        rl_main_left.setOnClickListener(this);
        tv_get_verificationCode.setOnClickListener(this);
        tv_next_step.setOnClickListener(this);
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
                setVerificationTime();
                sendVerification();
                break;
            case R.id.tv_next_step:
                if (TextUtils.isEmpty(tv_inputPhoneNum.getText().toString().trim())) {
                    Toast.makeText(ForgetPwdForConfirmPwdActivity.this, R.string.mobile_input, Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!CommonUtils.isMobileNO(tv_inputPhoneNum.getText().toString())) {
                    ToastUtils.showToast(getResources().getString(R.string.valid_phonenumber));
                    return;
                }
                if (TextUtils.isEmpty(et_moblie_login.getText().toString())) {
                    Toast.makeText(ForgetPwdForConfirmPwdActivity.this, R.string.confirm_pwd_input, Toast.LENGTH_SHORT).show();
                    return;
                }
                TestCodeObtainDataFromNetListener testCodeObtainDataFromNetListener = new TestCodeObtainDataFromNetListener(this);
                SendRequest.registerTestCode(tv_inputPhoneNum.getText().toString(), et_moblie_login.getText().toString(), testCodeObtainDataFromNetListener);
                break;
        }
    }

    private void setVerificationTime() {
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
    }

    private static class ModifyPwdObtainDataFromNetListener extends ObtainDataFromNetListener<String, String> {
        private ForgetPwdForConfirmPwdActivity weak_activity;

        public ModifyPwdObtainDataFromNetListener(ForgetPwdForConfirmPwdActivity activity) {
            weak_activity = new WeakReference<>(activity).get();
        }

        @Override
        public void onSuccess(String res) {
            if (weak_activity != null) {
                weak_activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        weak_activity.mCustomLoadingDialog.dismiss();
                        ToastUtils.showToast(R.string.modify_success);
                        weak_activity.setResult(102);
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
                            ToastUtils.showToast(R.string.modify_fail);
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
                        weak_activity.mCustomLoadingDialog.setTitle(weak_activity.getResources().getString(R.string.modifying));
                    }
                });
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 101 && resultCode == 102) {
            setResult(102);
            back();
        }
    }

    //调用接口发送短信验证码
    private void sendVerification() {
        //生成六位数随机数字
        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 0; i < 6; i++) {
            int num = (int) (Math.random() * 10);
            stringBuffer.append(num);
        }
//        randomNumber = stringBuffer.toString();
        if (!CommonUtils.isMobileNO(tv_inputPhoneNum.getText().toString())) {
            ToastUtils.showToast(getResources().getString(R.string.valid_phonenumber));
            return;
        }
        SmsObtainDataFromNetListener smsObtainDataFromNetListener = new SmsObtainDataFromNetListener(this);
        SendRequest.sendSmsVerification(tv_inputPhoneNum.getText().toString().trim(), 1+"", smsObtainDataFromNetListener);
    }

    private static class SmsObtainDataFromNetListener extends ObtainDataFromNetListener<String, String> {
        private ForgetPwdForConfirmPwdActivity weak_activity;

        public SmsObtainDataFromNetListener(ForgetPwdForConfirmPwdActivity activity) {
            weak_activity = new WeakReference<>(activity).get();
        }

        @Override
        public void onSuccess(String res) {
            if (weak_activity != null) {
                weak_activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        weak_activity.mCustomLoadingDialog.dismiss();
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
                        weak_activity.mCustomLoadingDialog.dismiss();
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mCustomLoadingDialog.dismiss();
        if (mSubscription_timer != null) {
            mSubscription_timer.unsubscribe();
        }
    }

    private static class TestCodeObtainDataFromNetListener extends ObtainDataFromNetListener<String, String> {
        private ForgetPwdForConfirmPwdActivity weak_activity;

        public TestCodeObtainDataFromNetListener(ForgetPwdForConfirmPwdActivity activity) {
            weak_activity = new WeakReference<>(activity).get();
        }

        @Override
        public void onSuccess(String res) {
            if (weak_activity != null) {
                weak_activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        weak_activity.mCustomLoadingDialog.dismiss();

                        MobclickAgent.onEvent(weak_activity, "getVerificationCodeNext");
                        UserInfo.registerNumber = weak_activity.tv_inputPhoneNum.getText().toString().trim();
                        Intent intent = new Intent(weak_activity, ModifyPwdForNewPwdActivity.class);
                        intent.putExtra("Code",weak_activity.et_moblie_login.getText().toString().trim());
                        weak_activity.startActivityForResult(intent, 101);
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
}
