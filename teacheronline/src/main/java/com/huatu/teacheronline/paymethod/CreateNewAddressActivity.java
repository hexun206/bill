package com.huatu.teacheronline.paymethod;

import android.content.Context;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.huatu.teacheronline.R;
import com.huatu.teacheronline.engine.ObtainDataFromNetListener;
import com.huatu.teacheronline.engine.SendRequest;
import com.huatu.teacheronline.paymethod.bean.PersonalAddressBean;
import com.huatu.teacheronline.paymethod.com.mrwujay.cascade.service.BaseAddressActivity;
import com.huatu.teacheronline.paymethod.kankan.wheel.chooseaddresswidget.adapters.ArrayWheelAdapter;
import com.huatu.teacheronline.paymethod.kankan.wheel.chooseaddresswidget.customview.OnWheelChangedListener;
import com.huatu.teacheronline.paymethod.kankan.wheel.chooseaddresswidget.customview.WheelView;
import com.huatu.teacheronline.utils.CommonUtils;
import com.huatu.teacheronline.utils.ToastUtils;
import com.huatu.teacheronline.utils.UserInfo;
import com.huatu.teacheronline.widget.CustomAlertDialog;

import java.lang.ref.WeakReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 新建地址界面
 *
 * @author wf
 * @date 2016/5/24
 */

public class CreateNewAddressActivity extends BaseAddressActivity implements View.OnTouchListener, OnWheelChangedListener {
    private TextView tv_main_right;
    private TextView tv_main_title;
    private ImageView ib_main_left;
    private EditText et_obtain_person;
    private EditText et_obtain_telephone;
    private EditText et_city_area;
    private EditText et_obtain_address_stress;
    private RelativeLayout rl_choose_province_city;
    private WheelView mViewProvince;
    private WheelView mViewCity;
    private WheelView mViewDistrict;
    private Button mBtnConfirm;
    private String uid;
    private CustomAlertDialog customAlertDialogUpdate;
    private CustomAlertDialog customAlertDialog;
    private int flag;//1 新建地址  2 编辑地址
    private String id;
    private RelativeLayout rl_main_left;
    private RelativeLayout rl_main_right;
    private PersonalAddressBean personalAddressBean;//编辑地址传过来的信息


    @Override
    public void initView() {
        setContentView(R.layout.activity_create_new_address);
        uid = CommonUtils.getSharedPreferenceItem(null, UserInfo.KEY_SP_USERID, null);
        flag = getIntent().getFlags();
        tv_main_title = (TextView) findViewById(R.id.tv_main_title);
        tv_main_right = (TextView) findViewById(R.id.tv_main_right);
        customAlertDialogUpdate = new CustomAlertDialog(this, R.layout.dialog_confirm_commit);

        ib_main_left = (ImageView) findViewById(R.id.ib_main_left);
        ib_main_left.setImageResource(R.drawable.back_arrow);
        rl_main_left = (RelativeLayout) findViewById(R.id.rl_main_left);
        rl_main_right = (RelativeLayout) findViewById(R.id.rl_main_right);
        et_obtain_person = (EditText) findViewById(R.id.et_obtain_person);
        et_obtain_telephone = (EditText) findViewById(R.id.et_obtain_telephone);
        et_city_area = (EditText) findViewById(R.id.et_city_area);
        et_obtain_address_stress = (EditText) findViewById(R.id.et_obtain_address_stress);
        rl_choose_province_city = (RelativeLayout) findViewById(R.id.rl_choose_province_city);
        if (flag == 1) {
            tv_main_title.setText("添加新地址");
            tv_main_right.setText("保存");
            tv_main_right.setTextColor(getResources().getColor(R.color.gray007));
            customAlertDialogUpdate.setTitle("提示<br/>确定提交吗?");
        } else if (flag == 2) {
            customAlertDialogUpdate.setTitle("提示<br/>确定保存修改吗？");
            tv_main_title.setText("修改地址");
            tv_main_right.setText("保存");
            tv_main_right.setTextColor(getResources().getColor(R.color.gray007));
            personalAddressBean = (PersonalAddressBean) getIntent().getSerializableExtra("PersonalAddressBean");
            id = personalAddressBean.getId();
            et_obtain_person.setText(personalAddressBean.getName());
            et_obtain_telephone.setText(personalAddressBean.getTel());
            et_city_area.setText(personalAddressBean.getProvince());
            et_obtain_address_stress.setText(personalAddressBean.getAddress());
        }
        setUpViews();
        setUpListener();
        setUpData();
    }

    /**
     * 找到省市控件
     */

    private void setUpViews() {
        mViewProvince = (WheelView) findViewById(R.id.id_province);
        mViewCity = (WheelView) findViewById(R.id.id_city);
        mViewDistrict = (WheelView) findViewById(R.id.id_district);
        mBtnConfirm = (Button) findViewById(R.id.btn_confirm);
    }

