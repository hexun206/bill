package com.huatu.teacheronline.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.widget.TextView;

import com.huatu.teacheronline.BaseActivity;
import com.huatu.teacheronline.R;
import com.huatu.teacheronline.bean.PersonalInfoBean;
import com.huatu.teacheronline.engine.ObtainDataFromNetListener;
import com.huatu.teacheronline.engine.SendRequest;

/**
 * 华图教师工具帮助类
 * Created by ljyu on 2016/10/13.
 */
public class TeacherOnlineUtils {

    private Context mContext;
    private ObtainDataLister obtatinDataListener;
    private String uid;
    private static final String TAG= "TeacherOnlineUtils";

    public static void setTextViewRightImage(Context context,TextView view,int drawable){
        Drawable img = context.getResources().getDrawable(drawable);
        // 调用setCompoundDrawables时，必须调用Drawable.setBounds()方法,否则图片不显示
        img.setBounds(0, 0, img.getMinimumWidth(), img.getMinimumHeight());
        view.setCompoundDrawables(null, null, img, null); //设置右图标
    }
    public static void setTextViewRightImage(Context context,TextView view,int drawable,int drawblepadding){
        Drawable img = context.getResources().getDrawable(drawable);
        // 调用setCompoundDrawables时，必须调用Drawable.setBounds()方法,否则图片不显示
        img.setBounds(0, 0, img.getMinimumWidth(), img.getMinimumHeight());
        view.setCompoundDrawables(null, null, img, null); //设置右图标
        view.setCompoundDrawablePadding(drawblepadding);
    }
    public static void setTextViewLeftImage(Context context,TextView view,int drawable){
        Drawable img = context.getResources().getDrawable(drawable);
        // 调用setCompoundDrawables时，必须调用Drawable.setBounds()方法,否则图片不显示
        img.setBounds(0, 0, img.getMinimumWidth(), img.getMinimumHeight());
        view.setCompoundDrawables(img, null, null, null); //设置右图标
    }
    public static void setTextViewLeftImage(Context context,TextView view,int drawable,int drawblepadding){
        Drawable img = context.getResources().getDrawable(drawable);
        // 调用setCompoundDrawables时，必须调用Drawable.setBounds()方法,否则图片不显示
        img.setBounds(0, 0, img.getMinimumWidth(), img.getMinimumHeight());
        view.setCompoundDrawables(img, null, null, null); //设置右图标
        view.setCompoundDrawablePadding(drawblepadding);
    }
    public static void setTextViewLeftRightImageNull(Context context,TextView view){
        // 调用setCompoundDrawables时，必须调用Drawable.setBounds()方法,否则图片不显示
        view.setCompoundDrawables(null, null, null, null); //设置右图标
    }

    public TeacherOnlineUtils(Context mContext){
        this.mContext = mContext;
    }

    /**
     * 获取用户信息(主要是刷新用户积分和金币)
     *
     * @param
     */
    public void loadPersonalInfo(BaseActivity activity) {
        obtatinDataListener = new ObtainDataLister(activity);
        uid = CommonUtils.getSharedPreferenceItem(null, UserInfo.KEY_SP_USERID, "");
        SendRequest.getPersonalInfo(uid, obtatinDataListener);
    }

    private class ObtainDataLister extends ObtainDataFromNetListener<PersonalInfoBean, String> {

        private BaseActivity weak_activity;

        public ObtainDataLister(BaseActivity activity) {
            this.weak_activity = activity;
        }

        @Override
        public void onSuccess(final PersonalInfoBean res) {
            if (weak_activity != null) {
                DebugUtil.e(TAG,"loadPersonalInfo:" + res.toString());
                //更新金币信息
                CommonUtils.putSharedPreferenceItem(null, UserInfo.KEY_SP_GOLD, res.getGold());
                CommonUtils.putSharedPreferenceItem(null, UserInfo.KEY_SP_POINT, res.getUserPoint());
            }
        }

        @Override
        public void onFailure(final String res) {
            if (weak_activity != null) {
                weak_activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (res.equals(SendRequest.ERROR_NETWORK)) {
                            ToastUtils.showToast(R.string.network);
                        } else if (res.equals(SendRequest.ERROR_SERVER)) {
                            ToastUtils.showToast(R.string.server_error);
                        }
                    }
                });
            }
        }
    }


}
