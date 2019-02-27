package com.huatu.teacheronline.utils;

import android.app.Activity;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.huatu.teacheronline.CustomApplication;
import com.huatu.teacheronline.R;

/**
 * Created by ljzyuhenda on 15/12/30.
 */
public class ToastUtils {
    private static String oldMsg;
    private static int oldMsgInt;
    protected static Toast toast = null;
    protected static Toast toastImage = null;
    protected static Toast toastSign = null;
    private static long oneTime = 0;
    private static long twoTime = 0;
    private static ImageView imageView;
    private Activity activity;

    public static void showToast(String s) {
        if (toast == null) {
            toast = Toast.makeText(CustomApplication.applicationContext, s, Toast.LENGTH_SHORT);
            toast.show();
            oneTime = System.currentTimeMillis();
        } else {
            twoTime = System.currentTimeMillis();
            if (s.equals(oldMsg)) {
                if (twoTime - oneTime > Toast.LENGTH_SHORT) {
                    toast.show();
                }
            } else {
                oldMsg = s;
                toast.setText(s);
                toast.show();
            }
        }
        oneTime = twoTime;
    }




    public static void showToastLong(String s) {
        if (toast == null) {
            toast = Toast.makeText(CustomApplication.applicationContext, s, Toast.LENGTH_LONG);
            toast.show();
            oneTime = System.currentTimeMillis();
        } else {
            twoTime = System.currentTimeMillis();
            if (s.equals(oldMsg)) {
                if (twoTime - oneTime > Toast.LENGTH_SHORT) {
                    toast.show();
                }
            } else {
                oldMsg = s;
                toast.setText(s);
                toast.show();
            }
        }
        oneTime = twoTime;
    }

    public static void showToast(int res) {
        if (toast == null) {
            toast = Toast.makeText(CustomApplication.applicationContext, res, Toast.LENGTH_SHORT);
            toast.show();
            oneTime = System.currentTimeMillis();
        } else {
            twoTime = System.currentTimeMillis();
            if (CustomApplication.applicationContext.getString(res).equals(oldMsg)) {
                if (twoTime - oneTime > Toast.LENGTH_SHORT) {
                    toast.show();
                }
            } else {
                oldMsg = CustomApplication.applicationContext.getString(res);
                toast.setText(res);
                toast.show();
            }
        }

        oneTime = twoTime;
    }

    /**
     * Toast图片
     * res 图片资源
     **/
    public static void showToastImgage(int res) {

        if (imageView == null) {
            //定义一个InageView对象
            imageView = new ImageView(CustomApplication.applicationContext);
            //为ImageView对象设置上去一张图片
            imageView.setImageResource(res);
            //将ImageView对象绑定到Toast对象imageToasr上面去
        }

        if (toastImage == null) {
            toastImage = Toast.makeText(CustomApplication.applicationContext, res, Toast.LENGTH_SHORT);
            toastImage.setGravity(Gravity.CENTER, 0, 0);
            toastImage.setView(imageView);
            toastImage.show();
            oneTime = System.currentTimeMillis();
        } else {
            twoTime = System.currentTimeMillis();
            if (CustomApplication.applicationContext.getString(res).equals(oldMsg)) {
                if (twoTime - oneTime > Toast.LENGTH_SHORT) {
                    toastImage.show();
                }
            } else {
                oldMsg = CustomApplication.applicationContext.getString(res);
                toastImage.setView(imageView);
                toastImage.show();
            }
        }
        oneTime = twoTime;
    }

    /**
     * Toast签到
     **/
    public void showToastSign(int msg, Activity activity) {
        View layout = activity.getLayoutInflater().inflate(R.layout.layout_sign, null);
        TextView tv_sign = (TextView) layout.findViewById(R.id.tv_sign_continuous);
        if (toastSign == null) {
            toastSign = new Toast(activity);
            toastSign.setView(layout);
            toastSign.setGravity(Gravity.CENTER, 0, 0);
            tv_sign.setText("累计签到" + msg + "天");
            toastSign.show();
            oneTime = System.currentTimeMillis();
        } else {
            twoTime = System.currentTimeMillis();
            if (msg == oldMsgInt) {
                if (twoTime - oneTime > Toast.LENGTH_SHORT) {
                    toastSign.show();
                }
            } else {
                oldMsgInt = msg;
                toastSign.setView(layout);
                tv_sign.setText("已经连续签到" + msg + "天");
                toastSign.show();
            }
        }
        oneTime = twoTime;
    }

    /**
     * Toast手势
     **/
    public static void showToastGestures(Activity activity) {
        View layout = activity.getLayoutInflater().inflate(R.layout.layout_gestures_toast, null);
        Toast toastGestures = new Toast(activity);
        toastGestures.setView(layout);
        toastGestures.setDuration(Toast.LENGTH_SHORT);
        toastGestures.setGravity(Gravity.CENTER, 0, 0);
        toastGestures.show();
    }

    /**
     * Toast做题次数提示
     **/
    public static void showToastquency(Activity activity,int num) {
        View layout = activity.getLayoutInflater().inflate(R.layout.layout_frequency_toast, null);
        RelativeLayout rl_tost= (RelativeLayout) layout.findViewById(R.id.rl_tost);
        TextView tv_nr= (TextView) layout.findViewById(R.id.tv_neirong);
        tv_nr.setText("此套题，您已经做过"+num+"次，请给予其他卷子一次机会");
        rl_tost.getBackground().setAlpha(150);
        Toast toastGestures = new Toast(activity);
        toastGestures.setView(layout);
        toastGestures.setDuration(Toast.LENGTH_SHORT);
        toastGestures.setGravity(Gravity.CENTER, 0, 0);
        toastGestures.show();

    }

    public static void showToastExercise(Activity context,String text){
        View layout = context.getLayoutInflater().inflate(R.layout.toast_exercise, null);
        TextView title = (TextView) layout.findViewById(R.id.tv_show_toast_ex);
        title.setText(text);
        Toast toast = new Toast(context);
        toast.setGravity(Gravity.TOP, 0, CommonUtils.getScreenHeight() / 9);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layout);
        toast.show();
    }

    /**
     * Toast多选
     **/
    public static void showToastMoreChoose(Activity activity) {
        View layout = activity.getLayoutInflater().inflate(R.layout.layout_morechoose_toast, null);
        Toast toastGestures = new Toast(activity);
        toastGestures.setView(layout);
        toastGestures.setDuration(Toast.LENGTH_SHORT);
        toastGestures.setGravity(Gravity.CENTER, 0, 0);
        toastGestures.show();
    }
}
