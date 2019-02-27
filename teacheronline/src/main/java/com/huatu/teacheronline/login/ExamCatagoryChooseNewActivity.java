package com.huatu.teacheronline.login;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.huatu.teacheronline.BaseActivity;
import com.huatu.teacheronline.CustomApplication;
import com.huatu.teacheronline.R;
import com.huatu.teacheronline.bean.ProvinceBean;
import com.huatu.teacheronline.bean.ProvinceWithCityBean;
import com.huatu.teacheronline.engine.ObtainDataFromNetListener;
import com.huatu.teacheronline.engine.SendRequest;
import com.huatu.teacheronline.sensorsdata.TrackUtil;
import com.huatu.teacheronline.utils.CommonUtils;
import com.huatu.teacheronline.utils.ToastUtils;
import com.huatu.teacheronline.utils.UserInfo;
import com.huatu.teacheronline.widget.AreaSelectionDialogNew;
import com.huatu.teacheronline.widget.CustomAlertDialog;
import com.umeng.analytics.MobclickAgent;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by ljzyuhenda on 16/1/18.
 * 选择考试类型科目
 */
public class ExamCatagoryChooseNewActivity extends BaseActivity {
    private TextView  tv_government,tv_province_city_eve;
    private RelativeLayout rl_main_left;
//    private TextView tv_complete;
    private String[] categoryKeyList;
    private String[] categoryNameList;
    private CustomAlertDialog customAlertDialog;//退出登陆
    private String uid;
    private String selectedCatetoryId;
    private String selectedStageName;
    private String selectedSubjectId;
    private String selectedCityXzqh;
    private boolean isRegister ;//防止不是注册的时候返回弹出退出登陆页面

    private ArrayList<ProvinceWithCityBean> mProvinceWithCityBeanList = new ArrayList<ProvinceWithCityBean>();
    private List<List<ProvinceBean>> mLists = new ArrayList<>();//遍历展开后的数组
    private List<ProvinceBean> cityList;//如果为null数组就new一个添加

    @Override
    public void initView() {
        setContentView(R.layout.activity_examcatagorychoosenew_layout);

        // 在当前的activity中注册广播
        IntentFilter filter = new IntentFilter();
        filter.addAction(getResources().getString(R.string.action_name_exit_activity));
        this.registerReceiver(this.broadcastReceiver, filter);

        categoryKeyList = getResources().getStringArray(R.array.key_category);
        categoryNameList = getResources().getStringArray(R.array.name_category);

        selectedCatetoryId = CommonUtils.getSharedPreferenceItem(null, UserInfo.KEY_SP_EXAMCATEGORY_ID, "");
        selectedStageName = CommonUtils.getSharedPreferenceItem(null, UserInfo.KEY_SP_EXAMSTAGE_NAME,"");
        selectedSubjectId = CommonUtils.getSharedPreferenceItem(null, UserInfo.KEY_SP_EXAMSUBJECT_ID, "");
        selectedCityXzqh = CommonUtils.getSharedPreferenceItem(null, UserInfo.KEY_SP_CITY_ID, "");

        rl_main_left = (RelativeLayout) findViewById(R.id.rl_main_left);
//        tv_main_title = (TextView) findViewById(R.id.tv_main_title);
//        tv_main_title.setText(R.string.catagory_exam_choose);

        tv_government = (TextView) findViewById(R.id.tv_government);
        tv_province_city_eve = (TextView) findViewById(R.id.tv_province_city_eve);
        uid = CommonUtils.getSharedPreferenceItem(null, UserInfo.KEY_SP_USERID, "");
        isRegister = getIntent().getBooleanExtra("isRegister",false);
    }

