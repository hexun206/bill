package com.huatu.teacheronline.login;

import android.app.Activity;
import android.content.Intent;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

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

/**
 * Created by zhxm on 2016/2/24.
 * 忘记密码
 */
public class ForgetPwdForPhoneActivity extends BaseActivity {

    private RelativeLayout rl_main_left;
    private TextView tv_main_title;
    private EditText et_inputPhoneNum;
    private TextView tv_next_step;
    private CustomAlertDialog mCustomLoadingDialog;
    private CustomAlertDialog confirmDialog;
    private int click_count = 0;
    private long currentTime = 0;

    @Override
    public void initView() {
        setContentView(R.layout.activity_registerforobtaincomfirmphone_layout);
        confirmDialog = new CustomAlertDialog(ForgetPwdForPhoneActivity.this, R.layout.dialog_confirm);
        mCustomLoadingDialog = new CustomAlertDialog(ForgetPwdForPhoneActivity.this, R.layout.dialog_loading_custom);

        rl_main_left = (RelativeLayout) findViewById(R.id.rl_main_left);
        tv_main_title = (TextView) findViewById(R.id.tv_main_title);
        tv_main_title.setText(R.string.find_pwd);

        et_inputPhoneNum = (EditText) findViewById(R.id.et_inputPhoneNum);
        tv_next_step = (TextView) findViewById(R.id.tv_next_step);
    }

    @Override
    public void setListener() {
        rl_main_left.setOnClickListener(this);
        tv_next_step.setOnClickListener(this);

        et_inputPhoneNum.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                // 修改手机号后，点击次数重新计时
                click_count = 0;
                if (s.length() == 11) {
                    if (CommonUtils.isMobileNO(s.toString())) {
                        UserInfo.registerNumber = s.toString();
                    } else {
                        confirmDialog.show();
                        confirmDialog.setOkOnClickListener(ForgetPwdForPhoneActivity.this);
                    }
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_main_left:
                back();
                break;
            case R.id.tv_next_step:
                click_count++;
                if (click_count == 2) {
                    currentTime = System.currentTimeMillis();
                } else if (click_count > 2) {
                    long interval = System.currentTimeMillis() - currentTime;
                    // 重置上次点击时间
                    currentTime = System.currentTimeMillis();
                    if (interval < 1000 * 60) {
                        // 一分钟内提示过于频繁
                        confirmDialog.show();
                        confirmDialog.setTitle(getResources().getString(R.string.too_offen));
                        confirmDialog.setOkOnClickListener(this);
                        return;
                    }
                }
                if (!CommonUtils.isMobileNO(et_inputPhoneNum.getText().toString())) {
                    ToastUtils.showToast(getResources().getString(R.string.valid_phonenumber));
                    return;
                }
                MobclickAgent.onEvent(this, "enterPhoneNumber");
                ObtainDataFromNetListenerVerifyMobileNO obtainDataFromNetListenerVerifyMobileNO = new ObtainDataFromNetListenerVerifyMobileNO(this);
                SendRequest.VerifyMobileNO(et_inputPhoneNum.getText().toString(), obtainDataFromNetListenerVerifyMobileNO);
                break;
            case R.id.tv_dialog_ok:
                confirmDialog.dismiss();
                break;
        }
    }

    private static class ObtainDataFromNetListenerVerifyMobileNO extends ObtainDataFromNetListener<String, String> {
        private ForgetPwdForPhoneActivity weak_activity;

        public ObtainDataFromNetListenerVerifyMobileNO(ForgetPwdForPhoneActivity activity) {
            weak_activity = new WeakReference<>(activity).get();
        }

        @Override
        public void onSuccess(final String res) {
            if (weak_activity != null) {
                weak_activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        weak_activity.mCustomLoadingDialog.dismiss();
                        if (!TextUtils.isEmpty(res)) {
                            // 已注册，跳转到下一页
                            Intent intent = new Intent(weak_activity, ForgetPwdForConfirmPwdActivity.class);
                            weak_activity.startActivityForResult(intent, 101);
                        } else {
                            // 未注册过，弹出对话框
                            weak_activity.confirmDialog.show();
                            weak_activity.confirmDialog.setTitle(weak_activity.getResources().getString(R.string.unregister_mobile));
                            weak_activity.confirmDialog.setOkOnClickListener(weak_activity);
                        }
                    }
                });
            }
        }

        @Override
        public void onStart() {
            super.onStart();
            weak_activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    weak_activity.mCustomLoadingDialog.show();
                }
            });
        }

        @Override
        public void onFailure(String res) {
            weak_activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    weak_activity.mCustomLoadingDialog.dismiss();
                    ToastUtils.showToast(R.string.verify_mobile_fail);
                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 101 && resultCode == 102) {
            back();
        }
    }

    public static void newIntent(Activity activity) {
        Intent intent = new Intent(activity, ForgetPwdForPhoneActivity.class);
        activity.startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mCustomLoadingDialog.dismiss();
        confirmDialog.dismiss();
    }
}
