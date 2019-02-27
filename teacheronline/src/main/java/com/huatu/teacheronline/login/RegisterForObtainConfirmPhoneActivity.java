package com.huatu.teacheronline.login;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
 * Created by zhxm on 2016/1/26.
 * 输入手机号页面
 * 注：现在暂时没有用到这个类
 */
public class RegisterForObtainConfirmPhoneActivity extends BaseActivity {
    private RelativeLayout rl_main_left;
    private TextView tv_main_title;
    private EditText et_inputPhoneNum;
    private TextView tv_next_step;
    private CustomAlertDialog mCustomLoadingDialog;
    private CustomAlertDialog confirmDialog;
    private int click_count = 0;
    private long currentTime = 0;
    // flag  0-注册手机号，1-第三方注册绑定手机号
    private int flag_mobile = -1;
    private TextView tv_skip;//跳过

    @Override
    public void initView() {
        setContentView(R.layout.activity_registerforobtaincomfirmphone_layout);
        // 在当前的activity中注册广播
        IntentFilter filter = new IntentFilter();
        filter.addAction(getResources().getString(R.string.action_name_exit_activity));
        this.registerReceiver(this.broadcastReceiver, filter);

        flag_mobile = getIntent().getIntExtra("flag_mobile", -1);
        confirmDialog = new CustomAlertDialog(RegisterForObtainConfirmPhoneActivity.this, R.layout.dialog_confirm);
        mCustomLoadingDialog = new CustomAlertDialog(RegisterForObtainConfirmPhoneActivity.this, R.layout.dialog_loading_custom);

        rl_main_left = (RelativeLayout) findViewById(R.id.rl_main_left);
        tv_main_title = (TextView) findViewById(R.id.tv_main_title);
        if (flag_mobile == 0) {
            tv_main_title.setText(R.string.register);
        } else if (flag_mobile == 1) {
            tv_main_title.setText(R.string.bind_mobile);
        }

        et_inputPhoneNum = (EditText) findViewById(R.id.et_inputPhoneNum);
        tv_next_step = (TextView) findViewById(R.id.tv_next_step);
        tv_skip = (TextView) findViewById(R.id.tv_skip);

    }

    @Override
    public void setListener() {
        rl_main_left.setOnClickListener(this);
        tv_next_step.setOnClickListener(this);
        tv_skip.setOnClickListener(this);

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
                        // 是电话号码，请求服务器，确认是否被注册
                        UserInfo.registerNumber = s.toString();
                    } else {
                        confirmDialog.show();
                        confirmDialog.setOkOnClickListener(RegisterForObtainConfirmPhoneActivity.this);
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
//                ObtainDataFromNetListenerVerifyMobileNO obtainDataFromNetListenerVerifyMobileNO = new ObtainDataFromNetListenerVerifyMobileNO(this);
//                SendRequest.VerifyMobileNO(et_inputPhoneNum.getText().toString(), obtainDataFromNetListenerVerifyMobileNO);
                // 跳转到下一页
//                Intent intent = new Intent(this, RegisterForObtainConfirmPwdActivity.class);
//                intent.putExtra("flag_mobile", this.flag_mobile);
//                startActivity(intent);
                RegisterForObtainConfirmPwdActivity.newIntent(this,flag_mobile);

                break;
            case R.id.tv_dialog_ok:
                confirmDialog.dismiss();
                break;
            case R.id.tv_skip:
                ExamCatagoryChooseNewActivity.newIntent(this,true);
                break;
        }
    }

    private static class ObtainDataFromNetListenerVerifyMobileNO extends ObtainDataFromNetListener<String, String> {
        private RegisterForObtainConfirmPhoneActivity weak_activity;

        public ObtainDataFromNetListenerVerifyMobileNO(RegisterForObtainConfirmPhoneActivity activity) {
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
                            // 已注册，弹出对话框
                            weak_activity.confirmDialog.show();
                            weak_activity.confirmDialog.setTitle(res);
                            weak_activity.confirmDialog.setOkOnClickListener(weak_activity);
                        } else {
                            // 未注册过，跳转到下一页
                            Intent intent = new Intent(weak_activity, RegisterForObtainConfirmPwdActivity.class);
                            intent.putExtra("flag_mobile", weak_activity.flag_mobile);
                            weak_activity.startActivity(intent);
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

    public static void newIntent(Activity activity, int flag) {
        // flag  0-注册手机号，1-绑定手机号
        Intent intent = new Intent(activity, RegisterForObtainConfirmPhoneActivity.class);
        intent.putExtra("flag_mobile", flag);
        activity.startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mCustomLoadingDialog.dismiss();
        confirmDialog.dismiss();
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
}