    @Override
    public void setListener() {
        rl_main_left.setOnClickListener(this);
//        rl_province_city_eve.setOnClickListener(this);
        tv_government.setOnClickListener(this);
        tv_province_city_eve.setOnClickListener(this);
//        tv_complete.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_main_left:
                if(isRegister){
                    backSkip();
                }else {
                    back();
                }
                break;
            case R.id.tv_government:
                //教师资格证
                MobclickAgent.onEvent(this, "TeacherCertification");
//                tv_government.setTextColor(getResources().getColor(R.color.green001));
                // 选择全国时情况地区数据，防止先选地区后又选全国时地区数据有值
                UserInfo.tempCategoryTypeId = categoryKeyList[0];
                UserInfo.tempCategoryTypeName = categoryNameList[0];
                UserInfo.tempCityId = "86";// 选择全国时，地区值为86
                UserInfo.tempCityName = "全国";
                UserInfo.tempProvinceId = "86";
                UserInfo.tempProvinceName = "全国";
                if (!TextUtils.isEmpty(UserInfo.tempCategoryTypeId)) {
                    ExamStageChooseNewActivity.newIntent(this);
                } else {
                    ToastUtils.showToast(R.string.catagory_exam_choose);
                }

                TrackUtil.trackSelectExamType(categoryNameList[0]);

                break;
            case R.id.tv_province_city_eve:
                //教师招聘考试
                MobclickAgent.onEvent(this, "Teacherrecruitment");
                TrackUtil.trackSelectExamType(categoryNameList[1]);
//                ProvinceChooseNewActivity.newIntent(this);
                getCityData();
                break;
            case R.id.tv_complete:
                if (!TextUtils.isEmpty(UserInfo.tempCategoryTypeId)) {
                    ExamStageChooseNewActivity.newIntent(this);
                } else {
                    ToastUtils.showToast(R.string.catagory_exam_choose);
                }
                break;
        }
    }

    private void getCityData() {
        ObtationListener obtationListener = new ObtationListener(this);
        SendRequest.getlicense(obtationListener);
    }

    private static class ObtationListener extends ObtainDataFromNetListener<ArrayList<ProvinceWithCityBean>, String> {
        private ExamCatagoryChooseNewActivity weak_activity;

        public ObtationListener(ExamCatagoryChooseNewActivity contextWeakReference) {
            weak_activity = new WeakReference<>(contextWeakReference).get();
        }

        public void onSuccess(final ArrayList<ProvinceWithCityBean> res) {
            if (weak_activity != null) {
                weak_activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(res != null){
                            weak_activity.flushContent_OnSucess(res);
                        }else {
                            ToastUtils.showToast(R.string.network);
                        }
                    }
                });
            }
        }

        @Override
        public void onFailure(String res) {
            ToastUtils.showToast(R.string.network);
        }
    }

    public void flushContent_OnSucess(List<ProvinceWithCityBean> res) {
        mProvinceWithCityBeanList.clear();
        mProvinceWithCityBeanList.addAll(res);
        Collections.sort(mProvinceWithCityBeanList);
        for (int i = 0; i < mProvinceWithCityBeanList.size(); i++) {//遍历数据后保存起来
            List<ProvinceBean> ProvinceBeanList = mProvinceWithCityBeanList.get(i).getCityList();
            if (null != ProvinceBeanList && ProvinceBeanList.size() > 0) {
                mLists.add(ProvinceBeanList);
            } else {
                cityList = new ArrayList<>();//如果为null数组就new一个添加
                mLists.add(ProvinceBeanList);
            }
        }
        showAreaSelectionDialog(mProvinceWithCityBeanList);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        UserInfo.tempCategoryTypeId = null;
        UserInfo.tempCategoryTypeName = null;
        UserInfo.tempCityId = null;
        UserInfo.tempCityName = null;
        UserInfo.tempProvinceId = null;
        UserInfo.tempProvinceName = null;
        this.unregisterReceiver(this.broadcastReceiver);
    }

    /**
     * 地区选择器
     */
    private void showAreaSelectionDialog(ArrayList<ProvinceWithCityBean> mProvinceWithCityBeanList) {
        AreaSelectionDialogNew mAreaSelectionDialog = new AreaSelectionDialogNew(this,mProvinceWithCityBeanList, R.style.FullScreenDialog, new AreaSelectionDialogNew.AreaSelectionDialogNewClickListener() {

            @Override
            public void onClick(View v,AreaSelectionDialogNew mAreaSelectionDialog) {
                switch (v.getId()) {
                    case R.id.btn_ok:
//                        tv_birthday.setText(DatePickerDialog.getDate());
//                        birthday = DatePickerDialog.getDate();
                        UserInfo.tempCategoryTypeId = categoryKeyList[1];
                        UserInfo.tempCategoryTypeName = categoryNameList[1];
                        UserInfo.tempCityId = mAreaSelectionDialog.getmCurrentCityXzqh();
                        UserInfo.tempCityName = mAreaSelectionDialog.getmCurrentCityName();
                        UserInfo.tempProvinceId = mAreaSelectionDialog.getmCurrentProviceXzqh();
                        UserInfo.tempProvinceName = mAreaSelectionDialog.getmCurrentProviceName();
                        if (!TextUtils.isEmpty(UserInfo.tempCategoryTypeId)) {
                            ExamStageChooseNewActivity.newIntent(ExamCatagoryChooseNewActivity.this);
                        } else {
                            ToastUtils.showToast(R.string.catagory_exam_choose);
                        }
                        break;
                    default:
                        break;
                }

            }

            ;
        });
        Window window = mAreaSelectionDialog.getWindow();
        window.setWindowAnimations(R.style.PopupAnimation);
        window.setGravity(Gravity.BOTTOM);
        window.getDecorView().setPadding(0, 0, 0, 0);
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(lp);
        mAreaSelectionDialog.show();
        tv_province_city_eve.setTextColor(getResources().getColor(R.color.white));
        tv_province_city_eve.setBackgroundColor(getResources().getColor(R.color.green004));
//                AreaSelectionDialog mAreaSelectionDialog = new AreaSelectionDialog(this,mProvinceWithCityBeanList, R.style.FullScreenDialog, new AreaSelectionDialog.AreaSelectionDialogClickListener() {
//
//            @Override
//            public void onClick(View v,AreaSelectionDialog mAreaSelectionDialog) {
//                switch (v.getId()) {
//                    case R.id.btn_ok:
////                        tv_birthday.setText(DatePickerDialog.getDate());
////                        birthday = DatePickerDialog.getDate();
//                        UserInfo.tempCategoryTypeId = categoryKeyList[1];
//                        UserInfo.tempCategoryTypeName = categoryNameList[1];
//                        UserInfo.tempCityId = mAreaSelectionDialog.getmCurrentCityXzqh();
//                        UserInfo.tempCityName = mAreaSelectionDialog.getmCurrentCityName();
//                        UserInfo.tempProvinceId = mAreaSelectionDialog.getmCurrentProviceXzqh();
//                        UserInfo.tempProvinceName = mAreaSelectionDialog.getmCurrentProviceName();
//                        if (!TextUtils.isEmpty(UserInfo.tempCategoryTypeId)) {
//                            ExamStageChooseNewActivity.newIntent(ExamCatagoryChooseNewActivity.this);
//                        } else {
//                            ToastUtils.showToast(R.string.catagory_exam_choose);
//                        }
//                        break;
//                    default:
//                        break;
//                }
//            }
//
//            ;
//        });
//        Window window = mAreaSelectionDialog.getWindow();
//        window.setWindowAnimations(R.style.PopupAnimation);
//        window.setGravity(Gravity.BOTTOM);
//        window.getDecorView().setPadding(0, 0, 0, 0);
//        WindowManager.LayoutParams lp = window.getAttributes();
//        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
//        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
//        window.setAttributes(lp);
//        mAreaSelectionDialog.show();
        mAreaSelectionDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                tv_province_city_eve.setTextColor(getResources().getColor(R.color.gray007));
                tv_province_city_eve.setBackgroundColor(getResources().getColor(R.color.white010));
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (null != data) {
            switch (resultCode) {
                case 10:// 从选择省份页面返回
                    String xzqh = data.getStringExtra(UserInfo.KEY_SP_CITY_ID);
                    String cityName = data.getStringExtra(UserInfo.KEY_SP_CITY_NAME);
                    String province_xzqh = data.getStringExtra(UserInfo.KEY_SP_PROVINCE_ID);
                    String province_name = data.getStringExtra(UserInfo.KEY_SP_PROVINCE_NAME);
                    if (!TextUtils.isEmpty(cityName)||!TextUtils.isEmpty(province_name)) {
//                        tv_government.setTextColor(getResources().getColor(R.color.black));
                        UserInfo.tempCategoryTypeId = categoryKeyList[1];
                        UserInfo.tempCategoryTypeName = categoryNameList[1];
                        UserInfo.tempCityId = xzqh;
                        UserInfo.tempCityName = cityName;
                        UserInfo.tempProvinceId = province_xzqh;
                        UserInfo.tempProvinceName = province_name;
                        if (!TextUtils.isEmpty(UserInfo.tempCategoryTypeId)) {
                            ExamStageChooseNewActivity.newIntent(this);
                        } else {
                            ToastUtils.showToast(R.string.catagory_exam_choose);
                        }
                    }
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 跳转页面
     *
     * @param context
     */
    public static void newIntent(Activity context) {
        Intent intent = new Intent(context, ExamCatagoryChooseNewActivity.class);
        context.startActivity(intent);
    }
    public static void newIntent(Activity context,boolean isRegister) {
        Intent intent = new Intent(context, ExamCatagoryChooseNewActivity.class);
        intent.putExtra("isRegister",isRegister);
        context.startActivity(intent);
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

    /**
     * 退出登陆
     */
    private void exitLogin() {
        MobclickAgent.onEvent(this, "exitAccount");
        // 退出登录
        customAlertDialog = new CustomAlertDialog(this, R.layout.dialog_exit_login);
        customAlertDialog.show();
        customAlertDialog.setOkOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                        uid = CommonUtils.getSharedPreferenceItem(null, UserInfo.KEY_SP_USERID, "");
                CancelObtainDataFromNetListener obtationListener = new CancelObtainDataFromNetListener(ExamCatagoryChooseNewActivity.this);
                //点击退出登录后调退出接口
                SendRequest.Canel_login(uid, obtationListener);
                CustomApplication.applicationContext.removeAlias(uid);
                customAlertDialog.dismiss();
                UserInfo.registerNumber = null;
                CommonUtils.clearSharedPreferenceItems();
                back();
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
        private ExamCatagoryChooseNewActivity weak_activity;
        public CancelObtainDataFromNetListener(ExamCatagoryChooseNewActivity activity) {
            this.weak_activity = new WeakReference<>(activity).get();
        }
        @Override
        public void onSuccess(String res) {
            if (this.weak_activity!=null){
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

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            if(isRegister){
                backSkip();
            }else {
                back();
            }
        }
        return false;
    }

    /**
     * 返回跳过
     */
    private void backSkip() {
        // 考试类型，地区，学段，科目必须全部选择之后才能跳转到首页否则退出app
        if (!TextUtils.isEmpty(selectedCatetoryId)) {
            if (categoryKeyList[0].equals(selectedCatetoryId)) {
                if (!TextUtils.isEmpty(selectedStageName) && !TextUtils.isEmpty(selectedSubjectId)) {
                    back();
                } else {
                    exitLogin();
                }
            } else if (categoryKeyList[1].equals(selectedCatetoryId)) {
                // 选择地区时，城市必须不能为空
                if (!TextUtils.isEmpty(selectedCityXzqh) && !TextUtils.isEmpty(selectedStageName) && !TextUtils.isEmpty(selectedSubjectId)) {
                    back();
                } else {
                    exitLogin();
                }
            } else {
                exitLogin();
            }
        } else {
            exitLogin();
        }
    }
}
