package com.huatu.teacheronline.widget;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.greendao.DirectBean;
import com.huatu.teacheronline.R;
import com.huatu.teacheronline.engine.ObtainDataFromNetListener;
import com.huatu.teacheronline.engine.SendRequest;
import com.huatu.teacheronline.utils.CommonUtils;
import com.huatu.teacheronline.utils.DebugUtil;
import com.huatu.teacheronline.utils.StringUtils;
import com.huatu.teacheronline.utils.ToastUtils;
import com.huatu.teacheronline.utils.UserInfo;

import java.lang.ref.WeakReference;


/**
 * 评价老师弹出框
 *
 * @author ljyu
 * @date 2017-11-8 09:30:42
 */
public class AppraiseAlertDialog {
    private DirectBean directBean;
    private AlertDialog alertDialog;
    private Activity context;
    private OnKeyDownClickListener onKeyDownClickListener;
    private Window window;

    private TextView tv_title_appraise;
    private TextView btn_comit_appraise;


    private AppraiseRatingBar rb_teacher_main;

    private EditText edit_my_comments_appraise;
    private TextView tv_commentsnum_appraise;
    private int MAX_LENGTH = 30;
    private float ratingStar1 = 0f;//老师评分
    private String message = "";//评论
    private String uid = "";
    private OnSubmitCompletedListener onSubmitCompletedListener;//监听打分完成

    public AppraiseAlertDialog(Activity context) {
        this.context = context;
        alertDialog = new AlertDialog.Builder(context).create();
    }

    public AppraiseAlertDialog(Activity context, DirectBean directBean) {
        this.context = context;
        alertDialog = new AlertDialog.Builder(context).create();
        this.directBean = directBean;
    }

    private void initDlg() {
        if (context.isFinishing()) {
            return;
        }
        uid = CommonUtils.getSharedPreferenceItem(null, UserInfo.KEY_SP_USERID, "");
        alertDialog.show();
        alertDialog.setCanceledOnTouchOutside(false);
        window = alertDialog.getWindow();
        window.setContentView(R.layout.dialog_appraise_teacher);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        window.setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);

        tv_title_appraise = (TextView) window.findViewById(R.id.tv_title_appraise);
        rb_teacher_main = (AppraiseRatingBar) window.findViewById(R.id.rb_teacher_main);

        edit_my_comments_appraise = (EditText) window.findViewById(R.id.edit_my_comments_appraise);
        tv_commentsnum_appraise = (TextView) window.findViewById(R.id.tv_commentsnum_appraise);
        btn_comit_appraise = (TextView) window.findViewById(R.id.btn_comit_appraise);
        btn_comit_appraise.setBackgroundResource(R.drawable.bt_dfwc_f);
        btn_comit_appraise.setEnabled(false);

        tv_title_appraise.setText("主讲：" + directBean.getTeacherDesc());
        if (edit_my_comments_appraise != null) {
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
        btn_comit_appraise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ratingStar1 == 0) {
                    ToastUtils.showToast(context.getString(R.string.texts_score_uncompleted));
                    return;
                }
//                if (StringUtils.isEmpty(edit_my_comments_appraise.getText().toString().trim())) {
//                    ToastUtils.showToast(context.getString(R.string.texts_course_not_evaluated));
//                    return;
//                }
                message = edit_my_comments_appraise.getText().toString().trim();
                DebugUtil.e("message:" + message + " ratingStar1:" + ratingStar1);
                submitComments();
            }
        });
        rb_teacher_main.setOnRatingChangeListener(new AppraiseRatingBar.OnRatingChangeListener() {
            @Override
            public void onRatingChange(float ratingCount) {
                ratingStar1 = ratingCount;
                checkIsAppraise();
            }
        });

        edit_my_comments_appraise.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                String content = edit_my_comments_appraise.getText().toString();
                tv_commentsnum_appraise.setText(content.length() + "/"
                        + MAX_LENGTH);
                checkIsAppraise();
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
    }

    /**
     * 检查是否能提交
     */
    private void checkIsAppraise() {
        if (ratingStar1 != 0 ) {
            btn_comit_appraise.setEnabled(true);
            btn_comit_appraise.setBackgroundResource(R.drawable.bt_dfwc_t);
        } else {
            btn_comit_appraise.setEnabled(false);
            btn_comit_appraise.setBackgroundResource(R.drawable.bt_dfwc_f);
        }
    }

    /**
     * 提交打分
     */
    private void submitComments() {
        alertDialog.dismiss();
        if (directBean != null) {
            SendRequest.submitComments(uid, directBean.getNumber(), ratingStar1, message, new
                    SubmitCommentObtainDataFromNetListener(context));
        }
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
            }
        }

        @Override
        public void onSuccess(final String res) {
            if (weak_activity != null) {

                if (!StringUtils.isEmpty(res)) {
                    weak_activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            alertDialog.dismiss();
                            ToastUtils.showToast(res);
                            if (onSubmitCompletedListener != null) {
                                onSubmitCompletedListener.submitCompleted(res);
                            }


                        }
                    });
                } else {
                    alertDialog.dismiss();
                    ToastUtils.showToast(R.string.server_error);
                    if (onSubmitCompletedListener != null) {
                        onSubmitCompletedListener.submitCompleted(context.getResources().getString(R.string.server_error));
                    }
                }
            }
        }

        @Override
        public void onFailure(final String res) {
            if (weak_activity != null) {
                weak_activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        alertDialog.dismiss();
                        if (res.equals(SendRequest.ERROR_NETWORK)) {
                            ToastUtils.showToast(R.string.network);
                            if (onSubmitCompletedListener != null) {
                                onSubmitCompletedListener.submitCompleted(context.getResources().getString(R.string.network));
                            }
                        } else if (res.equals(SendRequest.ERROR_SERVER)) {
                            ToastUtils.showToast(R.string.server_error);
                            if (onSubmitCompletedListener != null) {
                                onSubmitCompletedListener.submitCompleted(context.getResources().getString(R.string.server_error));
                            }
                        } else {
                            if (onSubmitCompletedListener != null) {
                                onSubmitCompletedListener.submitCompleted(res);
                            }
                            ToastUtils.showToast(res);
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
        if (null != alertDialog && alertDialog.isShowing() && !context.isFinishing()) {
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

    public interface OnSubmitCompletedListener {
        void submitCompleted(String res);
    }

    public void setOnSubmitCompletedListener(OnSubmitCompletedListener onSubmitCompletedListener) {
        this.onSubmitCompletedListener = onSubmitCompletedListener;
    }

}

