package com.bysj.bill_system.activity;


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bysj.bill_system.R;
import com.bysj.bill_system.adapter.TiebaDetailAdapter;
import com.bysj.bill_system.bean.AccountBean;
import com.bysj.bill_system.bean.ReplyBean;
import com.bysj.bill_system.bean.TiebaBean;
import com.bysj.bill_system.dialog.ReplyDialog;
import com.bysj.bill_system.listener.OnReplyClickListener;
import com.bysj.bill_system.listener.RefreshListener;
import com.bysj.bill_system.sqlite.TiebaDao;
import com.bysj.bill_system.utils.DataUtils;
import com.bysj.bill_system.utils.ToastUtils;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.OnClick;

public class TiebaDetailActivity extends BaseActivity implements OnReplyClickListener {

    @BindView(R.id.vStub)
    View vStub;
    @BindView(R.id.rlBack)
    RelativeLayout rlBack;
    @BindView(R.id.tvTitle)
    TextView tvTitle;
    @BindView(R.id.tvRight)
    TextView tvRight;
    @BindView(R.id.rvDetail)
    RecyclerView rvDetail;
    @BindView(R.id.etReview)
    EditText etReview;
    @BindView(R.id.tvConfirm)
    TextView tvConfirm;
    @BindView(R.id.rlBottom)
    RelativeLayout rlBottom;

    TiebaDetailAdapter adapter;
    TiebaBean tiebaBean;
    AccountBean accountBean;
    int id;

    @Override
    void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_tieba_detail);
    }

    @SuppressLint("WrongConstant")
    @Override
    void initData() {
        tvTitle.setText("帖子详情");
        accountBean = DataUtils.getLoginAccount(this);
        id = getIntent().getIntExtra("id", 0);
        adapter = new TiebaDetailAdapter(this, this);
        rvDetail.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        rvDetail.setAdapter(adapter);
        getData();
    }

    @OnClick({R.id.rlBack, R.id.tvConfirm})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rlBack:
                finish();
                break;
            case R.id.tvConfirm:
                if (etReview.getText().toString().isEmpty())
                    ToastUtils.showToast(this, "请填写评论后提交");
                else {
                    ReplyBean replyBean = new ReplyBean();
                    replyBean.sendTime = System.currentTimeMillis();
                    replyBean.content = etReview.getText().toString();
                    replyBean.ssid = 0;
                    replyBean.pid = tiebaBean.id;
                    replyBean.ownerPhone = accountBean.phone;
                    replyBean.owner = accountBean.nickname == null || accountBean.nickname.isEmpty() ? "用户" + accountBean.phone.substring(7, 11) : accountBean.nickname;
                    replyBean.toNamePhone = tiebaBean.phone;
                    replyBean.toName = tiebaBean.nickname;
                    TiebaDao.getInstance(this).replyTie(replyBean);
                    etReview.setText("");
                    etReview.clearFocus();
                    getData();
                }
                break;
        }
    }

    private void getData() {
        showLoadingDialog();
        new Thread(new Runnable() {
            @Override
            public void run() {
                tiebaBean = TiebaDao.getInstance(TiebaDetailActivity.this).queryDetail(id);
                synchronized (Thread.currentThread()) {
                    try {
                        Thread.currentThread().wait(300);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                List<ReplyBean> list = new ArrayList<>();
                ReplyBean replyBean = new ReplyBean();
                replyBean.tiebaBean = tiebaBean;
                list.add(replyBean);
                if (tiebaBean.replys != null)
                    list.addAll(tiebaBean.replys);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        hiddenLoadingDialog();
                        adapter.setList(list);
                    }
                });
            }
        }).start();
    }

    @Override
    public void onClick(ReplyBean replyBean) {
        new ReplyDialog(this, replyBean, accountBean, new RefreshListener() {
            @Override
            public void refresh() {
                getData();
            }
        }).show();

    }
}
