package com.huatu.teacheronline.widget;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.huatu.teacheronline.R;

/**
 * 提醒弹出框
 * Created by ljyu on 2016/10/24.
 */
public class MindDialog {

    public static void showMoreSubDialog(Context context){
        final Dialog dialog = new Dialog(context, R.style.mindDialog);
        dialog.setContentView(R.layout.dialog_mind_moresub);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setCancelable(true);
        dialog.show();

        Window win = dialog.getWindow();
        win.findViewById(R.id.rl_mind_dialog).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        win.setLayout(WindowManager.LayoutParams.FILL_PARENT, WindowManager.LayoutParams.FILL_PARENT);
    }
}
