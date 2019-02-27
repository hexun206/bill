package com.huatu.teacheronline.widget;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.huatu.teacheronline.R;


public class VideoPopupWindow extends PopupWindow {
    private LinearLayout ll_root;
    private Animation animationIn, animationOut;
    private boolean isDismiss = false;

    public VideoPopupWindow(Context context, View.OnClickListener l) {
        super(context);
        View inflate = LayoutInflater.from(context).inflate(R.layout.dialog_video_upload, null);
        this.setWidth(LinearLayout.LayoutParams.MATCH_PARENT);
        this.setHeight(LinearLayout.LayoutParams.MATCH_PARENT);
        this.setBackgroundDrawable(new ColorDrawable());
        this.setFocusable(true);
        this.setOutsideTouchable(true);
        this.update();
        this.setBackgroundDrawable(new ColorDrawable());
        this.setContentView(inflate);
        animationIn = AnimationUtils.loadAnimation(context, R.anim.up_in);
        animationOut = AnimationUtils.loadAnimation(context, R.anim.down_out);
        ll_root = (LinearLayout) inflate.findViewById(R.id.ll_root);
        inflate.findViewById(R.id.tv_record).setOnClickListener(l);
        inflate.findViewById(R.id.tv_upload).setOnClickListener(l);
        inflate.findViewById(R.id.tv_cancel).setOnClickListener(l);
        inflate.findViewById(R.id.view_space).setOnClickListener(l);
    }

    @Override
    public void showAsDropDown(View parent) {
        try {
            if (Build.VERSION.SDK_INT >= 24) {
                int[] location = new int[2];
                parent.getLocationOnScreen(location);
                int x = location[0];
                int y = location[1] + parent.getHeight();
                this.showAtLocation(parent, Gravity.BOTTOM, x, y);
            } else {
                this.showAtLocation(parent, Gravity.BOTTOM, 0, 0);
            }

            isDismiss = false;
            ll_root.startAnimation(animationIn);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public interface OnAnimationEndListener {
        void onAnimationEnd(Animation animation);
    }


    public void dissmissWithNotAnim() {
        isDismiss = false;
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.JELLY_BEAN) {
            dismiss4Pop();
        } else {
            VideoPopupWindow.super.dismiss();
        }
    }


    @Override
    public void dismiss() {
        if (isDismiss) {
            return;
        }
        isDismiss = true;
        ll_root.startAnimation(animationOut);
        dismiss();
        animationOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                isDismiss = false;
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.JELLY_BEAN) {
                    dismiss4Pop();
                } else {
                    VideoPopupWindow.super.dismiss();
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
    }

    /**
     * 在android4.1.1和4.1.2版本关闭PopWindow
     */
    private void dismiss4Pop() {
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                VideoPopupWindow.super.dismiss();
            }
        });
    }

//    @Override
//    public void onClick(View v) {
//        int id = v.getId();
//        if (id == R.id.picture_tv_photo) {
//            if (onItemClickListener != null) {
//                onItemClickListener.onItemClick(0);
//                VideoPopupWindow.super.dismiss();
//            }
//        }
//        if (id == R.id.picture_tv_video) {
//            if (onItemClickListener != null) {
//                onItemClickListener.onItemClick(1);
//                VideoPopupWindow.super.dismiss();
//            }
//        }
//        dismiss();
//    }
//
//    private OnItemClickListener onItemClickListener;
//
//    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
//        this.onItemClickListener = onItemClickListener;
//    }
//
//    public interface OnItemClickListener {
//        void onItemClick(int positon);
//    }
}
