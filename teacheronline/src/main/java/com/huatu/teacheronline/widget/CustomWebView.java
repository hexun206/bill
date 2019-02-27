package com.huatu.teacheronline.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewConfiguration;

import com.github.lzyzsd.jsbridge.BridgeWebView;
import com.huatu.teacheronline.utils.DimensUtil;

/**
 * Created by ljzyuhenda on 16/2/4.
 */
public class CustomWebView extends BridgeWebView {
    private Context context;
    private float downX;
    private float downY;
    private final int MinInstance = ViewConfiguration.get(getContext()).getScaledTouchSlop();
    private onMoveListener listener;
    private Boolean estimateSwitch = false; //估分是否开启

    public CustomWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    public CustomWebView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
    }

    public CustomWebView(Context context) {
        super(context);
        this.context = context;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downX = event.getX();
                downY = event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
                float currentX = event.getX();
                float currentY = event.getY();
                if(estimateSwitch){
                    //预防估分和翻页手势冲突
                    if( event.getRawY() > DimensUtil.getDisplayHeight(context)*0.8){
                        return super.onTouchEvent(event);
                    }
                }
                long currentTime = System.currentTimeMillis();
                if (Math.abs(downX - currentX) > Math.abs(downY - currentY)) {
                    if (currentX - downX > MinInstance) {
                        if (listener != null) {
                            listener.movePrivous();

                            return true;
                        }
                    } else if (downX - currentX > MinInstance) {
                        if (listener != null) {
                            listener.moveNext();

                            return true;
                        }
                    }
                }
        }

        return super.onTouchEvent(event);
    }

    public interface onMoveListener {
        void movePrivous();

        void moveNext();
    }

    public void setOnMoveListener(onMoveListener listener) {
        this.listener = listener;
    }

    /**
     * 设置估分手势开启关闭
     * 预防切题手势冲突
     * @param estimateSwitch
     */
    public void setOnEstimateListenerSwitch(Boolean estimateSwitch) {
        this.estimateSwitch = estimateSwitch;
    }
}
