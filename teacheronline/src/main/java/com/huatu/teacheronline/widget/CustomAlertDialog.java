package com.huatu.teacheronline.widget;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.Html;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.huatu.teacheronline.R;
import com.huatu.teacheronline.utils.CommonUtils;


/**
 * Created by ply on 2015/7/8.
 */
public class CustomAlertDialog {
    private Activity context;
    public TextView tv_content;
    private TextView ok;
    private TextView cancel;
    private AlertDialog alertDialog;
    private int resourceID;
    private EditText ed_context;
    private View.OnClickListener okOnClickListener;
    private View.OnClickListener cancelOnClickListener;
    private String title;
    private Window window;
    private TextView tv_update_percent;
    private View view_progress;
    private TextView tv_update_info;
    private OnKeyDownClickListener onKeyDownClickListener;

    public CustomAlertDialog(Activity context, int resourceID) {
        this.resourceID = resourceID;
        this.context = context;
        alertDialog = new AlertDialog.Builder(context).create();
    }

    private void initDlg() {
        if (context.isFinishing()) {
            return;
        }
        alertDialog.show();
        window = alertDialog.getWindow();
        // *** 主要就是在这里实现这种效果的.
        // 设置窗口的内容页面,item_mainactivity_dialog.xml文件中定义view内容
        window.setContentView(resourceID);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        window.setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);

        //设置title
        tv_content = (TextView) window.findViewById(R.id.tv_dialog_content);
        ed_context = (EditText) window.findViewById(R.id.ed_password);
        ok = (TextView) window.findViewById(R.id.tv_dialog_ok);
        // 关闭alert对话框架
        cancel = (TextView) window.findViewById(R.id.tv_dialog_cancel);
        if (ed_context != null) {
            //下面两行代码加入后即可弹出输入法
            window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
            window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        }

        tv_update_percent = (TextView) window.findViewById(R.id.tv_update_percent);
        view_progress = window.findViewById(R.id.view_progress);
        tv_update_info = (TextView) window.findViewById(R.id.tv_update_info);
        alertDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if(onKeyDownClickListener != null){
                    onKeyDownClickListener.setOnKeyListener(dialog,keyCode, event);
                }
                return false;
            }
        });
    }

    public void setOkOnClickListener(View.OnClickListener okOnClickListener) {
        this.okOnClickListener = okOnClickListener;

        if (ok != null && okOnClickListener != null) {
            ok.setOnClickListener(okOnClickListener);
        }
    }

    public void setCancelOnClickListener(View.OnClickListener cancelOnClickListener) {
        this.cancelOnClickListener = cancelOnClickListener;

        if (cancel != null && cancelOnClickListener != null) {
            cancel.setOnClickListener(cancelOnClickListener);
        }
    }
    public void setOnKeyDownClickListener(OnKeyDownClickListener onKeyDownClickListener) {
        this.onKeyDownClickListener = onKeyDownClickListener;
    }

    public void setTitle(String title) {
        this.title = title;
        if (tv_content != null && title != null) {
            tv_content.setText(Html.fromHtml(title));
        }
    }
    public void setCancelGone() {
        if (cancel != null ) {
            cancel.setVisibility(View.GONE);
        }
    }

    public String getEditContext() {
        return ed_context.getText().toString().replace(" ", "");
    }
    public EditText getEditText() {
        if(ed_context != null){
            return ed_context;
        }else {
            return null;
        }
    }

    public void setCancelable(boolean isCancelable) {
        alertDialog.setCancelable(isCancelable);
    }

    public void dismiss() {
        if (null != alertDialog && alertDialog.isShowing()&& !context.isFinishing()) {
            alertDialog.dismiss();
        }
    }

    public void show() {
        if (window == null) {
            initDlg();

            if (ok != null && okOnClickListener != null) {
                ok.setOnClickListener(okOnClickListener);
            }

            if (cancel != null && cancelOnClickListener != null) {
                cancel.setOnClickListener(cancelOnClickListener);
            }

            if (tv_content != null && title != null) {
                tv_content.setText(Html.fromHtml(title));
            }
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

    public void setProgress(long bytesWritten, long totalSize) {
        int percent;
        if (totalSize == 0) {
            percent = 0;
        } else {
            percent = (int) (bytesWritten * 100 / totalSize);
        }

        tv_update_info.setText(bytesWritten + "/" + totalSize);
        tv_update_percent.setText(percent + "%");
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams((int) (CommonUtils.getScreenWidth() * 0.7694 * percent / 100),
                (int) (CommonUtils.getScreenWidth() * 0.0222));
        view_progress.setLayoutParams(params);
    }

    public void setTag(Object objects) {
        ok.setTag(objects);
    }
    public void setCanceledOnTouchOutside(boolean bo) {
        if(alertDialog != null){
            alertDialog.setCanceledOnTouchOutside(bo);
        }
    }

    public Object getTag() {
        return (Object) ok.getTag();
    }

    public interface OnKeyDownClickListener{
        void setOnKeyListener(DialogInterface dialog, int keyCode, KeyEvent event);
    }

    public void setCancelText(String text){
        cancel.setText(text);
    }
    public void setOkText(String text){
        ok.setText(text);
    }
}

