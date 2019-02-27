package com.huatu.teacheronline.login;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Environment;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.gensee.utils.StringUtil;
import com.huatu.teacheronline.BaseActivity;
import com.huatu.teacheronline.CustomApplication;
import com.huatu.teacheronline.R;
import com.huatu.teacheronline.bean.PersonalInfoBean;
import com.huatu.teacheronline.engine.ObtainDataFromNetListener;
import com.huatu.teacheronline.engine.SendRequest;
import com.huatu.teacheronline.utils.CommonUtils;
import com.huatu.teacheronline.utils.PhotoUtils;
import com.huatu.teacheronline.utils.ToastUtils;
import com.huatu.teacheronline.utils.UserInfo;
import com.huatu.teacheronline.widget.CustomAlertDialog;
import com.umeng.analytics.MobclickAgent;

import java.lang.ref.WeakReference;

/**
 * Created by ljzyuhenda on 16/1/14.
 * 完善资料页面
 */
public class CompleteDatasActivity extends BaseActivity {
    private RelativeLayout rl_main_left;
    private TextView tv_main_title;
    private EditText et_nickname_input, et_pwd_input, et_pwd_confirm;
    private TextView tv_complete;
    private SimpleDraweeView iv_face;
    private CustomAlertDialog customAlertDialog;
    private PhotoUtils photoUtils;
    private String facePath_local;
    private String code = "";

    @Override
    public void initView() {
        setContentView(R.layout.activity_completedatas_layout);
        // 在当前的activity中注册广播
        IntentFilter filter = new IntentFilter();
        filter.addAction(getResources().getString(R.string.action_name_exit_activity));
        this.registerReceiver(this.broadcastReceiver, filter);

        photoUtils = new PhotoUtils(this, UserInfo.registerNumber);

        rl_main_left = (RelativeLayout) findViewById(R.id.rl_main_left);
        tv_main_title = (TextView) findViewById(R.id.tv_main_title);
        tv_main_title.setText(R.string.complete_data);
        code = getIntent().getStringExtra("Code");

        iv_face = (SimpleDraweeView) findViewById(R.id.register_face);
        et_nickname_input = (EditText) findViewById(R.id.et_nickname_input);
        et_pwd_input = (EditText) findViewById(R.id.et_pwd_input);
        et_pwd_confirm = (EditText) findViewById(R.id.et_pwd_confirm);
        tv_complete = (TextView) findViewById(R.id.tv_complete);
    }

    @Override
    public void setListener() {
        rl_main_left.setOnClickListener(this);
        iv_face.setOnClickListener(this);
        tv_complete.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rl_main_left:
                back();
                break;
            case R.id.tv_complete:
                // 完成注册
                String nickname = et_nickname_input.getText().toString().trim();
                String pwd = et_pwd_input.getText().toString().trim();
                String pwd_confirm = et_pwd_confirm.getText().toString().trim();
                if (TextUtils.isEmpty(nickname)) {
                    ToastUtils.showToast(R.string.nickname_input);
                    return;
                }
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
                MobclickAgent.onEvent(this, "perfectInformation");
                register(pwd, nickname);
                break;
            case R.id.register_face:
                MobclickAgent.onEvent(this, "perfectInformationPic");
                // 选择头像
                photoUtils.showPicturePicker(true);
                break;
        }
    }

    private void register(String pwd, String nickname) {
        facePath_local = Environment.getExternalStorageDirectory() + "/huatu/" + UserInfo.registerNumber + ".jpg";
        ObtainDataFromNetListenerRegister obtainDataFromNetListenerRegister = new ObtainDataFromNetListenerRegister(this);
        SendRequest.registerNewAccount(1, UserInfo.registerNumber, pwd,code, nickname, facePath_local, null, null,"", obtainDataFromNetListenerRegister);
    }

    private static class ObtainDataFromNetListenerRegister extends ObtainDataFromNetListener<PersonalInfoBean, String> {
        private CompleteDatasActivity weak_activity;

        public ObtainDataFromNetListenerRegister(CompleteDatasActivity activity) {
            weak_activity = new WeakReference<>(activity).get();
        }

        @Override
        public void onSuccess(final PersonalInfoBean personalInfoBean) {
            if (weak_activity != null) {
                weak_activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        weak_activity.customAlertDialog.dismiss();
                        if (null != personalInfoBean) {
                            if (!TextUtils.isEmpty(personalInfoBean.getId())) {
                                CommonUtils.putSharedPreferenceItem(null, UserInfo.KEY_SP_USERID, personalInfoBean.getId());
                                Intent intent = new Intent(weak_activity, LoginActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                weak_activity.startActivity(intent);
                                weak_activity.photoUtils.deletePhoto(weak_activity.facePath_local);
                            } else {
                                ToastUtils.showToast(R.string.register_fail);
                            }
                        } else {
                            ToastUtils.showToast(R.string.register_fail);
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
                    weak_activity.customAlertDialog.setTitle(weak_activity.getResources().getString(R.string.registering));
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
                        ToastUtils.showToast(R.string.register_fail);
                    } else {
                        ToastUtils.showToast(res);
                    }
                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case UserInfo.TAKE_PICTURE:
                    photoUtils.takePictureForResult(iv_face);
                    break;
                case UserInfo.CHOOSE_PICTURE:
                    photoUtils.choosePictureForResult(data, iv_face);
                    break;
                case UserInfo.CROP:
                    // 截图界面
                    photoUtils.cropForResult(data);
                    break;
                case UserInfo.CROP_PICTURE:
                    photoUtils.cropPictureForResult(data, iv_face, null);
                    break;
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != customAlertDialog) {
            customAlertDialog.dismiss();
        }
        if(!StringUtil.isEmpty(facePath_local)){
            photoUtils.deletePhoto(facePath_local);
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
}
