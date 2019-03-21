package com.huatu.teacheronline.widget;

import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.huatu.teacheronline.BaseActivity;
import com.huatu.teacheronline.R;
import com.huatu.teacheronline.utils.DimensUtil;
import com.zhy.android.percent.support.PercentRelativeLayout;


/**
 * 选择清晰度
 *
 * @author ljyu
 * @date 2016-7-8 13:26:09
 */
public class ChooseSpeedPopwindows {

    private BaseActivity context;
    protected PopupWindow mWindow;
    private WindowManager mWindowManager;
    protected PopupWindow popupWindow;
    private TextView tv_speed_original;
    private TextView tv_speed_quick1;
    private TextView tv_speed_quick2;
    private TextView tv_speed_quick3;

    public ChooseSpeedPopwindows(BaseActivity context) {
        this.context = context;
    }

    public void show(View pearent, View.OnClickListener listener) {
        mWindow.setWidth(DimensUtil.dpToPixels(context, 48));
        mWindow.setHeight(PercentRelativeLayout.LayoutParams.WRAP_CONTENT);
//        mWindow.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.img_drop_box));
        mWindow.setTouchable(true);//设置PopupWindow可触摸
        mWindow.setFocusable(true);//设置PopupWindow可触摸
        mWindow.setOutsideTouchable(true);
        View mRootView = context.getLayoutInflater().inflate(R.layout.layout_choose_speed, null);
        mRootView.findViewById(R.id.tv_speed_original).setOnClickListener(listener);
        mRootView.findViewById(R.id.tv_speed_quick1).setOnClickListener(listener);
        mRootView.findViewById(R.id.tv_speed_quick2).setOnClickListener(listener);
        mRootView.findViewById(R.id.tv_speed_quick3).setOnClickListener(listener);
        //设置PopupWindow显示和隐藏时的动画
//		mWindow.setAnimationStyle(R.style.popupAnimation);
        // 设置PopupWindow外部区域是否可触摸
        mWindow.setContentView(mRootView);
        mWindow.showAsDropDown(pearent);
    }

