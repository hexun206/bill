package com.huatu.teacheronline;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.Display;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.view.SimpleDraweeView;
import com.gensee.utils.StringUtil;
import com.huatu.teacheronline.bean.AdBean;
import com.huatu.teacheronline.bean.EventMessage;
import com.huatu.teacheronline.bean.PersonalInfoBean;
import com.huatu.teacheronline.bean.PopupAdsBean;
import com.huatu.teacheronline.bean.ScrollBean;
import com.huatu.teacheronline.bean.TabItemBean;
import com.huatu.teacheronline.direct.DataStore_Direct;
import com.huatu.teacheronline.direct.DirectHomePageActivity;
import com.huatu.teacheronline.direct.VideoDownLoadService;
import com.huatu.teacheronline.direct.manager.RecodeRequestFailureManager;
import com.huatu.teacheronline.engine.ObtainDataFromNetListener;
import com.huatu.teacheronline.engine.SendRequest;
import com.huatu.teacheronline.exercise.DataStore_ExamLibrary;
import com.huatu.teacheronline.exercise.SendRequestUtilsForExercise;
import com.huatu.teacheronline.login.LoginActivity;
import com.huatu.teacheronline.sensorsdata.TrackUtil;
import com.huatu.teacheronline.utils.CheckVersionCodeFile;
import com.huatu.teacheronline.utils.CommonUtils;
import com.huatu.teacheronline.utils.FrescoUtils;
import com.huatu.teacheronline.utils.StringUtils;
import com.huatu.teacheronline.utils.ToastUtils;
import com.huatu.teacheronline.utils.UserInfo;
import com.huatu.teacheronline.widget.CustomAlertDialog;
import com.huatu.teacheronline.widget.HuaTuFragmentTabHost;
import com.tbruyelle.rxpermissions2.Permission;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.umeng.analytics.MobclickAgent;
import com.umeng.socialize.UMShareAPI;
import com.uuzuche.lib_zxing.activity.CodeUtils;
import com.zhy.android.percent.support.PercentRelativeLayout;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.functions.Consumer;

/**
 * 首页
 *
 * @author ljyu
 * @time 2017-6-21 11:41:23
 */
public class HomeActivity extends BaseActivity {

    private CustomAlertDialog customAlertDialog;
    private boolean isCheckAppVersion = true;
    private ObtainDataLister obtatinDataListener;
    private String currentSelectedInfo;
    private long exitTime = 0;
    private AlertDialog builder;
    private AlertDialog builder_code;
    private AlertDialog builder_code_url;
    private String result;
    private ArrayList<ScrollBean> Scrolist = new ArrayList<>();//跑马灯数组
    private AlertDialog builder_advert;
    private ArrayList<PopupAdsBean> popupAdsBeans = new ArrayList<>();//广告数组
    private String uid;
    private String facePath_sp;
    private String nickname_sp;
    private int QRC_REQUEST_CODE = 101;//扫描二维码
    private HuaTuFragmentTabHost mTabHost;
    private List<TabItemBean> items = new ArrayList<TabItemBean>();
    private int lastTabCurrent = 0;
    private int[] drawableIds = new int[]{R.drawable.tab_home, R.drawable.tab_shops, R.drawable.tab_me};
    private int[] pressedDrawableIds = new int[]{R.drawable.tab_home_press, R.drawable.tab_shops_press, R.drawable.tab_me_press};
    private TextView[] views = new TextView[3];
    private boolean mPermissionSetting;
    private RxPermissions mRxPermissions;

