package com.huatu.teacheronline.paymethod;

import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.huatu.teacheronline.BaseActivity;
import com.huatu.teacheronline.R;
import com.huatu.teacheronline.engine.ObtainDataFromNetListener;
import com.huatu.teacheronline.engine.SendRequest;
import com.huatu.teacheronline.paymethod.adapter.ChooseObtainAddressAdapter;
import com.huatu.teacheronline.paymethod.bean.PersonalAddressBean;
import com.huatu.teacheronline.utils.CommonUtils;
import com.huatu.teacheronline.utils.UserInfo;
import com.huatu.teacheronline.widget.CustomAlertDialog;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * 类说明：选择收货地址
 *
 * @author wangf
 * @date 2016年5月23日16:19:21
 **/
public class ChooseObtainAddressActivity extends BaseActivity implements AdapterView.OnItemClickListener {
    private TextView tv_main_title;
    private TextView tv_main_right;
    private ListView lv_obtainaddress;
    private ImageView ib_main_left;
    private ChooseObtainAddressAdapter chooseObtainAddressAdapter;
    private List<PersonalAddressBean> personalAddressBeans = new ArrayList<>();
    private RelativeLayout iv_no_address;
    private TextView tv_no_address;
    private RelativeLayout rl_main_left;
    private RelativeLayout rl_main_right;
    public CustomAlertDialog customAlertDialog;
    private RelativeLayout rl_add_address;

//    private SwipeRefreshLayout mPullToRefreshLayout;

    @Override
    public void initView() {

        setContentView(R.layout.activity_choose_obtain_address);
        tv_main_title = (TextView) findViewById(R.id.tv_main_title);
        tv_main_title.setText("收货地址管理");
        tv_main_right = (TextView) findViewById(R.id.tv_main_right);
        tv_main_right.setText("新建");
        tv_main_right.setVisibility(View.GONE);
        ib_main_left = (ImageView) findViewById(R.id.ib_main_left);
        ib_main_left.setImageResource(R.drawable.back_arrow);
        rl_main_left = (RelativeLayout) findViewById(R.id.rl_main_left);
        rl_main_right = (RelativeLayout) findViewById(R.id.rl_main_right);
        iv_no_address = (RelativeLayout) findViewById(R.id.iv_no_address);
        lv_obtainaddress = (ListView) findViewById(R.id.lv_obtainaddress);
        tv_no_address = (TextView) findViewById(R.id.tv_no_address);
        rl_add_address = (RelativeLayout) findViewById(R.id.rl_add_address);
        rl_add_address.setOnClickListener(this);
//        mPullToRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.live_refresh_layout);
        chooseObtainAddressAdapter = new ChooseObtainAddressAdapter(this);
        chooseObtainAddressAdapter.setPersonalAddressBeanList(personalAddressBeans);
        lv_obtainaddress.setAdapter(chooseObtainAddressAdapter);
        lv_obtainaddress.setOnItemClickListener(this);
        setListener();


    }

    public void loadAddressInfomation() {
        ObtainDataFromNetListenerShowAddress obtainDataFromNetListenerShowAddress = new ObtainDataFromNetListenerShowAddress(this);
        SendRequest.showAddress(CommonUtils.getSharedPreferenceItem(null, UserInfo.KEY_SP_USERID, null), obtainDataFromNetListenerShowAddress);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {
            case R.id.lv_obtainaddress:
                Intent intent = new Intent(ChooseObtainAddressActivity.this, ChoosePayMethodActivity.class);
                intent.putExtra("addressId", personalAddressBeans.get(position).getId());
                intent.putExtra("personName", personalAddressBeans.get(position).getName());
                intent.putExtra("tel", personalAddressBeans.get(position).getTel());
                intent.putExtra("province", personalAddressBeans.get(position).getProvince());
                intent.putExtra("addressStreet", personalAddressBeans.get(position).getAddress());
                setResult(RESULT_OK, intent);
                ChooseObtainAddressActivity.this.finish();
                break;
        }


    }


    private static class ObtainDataFromNetListenerShowAddress extends ObtainDataFromNetListener<List<PersonalAddressBean>, String> {

        private ChooseObtainAddressActivity weak_activity;

        public ObtainDataFromNetListenerShowAddress(ChooseObtainAddressActivity activity) {
            weak_activity = new WeakReference<>(activity).get();
        }

        @Override
        public void onStart() {
            super.onStart();
            if (weak_activity != null) {
                weak_activity.lv_obtainaddress.setVisibility(View.VISIBLE);
                weak_activity.iv_no_address.setVisibility(View.GONE);
                weak_activity.tv_no_address.setVisibility(View.GONE);
            }
        }

        @Override
        public void onSuccess(final List<PersonalAddressBean> res) {
            if (weak_activity != null) {
                weak_activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        weak_activity.flushContent_OnSucess(res);
                    }
                });
            }
        }


        @Override
        public void onFailure(String res) {
            if (weak_activity != null) {
                weak_activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        weak_activity.lv_obtainaddress.setVisibility(View.GONE);
                        weak_activity.iv_no_address.setVisibility(View.VISIBLE);
                        weak_activity.tv_no_address.setVisibility(View.VISIBLE);
                    }
                });
            }


        }
    }


    @Override
    public void setListener() {
        ib_main_left.setOnClickListener(this);
        tv_main_right.setOnClickListener(this);
        rl_main_left.setOnClickListener(this);
        rl_main_right.setOnClickListener(this);
        rl_add_address.setOnClickListener(this);
//        mPullToRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//                personalAddressBeans.clear();
//                chooseObtainAddressAdapter.setPersonalAddressBeanList(personalAddressBeans);
//                chooseObtainAddressAdapter.notifyDataSetChanged();
//                loadAddressInfomation();
//                mPullToRefreshLayout.setRefreshing(false);
//            }
//        });
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.rl_main_left:
            case R.id.ib_main_left:
                finish();
                break;
            case R.id.rl_add_address:
                Intent intent = new Intent(ChooseObtainAddressActivity.this, CreateNewAddressActivity.class);
                intent.setFlags(1);
                startActivity(intent);
                break;
            case R.id.tv_main_right:
//                Intent intent = new Intent(ChooseObtainAddressActivity.this, CreateNewAddressActivity.class);
//                intent.setFlags(1);
//                startActivity(intent);
                break;
        }
    }

    public void flushContent_OnSucess(List<PersonalAddressBean> res) {
        if ((res == null || res.size() == 0)) {
//            ToastUtils.showToast(R.string.no_data);
            iv_no_address.setVisibility(View.VISIBLE);
            tv_no_address.setVisibility(View.VISIBLE);
            lv_obtainaddress.setVisibility(View.GONE);
        } else {
            personalAddressBeans.clear();
            iv_no_address.setVisibility(View.GONE);
            tv_no_address.setVisibility(View.GONE);
            tv_main_right.setText("添加");
            lv_obtainaddress.setVisibility(View.VISIBLE);
            personalAddressBeans.addAll(res);
            chooseObtainAddressAdapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadAddressInfomation();
//        chooseObtainAddressAdapter.notifyDataSetChanged();
    }
}
