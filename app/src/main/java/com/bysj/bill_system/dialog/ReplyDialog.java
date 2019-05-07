package com.bysj.bill_system.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.bysj.bill_system.R;
import com.bysj.bill_system.bean.AccountBean;
import com.bysj.bill_system.bean.ReplyBean;
import com.bysj.bill_system.listener.RefreshListener;
import com.bysj.bill_system.sqlite.TiebaDao;
import com.bysj.bill_system.utils.ToastUtils;

import androidx.annotation.NonNull;


public class ReplyDialog extends Dialog {
    Context context;
    ReplyBean replyBean;
    AccountBean accountBean;
    RefreshListener refreshListener;

    public ReplyDialog(@NonNull Context context, ReplyBean replyBean, AccountBean accountBean, RefreshListener refreshListener) {
        super(context, R.style.dialog);
        this.replyBean = replyBean;
        this.accountBean = accountBean;
        this.refreshListener = refreshListener;
        initView(context);
    }

    private void initView(Context context) {
        this.context = context;
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_reply, null, false);
        setContentView(view);
        EditText etContent = view.findViewById(R.id.etContent);
        TextView tvConfirm = view.findViewById(R.id.tvConfirm);
        tvConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (etContent.getText().toString().isEmpty()) {
                    ToastUtils.showToast(context, "请填写评论");
                } else {
                    ReplyBean bean = new ReplyBean();
                    bean.sendTime = System.currentTimeMillis();
                    bean.content = etContent.getText().toString();
                    bean.ssid = replyBean.id;
                    bean.pid = replyBean.pid;
                    bean.ownerPhone = accountBean.phone;
                    bean.owner = accountBean.nickname == null || accountBean.nickname.isEmpty() ? "用户" + accountBean.phone.substring(7, 11) : accountBean.nickname;
                    bean.toNamePhone = replyBean.ownerPhone;
                    bean.toName = replyBean.owner;
                    TiebaDao.getInstance(context).replyTie(bean);
                    dismiss();
                    if (refreshListener != null)
                        refreshListener.refresh();
                }
            }
        });
        findViewById(R.id.llBacLayout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        findViewById(R.id.llContent).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }
}
