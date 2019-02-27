package com.huatu.teacheronline.personal;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Environment;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.generic.RoundingParams;
import com.facebook.drawee.view.SimpleDraweeView;
import com.gensee.utils.StringUtil;
import com.huatu.teacheronline.BaseActivity;
import com.huatu.teacheronline.R;
import com.huatu.teacheronline.bean.PersonalInfoBean;
import com.huatu.teacheronline.engine.ObtainDataFromNetListener;
import com.huatu.teacheronline.engine.SendRequest;
import com.huatu.teacheronline.utils.CommonUtils;
import com.huatu.teacheronline.utils.FrescoUtils;
import com.huatu.teacheronline.utils.PhotoUtils;
import com.huatu.teacheronline.utils.ToastUtils;
import com.huatu.teacheronline.utils.UserInfo;
import com.huatu.teacheronline.widget.CustomAlertDialog;
import com.huatu.teacheronline.widget.DatePickerDialog;
import com.umeng.analytics.MobclickAgent;

import java.lang.ref.WeakReference;

/**
 * Created by zhxm on 2016/2/22.
 * 个人信息编辑页
 */
public class ChangePwdActivity extends BaseActivity {
    private RelativeLayout rl_main_left, rl_main_right;
    private TextView tv_main_title, tv_main_right;
    private ImageView ib_main_right;
    private CustomAlertDialog customAlertDialog;
    private EditText et_oldpwd;
    private EditText et_newpwd;
    private EditText et_newtpwd;
    private CustomAlertDialog mCustomLoadingDialog;
    private String account;
    private int first_pass;//0设置密码  1 修改密码
    private LinearLayout ll_oldpwd;
    private int type; //2设置密码 1 修改密码
    private String mobile;//当前手机号


    @Override
    public void initView() {
        setContentView(R.layout.activity_change_pwd);
        String userid = CommonUtils.getSharedPreferenceItem(null, UserInfo.KEY_SP_USERID, "");
        account = CommonUtils.getSharedPreferenceItem(null, UserInfo.KEY_SP_ACCOUNT, "");
        mobile = CommonUtils.getSharedPreferenceItem(null, UserInfo.KEY_SP_MOBILE, "");
        first_pass = Integer.parseInt(CommonUtils.getSharedPreferenceItem(null, UserInfo.KEY_SP_FIRST_PASS, "0"));
        rl_main_left = (RelativeLayout) findViewById(R.id.rl_main_left);
        ll_oldpwd = (LinearLayout) findViewById(R.id.ll_oldpwd);
        tv_main_title = (TextView) findViewById(R.id.tv_main_title);
        rl_main_right = (RelativeLayout) findViewById(R.id.rl_main_right);
        rl_main_right.setVisibility(View.VISIBLE);
        ib_main_right = (ImageView) findViewById(R.id.ib_main_right);
        ib_main_right.setVisibility(View.GONE);
        tv_main_right = (TextView) findViewById(R.id.tv_main_right);
        tv_main_right.setText(R.string.save);
        tv_main_right.setVisibility(View.VISIBLE);
        et_oldpwd = (EditText)findViewById(R.id.et_oldpwd);
        et_newpwd = (EditText)findViewById(R.id.et_newpwd);
        et_newtpwd = (EditText)findViewById(R.id.et_newtpwd);
        mCustomLoadingDialog = new CustomAlertDialog(ChangePwdActivity.this, R.layout.dialog_loading_custom);
        if(first_pass == 0){
            ll_oldpwd.setVisibility(View.GONE);
            tv_main_title.setText(R.string.setPwd);
            type = 2;
        }else if(first_pass == 1){
            tv_main_title.setText(R.string.change_pwd);
            ll_oldpwd.setVisibility(View.VISIBLE);
            type = 1;
        }
    }

    @Override
    public void setListener() {
        rl_main_left.setOnClickListener(this);
        rl_main_right.setOnClickListener(this);
    }

