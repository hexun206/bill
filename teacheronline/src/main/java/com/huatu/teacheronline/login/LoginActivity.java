package com.huatu.teacheronline.login;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.huatu.teacheronline.BaseActivity;
import com.huatu.teacheronline.CustomApplication;
import com.huatu.teacheronline.HomeActivity;
import com.huatu.teacheronline.R;
import com.huatu.teacheronline.bean.EventMessage;
import com.huatu.teacheronline.bean.PersonalInfoBean;
import com.huatu.teacheronline.engine.ObtainDataFromNetListener;
import com.huatu.teacheronline.engine.SendRequest;
import com.huatu.teacheronline.exercise.bean.CategoryBean;
import com.huatu.teacheronline.sensorsdata.TrackUtil;
import com.huatu.teacheronline.utils.CommonUtils;
import com.huatu.teacheronline.utils.DebugUtil;
import com.huatu.teacheronline.utils.ToastUtils;
import com.huatu.teacheronline.utils.UserInfo;
import com.huatu.teacheronline.widget.CustomAlertDialog;
import com.orhanobut.logger.Logger;
import com.umeng.analytics.MobclickAgent;
import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.bean.SHARE_MEDIA;

import org.greenrobot.eventbus.EventBus;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Map;

import cn.xiaoneng.uiapi.Ntalker;


/**
 * Created by ljzyuhenda on 16/1/13.
 * 登录页面
 */
public class LoginActivity extends BaseActivity {
    private static final String TAG = "LoginActivity";
    private TextView tv_login;
    private TextView tv_register_quick;
    private TextView tv_forget_pwd;
    private ImageView iv_weixin;
    private ImageView iv_qq;
    private EditText et_moblie_login, et_pwd_login;
    //友盟授权登录
    private UMShareAPI mShareAPI = null;
    private CustomAlertDialog mCustomLoadingDialog;
    private String[] categoryKeyList;
    private String quick_login_way;

    @Override
    public void initView() {
        setContentView(R.layout.activity_login);
        // 在当前的activity中注册广播
        IntentFilter filter = new IntentFilter();
        filter.addAction(getResources().getString(R.string.action_name_exit_activity));
        this.registerReceiver(this.broadcastReceiver, filter);
        categoryKeyList = getResources().getStringArray(R.array.key_category);
        et_moblie_login = (EditText) findViewById(R.id.et_moblie_login);
        if (!TextUtils.isEmpty(UserInfo.registerNumber)) {
            et_moblie_login.setText(UserInfo.registerNumber);
        }
        et_pwd_login = (EditText) findViewById(R.id.et_pwd_login);
        tv_login = (TextView) findViewById(R.id.tv_login);
        tv_register_quick = (TextView) findViewById(R.id.tv_register_quick);
        tv_forget_pwd = (TextView) findViewById(R.id.tv_forget_pwd);
        iv_weixin = (ImageView) findViewById(R.id.iv_weixin);
        iv_qq = (ImageView) findViewById(R.id.iv_qq);

        mShareAPI = UMShareAPI.get(CustomApplication.applicationContext);
        mCustomLoadingDialog = new CustomAlertDialog(LoginActivity.this, R.layout.dialog_loading_custom);

    }

    @Override
    protected void onStart() {
        super.onStart();
        deleteAauthVerify(SHARE_MEDIA.WEIXIN);
    }

