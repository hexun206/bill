package com.huatu.teacheronline.utils;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

public class DebugUtil {
    public static final String TAG = "DebugUtil";
    public static final boolean DEBUG = false;

    public static void toast(Context context, String content) {
        Toast.makeText(context, content, Toast.LENGTH_SHORT).show();
    }

    public static void d(String tag, String msg) {
        if (DEBUG && !TextUtils.isEmpty(msg)) {
            Log.d(tag, msg);
        }
    }

    public static void d(String msg) {
        if (DEBUG && !TextUtils.isEmpty(msg)) {
            Log.d(TAG, msg);
        }
    }

    public static void e(String tag, String error) {
        if (DEBUG && !TextUtils.isEmpty(error)) {
            Log.e(tag, error);
        }
    }

    public static void e(String error) {
        if (DEBUG && !TextUtils.isEmpty(error)) {
            Log.e(TAG, error);
        }
    }

    public static void i(String tag, String msg) {
        if (DEBUG && !TextUtils.isEmpty(msg)) {
            Log.i(tag, msg);
        }
    }

    public static void i(String msg) {
        if (DEBUG && !TextUtils.isEmpty(msg)) {
            Log.i(TAG, msg);
        }
    }
}
