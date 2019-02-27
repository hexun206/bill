package com.huatu.teacheronline.widget;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.huatu.teacheronline.R;
import com.huatu.teacheronline.utils.CommonUtils;
import com.huatu.teacheronline.utils.TeacherOnlineUtils;

/**
 * Created by 18250 on 2016/10/11.
 */
public class PopwindowDoExercise {
    private View view_one;
    private TextView tv_collection_exercise;
    private TextView tv_correction_exercise;
    private TextView tv_share_exercise;
    private PopupWindow window;
    private Context context;

    int maxSize = 0;
    private String[] stringData;

    public PopwindowDoExercise(Context context, View.OnClickListener OnClickListener) {
        window = new PopupWindow(context);
        this.context = context;
        window.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        window.setWidth(WindowManager.LayoutParams.WRAP_CONTENT);
        window.setFocusable(true);
        //点击 back 键的时候，窗口会自动消失
        window.setBackgroundDrawable(new BitmapDrawable());
        View localView = LayoutInflater.from(context).inflate(R.layout.pw_do_exercise, null);
        tv_collection_exercise = (TextView)localView.findViewById(R.id.tv_collection_exercise);
        tv_correction_exercise = (TextView)localView.findViewById(R.id.tv_correction_exercise);
        tv_share_exercise = (TextView)localView.findViewById(R.id.tv_share_exercise);
        view_one = localView.findViewById(R.id.view_one);
        tv_collection_exercise.setOnClickListener(OnClickListener);
        tv_correction_exercise.setOnClickListener(OnClickListener);
        tv_share_exercise.setOnClickListener(OnClickListener);
        //设置显示的视图
        window.setContentView(localView);
    }

    public void dismiss() {
        window.dismiss();
    }

    /**
     *
     * @param type 1.收藏 纠错 分享 2.取消收藏 纠错分享 3.错题解析 全部解析 继续做题 4.模块题海 5在线模考和真题演练的收藏和错题
     */
    public void setData(int type){
        tv_collection_exercise.setVisibility(View.VISIBLE);
        tv_correction_exercise.setVisibility(View.VISIBLE);
        tv_share_exercise.setVisibility(View.VISIBLE);


        if (type == 1) {
            stringData = context.getResources().getStringArray(R.array.do_ex_ccs);
            setTextViewDrawableLeft(tv_collection_exercise,R.drawable.ic_scbt,10);
            setTextViewDrawableLeft(tv_correction_exercise,R.drawable.ic_jcbt,10);
            setTextViewDrawableLeft(tv_share_exercise,R.drawable.ic_fxbt,10);
            tv_correction_exercise.setVisibility(View.GONE);
        }
        if (type == 2) {
            stringData =context.getResources().getStringArray(R.array.do_ex_ucs);
            setTextViewDrawableLeft(tv_collection_exercise,R.drawable.ic_qxsc,10);
            setTextViewDrawableLeft(tv_correction_exercise,R.drawable.ic_jcbt,10);
            setTextViewDrawableLeft(tv_share_exercise,R.drawable.ic_fxbt,10);
            tv_correction_exercise.setVisibility(View.GONE);
        }
        if (type == 3) {
            stringData =context.getResources().getStringArray(R.array.do_ex_eac);
            setTextViewDrawableLeft(tv_collection_exercise,R.drawable.ic_ctjx,10);
            setTextViewDrawableLeft(tv_correction_exercise,R.drawable.ic_qbjx,10);
            setTextViewDrawableLeft(tv_share_exercise,R.drawable.ic_jxzt_result,10);
        }
        if (type == 4) {
            stringData =context.getResources().getStringArray(R.array.do_ex_module);
            setTextViewDrawableLeft(tv_collection_exercise,R.drawable.ic_scbt,10);
            tv_correction_exercise.setVisibility(View.GONE);
            view_one.setVisibility(View.GONE);
            setTextViewDrawableLeft(tv_share_exercise, R.drawable.ic_jcbt, 10);
        }
        if (type == 5) {
            stringData =context.getResources().getStringArray(R.array.do_ex_ec);
            setTextViewDrawableLeft(tv_collection_exercise,R.drawable.ic_jcbt,10);
            tv_correction_exercise.setVisibility(View.GONE);
            tv_collection_exercise.setVisibility(View.GONE);
            view_one.setVisibility(View.GONE);
            setTextViewDrawableLeft(tv_share_exercise, R.drawable.ic_fxbt, 10);
        }
        if (type == 1 || type == 2|| type == 3) {
            tv_collection_exercise.setText(stringData[0]);
            tv_correction_exercise.setText(stringData[1]);
            tv_share_exercise.setText(stringData[2]);
        }else if (type == 4){
            tv_collection_exercise.setText(stringData[0]);
            tv_share_exercise.setText(stringData[1]);
        }
        else if (type == 5){
            tv_collection_exercise.setText(stringData[0]);
            tv_share_exercise.setText(stringData[1]);
        }

    }


    /**
     * @param paramView 点击的按钮
     */
    public void show(View paramView) {
        //更新窗口状态-anchorView.getWidth()-offsetX,-offsetY
        window.showAsDropDown(paramView, -paramView.getWidth()/2, -CommonUtils.dip2px(20));
//        window.showAtLocation(paramView,Gravity.CENTER , (int) (paramView.getWidth()/2-CommonUtils.getScreenWidth()*0.014), paramView.getHeight());
        window.update();
    }

    private void setTextViewDrawableLeft(TextView view,int drawable,int drawblepadding){
        TeacherOnlineUtils.setTextViewLeftImage(context,view,drawable,drawblepadding);
    }


}