    //屏蔽返回键
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return keyCode == KeyEvent.KEYCODE_BACK;
    }

    @Override
    public void setListener() {
        tv_login.setOnClickListener(this);
        tv_register_quick.setOnClickListener(this);
        tv_forget_pwd.setOnClickListener(this);
        iv_weixin.setOnClickListener(this);
        iv_qq.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        SHARE_MEDIA platform;
        switch (view.getId()) {
            case R.id.tv_login:
                // 手机号登录
                String inputMobile = et_moblie_login.getText().toString().trim();
                String inputPwd = et_pwd_login.getText().toString().trim();
                if (TextUtils.isEmpty(inputMobile)) {
                    ToastUtils.showToast(R.string.mobile_input_username);
                    return;
                }
                if (TextUtils.isEmpty(inputPwd)) {
                    ToastUtils.showToast(R.string.pwd_login_input);
                    return;
                }
                if (inputPwd.length() < 6) {
                    ToastUtils.showToast(R.string.pwd_length);
                    return;
                }
                MobclickAgent.onEvent(LoginActivity.this, "loginByUser");
                loginByMobile(inputMobile, inputPwd);
                break;
            case R.id.tv_register_quick:

                TrackUtil.trackSignUpClick();

                // 快速注册
//                RegisterForObtainConfirmPhoneActivity.newIntent(LoginActivity.this, 0);
                RegisterForObtainConfirmPwdActivity.newIntent(this, 0);
                MobclickAgent.onEvent(LoginActivity.this, "fastRegist");
                break;
            case R.id.tv_forget_pwd:
                // 忘记密码
                Intent intent1 = new Intent(this, ForgetPwdForConfirmPwdActivity.class);
                startActivity(intent1);
//                startActivity(new Intent(this, ReportEvaluationActivity.class));
                MobclickAgent.onEvent(LoginActivity.this, "userFogetpwd");
                break;
            case R.id.iv_qq:
                // qq授权登录
                platform = SHARE_MEDIA.QQ;
                startOauthVerify(platform);
                MobclickAgent.onEvent(LoginActivity.this, "loginByQQ");
                break;
            case R.id.iv_weixin:
                // 微信授权登录
                platform = SHARE_MEDIA.WEIXIN;
                startOauthVerify(platform);
                MobclickAgent.onEvent(LoginActivity.this, "loginByWeiXin");
                break;
        }
    }

    // 通过手机号登录
    private void loginByMobile(String inputMobile, String inputPwd) {
        LoginObtainDataFromNetListener loginObtainDataFromNetListener = new LoginObtainDataFromNetListener(this);
        SendRequest.loginByMobile(inputMobile, inputPwd, loginObtainDataFromNetListener);
    }

    // 根据科目id获取科目名称
    private void getSubjects(String selectedSubjectId) {
        SubjectObtainDataFromNetListener subjectObtainDataFromNetListener = new SubjectObtainDataFromNetListener(this);
        SendRequest.getCategoryBeansByIds(selectedSubjectId, subjectObtainDataFromNetListener);
    }

    private static class LoginObtainDataFromNetListener extends ObtainDataFromNetListener<PersonalInfoBean, String> {
        private LoginActivity weak_activity;

        public LoginObtainDataFromNetListener(LoginActivity activity) {
            weak_activity = new WeakReference<>(activity).get();
        }

        @Override
        public void onStart() {
            super.onStart();
            if (weak_activity != null) {
                weak_activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        weak_activity.mCustomLoadingDialog.show();
                        weak_activity.mCustomLoadingDialog.setTitle(weak_activity.getResources().getString(R.string.logining));
                    }
                });
            }
        }

        @Override
        public void onSuccess(PersonalInfoBean res) {
            if (weak_activity != null) {
                //登录成功后销毁旧的首页，再创建新的Mai
                EventBus.getDefault().post(new EventMessage(100, null));
                //登录成功后先保存学员和非学员的身份
                CommonUtils.putSharedPreferenceItem(null, UserInfo.KEY_SP_NON_STUDENT, res.getface_courses());
                Ntalker.getBaseInstance().login(res.getId(), CommonUtils.getSharedPreferenceItem(null, UserInfo.KEY_SP_NICKNAME, ""), 0);
                weak_activity.saveData(res);


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
                        } else {
                            ToastUtils.showToast(res);
                        }
                    }
                });


            }
        }
    }

    private static class SubjectObtainDataFromNetListener extends ObtainDataFromNetListener<List<CategoryBean>, String> {
        private WeakReference<LoginActivity> weak_activity;

        public SubjectObtainDataFromNetListener(LoginActivity activity) {
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

    // 登录成功后，保存数据并跳转页面
    private void saveData(PersonalInfoBean personalInfoBean) {
        String selectedCatetoryId = personalInfoBean.getType_id();// 考试类型
        String selectedCityXzqh = personalInfoBean.getCity();// 地区城市
        String selectedProvinceXzqh = personalInfoBean.getProvince();// 地区省份
        String selectedStageName = personalInfoBean.getSec_id();// 考试学段
        String selectedSubjectId = personalInfoBean.getSub_id();// 考试科目
        String selectedSubjects = personalInfoBean.getSub_ids();// 多科目考试科目
        String token = personalInfoBean.getaccessToken();//token令牌
        String sex = null;
        if ("1".equals(personalInfoBean.getSex())) {
            sex = getResources().getString(R.string.info_sex_male);
        } else if ("0".equals(personalInfoBean.getSex())) {
            sex = getResources().getString(R.string.info_sex_female);
        }
        //添加友盟的指定用户推送
        CustomApplication.applicationContext.addAlias(personalInfoBean.getId());
        DebugUtil.e("saveData:" + personalInfoBean.getId());
        CommonUtils.putSharedPreferenceItems(null,
                new String[]{UserInfo.KEY_SP_USERID, UserInfo.KEY_SP_ACCOUNT, UserInfo.KEY_SP_PASSWORD, UserInfo.KEY_SP_MOBILE, UserInfo.KEY_SP_NICKNAME
                        , UserInfo.KEY_SP_BIRTHDAY, UserInfo.KEY_SP_CITY_ID, UserInfo.KEY_SP_CITY_NAME, UserInfo.KEY_SP_PROVINCE_ID, UserInfo.KEY_SP_PROVINCE_NAME,
                        UserInfo.KEY_SP_EXAMCATEGORY_ID, UserInfo.KEY_SP_EXAMCATEGORY_NAME, UserInfo.KEY_SP_EXAMSTAGE_ID,
                        UserInfo.KEY_SP_EXAMSTAGE_NAME, UserInfo.KEY_SP_EXAMSUBJECT_ID, UserInfo.KEY_SP_EXAMSUBJECTS_ID_NAME, UserInfo.KEY_SP_SEX, UserInfo.KEY_SP_FACEPATH,
                        UserInfo.KEY_SP_GOLD, UserInfo.KEY_SP_ACCESSToken, UserInfo.KEY_SP_POINT},
                new String[]{personalInfoBean.getId(), personalInfoBean.getAccount(), personalInfoBean.getPassword(), personalInfoBean.getMobile(),
                        personalInfoBean.getNickname(), personalInfoBean.getBirthday(),
                        selectedCityXzqh, CommonUtils.getInstance().getCityNameByXzqh(selectedCityXzqh), selectedProvinceXzqh,
                        CommonUtils.getInstance().getProvinceNameByXzqh(selectedProvinceXzqh), selectedCatetoryId,
                        CommonUtils.getExamCategoryValue(selectedCatetoryId), CommonUtils.getExamStageKey(selectedStageName),
                        selectedStageName, selectedSubjectId, selectedSubjects, sex, personalInfoBean.getFace(), personalInfoBean.getGold(), token, personalInfoBean.getUserPoint()});
        // 考试类型，地区，学段，科目必须全部选择之后才能跳转到首页
        if (!TextUtils.isEmpty(selectedCatetoryId)) {
            if (categoryKeyList[0].equals(selectedCatetoryId)) {
                if (!TextUtils.isEmpty(selectedStageName) && !TextUtils.isEmpty(selectedSubjectId)) {
                    getSubjects(selectedSubjectId);
//                    MainActivity.newIntent(this);
                } else {
                    newIntentToExamCatagoryChooseActivity();
                }
            } else if (categoryKeyList[1].equals(selectedCatetoryId)) {
                // 选择地区时，城市必须不能为空
                if (!TextUtils.isEmpty(selectedCityXzqh) && !TextUtils.isEmpty(selectedStageName) && !TextUtils.isEmpty(selectedSubjectId)) {
                    getSubjects(selectedSubjectId);
//                    MainActivity.newIntent(this);
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

    private void newIntentToMainActivity() {
        mCustomLoadingDialog.dismiss();
        // 当第一次进入程序，选择考试类型，学段等项目时，进到主页面之后要finish掉之前的页面
        Intent intent = new Intent();
        intent.setAction(getResources().getString(R.string.action_name_exit_activity));
        this.sendBroadcast(intent);
        CommonUtils.putSharedPreferenceItemForBoolean(null, UserInfo.KEY_SP_IFLOGIN, true);
//        MainActivity.newIntent(this);
        HomeActivity.newIntent(this);
    }

    private void newIntentToExamCatagoryChooseActivity() {
        if (null != mCustomLoadingDialog && mCustomLoadingDialog.isShow()) {
            mCustomLoadingDialog.dismiss();
        }
        ExamCatagoryChooseNewActivity.newIntent(this, true);
    }

    private void startOauthVerify(SHARE_MEDIA platform) {
        mShareAPI.doOauthVerify(LoginActivity.this, platform, umAuthListener);
    }

    private void deleteAauthVerify(SHARE_MEDIA platform) {
        mShareAPI.deleteOauth(LoginActivity.this, platform, umdelAuthListener);
    }

    /**
     * auth callback interface
     **/
    private UMAuthListener umAuthListener = new UMAuthListener() {
        @Override
        public void onStart(SHARE_MEDIA share_media) {

        }

        @Override
        public void onComplete(SHARE_MEDIA platform, int action, Map<String, String> data) {
            mShareAPI.getPlatformInfo(LoginActivity.this, platform, umAuthListenerForGetInfo);
        }

        @Override
        public void onError(SHARE_MEDIA platform, int action, Throwable t) {
            DebugUtil.e("umAuthListener ", "SHARE_MEDIA:" + platform + " action:" + action + " t:" + t.toString());
            ToastUtils.showToast("Authorize fail");
        }

        @Override
        public void onCancel(SHARE_MEDIA platform, int action) {
            ToastUtils.showToast("Authorize cancel");
        }
    };

    /**
     * 授权登陆成功后获取用户信息
     **/
    private UMAuthListener umAuthListenerForGetInfo = new UMAuthListener() {
        @Override
        public void onStart(SHARE_MEDIA share_media) {

        }

        @Override
        public void onComplete(SHARE_MEDIA platform, int action, Map<String, String> data) {
            if (null != data) {
                String nickname = "华图教师用户";
                String third_face = null;
                String secypt_keys = data.get("openid");
                String unionId = "";
                if (platform == SHARE_MEDIA.QQ) {
                    nickname = data.get("screen_name");
                    third_face = data.get("profile_image_url");
                } else if (platform == SHARE_MEDIA.WEIXIN) {
                    nickname = data.get("nickname");
                    third_face = data.get("headimgurl");
                    unionId = data.get("unionid");
                    DebugUtil.e("UMAuthListener:unionid   " + unionId);
                }
                registerByThirdPlatform(platform, nickname, third_face, secypt_keys, unionId);
            }
//            ToastUtils.showToast("Authorize succeed");
        }

        @Override
        public void onError(SHARE_MEDIA platform, int action, Throwable t) {

            DebugUtil.e("umAuthListenerForGetInfo", "SHARE_MEDIA:" + platform + " action:" + action + " t:" + t.toString());
            ToastUtils.showToast("Authorize fail");
        }

        @Override
        public void onCancel(SHARE_MEDIA platform, int action) {
            ToastUtils.showToast("Authorize cancel");
        }
    };

    /**
     * delauth callback interface
     **/
    private UMAuthListener umdelAuthListener = new UMAuthListener() {
        @Override
        public void onStart(SHARE_MEDIA share_media) {

        }

        @Override
        public void onComplete(SHARE_MEDIA platform, int action, Map<String, String> data) {
            Logger.i("delete Authorize succeed");
        }

        @Override
        public void onError(SHARE_MEDIA platform, int action, Throwable t) {
            Logger.e("delete Authorize fail");
        }

        @Override
        public void onCancel(SHARE_MEDIA platform, int action) {
            Logger.i("delete Authorize cancel");
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        mShareAPI.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 第三方注册
     *
     * @param platform
     * @param nickname    昵称
     * @param third_face  头像地址
     * @param secypt_keys 唯一key值
     */
    private void registerByThirdPlatform(SHARE_MEDIA platform, String nickname, String third_face, String secypt_keys, String secypt_keys_wx) {
        int type = -1;
        if (platform == SHARE_MEDIA.QQ) {
            quick_login_way = "QQ";

            type = 2;
        } else if (platform == SHARE_MEDIA.WEIXIN) {
            quick_login_way = "微信";
            type = 3;
        }
        ObtainDataFromNetListenerRegister obtainDataFromNetListenerRegister = new ObtainDataFromNetListenerRegister(this);
        SendRequest.registerNewAccount(type, null, null, "", nickname, null, third_face, secypt_keys, secypt_keys_wx, obtainDataFromNetListenerRegister);
    }

    private static class ObtainDataFromNetListenerRegister extends ObtainDataFromNetListener<PersonalInfoBean, String> {
        private LoginActivity weak_activity;

        public ObtainDataFromNetListenerRegister(LoginActivity activity) {
            weak_activity = new WeakReference<>(activity).get();
        }

        @Override
        public void onSuccess(final PersonalInfoBean personalInfoBean) {
            if (weak_activity != null) {
                if (null != personalInfoBean) {
                    //登录成功后销毁旧的首页，再创建新的Mai
                    EventBus.getDefault().post(new EventMessage(100, null));
                    weak_activity.receiveData(personalInfoBean);
                } else {
                    ToastUtils.showToast(R.string.server_error);
                }

                weak_activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        weak_activity.mCustomLoadingDialog.dismiss();
                    }
                });
//                TrackUtil.trackLoginInfo(weak_activity.quick_login_way, personalInfoBean.getAccount(), true, null);
            }


        }

        @Override
        public void onStart() {
            super.onStart();
            weak_activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    weak_activity.mCustomLoadingDialog.show();
                    weak_activity.mCustomLoadingDialog.setTitle(weak_activity.getResources().getString(R.string.logining));
                }
            });
        }

        @Override
        public void onFailure(final String res) {
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
            if (weak_activity != null) {
                TrackUtil.trackLoginInfo(weak_activity.quick_login_way, null, false, "第三方注册异常" + res);
            }
        }
    }

    private void receiveData(PersonalInfoBean personalInfoBean) {
        String selectedCatetoryId = personalInfoBean.getType_id();// 考试类型
        String selectedCityXzqh = personalInfoBean.getCity();// 地区城市
        String selectedProvinceXzqh = personalInfoBean.getProvince();// 地区省份
        String selectedStageName = personalInfoBean.getSec_id();// 考试学段
        String selectedSubjectId = personalInfoBean.getSub_id();// 考试科目
        String selectedSubjects = personalInfoBean.getSub_ids();// 多科目考试科目
        String sex = null;
        if ("1".equals(personalInfoBean.getSex())) {
            sex = getResources().getString(R.string.info_sex_male);
        } else if ("0".equals(personalInfoBean.getSex())) {
            sex = getResources().getString(R.string.info_sex_female);
        }
        // mobile有值代表绑定过手机号，否则没绑定过
        String mobile = personalInfoBean.getMobile();
        //添加友盟的指定用户推送
        CustomApplication.applicationContext.addAlias(personalInfoBean.getId());

        boolean isFirstLogin = false;
        if (!TextUtils.isEmpty(mobile.toString().trim())) {
            saveData(personalInfoBean);
        } else {
            //第一次第三方登陆需要提示绑定号码
            if (!CommonUtils.getSharedPreferenceItemForBoolean(null, UserInfo.KEY_SP_ISFRIST_OPEN_THIRD_LOGIN)) {
                isFirstLogin = true;
                //先把第三方昵称和头像保存
                CommonUtils.putSharedPreferenceItems(null,
                        new String[]{UserInfo.KEY_SP_USERID, UserInfo.KEY_SP_ACCOUNT, UserInfo.KEY_SP_PASSWORD, UserInfo.KEY_SP_MOBILE, UserInfo.KEY_SP_NICKNAME
                                , UserInfo.KEY_SP_BIRTHDAY, UserInfo.KEY_SP_CITY_ID, UserInfo.KEY_SP_CITY_NAME, UserInfo.KEY_SP_PROVINCE_ID, UserInfo.KEY_SP_PROVINCE_NAME,
                                UserInfo.KEY_SP_EXAMCATEGORY_ID, UserInfo.KEY_SP_EXAMCATEGORY_NAME, UserInfo.KEY_SP_EXAMSTAGE_ID,
                                UserInfo.KEY_SP_EXAMSTAGE_NAME, UserInfo.KEY_SP_EXAMSUBJECT_ID, UserInfo.KEY_SP_EXAMSUBJECTS_ID_NAME, UserInfo.KEY_SP_SEX, UserInfo.KEY_SP_FACEPATH,
                                UserInfo.KEY_SP_GOLD},
                        new String[]{personalInfoBean.getId(), personalInfoBean.getAccount(), personalInfoBean.getPassword(), personalInfoBean.getMobile(),
                                personalInfoBean.getNickname(), personalInfoBean.getBirthday(),
                                selectedCityXzqh, CommonUtils.getInstance().getCityNameByXzqh(selectedCityXzqh), selectedProvinceXzqh,
                                CommonUtils.getInstance().getProvinceNameByXzqh(selectedProvinceXzqh), selectedCatetoryId,
                                CommonUtils.getExamCategoryValue(selectedCatetoryId), CommonUtils.getExamStageKey(selectedStageName),
                                selectedStageName, selectedSubjectId, selectedSubjects, sex, personalInfoBean.getFace(), personalInfoBean.getGold()});
                BindMobileMindActivity.newIntent(this);
                CommonUtils.putSharedPreferenceItemForBoolean(null, UserInfo.KEY_SP_ISFRIST_OPEN_THIRD_LOGIN, true);
            } else {
                saveData(personalInfoBean);
            }

        }


        TrackUtil.trackQuickLoginClick(quick_login_way, personalInfoBean.getAccount(), isFirstLogin);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mCustomLoadingDialog.dismiss();
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
