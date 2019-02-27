package com.huatu.teacheronline.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.os.Build;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.greendao.DirectBean;
import com.huatu.teacheronline.BaseActivity;
import com.huatu.teacheronline.R;
import com.huatu.teacheronline.direct.bean.SelectAcademicBean;
import com.huatu.teacheronline.utils.CommonUtils;
import com.huatu.teacheronline.utils.StringUtils;

import java.lang.reflect.Method;
import java.util.List;


/**
 * 选择清晰度
 *
 * @author ljyu
 * @date 2016-7-8 13:26:09
 */
public class ChooseSubjectPopwindows {

    private BaseActivity context;
    protected PopupWindow mWindow;
    int pad1 = CommonUtils.dip2px(5);
    int pad2 = CommonUtils.dip2px(6);

    private String[] learning_periods, wy_subjects;//学段 学科
    public RadioGroup gv_subjects, gv_learning_period;//学段 学科

    private View view;

    public View getContentView() {
        return view;
    }

    public ChooseSubjectPopwindows(BaseActivity context) {
        this.context = context;
    }

    public void showPopWindow(View v, SelectAcademicBean selectAcademicBean, DirectBean directBean, View.OnClickListener onClickListener, RadioGroup.OnCheckedChangeListener OnCheckedChangeListener) {
        Button btItem1, btItem2, btItem3;
        view = LayoutInflater.from(context).inflate(R.layout.popwindow_choose_subject_layout, null);
        //设置屏幕的高度和宽度
        mWindow = new PopupWindow(view, CommonUtils.getScreenWidth(), LinearLayout.LayoutParams.WRAP_CONTENT);
        //如果不设置背景颜色的话，无法是pop dimiss掉。
        mWindow.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.popupwindow_background));
        mWindow.setOutsideTouchable(true);
        view.setClickable(true);
        mWindow.setAnimationStyle(R.style.choose_sub_popupwindow_anim_style);
        TextView tv_direct_title_choose_sub = (TextView)view.findViewById(R.id.tv_direct_title_choose_sub);
        LinearLayout ll_learning_period_choose_sub = (LinearLayout)view.findViewById(R.id.ll_learning_period_choose_sub);
        LinearLayout ll_subjects_choose_sub = (LinearLayout)view.findViewById(R.id.ll_subjects_choose_sub);
        TextView tv_direct_time_choose_sub = (TextView)view.findViewById(R.id.tv_direct_time_choose_sub);
        ImageView img_close_choose_sub = (ImageView)view.findViewById(R.id.img_close_choose_sub);
        Button bt_yes_choose_sub = (Button)view.findViewById(R.id.bt_yes_choose_sub);
        learning_periods = context.getResources().getStringArray(R.array.learning_period);
        wy_subjects = context.getResources().getStringArray(R.array.wh_subjects);
        gv_learning_period = (RadioGroup) view.findViewById(R.id.gv_learning_period_choose_sub);
        gv_subjects = (RadioGroup) view.findViewById(R.id.gv_subjects_choose_sub);
        tv_direct_title_choose_sub.setText(directBean.getTitle());
        tv_direct_time_choose_sub.setText(directBean.getTitle());
        if (selectAcademicBean.getStudySection().size()>0) {
            ll_learning_period_choose_sub.setVisibility(View.VISIBLE);
            iniSelectData(gv_learning_period, "", selectAcademicBean.getStudySection());
        }
        if (selectAcademicBean.getSubject().size()>0) {
            ll_subjects_choose_sub.setVisibility(View.VISIBLE);
            iniSelectData(gv_subjects,"",selectAcademicBean.getSubject());
        }

        gv_learning_period.setOnCheckedChangeListener(OnCheckedChangeListener);
        gv_subjects.setOnCheckedChangeListener(OnCheckedChangeListener);

        if (StringUtils.isEmpty(directBean.getHd())) {
            tv_direct_time_choose_sub.setText(directBean.getZhibotime() + "-" + directBean.getZhiboendtime() + "(" + directBean.getLessionCount() + "课时" + ")");
        }else {
            String textStr = "<font color=\"#00b38a\">" + directBean.getHd() + "</font>"+"<font color=\"#999999\">"+ "   (" + directBean.getLessionCount() + "课时" + ")" + "</font>";
            tv_direct_time_choose_sub.setText(textStr);
        }
        img_close_choose_sub.setOnClickListener(onClickListener);
        bt_yes_choose_sub.setOnClickListener(onClickListener);
