package com.huatu.teacheronline.personal;

import android.app.Fragment;
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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.generic.RoundingParams;
import com.facebook.drawee.view.SimpleDraweeView;
import com.gensee.utils.StringUtil;
import com.huatu.teacheronline.BaseActivity;
import com.huatu.teacheronline.CustomApplication;
import com.huatu.teacheronline.R;
import com.huatu.teacheronline.bean.PersonalInfoBean;
import com.huatu.teacheronline.engine.ObtainDataFromNetListener;
import com.huatu.teacheronline.engine.SendRequest;
import com.huatu.teacheronline.login.LoginActivity;
import com.huatu.teacheronline.login.RegisterForObtainConfirmPwdActivity;
import com.huatu.teacheronline.utils.CommonUtils;
import com.huatu.teacheronline.utils.FrescoUtils;
import com.huatu.teacheronline.utils.PhotoUtils;
import com.huatu.teacheronline.utils.ToastUtils;
import com.huatu.teacheronline.utils.UserInfo;
import com.huatu.teacheronline.widget.CustomAlertDialog;
import com.umeng.analytics.MobclickAgent;
import com.zhy.android.percent.support.PercentLinearLayout;

import java.lang.ref.WeakReference;

import cn.xiaoneng.uiapi.Ntalker;

public class PersonalaccountActivity extends BaseActivity {
    private RelativeLayout rl_main_left, rl_main_right;
    private TextView tv_main_title, tv_main_right;
    private ImageView ib_main_right;
    private SimpleDraweeView iv_personal_face;
    //    private TextView tv_nickname;
    private TextView et_nickname;
    private RelativeLayout rl_sex;
    private TextView tv_sex;
    private PhotoUtils photoUtils;
    private CustomAlertDialog customAlertDialog;
    private CustomAlertDialog inputNickAlertDialog;
    private int selectedSexPosition = 1;
    private String facePath_sp;
    private String facePath_local;
    private TextView tv_account;
    private RelativeLayout rl_birthday;
    private TextView tv_birthday;
    private String birthday ;
    private PercentLinearLayout rl_mobile;
    private TextView tv_mobile;
    private String mobile;
    private int flag_mobile = -1;//flag_mobile 0表示注册 1表示绑定手机号 2 表示更换绑定手机号
    private TextView tv_info_pwd;
    private EditText edt_nickname_dialog;//
    private String nickname;//昵称
    private String uid;