    /**
     * @param v
     * @param listener
     * @param speedType speedType 0：原始 1：1.25倍 2：1.5倍 3：2.0倍
     */
    public void showPopUp(View v, View.OnClickListener listener, int speedType) {
        View view = context.getLayoutInflater().inflate(R.layout.layout_choose_speed, null);

        View line1 = view.findViewById(R.id.line_speed_1);
        View line2 = view.findViewById(R.id.line_speed_2);
        View line3 = view.findViewById(R.id.line_speed_3);

        tv_speed_original = (TextView) view.findViewById(R.id.tv_speed_original);
        tv_speed_original.setOnClickListener(listener);

        tv_speed_quick1 = (TextView) view.findViewById(R.id.tv_speed_quick1);
        tv_speed_quick1.setOnClickListener(listener);

        tv_speed_quick2 = (TextView) view.findViewById(R.id.tv_speed_quick2);
        tv_speed_quick2.setOnClickListener(listener);

        tv_speed_quick3 = (TextView) view.findViewById(R.id.tv_speed_quick3);
        tv_speed_quick3.setOnClickListener(listener);

//        if (speedType == 0) {
//            tv_speed_original.setVisibility(View.GONE);
//            line1.setVisibility(View.GONE);
//        }
//        if (speedType == 1) {
//            tv_speed_quick1.setVisibility(View.GONE);
//            line2.setVisibility(View.GONE);
//        }
//        if (speedType == 2) {
//            tv_speed_quick2.setVisibility(View.GONE);
//            line3.setVisibility(View.GONE);
//        }
//        if (speedType == 3) {
//            tv_speed_quick3.setVisibility(View.GONE);
//            line3.setVisibility(View.GONE);
//        }

        popupWindow = new PopupWindow(view, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);

        int popupWidth = view.getMeasuredWidth();
        int popupHeight = view.getMeasuredHeight();
        int[] location = new int[2];
        // 允许点击外部消失
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.setOutsideTouchable(true);
        popupWindow.setFocusable(true);
        // 获得位置
        v.getLocationOnScreen(location);
//        popupWindow.showAtLocation(v, Gravity.BOTTOM,0,0);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            popupWindow.showAsDropDown(v, -popupWidth, 0, Gravity.RIGHT);
        } else {
            popupWindow.showAsDropDown(v, 0, 0);
        }

    }

    public void showInterViewPopUp(View v, View.OnClickListener listener, int speedType) {
        View view = context.getLayoutInflater().inflate(R.layout.layout_interview_choose_speed, null);


        tv_speed_original = (TextView) view.findViewById(R.id.tv_speed_original);
        tv_speed_original.setOnClickListener(listener);

        tv_speed_quick1 = (TextView) view.findViewById(R.id.tv_speed_quick1);
        tv_speed_quick1.setOnClickListener(listener);

        tv_speed_quick2 = (TextView) view.findViewById(R.id.tv_speed_quick2);
        tv_speed_quick2.setOnClickListener(listener);

        tv_speed_quick3 = (TextView) view.findViewById(R.id.tv_speed_quick3);
        tv_speed_quick3.setOnClickListener(listener);

        if (speedType == 0) {
            tv_speed_original.setVisibility(View.GONE);
        }
        if (speedType == 1) {
            tv_speed_quick1.setVisibility(View.GONE);
        }
        if (speedType == 2) {
            tv_speed_quick2.setVisibility(View.GONE);
        }
        if (speedType == 3) {
            tv_speed_quick3.setVisibility(View.GONE);
        }

        popupWindow = new PopupWindow(view, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);

        int popupWidth = view.getMeasuredWidth();
        int popupHeight = view.getMeasuredHeight();
        int[] location = new int[2];
        // 允许点击外部消失
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.setOutsideTouchable(true);
        popupWindow.setFocusable(true);
        // 获得位置
        v.getLocationOnScreen(location);
        popupWindow.showAsDropDown(v);
//        popupWindow.showAtLocation(v, Gravity.NO_GRAVITY, (location[0] + v.getWidth() / 2) - popupWidth / 2, location[1] - popupHeight);
    }

    public void dissmiss() {
//        mWindow.dismiss();
        if (popupWindow != null) {
            popupWindow.dismiss();
        }

    }

    public void setClickViewGone(View clickViewGone) {
        switch (clickViewGone.getId()) {
            case R.id.tv_speed_original:
                tv_speed_original.setVisibility(View.GONE);
                tv_speed_quick1.setVisibility(View.VISIBLE);
                tv_speed_quick2.setVisibility(View.VISIBLE);
                tv_speed_quick3.setVisibility(View.VISIBLE);
                popupWindow.getContentView().findViewById(R.id.tv_speed_original).setVisibility(View.GONE);
                break;
            case R.id.tv_speed_quick1:
                popupWindow.getContentView().findViewById(R.id.tv_speed_quick1).setVisibility(View.GONE);
                tv_speed_original.setVisibility(View.VISIBLE);
                tv_speed_quick1.setVisibility(View.GONE);
                tv_speed_quick2.setVisibility(View.VISIBLE);
                tv_speed_quick3.setVisibility(View.VISIBLE);
                break;
            case R.id.tv_speed_quick2:
                tv_speed_original.setVisibility(View.VISIBLE);
                tv_speed_quick1.setVisibility(View.VISIBLE);
                tv_speed_quick2.setVisibility(View.GONE);
                tv_speed_quick3.setVisibility(View.VISIBLE);
                break;
            case R.id.tv_speed_quick3:
                tv_speed_original.setVisibility(View.VISIBLE);
                tv_speed_quick1.setVisibility(View.VISIBLE);
                tv_speed_quick2.setVisibility(View.VISIBLE);
                tv_speed_quick3.setVisibility(View.GONE);
                break;
        }
    }
}
