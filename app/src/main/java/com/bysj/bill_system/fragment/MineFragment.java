package com.bysj.bill_system.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bysj.bill_system.R;
import com.bysj.bill_system.activity.CalendarActivity;
import com.bysj.bill_system.activity.LoginActivity;
import com.bysj.bill_system.activity.PersonalSettingActivity;
import com.bysj.bill_system.activity.RegisterActivity;
import com.bysj.bill_system.activity.WebActivity;
import com.bysj.bill_system.utils.DataUtils;

import androidx.annotation.Nullable;
import butterknife.BindView;
import butterknife.OnClick;

public class MineFragment extends BaseFragment {
    @BindView(R.id.vStub)
    View vStub;
    @BindView(R.id.rlAccountInfo)
    RelativeLayout rlAccountInfo;
    @BindView(R.id.rlCalendar)
    RelativeLayout rlCalendar;
    @BindView(R.id.rlCourier)
    RelativeLayout rlCourier;
    @BindView(R.id.rlChangePassword)
    RelativeLayout rlChangePassword;
    @BindView(R.id.tvConfirm)
    TextView tvConfirm;
    @BindView(R.id.ivHead)
    ImageView ivHead;

    @Override
    public int getLayoutId() {
        return R.layout.fragment_mine_layout;
    }

    @Override
    public void initUI(View view, @Nullable Bundle savedInstanceState) {

    }

    @OnClick({R.id.rlAccountInfo, R.id.rlCalendar, R.id.rlCourier, R.id.rlChangePassword, R.id.tvConfirm})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rlAccountInfo:
                startActivity(new Intent(getActivity(), PersonalSettingActivity.class));
                break;
            case R.id.rlCalendar:
                startActivity(new Intent(getActivity(), CalendarActivity.class));
                break;
            case R.id.rlCourier:
                startActivity(new Intent(getActivity(), WebActivity.class).putExtra("title", "快递查询").putExtra("url", "http://www.kuaidi.com/"));
                break;
            case R.id.rlChangePassword:
                startActivity(new Intent(getActivity(), RegisterActivity.class).putExtra("title", "修改密码"));
                break;
            case R.id.tvConfirm:
                showLoadingDialog();
                DataUtils.outLoginAccount(getActivity());
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        hiddenLoadingDialog();
                        startActivity(new Intent(getActivity(), LoginActivity.class));
                        getActivity().finish();
                    }
                }, 500);
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Glide.with(this).load(DataUtils.getLoginAccount(getActivity()).headUrl).apply(new RequestOptions().circleCrop().error(R.mipmap.ic_header)).into(ivHead);
    }
}
