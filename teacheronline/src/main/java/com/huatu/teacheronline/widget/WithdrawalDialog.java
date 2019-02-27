package com.huatu.teacheronline.widget;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.greendao.DirectBean;
import com.huatu.teacheronline.R;
import com.huatu.teacheronline.engine.ObtainDataFromNetListener;
import com.huatu.teacheronline.engine.SendRequest;
import com.huatu.teacheronline.utils.CommonUtils;
import com.huatu.teacheronline.utils.StringUtils;
import com.huatu.teacheronline.utils.ToastUtils;
import com.huatu.teacheronline.utils.UserInfo;

import java.lang.ref.WeakReference;


/**
 * 提现弹出框
 *
 * @author ljyu
 * @date 2018-2-24 11:43:51
 */
public class WithdrawalDialog {
    private CustomAlertDialog mCustomLoadingDialog;
    private String orderId = "";
    private String ftype = "";
    private String rid = "";
    private AlertDialog alertDialog;
    private Activity context;
    private OnKeyDownClickListener onKeyDownClickListener;
    private Window window;

    private TextView tv_rmb_withdrawa;
    private TextView tv_mind_withdrawal;
    private TextView btn_comit_withdrawal;

    private EditText et_alipy_account, et_alipy_name;

    private String account = "";//账号
    private String name = "";//姓名
    private String uid = "";
    private String wechatAccount = "";//微信账户
    private DirectBean directBean;
    private RelativeLayout rl_withdrawal_money;//返回多少钱
    private RelativeLayout rl_input_withdrawal;//填写支付宝账号
    private boolean isInputAccount = false;
    private ImageView img_close_withdrawa_dialog;
    private ImageView img_clean_account;
    private ImageView img_clean_name;
    private OnWithdrawaledListener onWithdrawaledListener;

    public WithdrawalDialog(Activity context, DirectBean directBean, String rid, String orderId) {
        this.context = context;
        this.directBean = directBean;
        this.rid = directBean.getNetClassId();
        this.orderId = directBean.getOrderid();
        this.wechatAccount = directBean.getReturnaccount();
        this.ftype = directBean.getFtype();
        alertDialog = new AlertDialog.Builder(context).create();
        mCustomLoadingDialog = new CustomAlertDialog(context, R.layout.dialog_loading_custom);
        mCustomLoadingDialog.setCanceledOnTouchOutside(false);
    }

    private void initDlg() {
        uid = CommonUtils.getSharedPreferenceItem(null, UserInfo.KEY_SP_USERID, "");
        alertDialog.show();
        alertDialog.setCanceledOnTouchOutside(false);
        window = alertDialog.getWindow();
        window.setContentView(R.layout.dialog_withdrawal);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        window.setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);

        rl_withdrawal_money = (RelativeLayout) window.findViewById(R.id.rl_withdrawal_money);
        rl_withdrawal_money.setVisibility(View.VISIBLE);
        rl_input_withdrawal = (RelativeLayout) window.findViewById(R.id.rl_input_withdrawal);
        rl_input_withdrawal.setVisibility(View.GONE);
        tv_rmb_withdrawa = (TextView) window.findViewById(R.id.tv_rmb_withdrawa);
        tv_mind_withdrawal = (TextView) window.findViewById(R.id.tv_mind_withdrawal);
        tv_mind_withdrawal.setVisibility(View.GONE);
        btn_comit_withdrawal = (TextView) window.findViewById(R.id.btn_comit_withdrawal);
        img_close_withdrawa_dialog = (ImageView) window.findViewById(R.id.img_close_withdrawa_dialog);

        et_alipy_account = (EditText) window.findViewById(R.id.et_alipy_account);
        et_alipy_name = (EditText) window.findViewById(R.id.et_alipy_name);
        img_clean_account = (ImageView) window.findViewById(R.id.img_clean_account);
        img_clean_name = (ImageView) window.findViewById(R.id.img_clean_name);
        img_clean_account.setVisibility(View.GONE);
        img_clean_name.setVisibility(View.GONE);
        if (directBean == null) {
            return;
        }
        tv_rmb_withdrawa.setText(directBean.getReturncash() + "");
        if (et_alipy_account != null) {
            //下面两行代码加入后即可弹出输入法
            window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
            window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        }

        alertDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (onKeyDownClickListener != null) {
                    onKeyDownClickListener.setOnKeyListener(dialog, keyCode, event);
                }
                return false;
            }
        });
        btn_comit_withdrawal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                account = et_alipy_account.getText().toString().trim();
                name = et_alipy_name.getText().toString().trim();
                if (ftype.equals("0")||ftype.equals("3")) {
                    //是否返回支付宝账户
                    if (isInputAccount) {
                        //是否是输入支付宝账号的界面
                        if (StringUtils.isEmpty(account)) {
                            ToastUtils.showToast(context.getString(R.string.text_accpasswordnotnull));
                            return;
                        }
                        if (StringUtils.isEmpty(orderId) || StringUtils.isEmpty(rid)) {
                            ToastUtils.showToast(context.getString(R.string.text_parameter_error));
                            return;
                        }
                        if (StringUtils.isEmpty(name)) {
                            ToastUtils.showToast(context.getString(R.string.text_accpasswordnotnull));
                            return;
                        }
                        submitComments();
                    } else {
                        rl_input_withdrawal.setVisibility(View.VISIBLE);
                        rl_withdrawal_money.setVisibility(View.GONE);
                        isInputAccount = true;
                        return;
                    }
                } else {
                    //返现微信账户
                    if (StringUtils.isEmpty(wechatAccount)) {
                        ToastUtils.showToast(context.getString(R.string.text_parameter_error));
                        return;
                    }
                    if (StringUtils.isEmpty(orderId) || StringUtils.isEmpty(rid)) {
                        ToastUtils.showToast(context.getString(R.string.text_parameter_error));
                        return;
                    }
                    submitComments();
                }
            }
        });
        alertDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
                    return true;
                } else {
                    return false;
                }
            }
        });
        img_close_withdrawa_dialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        img_clean_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                et_alipy_account.setText("");
                img_clean_account.setVisibility(View.GONE);
            }
        });
        img_clean_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                et_alipy_name.setText("");
                img_clean_name.setVisibility(View.GONE);
            }
        });

    }

    /**
     * 提交
     */
    private void submitComments() {
        if (ftype.equals("0") || ftype.equals("3")) {
            if (directBean != null) {
                SendRequest.getWithdrawals(uid, orderId,rid, directBean.getLessonid(),  directBean.getReturncash() + "", ftype, account, name,
                        directBean.getTitle(),directBean.getClassTitle(), new
                        SubmitCommentObtainDataFromNetListener(context));
            }
        } else {
            if (directBean != null) {
                SendRequest.getWithdrawals(uid, orderId, rid, directBean.getLessonid(), directBean.getReturncash() + "", ftype, wechatAccount, "",
                        directBean.getTitle(),directBean.getClassTitle(),new
                        SubmitCommentObtainDataFromNetListener(context));
            }
        }
    }

    public void setDirectBean(DirectBean directBean) {
        this.directBean = directBean;
    }

    class SubmitCommentObtainDataFromNetListener extends ObtainDataFromNetListener<String, String> {

        private Activity weak_activity;

        public SubmitCommentObtainDataFromNetListener(Activity context) {
            weak_activity = new WeakReference<>(context).get();
        }

        @Override
        public void onStart() {
            super.onStart();
            if (weak_activity != null) {
                mCustomLoadingDialog.show();
            }
        }

        @Override
        public void onSuccess(final String res) {
            if (weak_activity != null) {
                mCustomLoadingDialog.dismiss();
                if (!StringUtils.isEmpty(res)) {
                    weak_activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            dismiss();
                            ToastUtils.showToast(res);
                            if (onWithdrawaledListener != null) {
                                onWithdrawaledListener.submitCompleted(res);
                            }
                        }
                    });
                } else {
                    dismiss();
                    ToastUtils.showToast(R.string.server_error);
                }
            }
        }

        @Override
        public void onFailure(final String res) {
            if (weak_activity != null) {
                mCustomLoadingDialog.dismiss();
                weak_activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (res.equals(SendRequest.ERROR_NETWORK)) {
                            ToastUtils.showToast(R.string.network);
                        } else if (res.equals(SendRequest.ERROR_SERVER)) {
                            ToastUtils.showToast(R.string.server_error);
                        } else {
                            if (ftype.equals("0")||ftype.equals("3")){
                                //支付宝
                                img_clean_account.setVisibility(View.VISIBLE);
                                img_clean_name.setVisibility(View.VISIBLE);
                                tv_mind_withdrawal.setVisibility(View.VISIBLE);
                                tv_mind_withdrawal.setText(res);
                            }else {
                                //微信直接提示
                                dismiss();
                                ToastUtils.showToast(res);
                            }

                        }
                    }
                });
            }
        }
    }

    public void setOnKeyDownClickListener(OnKeyDownClickListener onKeyDownClickListener) {
        this.onKeyDownClickListener = onKeyDownClickListener;
    }


    public void dismiss() {
        if (null != alertDialog && alertDialog.isShowing()) {
            alertDialog.dismiss();
        }
    }

    public void show() {
        if (window == null) {
            initDlg();
        } else {
            alertDialog.show();
        }
    }

    public boolean isShow() {
        return alertDialog.isShowing();
    }

    public View findViewById(int resourceID) {
        return alertDialog.findViewById(resourceID);
    }

    public void setCanceledOnTouchOutside(boolean bo) {
        if (alertDialog != null) {
            alertDialog.setCanceledOnTouchOutside(bo);
        }
    }

    public interface OnKeyDownClickListener {
        void setOnKeyListener(DialogInterface dialog, int keyCode, KeyEvent event);
    }

    public interface OnWithdrawaledListener {
        void submitCompleted(String res);
    }
    public void setOnWithdrawaledListener(OnWithdrawaledListener onWithdrawaledListener) {
        this.onWithdrawaledListener = onWithdrawaledListener;
    }

}

