package com.bysj.bill_system.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bysj.bill_system.R;
import com.bysj.bill_system.bean.AccountBean;
import com.bysj.bill_system.utils.DataUtils;
import com.bysj.bill_system.utils.ToastUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RegisterActivity extends BaseActivity {

    @BindView(R.id.etPhone)
    EditText etPhone;
    @BindView(R.id.etPassword)
    EditText etPassword;
    @BindView(R.id.etPasswordAgain)
    EditText etPasswordAgain;

    List<AccountBean> accountBeanList;
    String pageTitle;
    @BindView(R.id.tl_title_back)
    RelativeLayout tlTitleBack;
    @BindView(R.id.tvTitle)
    TextView tvTitle;
    @BindView(R.id.btnLogin)
    TextView btnLogin;

    @Override
    void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_register);
    }

    @Override
    void initData() {
        accountBeanList = DataUtils.getUserData(this);
        pageTitle = getIntent().getStringExtra("title");
        if (pageTitle == null)
            pageTitle = "注册";
        else {
            tvTitle.setText(pageTitle);
            btnLogin.setText(pageTitle);
        }
    }

    @OnClick({R.id.tl_title_back, R.id.btnLogin})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tl_title_back:
                finish();
                break;
            case R.id.btnLogin:
                if (etPhone.getText().length() < 11) {
                    ToastUtils.showToast(this, "请输入11位手机号码");
                    return;
                }
                if (etPassword.getText().length() < 6) {
                    ToastUtils.showToast(this, "请输入至少六位密码");
                    return;
                }
                if (!etPassword.getText().toString().equals(etPasswordAgain.getText().toString())) {
                    ToastUtils.showToast(this, "两次输入密码不相同");
                    return;
                }
                ToastUtils.showToast(this, pageTitle + "成功");
                DataUtils.addUserData(this, new AccountBean(etPhone.getText().toString(), etPassword.getText().toString()));
                finish();
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }
}