    @Override
    public void initView() {
        setContentView(R.layout.activity_personalaccount);
        nickname = CommonUtils.getSharedPreferenceItem(null, UserInfo.KEY_SP_NICKNAME, "");
        String account = CommonUtils.getSharedPreferenceItem(null, UserInfo.KEY_SP_ACCOUNT, "");
        uid = CommonUtils.getSharedPreferenceItem(null, UserInfo.KEY_SP_USERID, null);
        birthday = CommonUtils.getSharedPreferenceItem(null, UserInfo.KEY_SP_BIRTHDAY, "");
        mobile = CommonUtils.getSharedPreferenceItem(null, UserInfo.KEY_SP_MOBILE, "");
        facePath_sp = CommonUtils.getSharedPreferenceItem(null, UserInfo.KEY_SP_FACEPATH, "");
        inputNickAlertDialog = new CustomAlertDialog(this, R.layout.dialog_inputnick_custom);
        if(StringUtil.isEmpty(mobile.toString().trim())){
            flag_mobile = 1;
        }else {
            flag_mobile = 2;
        }
        facePath_local = Environment.getExternalStorageDirectory().getPath() + "/huatu/" + uid + ".jpg";
        // 教师网原有注册用户可能没有绑定手机号，用userid代替手机号创建头像
        photoUtils = new PhotoUtils(this, uid);
        rl_main_left = (RelativeLayout) findViewById(R.id.rl_main_left);
        tv_main_title = (TextView) findViewById(R.id.tv_main_title);
        tv_main_title.setText(R.string.personal_info);
        rl_main_right = (RelativeLayout) findViewById(R.id.rl_main_right);
        rl_main_right.setVisibility(View.VISIBLE);
        ib_main_right = (ImageView) findViewById(R.id.ib_main_right);
        ib_main_right.setVisibility(View.GONE);
        tv_main_right = (TextView) findViewById(R.id.tv_main_right);
        tv_main_right.setText(R.string.save);
        tv_main_right.setVisibility(View.VISIBLE);
        iv_personal_face = (SimpleDraweeView) findViewById(R.id.iv_personal_face);
//        tv_nickname = (TextView) findViewById(R.id.tv_nickname);
//        tv_nickname.setText(nickname);
        et_nickname = (TextView) findViewById(R.id.et_nickname);
        et_nickname.setText(nickname);
        rl_sex = (RelativeLayout) findViewById(R.id.rl_sex);
        tv_sex = (TextView) findViewById(R.id.tv_sex);
        tv_sex.setText(CommonUtils.getSharedPreferenceItem(null, UserInfo.KEY_SP_SEX, getResources().getString(R.string.info_sex_male)));
        String first_pass = CommonUtils.getSharedPreferenceItem(null, UserInfo.KEY_SP_FIRST_PASS, "0");
        tv_account = (TextView) findViewById(R.id.tv_account);
        tv_account.setText(account);
        rl_birthday = (RelativeLayout) findViewById(R.id.rl_birthday);
        tv_birthday = (TextView) findViewById(R.id.tv_birthday);
//        tv_birthday.setText(birthday);
        rl_mobile = (PercentLinearLayout) findViewById(R.id.rl_mobile);
        tv_mobile = (TextView) findViewById(R.id.tv_mobile);
        if(StringUtil.isEmpty(mobile.trim().toString())){
            tv_mobile.setText("马上绑定，获得金币");
        }else {
            tv_mobile.setText(mobile);
        }
        if(StringUtil.isEmpty(birthday.trim().toString())){
            tv_birthday.setText("请选择");
        }else {
            tv_birthday.setText(birthday);
        }
        findViewById(R.id.rl_changePwd).setOnClickListener(this);
        findViewById(R.id.rl_nickname).setOnClickListener(this);
        tv_info_pwd = (TextView) findViewById(R.id.tv_info_pwd);
        tv_info_pwd.setText((Integer.parseInt(first_pass) == 0) ? "设置密码" : "修改密码");
        GenericDraweeHierarchyBuilder builder = new GenericDraweeHierarchyBuilder(getResources());
        GenericDraweeHierarchy hierarchy = builder
                .setFadeDuration(100)
                .setPlaceholderImage(getResources().getDrawable(R.drawable.morentouxiang), ScalingUtils.ScaleType.CENTER_CROP)
                .setFailureImage(getResources().getDrawable(R.drawable.morentouxiang), ScalingUtils.ScaleType.CENTER_CROP)
                .setActualImageScaleType(ScalingUtils.ScaleType.CENTER_CROP)
                .build();
        RoundingParams mRoundingParams = new RoundingParams();
        mRoundingParams.setRoundAsCircle(true);
        mRoundingParams.setBorder(Color.WHITE, 6);
        hierarchy.setRoundingParams(mRoundingParams);
        iv_personal_face.setHierarchy(hierarchy);
        FrescoUtils.setFrescoImageUri(iv_personal_face, facePath_sp, R.drawable.morentouxiang);

    }

