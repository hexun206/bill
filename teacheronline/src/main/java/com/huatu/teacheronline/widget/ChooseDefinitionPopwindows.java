package com.huatu.teacheronline.widget;

import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

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
public class ChooseDefinitionPopwindows {

    private BaseActivity context;
    protected PopupWindow mWindow;
    private WindowManager mWindowManager;
    protected PopupWindow popupWindow;

    public ChooseDefinitionPopwindows(BaseActivity context) {
        this.context = context;
    }

    public void show(View pearent, View.OnClickListener listener) {
        mWindow.setWidth(DimensUtil.dpToPixels(context, 48));
        mWindow.setHeight(PercentRelativeLayout.LayoutParams.WRAP_CONTENT);
//        mWindow.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.img_drop_box));
        mWindow.setTouchable(true);//设置PopupWindow可触摸
        mWindow.setFocusable(true);//设置PopupWindow可触摸
        mWindow.setOutsideTouchable(true);
        View mRootView = context.getLayoutInflater().inflate(R.layout.layout_choose_definition, null);
        mRootView.findViewById(R.id.tv_general_definition).setOnClickListener(listener);
        mRootView.findViewById(R.id.tv_hd_definition).setOnClickListener(listener);
        //设置PopupWindow显示和隐藏时的动画
//		mWindow.setAnimationStyle(R.style.popupAnimation);
        // 设置PopupWindow外部区域是否可触摸
        mWindow.setContentView(mRootView);
        mWindow.showAsDropDown(pearent);
    }

    public void showPopUp(View v, View.OnClickListener listener) {
        View view = context.getLayoutInflater().inflate(R.layout.layout_choose_definition, null);
        popupWindow = new PopupWindow(view, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        view.findViewById(R.id.tv_general_definition).setOnClickListener(listener);
        view.findViewById(R.id.tv_hd_definition).setOnClickListener(listener);
        int popupWidth = view.getMeasuredWidth();
        int popupHeight = view.getMeasuredHeight();
        int[] location = new int[2];
        // 允许点击外部消失
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.setOutsideTouchable(true);
        popupWindow.setFocusable(true);
        // 获得位置
        v.getLocationOnScreen(location);
        popupWindow.showAtLocation(v, Gravity.NO_GRAVITY, (location[0] + v.getWidth() / 2) - popupWidth / 2, location[1] - popupHeight);
    }

    public void dissmiss() {
//        mWindow.dismiss();
        popupWindow.dismiss();
    }

}