/** * 设置popwindow的弹出的位置. *
 1：首先要判断是否有navigation bar。如果有的的话，要把他们的高度给加起来。 * *
 2：showAtLocation（）；是pop相对于屏幕而言的。 * *
 3：如果是 pop.showAsDropDown();则是相对于你要点击的view的位置。设置的坐标。
 */
//        if (checkDeviceHasNavigationBar2(context)) {
//            int heigth_tobottom = 100+getNavigationBarHeight();
//            mWindow.showAtLocation(v, Gravity.BOTTOM, 0, getNavigationBarHeight());
//        } else {
            mWindow.showAtLocation(v, Gravity.BOTTOM, 0, 0);
//        }
//设置 背景的颜色为 0.5f 的透明度
        backgroundAlpha(0.5f);
        mWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                //当popwindow消失的时候，恢复背景的颜色。
                backgroundAlpha(1.0f);
            }

        });
    }

    /**
     * /获取是否存在虚拟按键 NavigationBar：如果是有就返回true,如果是没有就是返回的false。第二种方法
     */
    private static boolean checkDeviceHasNavigationBar2(Context context) {
        boolean hasNavigationBar = false;
        Resources rs = context.getResources();
        int id = rs.getIdentifier("config_showNavigationBar", "bool", "android");
        if (id > 0) {
            hasNavigationBar = rs.getBoolean(id);
        }
        try {
            Class systemPropertiesClass = Class.forName("android.os.SystemProperties");
            Method m = systemPropertiesClass.getMethod("get", String.class);
            String navBarOverride = (String) m.invoke(systemPropertiesClass, "qemu.hw.mainkeys");
            if ("1".equals(navBarOverride)) {
                hasNavigationBar = false;
            } else if ("0".equals(navBarOverride)) {
                hasNavigationBar = true;
            }
        } catch (Exception e) {
        }
        return hasNavigationBar;
    }


    /**
     * 获取navigationbar的高度。
     */
    private int getNavigationBarHeight() {
        Resources resources = context.getResources();
        int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
        int height = resources.getDimensionPixelSize(resourceId);
        return height;
    }

    /**
     * 设置添加屏幕的背景透明度
     *
     * @param bgAlpha
     */
    public void backgroundAlpha(float bgAlpha) {
        WindowManager.LayoutParams lp = context.getWindow().getAttributes();
        lp.alpha = bgAlpha; //0.0-1.0
        context.getWindow().setAttributes(lp);
    }

    /**
     * 筛选数据适配
     **/
    public void iniSelectData(RadioGroup contentRg, String selctorText, List<String> datas) {
        contentRg.removeAllViews();
        for (int i = 0; i < datas.size(); i++) {
            RadioButton tempButton = new RadioButton(context);
//            ColorStateList colorStateList = context.getColorStateList(R.color.text_check_staus);
//            ColorStateList.valueOf(context.getResources().getColor(R.color.text_check_staus));
            tempButton.setTextColor(context.getResources().getColor(R.color.text_check_staus));
            tempButton.setBackgroundResource(R.drawable.bg_select_gv);   // 设置RadioButton的背景图片
            tempButton.setButtonDrawable(android.R.color.transparent);
            tempButton.setTextSize(12f);
//            tempButton.setButtonDrawable(R.drawable.xxx);           // 设置按钮的样式
//            tempButton.setPadding(pad2, pad1, pad2, pad1);                 // 设置文字距离按钮四周的距离

            RadioGroup.LayoutParams lp = new RadioGroup.LayoutParams(RadioGroup.LayoutParams.WRAP_CONTENT, RadioGroup.LayoutParams.WRAP_CONTENT);
            lp.setMargins(pad2, pad1, pad2, pad1);
            String data = datas.get(i);
            if (datas.get(i).length() == 2 || data.length() == 3 || data.length() == 4) {
                lp.width = CommonUtils.dip2px(75);
                lp.height = CommonUtils.dip2px(36);
            } else if (data.length() == 5 || data.length() == 6) {
                lp.width = CommonUtils.dip2px(100);
                lp.height = CommonUtils.dip2px(36);
            } else if (data.length() > 5) {
                lp.width = CommonUtils.dip2px(120);
                lp.height = CommonUtils.dip2px(36);
            }
            tempButton.setGravity(Gravity.CENTER);
            tempButton.setLayoutParams(lp);
            tempButton.setText(data);
            contentRg.addView(tempButton);
            if (data.equals(selctorText)) {
                contentRg.check(tempButton.getId());
            }
        }
    }

    public void dissmiss() {
        mWindow.dismiss();
    }
}