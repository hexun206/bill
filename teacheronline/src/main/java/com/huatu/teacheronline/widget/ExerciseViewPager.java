package com.huatu.teacheronline.widget;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.huatu.teacheronline.utils.DebugUtil;
import com.huatu.teacheronline.utils.ToastUtils;

/**
 * 做题页面pager
 * @author ljyu
 * Created by ljyu on 2017/7/12.
 */
public class ExerciseViewPager extends ViewPager {

    private boolean isScrollable = true;//是否可以滚动
    private int moveNext = 1;//错题中心填空题 多选题 需要滑两下才下一题
    private int position;//当前位置
    private static final  String TAG = "ExerciseViewPager";

    public boolean isScrollable() {
        return isScrollable;
    }

    public void setScrollable(boolean isScrollable,int position) {
        this.isScrollable = isScrollable;
        this.position = position;
    }

    public ExerciseViewPager(Context context) {
        super(context);
    }

    public ExerciseViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        DebugUtil.e(TAG,"onTouchEvent isScrollable"+ isScrollable+ " position:"+position);
        if (isScrollable){
            return super.onTouchEvent(event);
        } else {
//            if (moveNext % 2 == 1) {
//                moveNext++;
//                //第一次滑动显示 解析
//                ToastUtils.showToast("第一次滑动显示 解析");
            isScrollable = true;
                return false;
//            } else {
//                //第二次才可以滑动下一题
//                moveNext = 1;
//                isScrollable = true;
//                return true;
//            }
//        }
//        switch (event.getAction()) {
//            case MotionEvent.ACTION_DOWN:
//                DebugUtil.e(TAG,"onTouchEvent ACTION_DOWN");
//                break;
//            case MotionEvent.ACTION_MOVE:
//                DebugUtil.e(TAG,"onTouchEvent ACTION_MOVE");
//                break;
//            case MotionEvent.ACTION_UP:
//                DebugUtil.e(TAG,"onTouchEvent ACTION_UP");
//                break;
        }

//        return super.onTouchEvent(event);
    }

//    @Override
//    public boolean onInterceptTouchEvent(MotionEvent arg0) {
//        if (isScrollable){
//            return super.onTouchEvent(arg0);
//        } else {
//            if (moveNext % 2 == 1) {
//                moveNext++;
//                //第一次滑动显示 解析
//                ToastUtils.showToast("第一次滑动显示 解析");
//                return false;
//            } else {
//                //第二次才可以滑动下一题
//                moveNext = 1;
//                isScrollable = true;
//                return true;
//            }
//        }
//    }

//    @Override
//    public void scrollTo(int x, int y){
//        if (isScrollable){
//            super.scrollTo(x, y);
//        }else {
//            if (moveNext % 2 == 1) {
//                moveNext++;
//                //第一次滑动显示 解析
//                ToastUtils.showToast("第一次滑动显示 解析");
//            } else {
//                //第二次才可以滑动下一题
//                moveNext = 1;
//                isScrollable = true;
//                super.scrollTo(x, y);
//            }
//        }
//    }
}

