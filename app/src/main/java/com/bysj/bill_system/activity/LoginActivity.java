package com.bysj.bill_system.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.bysj.bill_system.R;
import com.bysj.bill_system.bean.AccountBean;
import com.bysj.bill_system.utils.DataUtils;
import com.bysj.bill_system.utils.ToastUtils;

import butterknife.BindView;
import butterknife.OnClick;

public class LoginActivity extends BaseActivity {

    @BindView(R.id.etPhone)
    EditText etPhone;
    @BindView(R.id.etPassword)
    EditText etPassword;

    @Override
    void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_login);
    }

    @Override
    void initData() {
        //二次登录处理 也就是在用户当前账号登录后 下次自动登录
        AccountBean loginAccount = DataUtils.getLoginAccount(this);
        if (loginAccount != null && loginAccount.phone != null && !loginAccount.phone.isEmpty())
            if (DataUtils.checkUserData(this, loginAccount)) {
                startActivity(new Intent(this, MainActivity.class));
                finish();
            }

    }

    @OnClick({R.id.btnLogin, R.id.btnCodeLogin})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btnLogin:
                if (etPhone.getText().length() < 11) {
                    ToastUtils.showToast(this, "请输入11位手机号码");
                    return;
                }
                if (etPassword.getText().length() < 6) {
                    ToastUtils.showToast(this, "请输入至少六位密码");
                    return;
                }
                AccountBean accountBean = new AccountBean(etPhone.getText().toString(), etPassword.getText().toString());
                if (DataUtils.checkUserData(this, accountBean)) {
                    startActivity(new Intent(this, MainActivity.class));
                    DataUtils.saveLoginAccount(this, accountBean);
                    finish();
                }
                break;
            case R.id.btnCodeLogin:
                startActivity(new Intent(this, RegisterActivity.class));
                break;
        }
    }

}