    /**
     * 设置省市监听
     */
    private void setUpListener() {
        // 添加change事件
        mViewProvince.addChangingListener(this);
        // 添加change事件
        mViewCity.addChangingListener(this);
        // 添加change事件
        mViewDistrict.addChangingListener(this);
        // 添加onclick事件
        mBtnConfirm.setOnClickListener(this);
    }

    @Override
    public void setListener() {
        ib_main_left.setOnClickListener(this);
        rl_main_left.setOnClickListener(this);
        tv_main_right.setOnClickListener(this);
        rl_main_right.setOnClickListener(this);
        et_city_area.setOnTouchListener(this);
        et_obtain_person.setOnTouchListener(this);
        et_obtain_telephone.setOnTouchListener(this);
        et_obtain_address_stress.setOnTouchListener(this);
        customAlertDialogUpdate.setOkOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customAlertDialogUpdate.dismiss();
                if (flag == 1) {
                    saveAddress();
                } else {
                    updateAddress();
                }
            }
        });
        customAlertDialogUpdate.setCancelOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customAlertDialogUpdate.dismiss();
            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_main_left:
            case R.id.ib_main_left:
                finish();
                break;
            /**
             * 点击新建或者添加按钮的相关逻辑
             */
            case R.id.rl_main_right:
            case R.id.tv_main_right:
                String person = et_obtain_person.getText().toString().trim();
                String telephone = et_obtain_telephone.getText().toString();
                String city_area = et_city_area.getText().toString().trim();
                String address_stress = et_obtain_address_stress.getText().toString().trim();
                if (flag == 1) {
                    if (inputInformation(person, telephone, city_area, address_stress)) return;
                    customAlertDialogUpdate.show();
                } else if (flag == 2) {
                    if (inputInformation(person, telephone, city_area, address_stress)) return;
                    customAlertDialogUpdate.show();
                }
                break;
            case R.id.btn_confirm:
                showSelectedResult();
                rl_choose_province_city.setVisibility(View.GONE);
                break;


        }
    }

    /**
     * 判断用户输入信息
     */
    private boolean inputInformation(String person, String telephone, String city_area, String address_stress) {
        if (TextUtils.isEmpty(person)) {
            ToastUtils.showToast("请输入收货人姓名");
            return true;
        }
        if (TextUtils.isEmpty(telephone)) {
            ToastUtils.showToast("请输入联系电话");
            return true;

        }
//        if (!isMobileNO(telephone)) {   服务端做判断，不需要前端设置手机格式
//            ToastUtils.showToast("请输入正确的手机格式");
//            return true;
//        }
        if (TextUtils.isEmpty(city_area)) {
            ToastUtils.showToast("请选择所在市区");
            return true;

        }
        if (TextUtils.isEmpty(address_stress)) {
            ToastUtils.showToast("请输入街道地址");
            return true;
        }
        if (address_stress.length() < 5) {
            ToastUtils.showToast("详细地址不少于5个字");
            return true;
        }
        return false;
    }

    /**
     * 判断是否是手机格式
     *
     * @param mobiles
     * @return
     */
    public static boolean isMobileNO(String mobiles) {

//        Pattern p = Pattern.compile("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");
        Pattern p = Pattern.compile("^[1][3578][0-9]{9}$");
        Matcher m = p.matcher(mobiles);

        return m.matches();

    }

    /**
     * 更新地址
     */

    private void updateAddress() {
        ObtainDataFromNetListenerUpdateAddress obtainDataFromNetListenerUpdateAddress = new ObtainDataFromNetListenerUpdateAddress(this);
        SendRequest.updateAddress(uid, id, et_obtain_person.getText().toString(), et_obtain_telephone.getText().toString(),
                et_city_area.getText().toString(), et_obtain_address_stress.getText().toString(), personalAddressBean.getMr(),
                obtainDataFromNetListenerUpdateAddress);
        finish();
//        startActivity(new Intent(CreateNewAddressActivity.this, ChooseObtainAddressActivity.class));

    }

    public static class ObtainDataFromNetListenerUpdateAddress extends ObtainDataFromNetListener<String, String> {

        private CreateNewAddressActivity weak_activity;

        public ObtainDataFromNetListenerUpdateAddress(CreateNewAddressActivity activity) {
            weak_activity = new WeakReference<>(activity).get();
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
        public void onSuccess(String res) {
            if (res != null) {
                weak_activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        weak_activity.customAlertDialog.dismiss();
                        ToastUtils.showToast("编辑成功");
                        weak_activity.finish();
                    }
                });
            }
        }


        @Override
        public void onFailure(final String res) {
            weak_activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (res.equals(SendRequest.ERROR_NETWORK)) {
                        ToastUtils.showToast(R.string.network);
                    } else if (res.equals(SendRequest.ERROR_SERVER)) {
                        ToastUtils.showToast(R.string.save_success);
                    } else {
                        ToastUtils.showToast(res);
                    }
                }
            });
        }
    }

    /**
     * 点击省市联动并进行显示
     */
    private void showSelectedResult() {

        String city_area = mCurrentProviceName + "," + mCurrentCityName + ","
                + mCurrentDistrictName;
        et_city_area.setText(city_area);
//        Toast.makeText(CreateNewAddressActivity.this, "当前选中:" + mCurrentProviceName + "," + mCurrentCityName + ","
//                + mCurrentDistrictName + "," + mCurrentZipCode, Toast.LENGTH_SHORT).show();
    }

    /**
     * 保存地址信息到服务器
     */

    private void saveAddress() {
        ObtainDataFromNetListenerSaveAddress obtainDataFromNetListenerSaveAddress = new ObtainDataFromNetListenerSaveAddress(this);
        SendRequest.saveAddress(uid, et_obtain_person.getText().toString(), et_obtain_telephone.getText().toString(),
                et_city_area.getText().toString(), et_obtain_address_stress.getText().toString(), 1,
                obtainDataFromNetListenerSaveAddress);
    }

    public static class ObtainDataFromNetListenerSaveAddress extends ObtainDataFromNetListener<String, String> {

        private CreateNewAddressActivity weak_activity;

        public ObtainDataFromNetListenerSaveAddress(CreateNewAddressActivity activity) {
            weak_activity = new WeakReference<>(activity).get();
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
        public void onSuccess(String res) {
            if (weak_activity != null) {
                weak_activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        weak_activity.customAlertDialog.dismiss();
                        ToastUtils.showToast(R.string.save_success);
                        weak_activity.finish();
//                        startActivity(new Intent(CreateNewAddressActivity.this, ChooseObtainAddressActivity.class));
                    }
                });
            }

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
                        ToastUtils.showToast(R.string.save_success);
                    } else {
                        ToastUtils.showToast(res);
                    }
                }
            });
        }
    }

    /**
     * 用户输入点击事件
     *
     * @param v
     * @param event
     * @return
     */
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (v.getId()) {
            case R.id.et_obtain_person:
                rl_choose_province_city.setVisibility(View.GONE);
                break;
            case R.id.et_obtain_telephone:
                rl_choose_province_city.setVisibility(View.GONE);
                break;
            case R.id.et_obtain_address_stress:
                rl_choose_province_city.setVisibility(View.GONE);
                break;
            case R.id.et_city_area:
                hintKbTwo();
                rl_choose_province_city.setVisibility(View.VISIBLE);
                break;
        }
        return false;
    }

    /**
     * 软键盘隐藏
     */
    private void hintKbTwo() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm.isActive() && getCurrentFocus() != null) {
            if (getCurrentFocus().getWindowToken() != null) {
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    }

    private void setUpData() {
        initProvinceDatas();
        mViewProvince.setViewAdapter(new ArrayWheelAdapter<String>(CreateNewAddressActivity.this, mProvinceDatas));
        // 设置可见条目数量
        mViewProvince.setVisibleItems(7);
        mViewCity.setVisibleItems(7);
        mViewDistrict.setVisibleItems(7);
        updateCities();
        updateAreas();
    }

    @Override
    public void onChanged(WheelView wheel, int oldValue, int newValue) {
        // TODO Auto-generated method stub
        if (wheel == mViewProvince) {
            updateCities();
            updateDistrict();
        } else if (wheel == mViewCity) {
            updateAreas();
            updateDistrict();
        } else if (wheel == mViewDistrict) {
            mCurrentDistrictName = mDistrictDatasMap.get(mCurrentCityName)[newValue];
            mCurrentZipCode = mZipcodeDatasMap.get(mCurrentDistrictName);
        }
    }

    /**
     * 根据当前的市，更新区WheelView的信息
     */
    private void updateAreas() {

        int pCurrent = mViewCity.getCurrentItem();
        mCurrentCityName = mCitisDatasMap.get(mCurrentProviceName)[pCurrent];
        String[] areas = mDistrictDatasMap.get(mCurrentCityName);

        if (areas == null) {
            areas = new String[]{""};
        }
        mViewDistrict.setViewAdapter(new ArrayWheelAdapter<String>(this, areas));
        mViewDistrict.setCurrentItem(0);
    }

    /**
     * 根据当前的省，更新市WheelView的信息
     */
    private void updateCities() {
        int pCurrent = mViewProvince.getCurrentItem();
        mCurrentProviceName = mProvinceDatas[pCurrent];
        String[] cities = mCitisDatasMap.get(mCurrentProviceName);
        if (cities == null) {
            cities = new String[]{""};
        }
        mViewCity.setViewAdapter(new ArrayWheelAdapter<String>(this, cities));
        mViewCity.setCurrentItem(0);
        updateAreas();
    }
    /**
     * 更新县WheelView的信息
     */
 private  void updateDistrict () {
     int pArea = mViewDistrict.getCurrentItem();
     mCurrentDistrictName = mDistrictDatasMap.get(mCurrentCityName)[pArea];
     mCurrentZipCode = mZipcodeDatasMap.get(mCurrentDistrictName);
 }
}