    @Override
    public void initView() {
        setContentView(R.layout.activity_home);

        Display mDisplay = getWindowManager().getDefaultDisplay();

        CustomApplication.width = mDisplay.getWidth();
        CustomApplication.height = mDisplay.getHeight();

//        super.initView();
        uid = CommonUtils.getSharedPreferenceItem(null, UserInfo.KEY_SP_USERID, "");
        facePath_sp = CommonUtils.getSharedPreferenceItem(null, UserInfo.KEY_SP_FACEPATH, null);
        nickname_sp = CommonUtils.getSharedPreferenceItem(null, UserInfo.KEY_SP_NICKNAME, null);
        views[0] = (TextView) findViewById(R.id.tab_home);
        views[1] = ((TextView) findViewById(R.id.tab_24));
        views[2] = ((TextView) findViewById(R.id.tab_me));
        views[0].setTextColor(getResources().getColor(R.color.green));

        views[0].setOnClickListener(onClickListener);
        views[1].setOnClickListener(onClickListener);
        views[2].setOnClickListener(onClickListener);
//        views[3].setOnClickListener(onClickListener);
        initDatas();

        //神策track click
        TrackUtil.trackClick(this, views[0], TrackUtil.TYPE_TAB, "首页");
        TrackUtil.trackClick(this, views[2], TrackUtil.TYPE_TAB, "我的");
        TrackUtil.trackClick(this, views[1], TrackUtil.TYPE_TAB, "24小时直播");

    }

    @Override
    protected void onStart() {
        super.onStart();
        //网络恢复后重新请求失败的听课记录
        RecodeRequestFailureManager.getInstance().checkRequestRetry();
        acceptPermissions();
    }

