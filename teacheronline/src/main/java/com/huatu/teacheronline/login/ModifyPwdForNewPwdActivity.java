package com.huatu.teacheronline.login;

import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.huatu.teacheronline.BaseActivity;
import com.huatu.teacheronline.R;
import com.huatu.teacheronline.engine.ObtainDataFromNetListener;
import com.huatu.teacheronline.engine.SendRequest;
import com.huatu.teacheronline.utils.ToastUtils;
import com.huatu.teacheronline.utils.UserInfo;
import com.huatu.teacheronline.widget.CustomAlertDialog;

import java.lang.ref.WeakReference;

/**
 * Created by zhxm on 2016/2/24.
 * 输入新密码
 */
public class ModifyPwdForNewPwdActivity extends BaseActivity {

    private RelativeLayout rl_main_left;
    private TextView tv_main_title, tv_next_step;
    private EditText et_pwd_input, et_pwd_confirm;
    private CustomAlertDialog customAlertDialog;
    String code = "";

    @Override
    public void initView() {
        setContentView(R.layout.activity_modify_pwd);
        code = getIntent().getStringExtra("Code");
        rl_main_left = (RelativeLayout) findViewById(R.id.rl_main_left);
        tv_main_title = (TextView) findViewById(R.id.tv_main_title);
        tv_main_title.setText(R.string.find_pwd);
        et_pwd_input = (EditText) findViewById(R.id.et_pwd_input);
        et_pwd_confirm = (EditText) findViewById(R.id.et_pwd_confirm);
        tv_next_step = (TextView) findViewById(R.id.tv_next_step);
    }

    @Override
    public void setListener() {
        rl_main_left.setOnClickListener(this);
        tv_next_step.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_main_left:
                back();
                break;
            case R.id.tv_next_step:
                String pwd = et_pwd_input.getText().toString().trim();
                String pwd_confirm = et_pwd_confirm.getText().toString().trim();

                if (TextUtils.isEmpty(pwd)) {
                    ToastUtils.showToast(R.string.pwd_input);
                    return;
                }
                if (pwd.length() < 6) {
                    ToastUtils.showToast(R.string.pwd_length);
                    return;
                }
                if (TextUtils.isEmpty(pwd_confirm)) {
                    ToastUtils.showToast(R.string.pwd_confirm);
                    return;
                }
                if (!pwd.equals(pwd_confirm)) {
                    ToastUtils.showToast(R.string.pwd_different);
                    return;
                }
                // 修改密码
                ObtainDataFromNetListenerForModifyPwd obtainDataFromNetListenerForModifyPwd = new ObtainDataFromNetListenerForModifyPwd(this);
                SendRequest.modifyPwd(UserInfo.registerNumber, pwd,code, obtainDataFromNetListenerForModifyPwd);
                break;
        }
    }

    private static class ObtainDataFromNetListenerForModifyPwd extends ObtainDataFromNetListener<String, String> {
        private ModifyPwdForNewPwdActivity weak_activity;

        public ObtainDataFromNetListenerForModifyPwd(ModifyPwdForNewPwdActivity activity) {
            weak_activity = new WeakReference<>(activity).get();
        }

        @Override
        public void onSuccess(final String res) {
            if (weak_activity != null) {
                weak_activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtils.showToast(res);
                        weak_activity.setResult(102);
                        weak_activity.finish();
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
                    weak_activity.customAlertDialog = new CustomAlertDialog(weak_activity, R.layout.dialog_loading_custom);
                    weak_activity.customAlertDialog.show();
                    weak_activity.customAlertDialog.setTitle(weak_activity.getResources().getString(R.string.modifying));
                }
            });
        }

        @Override
        public void onFailure(final String res) {
            weak_activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    weak_activity.customAlertDialog.dismiss();
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
    protected void onDestroy() {
        super.onDestroy();
        if (null != customAlertDialog) {
            customAlertDialog.dismiss();
        }
    }
}
