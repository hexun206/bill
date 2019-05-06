package com.bysj.bill_system.utils;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

public class ToastUtils {
    private static Toast mToast = null;

    /**
     * 显示一个toast提示
     *
     * @param resouceId toast字符串资源id
     */
    public static void showToast(Context context, int resouceId) {
        showToast(context, context.getResources().getString(resouceId));
    }

    /**
     * 显示一个toast提示
     *
     * @param text toast字符串
     */
    public static void showToast(Context context, String text) {
        showToast(context, text, Toast.LENGTH_SHORT);
    }


    /**
     * 显示一个toast提示
     *
     * @param context  context 上下文对象
     * @param text     toast字符串
     * @param duration toast显示时间
     */
    public static void showToast(final Context context, final String text, final int duration) {
        /**
         * 保证运行在主线程
         */
        ((Activity) context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mToast == null) {
                    mToast = Toast.makeText(context, text, duration);
                } else {
                    mToast.setText(text);
                    mToast.setDuration(duration);
                }
                mToast.show();
            }
        });
    }
}