    private void acceptPermissions() {
        mPermissionSetting = false;
        mRxPermissions = new RxPermissions(this);
        mRxPermissions
                .requestEach(android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.CAMERA)
                .subscribe(new Consumer<Permission>() {
                    @Override
                    public void accept(Permission permission) throws Exception {
                        if (permission.granted) {

                        } else if (permission.shouldShowRequestPermissionRationale) {
                            // Denied permission without ask never again
                            mRxPermissions
                                    .request(permission.name)
                                    .subscribe(new Consumer<Boolean>() {
                                        @Override
                                        public void accept(Boolean granted) throws Exception {
                                            if (!granted) {
                                                Toast.makeText(HomeActivity.this, "请允许授予读写存储权限及拍照权限!", Toast.LENGTH_SHORT).show();
                                                finish();

                                            }
                                        }
                                    });

                        } else {
                            // Denied permission with ask never again
                            // Need to go to the settings
                            if (mPermissionSetting) {
                                return;
                            }
                            mPermissionSetting = true;
                            Toast.makeText(HomeActivity.this, "请在设置界面授予读写存储权限及拍照权限!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent();
                            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            Uri uri = Uri.fromParts("package", getPackageName(), null);
                            intent.setData(uri);
                            startActivity(intent);
                        }


                    }
                });


    }


    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.tab_home:
                    mTabHost.setCurrentTab(0);
                    viewHandler(0);
                    lastTabCurrent = 0;
                    break;
//                case R.id.tab_24:
//
////                    viewHandler(1);
////                    mTabHost.setCurrentTab(1);
////                    lastTabCurrent = 1;
//
//                    DirectBean directBean = new DirectBean();
//                    directBean.setRid("-1");
//                    directBean.setTitle("24小时大咖直播");
//                    directBean.setIs_fufei("1");
//                    directBean.setIs_buy("1");
//                    directBean.setIsTrial(0);
//                    directBean.setCustomer("http://chat.looyuoms.com/chat/chat/p.do?c=20001211&f=10062439&g=10059670&refer=appsh");
////                    DirectPlayDetailsActivityForRtsdk.newIntent(HomeActivity.this, directBean, 0, 2);
//                    PlayerActivityForBjysdk.newIntent(HomeActivity.this, directBean, 0, 2);
//                    break;

                case R.id.tab_me:
                    viewHandler(2);
                    mTabHost.setCurrentTab(2);
                    lastTabCurrent = 2;
                    break;
            }

        }
    };


    /**
     * 设置图标
     *
     * @param position
     */
    @SuppressLint("ResourceAsColor")
    public void viewHandler(int position) {
        if (position < 0 || position > 2) {
            return;
        }

        for (int i = 0; i < views.length; i++) {
            if (i == 1) continue;
            TextView view = views[i];
            if (position == i) {
                view.setCompoundDrawables(null, getDrawabless(pressedDrawableIds[i]), null, null);
                view.setTextColor(getResources().getColor(R.color.green));
            } else {
                view.setCompoundDrawables(null, getDrawabless(drawableIds[i]), null, null);
                view.setTextColor(getResources().getColor(R.color.gray004));
            }
        }
    }

    /**
     * 获取图片资源
     *
     * @param res
     * @return
     */
    public Drawable getDrawabless(int res) {
        Drawable drawable = getResources().getDrawable(res);
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        return drawable;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                getSupportFragmentManager().popBackStack();
            } else {
                exitApp();
            }
        }
        return false;
    }


    @Override
    public void setListener() {
//        super.setListener();
    }

    /**
     * 初始化科目表
     */
    private void initDatas() {
        // 实例化TabHost对象，得到TabHost
        mTabHost = (HuaTuFragmentTabHost) findViewById(android.R.id.tabhost);
        mTabHost.setup(this, getFragmentManager(), R.id.realtabcontent);
        items.add(new TabItemBean(R.drawable.selector_tab_home, HomeFragment.class, "首页"));
        items.add(new TabItemBean(R.drawable.selector_tab_shops, LivingFragment.class, "24小时直播"));
        items.add(new TabItemBean(R.drawable.selector_tab_me, MeFragment.class, "我的"));

        for (int i = 0; i < items.size(); i++) {
            TabItemBean item = items.get(i);
            // 为每一个Tab按钮设置图标、文字和内容
            TabHost.TabSpec tabSpec = mTabHost.newTabSpec(i + "").setIndicator(i + "");
            // 将Tab按钮添加进Tab选项卡中
            mTabHost.addTab(tabSpec, item.getClzz(), null);
        }
    }

    @Override
    public void onClick(View view) {
//        super.onClick(view);
        switch (view.getId()) {
        }
    }

    /**
     * 退出登陆
     */
    public void exitLogin() {
        MobclickAgent.onEvent(this, "exitAccount");
        // 退出登录
        customAlertDialog = new CustomAlertDialog(this, R.layout.dialog_exit_login);
        customAlertDialog.show();
        customAlertDialog.setOkOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                        uid = CommonUtils.getSharedPreferenceItem(null, UserInfo.KEY_SP_USERID, "");
                CancelObtainDataFromNetListener obtationListener = new CancelObtainDataFromNetListener(HomeActivity.this);
                //点击退出登录后调退出接口
                SendRequest.Canel_login(uid, obtationListener);
                CustomApplication.applicationContext.removeAlias(uid);
                customAlertDialog.dismiss();
                finish();
                UserInfo.registerNumber = null;
                CommonUtils.clearSharedPreferenceItems();
                Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != customAlertDialog) {
            customAlertDialog.dismiss();
        }
        //如果下载服务开起来了就关闭
        stopService(new Intent(getApplicationContext(), VideoDownLoadService.class));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(EventMessage event) {
        if (event.getType() == 100) {
            try {
                finish();//接收从登录页面传来的消息，销毁旧的Mai
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        /** attention to this below ,must add this**/
        if (requestCode == 201) {
            if (resultCode == 202) {
                // 个人信息更新后返回，需要更新首页信息
//                nickname_sp = CommonUtils.getSharedPreferenceItem(null, UserInfo.KEY_SP_NICKNAME, null);
//                tv_name_personal_slide.setText(nickname_sp);
//                facePath_sp = CommonUtils.getSharedPreferenceItem(null, UserInfo.KEY_SP_FACEPATH, null);
//                iv_personal_slide.setHierarchy(hierarchy_slide);
//                FrescoUtils.setFrescoImageUri(iv_personal_slide, facePath_sp, R.drawable.morentouxiang);
            }

            return;
        }
        /**
         * 处理二维码扫描结果
         */
        if (requestCode == QRC_REQUEST_CODE) {
            //处理扫描结果（在界面上显示）
            if (null != data) {
                Bundle bundle = data.getExtras();
                if (bundle == null) {
                    return;
                }
                if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_SUCCESS) {
                    result = bundle.getString(CodeUtils.RESULT_STRING);
                    String Intercept_result = result.substring(0, 4);
//                    Toast.makeText(this, "解析结果:" + result, Toast.LENGTH_LONG).show();
                    if (Intercept_result.equals("jszx")) {
                        Dialog_code();
                    } else {
                        Dialog_url_code();
                    }
//                    Toast.makeText(this, "解析结果:" + result, Toast.LENGTH_LONG).show();
                } else if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_FAILED) {
                    Toast.makeText(HomeActivity.this, "解析二维码失败", Toast.LENGTH_LONG).show();
                }
            }
        }
        UMShareAPI.get(CustomApplication.applicationContext).onActivityResult(requestCode, resultCode, data);
    }

    public static void newIntent(Activity context) {
        Intent intent = new Intent(context, HomeActivity.class);
        context.startActivity(intent);
    }

    /**
     * 跳转到我的课程页面
     *
     * @param context
     * @param activity_From
     * @param videoType
     */
    public static void newIntentFlag(Activity context, String activity_From, int videoType) {
        Intent intent = new Intent(context, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("Activity_From", activity_From);
        intent.putExtra("videoType", videoType);
        context.startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //重置题库储存数据为空，以减少内存占用
        DataStore_ExamLibrary.resetDatas();
        DataStore_Direct.resetDatas();
        iniUserInfo();

//        acceptPermissions();
    }

    /**
     * 初始化用户信息 科目信息 版本更新
     */
    private void iniUserInfo() {
        //检查版本更新
        if (isCheckAppVersion) {
            checkVersion();
            isCheckAppVersion = false;
        }
        obtatinDataListener = new ObtainDataLister(this);
        SendRequest.getPersonalInfo(uid, obtatinDataListener);
//        initCurrentSelectedInfo();
    }

    public void checkVersion() {
        ObtationListener obtationListener = new ObtationListener(this);
        SendRequest.getVersion(obtationListener);
    }

    public class ObtationListener extends ObtainDataFromNetListener<String[], String> {
        public HomeActivity weak_activity;

        public ObtationListener(HomeActivity activity) {
            weak_activity = new WeakReference<>(activity).get();
        }

        @Override
        public void onSuccess(final String[] res) {
            if (weak_activity != null) {
                weak_activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        CheckVersionCodeFile checkVersionCodeFile = new CheckVersionCodeFile(HomeActivity.this, CheckVersionCodeFile.FromMainActivity, res,
                                null);
                        checkVersionCodeFile.checkVersion();
                    }
                });
            }
        }

        @Override
        public void onFailure(String res) {
        }
    }

    /**
     * 更新首页的科目选择信息
     */
    private void initCurrentSelectedInfo() {
        SendRequestUtilsForExercise sendRequestUtilsForExercise = SendRequestUtilsForExercise.getInstance();
        sendRequestUtilsForExercise.assignDatas();
        Resources resources = getResources();
        String selectedSubject = CommonUtils.getSharedPreferenceItem(null, UserInfo.KEY_SP_EXAMSUBJECT_NAME, null);
        if (StringUtils.isEmpty(selectedSubject)) {
            return;
        }
        if (selectedSubject.equals("语文") || selectedSubject.equals("英语") ||
                selectedSubject.equals("体育") || selectedSubject.equals("音乐") || selectedSubject.equals("数学")
                || selectedSubject.equals("美术")) {
            selectedSubject = selectedSubject + "专业知识";
            CommonUtils.putSharedPreferenceItem(null, UserInfo.KEY_SP_EXAMSUBJECT_NAME, selectedSubject);
        }
        String examstagName = CommonUtils.getSharedPreferenceItem(null, UserInfo.KEY_SP_EXAMSTAGE_NAME, null);
        if (getResources().getString(R.string.key_stage_government_confirm).equals(sendRequestUtilsForExercise.examType)) {
            currentSelectedInfo = resources.getString(R.string.current_info) + resources.getString(R.string.current_gj) + resources.getString(R.string.dot) +
                    examstagName + resources.getString(R.string.dot)
                    + selectedSubject;
        } else {
            String cityname = CommonUtils.getSharedPreferenceItem(null, UserInfo.KEY_SP_CITY_NAME, null);
            String provinname = CommonUtils.getSharedPreferenceItem(null, UserInfo.KEY_SP_PROVINCE_NAME, null);
            if (TextUtils.isEmpty(cityname)) {
                cityname = CommonUtils.getInstance().getCityNameByXzqh(UserInfo.KEY_SP_CITY_ID);
                CommonUtils.putSharedPreferenceItem(null, UserInfo.KEY_SP_CITY_ID, cityname);
                if (TextUtils.isEmpty(cityname)) {
                    cityname = "";
                }
            }
            if (TextUtils.isEmpty(provinname)) {
                provinname = CommonUtils.getInstance().getProvinceNameByXzqh(UserInfo.KEY_SP_PROVINCE_ID);
                CommonUtils.putSharedPreferenceItem(null, UserInfo.KEY_SP_PROVINCE_ID, provinname);
                if (TextUtils.isEmpty(provinname)) {
                    provinname = "";
                }
            }
            //防止直辖市
            if (provinname.equals(cityname)) {
                currentSelectedInfo = resources.getString(R.string.current_info) + cityname + resources.getString(R.string.current_dq) + resources.getString(R
                        .string.dot) + examstagName + resources.getString(R.string.dot) + selectedSubject;
            } else {
                currentSelectedInfo = resources.getString(R.string.current_info) + provinname + cityname + resources.getString(R.string.current_dq) +
                        resources.getString(R
                                .string.dot) + examstagName + resources.getString(R.string.dot) + selectedSubject;
            }
        }

//        tv_update_info.setText(currentSelectedInfo);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        initCurrentSelectedInfo();
        if (intent != null && "ToMyDirectActivity".equals(intent.getStringExtra("Activity_From"))) {
//            MyDirectActivity.newIntent(this, "0");
            int videoType = intent.getIntExtra("videoType", 0);
            DirectHomePageActivity.newIntent(this, videoType, true);
        }
    }


    /****
     * 实时请求用户信息
     *****/
    private class ObtainDataLister extends ObtainDataFromNetListener<PersonalInfoBean, String> {

        private HomeActivity weak_activity;

        public ObtainDataLister(HomeActivity activity) {
            weak_activity = new WeakReference<>(activity).get();
        }

        @Override
        public void onStart() {
            super.onStart();
        }

        @Override
        public void onSuccess(final PersonalInfoBean res) {
            if (weak_activity != null) {
                if (res != null) {
//                    weak_activity.runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            //更新金币信息
//                            CommonUtils.putSharedPreferenceItem(null, UserInfo.KEY_SP_GOLD, res.getGold());
//                            CommonUtils.putSharedPreferenceItem(null, UserInfo.KEY_SP_USERID, res.getId());
//                            CommonUtils.putSharedPreferenceItem(null, UserInfo.KEY_SP_POINT, res.getUserPoint());
//                            CommonUtils.putSharedPreferenceItem(null, UserInfo.KEY_SP_FIRST_PASS, res.getFirst_pass());
//                            initCarsuelView(res.getAdvertising());
//                            weak_activity.Scrolist.addAll(res.getscroll_bar());
//                            weak_activity.iniFlipperview(res.getscroll_bar());//跑马灯广告实现方法
//                            weak_activity.tv_gold_number.setText(res.getGold());
//                            weak_activity.left_gold_number.setText(res.getGold());
//                            weak_activity.tv_name_personal_slide.setText(res.getNickname());
//                            weak_activity.tv_sign.setText(res.getNum());
//                            popupAdsBeans.add(res.getPopup_ads());
//                            if (res.getPopup_ads()!=null&&!res.getPopup_ads().equals("")){
//                                if (!time_date.equals(sp_time_data)){
//                                    Whether=2;
//                                    CommonUtils.putSharedPreferenceItem(null, UserInfo.KEY_SP_TIME_DATA, time_date);
//                                    //KEY_SP_AdVERT 保存第一次安装登录后 点击完提示弹出广告。之后不需要
//                                    if (CommonUtils.getSharedPreferenceItem(null,UserInfo.KEY_SP_AdVERT,"").equals("Openad")){
//                                        DialogAdvert();
//                                    }
//                                }
//                            }
//                            if (res.getlession_status().equals("1")){//首页我的课程小红点控制
//                                img_live_icon.setVisibility(View.GONE);
//                            }else{
//                                img_live_icon.setVisibility(View.VISIBLE);
//                            }
//                            if (!StringUtil.isEmpty(res.getMsg_status())) {//首页我的消息小红点显示控制
//                                if (Integer.parseInt(res.getMsg_status()) == 0) {
//                                    img_msg_icon.setVisibility(View.VISIBLE);
//                                } else {
//                                    img_msg_icon.setVisibility(View.GONE);
//                                }
//                            }
//                            updataUserInfo(res);
//                        }
//                    });
                }
            }
        }

        @Override
        public void onFailure(final String res) {
            if (weak_activity != null) {
                weak_activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (res.equals(SendRequest.ERROR_NETWORK)) {
                            ToastUtils.showToast(R.string.network);
                        } else if (res.equals(SendRequest.ERROR_SERVER)) {
                            ToastUtils.showToast(R.string.server_error);
                        }
                        initCurrentSelectedInfo();
                    }
                });
            }
        }
    }

    /**
     * 实时更新用户信息
     *
     * @param res
     */
    private void updataUserInfo(PersonalInfoBean res) {
        String selectedCatetoryId = res.getType_id();// 考试类型
        String selectedCityXzqh = res.getCity();// 地区城市
        String selectedProvinceXzqh = res.getProvince();// 地区省份
        String selectedStageName = res.getSec_id();// 考试学段
        String selectedSubjectId = res.getSub_id();// 考试科目id
        String selectedSubjectName = "";// 考试科目名称
        String selectedSubjects = res.getSub_ids();// 多科目考试科目
        String sex = null;
        if ("1".equals(res.getSex())) {
            sex = getResources().getString(R.string.info_sex_male);
        } else if ("0".equals(res.getSex())) {
            sex = getResources().getString(R.string.info_sex_female);
        }
        if (StringUtil.isEmpty(selectedSubjects)) {
            CommonUtils.putSharedPreferenceItem(null, UserInfo.KEY_SP_EXAMSUBJECTS_ID_NAME, res.getSub_id() + "_" + CommonUtils
                    .getSharedPreferenceItem(null, UserInfo.KEY_SP_EXAMSUBJECT_NAME, ""));
            selectedSubjects = res.getSub_id() + "_" + CommonUtils.getSharedPreferenceItem(null, UserInfo.KEY_SP_EXAMSUBJECT_NAME, "");
        } else {
            CommonUtils.putSharedPreferenceItem(null, UserInfo.KEY_SP_EXAMSUBJECTS_ID_NAME, res.getSub_ids());
        }
        String localSubjectId = CommonUtils.getSharedPreferenceItem(null, UserInfo.KEY_SP_EXAMSUBJECT_ID, "");
        //为了更新科目名称
        if (!StringUtil.isEmpty(localSubjectId) && selectedSubjects.contains(localSubjectId)) {
            //如果本地保存的当前选中的科目在多科目选项里面就不更改前面显示
        } else {
            //如果本地保存的当前选中的科目不在多科目选项里面就更新科目显示并且保存本地
            String[] split = selectedSubjects.split(",");
            for (int i = 0; i < split.length; i++) {
                String[] split1 = split[i].split("_");
                if (selectedSubjectId.equals(split1[0])) {
                    selectedSubjectName = split1[1];
                }
            }
            CommonUtils.putSharedPreferenceItem(null, UserInfo.KEY_SP_EXAMSUBJECT_ID, selectedSubjectId);
            CommonUtils.putSharedPreferenceItem(null, UserInfo.KEY_SP_EXAMSUBJECT_NAME, selectedSubjectName);
        }
        CommonUtils.putSharedPreferenceItems(null,
                new String[]{UserInfo.KEY_SP_MOBILE, UserInfo.KEY_SP_NICKNAME, UserInfo.KEY_SP_BIRTHDAY, UserInfo.KEY_SP_CITY_ID, UserInfo.KEY_SP_CITY_NAME,
                        UserInfo.KEY_SP_PROVINCE_ID, UserInfo.KEY_SP_PROVINCE_NAME,
                        UserInfo.KEY_SP_EXAMCATEGORY_ID, UserInfo.KEY_SP_EXAMCATEGORY_NAME, UserInfo.KEY_SP_EXAMSTAGE_ID,
                        UserInfo.KEY_SP_EXAMSTAGE_NAME, UserInfo.KEY_SP_SEX, UserInfo.KEY_SP_FACEPATH},
                new String[]{res.getMobile(), res.getNickname(), res.getBirthday(),
                        selectedCityXzqh, CommonUtils.getInstance().getCityNameByXzqh(selectedCityXzqh), selectedProvinceXzqh,
                        CommonUtils.getInstance().getProvinceNameByXzqh(selectedProvinceXzqh), selectedCatetoryId,
                        CommonUtils.getExamCategoryValue(selectedCatetoryId), CommonUtils.getExamStageKey(selectedStageName),
                        selectedStageName, sex, res.getFace()});
        initCurrentSelectedInfo();
    }

    /**
     * 返回键双击退出APP
     */
    private void exitApp() {
        if ((System.currentTimeMillis() - exitTime) > 2000) {
            Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
            exitTime = System.currentTimeMillis();
        } else {
            finish();
        }
    }

    /**
     * 初始化轮播图
     */
    public void initCarsuelView(ArrayList<AdBean> adBeans) {
//        img_mian_adpic.setPlayDelay(3300);
//        AdLoopAdapter mLoopAdapter = new AdLoopAdapter(img_mian_adpic, adBeans, this);
//        img_mian_adpic.setAdapter(mLoopAdapter);
//        img_mian_adpic.setHintView(new IconHintView(this, R.drawable.ad_red_circleviewindicator, R.drawable.ad_white_circleviewindicator));
    }

    /**
     * 检测服务是否正在运行
     *
     * @param context
     * @param service_Name
     * @return
     */
    private boolean isServiceRunning(Context context, String service_Name) {
        ActivityManager manager =
                (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (service_Name.equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    private static class CancelObtainDataFromNetListener extends ObtainDataFromNetListener<String, String> {
        private HomeActivity weak_activity;

        public CancelObtainDataFromNetListener(HomeActivity activity) {
            this.weak_activity = new WeakReference<>(activity).get();
        }

        @Override
        public void onSuccess(String res) {
            if (this.weak_activity != null) {
//                ToastUtils.showToast("退出成功");
            }
        }

        @Override
        public void onFailure(final String res) {
            this.weak_activity.runOnUiThread(new Runnable() {
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

    /**
     * 扫描二维码弹出对话框
     */
    private void Dialog_code() {
        builder_code = new AlertDialog.Builder(HomeActivity.this, R.style.Dialog_Fullscreen).create();//让对话框全屏
        builder_code.setCancelable(false);
        builder_code.show();
        Window window = builder_code.getWindow();
        window.setContentView(R.layout.scan_code_login);
        TextView tv_code_canle = (TextView) window.findViewById(R.id.tv_code_canle);
        tv_code_canle.setOnClickListener(this);
        TextView tv_confirm_login = (TextView) window.findViewById(R.id.tv_confirm_login);
        tv_confirm_login.setOnClickListener(this);
        PercentRelativeLayout rl_dialog_left = (PercentRelativeLayout) window.findViewById(R.id.rl_dialog_left);
        rl_dialog_left.setOnClickListener(this);
    }

    /**
     * 扫描二维码弹出网页
     */
    private void Dialog_url_code() {
        ToastUtils.showToast("请稍等。。。");
        builder_code_url = new AlertDialog.Builder(HomeActivity.this, R.style.Dialog_Fullscreen).create();//让对话框全屏
        builder_code_url.setCancelable(false);
        builder_code_url.show();
        Window window = builder_code_url.getWindow();
        window.setContentView(R.layout.dialog_url_code);
        WebView wb_url = (WebView) window.findViewById(R.id.cwq_web);
        final TextView tv_web_title = (TextView) window.findViewById(R.id.tv_main_title);
        WebSettings mSettings = wb_url.getSettings();
        mSettings.setUseWideViewPort(true);
        mSettings.setJavaScriptEnabled(true);
        mSettings.setLoadWithOverviewMode(true);
        wb_url.loadUrl(result);
        wb_url.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return super.shouldOverrideUrlLoading(view, url);
            }
        });
        // 设置setWebChromeClient对象
        WebChromeClient wvcc = new WebChromeClient() {
            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                tv_web_title.setText(title);
            }
        };
        wb_url.setWebChromeClient(wvcc);
        RelativeLayout rl_dialog_canle = (RelativeLayout) window.findViewById(R.id.rl_dialog_canle);
        rl_dialog_canle.setOnClickListener(this);
    }

    /**
     * 首页广告
     */
    private void DialogAdvert() {
        builder_advert = new AlertDialog.Builder(HomeActivity.this, R.style.Dialog_Fullscreenx).create();
        builder_advert.setCancelable(false);
        builder_advert.show();
        Window window = builder_advert.getWindow();
        window.setContentView(R.layout.dialog_advertisement);
        RelativeLayout iv_canle = (RelativeLayout) window.findViewById(R.id.rl_canle);
        iv_canle.setOnClickListener(this);
        SimpleDraweeView sdv_adver = (SimpleDraweeView) window.findViewById(R.id.sdv_draweeView);
        sdv_adver.setOnClickListener(this);
        GenericDraweeHierarchyBuilder builder =
                new GenericDraweeHierarchyBuilder(getResources());
        GenericDraweeHierarchy hierarchy = builder
                .setFadeDuration(200)
                .setPlaceholderImage(getResources().getDrawable(R.drawable.ic_loading), ScalingUtils.ScaleType.FIT_XY)
                .setFailureImage(getResources().getDrawable(R.drawable.ic_loading), ScalingUtils.ScaleType.FIT_XY)
                .setActualImageScaleType(ScalingUtils.ScaleType.FIT_XY)
                .build();
        sdv_adver.setHierarchy(hierarchy);
        FrescoUtils.setFrescoImageUri(sdv_adver, popupAdsBeans.get(0).getUrl(), R.drawable.ic_loading);
    }

    @Override
    public void onReceiveBroadCast(Context context, Intent intent) {
        super.onReceiveBroadCast(context, intent);
    }

    @Override
    public void onNetReConnected() {
        super.onNetReConnected();
        iniUserInfo();
        //网络恢复后重新请求失败的听课记录
        RecodeRequestFailureManager.getInstance().checkRequestRetry();
    }
}