    private void saveInfo() {
        String newPwd = et_newpwd.getText().toString().trim();
        String newtPwd = et_newtpwd.getText().toString().trim();
        String oldPwd = et_oldpwd.getText().toString().trim();//旧密码
        if(first_pass == 1){//设置
            if(StringUtil.isEmpty(oldPwd)){
                ToastUtils.showToast("请输入旧密码");
                return;
            }
            if (StringUtil.isEmpty(newPwd)){
                ToastUtils.showToast("请输入新密码");
                return;
            }
            if(StringUtil.isEmpty(newtPwd)){
                ToastUtils.showToast("请再次输入新密码");
                return;
            }
        }else {
            if(StringUtil.isEmpty(newPwd)){
                ToastUtils.showToast("请输入新密码");
                return;
            }
            if(StringUtil.isEmpty(newtPwd)){
                ToastUtils.showToast("请再次输入新密码");
                return;
            }
        }
        if(newtPwd.length()<6||newtPwd.length()>16){
            ToastUtils.showToast("请输入正确密码格式");
            return;
        }
        if(!newtPwd.equals(newPwd)) {
            ToastUtils.showToast("新密码输入不一致");
            return;
        }
        if (oldPwd.equals(newPwd)&&oldPwd.equals(newtPwd)&&newtPwd.equals(newPwd)){
            ToastUtils.showToast("新旧密码不能一致，请重新修改");
            return;
        }
        ObtainDataListerForChangePwd obtainDataListerForChangePwd = new ObtainDataListerForChangePwd(ChangePwdActivity.this);
        SendRequest.changePwd(account, newPwd, oldPwd, type+"",obtainDataListerForChangePwd);
    }

    private class ObtainDataListerForChangePwd extends ObtainDataFromNetListener<String, String> {
        private ChangePwdActivity weak_activity;

        public ObtainDataListerForChangePwd(ChangePwdActivity activity) {
            weak_activity = new WeakReference<>(activity).get();
        }

        @Override
        public void onStart() {
            super.onStart();
            if (weak_activity != null) {
                weak_activity.mCustomLoadingDialog.show();
            }
        }
        @Override
        public void onSuccess(final String res) {
            if (weak_activity != null) {
                weak_activity.mCustomLoadingDialog.dismiss();
                weak_activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        CommonUtils.putSharedPreferenceItem(null,UserInfo.KEY_SP_FIRST_PASS,"1");
                        if(weak_activity.first_pass == 1){
                            ToastUtils.showToast("修改成功，请使用新密码登录");
                            weak_activity.setResult(102);
                            weak_activity.finish();
                        }else if(weak_activity.first_pass == 0){
                            if(StringUtil.isEmpty(mobile)){
                                ToastUtils.showToast("您可以使用账号、密码登录了");
                            }else {
                                ToastUtils.showToast("您可以使用手机号、密码登陆了");
                            }
                            weak_activity.setResult(102);
                            weak_activity.finish();
                        }
                    }
                });
            }
        }

        @Override
        public void onFailure(final String res) {
            weak_activity.mCustomLoadingDialog.dismiss();
            if (weak_activity != null) {
                weak_activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
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
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_main_left:
                back();
                break;
            case R.id.rl_main_right:
                // 保存
                saveInfo();
                break;
        }
    }

    // 点击非EditText区域，关闭键盘
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (isShouldHideInput(v, ev)) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
            return super.dispatchTouchEvent(ev);
        }
        // 必不可少，否则所有的组件都不会有TouchEvent了
        if (getWindow().superDispatchTouchEvent(ev)) {
            return true;
        }
        return onTouchEvent(ev);
    }

    public boolean isShouldHideInput(View v, MotionEvent event) {
        if (v != null && (v instanceof EditText)) {
            int[] edittextLocation = new int[2];
            et_oldpwd.getLocationOnScreen(edittextLocation);
            //获取输入框当前的location位置
            v.getLocationInWindow(edittextLocation);
            int left = edittextLocation[0];
            int top = edittextLocation[1];
            int bottom = top + v.getHeight();
            int right = left + v.getWidth();
            if (event.getX() > left && event.getX() < right
                    && event.getY() > top && event.getY() < bottom) {
                // 点击的是输入框区域，保留点击EditText的事件
                return false;
            } else {
                return true;
            }
        }
        return false;
    }

    public static void newIntent(Activity context) {
        Intent intent = new Intent(context, ChangePwdActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != customAlertDialog) {
            customAlertDialog.dismiss();
        }
    }
}