    @Override
    public void setListener() {
        rl_main_left.setOnClickListener(this);
        rl_main_right.setOnClickListener(this);
        iv_personal_face.setOnClickListener(this);
        rl_sex.setOnClickListener(this);
        rl_birthday.setOnClickListener(this);
        rl_mobile.setOnClickListener(this);
        inputNickAlertDialog.setOkOnClickListener(this);
        findViewById(R.id.rl_exit).setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_main_left:
                back();
                break;
            case R.id.rl_main_right:
                // 保存
                MobclickAgent.onEvent(this, "personalPreservationOnClik");
                saveInfo();
                break;
            case R.id.iv_personal_face:
                MobclickAgent.onEvent(this, "personalPicOnClik");
                // 修改头像
                photoUtils.showPicturePicker(true);
                break;
            case R.id.rl_sex:
                MobclickAgent.onEvent(this, "personalSexOnClik");
                // 修改性别
                android.app.AlertDialog.Builder builder1 = new android.app.AlertDialog.Builder(PersonalaccountActivity.this);
                final String[] mItems = getResources().getStringArray(R.array.info_sex);
                int selectPosition = 0;
                for (int i = 0; i < mItems.length; i++) {
                    if (tv_sex.getText().toString().trim().equals(mItems[i])) {
                        selectPosition = i;
                        break;
                    }
                }
                builder1.setSingleChoiceItems(mItems, selectPosition, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                MobclickAgent.onEvent(PersonalaccountActivity.this, "personalFemaleOnClik");
                                selectedSexPosition = 0;
                                tv_sex.setText(getResources().getString(R.string.info_sex_female));
                                dialog.dismiss();
                                break;
                            case 1:
                                MobclickAgent.onEvent(PersonalaccountActivity.this, "personalMaleOnClik");
                                selectedSexPosition = 1;
                                tv_sex.setText(getResources().getString(R.string.info_sex_male));
                                dialog.dismiss();
                                break;
                        }
                    }
                });
                builder1.create().show();
                break;
            case R.id.rl_birthday:
                showBirthdayDialog();
                break;
            case R.id.rl_changePwd:
                startActivityForResult(new Intent(PersonalaccountActivity.this,ChangePwdActivity.class),101);
                break;
            case R.id.rl_mobile:
                if(flag_mobile != 2){
                    Intent intent = new Intent(this, RegisterForObtainConfirmPwdActivity.class);
                    intent.putExtra("flag_mobile", this.flag_mobile);
                    startActivityForResult(intent,101);
                }
                break;
            case R.id.rl_nickname:
                inputNickAlertDialog.setCanceledOnTouchOutside(true);
                inputNickAlertDialog.show();
                edt_nickname_dialog = inputNickAlertDialog.getEditText();
                edt_nickname_dialog.setText(nickname);
                edt_nickname_dialog.setSelection(nickname.length());
                break;
            case R.id.tv_dialog_ok:
                if(!StringUtil.isEmpty(edt_nickname_dialog.getText().toString().trim())){
                    et_nickname.setText(edt_nickname_dialog.getText().toString());
                }else {
                    et_nickname.setText(nickname);
                }
                inputNickAlertDialog.dismiss();
                break;
            case R.id.rl_exit:
                exitLogin();
                break;
        }
    }

    private void saveInfo() {
        ObtainDataFromNetListenerSaveInfo obtainDataFromNetListenerSaveInfo = new ObtainDataFromNetListenerSaveInfo(this);
        SendRequest.saveInformation(et_nickname.getText().toString(), selectedSexPosition, facePath_local, birthday, obtainDataFromNetListenerSaveInfo);
    }

    private static class ObtainDataFromNetListenerSaveInfo extends ObtainDataFromNetListener<PersonalInfoBean, String> {
        private PersonalaccountActivity weak_activity;

        public ObtainDataFromNetListenerSaveInfo(PersonalaccountActivity activity) {
            weak_activity = new WeakReference<>(activity).get();
        }

        @Override
        public void onSuccess(final PersonalInfoBean res) {
            if (weak_activity != null) {
                weak_activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        weak_activity.customAlertDialog.dismiss();
                        ToastUtils.showToast(R.string.save_success);
                        if (null != res) {
                            // 有更改数据并且保存成功，否则没更改数据
                            String sex = null;
                            if ("1".equals(res.getSex())) {
                                sex = weak_activity.getResources().getString(R.string.info_sex_male);
                            } else if ("0".equals(res.getSex())) {
                                sex = weak_activity.getResources().getString(R.string.info_sex_female);
                            }
                            //上传头像，更新金币信息
                            CommonUtils.putSharedPreferenceItems(null, new String[]{UserInfo.KEY_SP_NICKNAME, UserInfo.KEY_SP_SEX, UserInfo.KEY_SP_FACEPATH,
                                    UserInfo.KEY_SP_BIRTHDAY,
                                    UserInfo.KEY_SP_GOLD,UserInfo.KEY_SP_POINT}, new String[]{res.getNickname(), sex, res.getFace(), res.getBirthday(), res.getGold(),res.getUserPoint()});
//                            weak_activity.tv_nickname.setText(res.getNickname());
                            // 有数据时202，首页需要更新数据，否则首页不需要更新
                            weak_activity.setResult(202);
                        } else {
                            weak_activity.setResult(203);
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
                    weak_activity.customAlertDialog = new CustomAlertDialog(weak_activity, R.layout.dialog_loading_custom);
                    weak_activity.customAlertDialog.show();
                    weak_activity.customAlertDialog.setTitle(weak_activity.getResources().getString(R.string.saveing));
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
                        ToastUtils.showToast(R.string.save_fail);
                    } else {
                        ToastUtils.showToast(res);
                    }
                }
            });
        }
    }
    public static void newIntent(Fragment context) {
        Intent intent = new Intent(context.getActivity(), PersonalaccountActivity.class);
        context.startActivityForResult(intent, 201);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case UserInfo.TAKE_PICTURE:
                    photoUtils.takePictureForResult(iv_personal_face);
                    break;
                case UserInfo.CHOOSE_PICTURE:
                    photoUtils.choosePictureForResult(data, iv_personal_face);
                    break;
                case UserInfo.CROP:
                    // 截图界面
                    photoUtils.cropForResult(data);
                    break;
                case UserInfo.CROP_PICTURE:
                    photoUtils.cropPictureForResult(data, iv_personal_face, facePath_local);
                    break;
            }
        }else if(resultCode == 101){
            //绑定手机
            mobile = CommonUtils.getSharedPreferenceItem(null, UserInfo.KEY_SP_MOBILE, "");
            tv_mobile.setText(mobile);
            this.flag_mobile = 2;
        }else if(resultCode == 102){
            //修改密码
            String first_pass = CommonUtils.getSharedPreferenceItem(null, UserInfo.KEY_SP_FIRST_PASS, "0");
            tv_info_pwd.setText( (Integer.parseInt(first_pass) == 0) ? "设置密码":"修改密码" );
        }else if(resultCode == 103){
            //绑定手机

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
            et_nickname.getLocationOnScreen(edittextLocation);
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
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != customAlertDialog) {
            customAlertDialog.dismiss();
        }

        photoUtils.deletePhoto(facePath_local);
    }

    /**
     * 日期选择器
     */
    private void showBirthdayDialog() {
        com.huatu.teacheronline.widget.DatePickerDialog datePickerDialog = new com.huatu.teacheronline.widget.DatePickerDialog(this, R.style.FullScreenDialog, new com.huatu.teacheronline.widget.DatePickerDialog.DatePickerDialogClickListener() {

            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.btn_ok:
                        tv_birthday.setText(com.huatu.teacheronline.widget.DatePickerDialog.getDate());
                        birthday = com.huatu.teacheronline.widget.DatePickerDialog.getDate();
                        break;

                    default:
                        break;
                }

            }

            ;
        });
        Window window = datePickerDialog.getWindow();
        window.setWindowAnimations(R.style.PopupAnimation);
        window.setGravity(Gravity.BOTTOM);
        window.getDecorView().setPadding(0, 0, 0, 0);
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(lp);
        datePickerDialog.show();
    }


    /**
     * 退出登陆
     */
    public void exitLogin() {
        // 退出登录
        customAlertDialog = new CustomAlertDialog(this, R.layout.dialog_exit_login);
        customAlertDialog.show();
        customAlertDialog.setOkOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                        uid = CommonUtils.getSharedPreferenceItem(null, UserInfo.KEY_SP_USERID, "");
                CancelObtainDataFromNetListener obtationListener = new CancelObtainDataFromNetListener(PersonalaccountActivity.this);
                //点击退出登录后调退出接口
                SendRequest.Canel_login(uid, obtationListener);
                CustomApplication.applicationContext.removeAlias(uid);
                customAlertDialog.dismiss();
                finish();
                UserInfo.registerNumber = null;
                CommonUtils.clearSharedPreferenceItems();
                Intent intent = new Intent(PersonalaccountActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
        customAlertDialog.setCancelOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customAlertDialog.dismiss();
            }
        });
    }
    private static class CancelObtainDataFromNetListener extends ObtainDataFromNetListener<String, String> {
        private PersonalaccountActivity weak_activity;

        public CancelObtainDataFromNetListener(PersonalaccountActivity activity) {
            this.weak_activity = new WeakReference<>(activity).get();
        }

        @Override
        public void onSuccess(String res) {
            if (this.weak_activity != null) {
                ToastUtils.showToast("退出中！");
                Ntalker.getBaseInstance().logout();//小能退出
            }
        }

        @Override
        public void onFailure(final String res) {
            this.weak_activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (res.equals(SendRequest.ERROR_NETWORK)) {
                         ToastUtils.showToast("退出成功！");
                    } else if (res.equals(SendRequest.ERROR_SERVER)) {
                         ToastUtils.showToast("退出成功！");
                    } else {
                         ToastUtils.showToast("退出成功！");
                    }
                }
            });
        }
    }
}
