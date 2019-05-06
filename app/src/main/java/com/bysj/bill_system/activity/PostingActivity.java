package com.bysj.bill_system.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.bysj.bill_system.R;
import com.bysj.bill_system.bean.AccountBean;
import com.bysj.bill_system.bean.TiebaBean;
import com.bysj.bill_system.config.Config;
import com.bysj.bill_system.sqlite.TiebaDao;
import com.bysj.bill_system.utils.DataUtils;
import com.bysj.bill_system.utils.ToastUtils;

import butterknife.BindView;
import butterknife.OnClick;

public class PostingActivity extends BaseActivity {

    @BindView(R.id.vStub)
    View vStub;
    @BindView(R.id.rlBack)
    RelativeLayout rlBack;
    @BindView(R.id.tvTitle)
    TextView tvTitle;
    @BindView(R.id.tvRight)
    TextView tvRight;
    @BindView(R.id.etTitle)
    EditText etTitle;
    @BindView(R.id.sType)
    Spinner sType;
    @BindView(R.id.etContent)
    EditText etContent;

    ArrayAdapter arr_adapter;
    TiebaBean tiebaBean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_posting);
    }

    @Override
    void initData() {
        tvTitle.setText("写帖子");
        tvRight.setText("发帖");
        tiebaBean = new TiebaBean();
        AccountBean loginAccount = DataUtils.getLoginAccount(this);
        tiebaBean.headUrl = loginAccount.headUrl;
        tiebaBean.nickname = loginAccount.nickname == null || loginAccount.nickname.isEmpty() ? "用户" + loginAccount.phone.substring(7, 11) : loginAccount.nickname;
        tiebaBean.phone = loginAccount.phone;
        initSpinner();
    }

    private void initSpinner() {
        //适配器
        arr_adapter = new ArrayAdapter<String>(this, R.layout.spinner_item, Config.TIEBA_TYPE);
        //设置样式
        arr_adapter.setDropDownViewResource(R.layout.spinner_item);
        sType.setAdapter(arr_adapter);
        sType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                tiebaBean.style = arr_adapter.getItem(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        tiebaBean.style = arr_adapter.getItem(0).toString();
    }

    @OnClick({R.id.rlBack, R.id.tvRight})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rlBack:
                finish();
                break;
            case R.id.tvRight:
                if (etTitle.getText().toString().isEmpty()) {
                    ToastUtils.showToast(this, "请输入标题");
                    return;
                }
                if (etContent.getText().toString().isEmpty()) {
                    ToastUtils.showToast(this, "请输入内容");
                    return;
                }
                tiebaBean.title = etTitle.getText().toString();
                tiebaBean.content = etContent.getText().toString();
                tiebaBean.time = System.currentTimeMillis();
                TiebaDao.getInstance(this).createTie(tiebaBean);
                finish();
                break;
        }
    }
}
