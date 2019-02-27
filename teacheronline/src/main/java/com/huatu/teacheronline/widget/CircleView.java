package com.huatu.teacheronline.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.huatu.teacheronline.R;
import com.huatu.teacheronline.utils.CommonUtils;

/**
 * Created by ljzyuhenda on 15/6/10.
 */
public class CircleView extends View {
    Paint paint;
    RectF area;
    int value = 100;
    int strokeWidth = 5;
    LinearGradient shader;

    public CircleView(Context context) {
        super(context);

        init();
    }

    public CircleView(Context context, AttributeSet attrs) {
        super(context, attrs);

        init();
    }

    public CircleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init();
    }

    public void init() {
        paint = new Paint();
        if (!isInEditMode()) {
            paint.setStrokeWidth(CommonUtils.dip2px(strokeWidth));
        }

        paint.setStyle(Paint.Style.STROKE);
        paint.setAntiAlias(true);
        area = new RectF();

//        shader =new LinearGradient(0, 0, 400, 0, new int[] {
//                Color.BLUE, Color.WHITE}, null,
//                Shader.TileMode.CLAMP);

        paint.setShader(shader);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //view的宽度和高度
        int height = getMeasuredHeight() - getPaddingLeft() * 2;
        int width = getMeasuredWidth() - getPaddingTop() * 2;
        int left = getPaddingLeft();
        int top = getPaddingTop();

        area.set(left, top, left + width, top + height);

        paint.setColor(getResources().getColor(R.color.green006));
        canvas.drawColor(Color.WHITE);
//        canvas.drawArc(area, 270, 360 * (100 - value) / 100, false, paint);
        canvas.drawArc(area, 270, value * 360 / 100, false, paint);

        paint.setColor(getResources().getColor(R.color.gray017));
//        canvas.drawArc(area, 270 , - 360 * value / 100, false, paint);
        canvas.drawArc(area, 270, (value - 100) * 360 / 100, false, paint);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);

        int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);

        int desiredWidth;
        int desiredHeight;

        if (widthSpecMode == MeasureSpec.EXACTLY) {
            desiredWidth = widthSpecSize;
        } else {
            desiredWidth = getSuggestedMinimumWidth();
            if (widthSpecMode == MeasureSpec.AT_MOST) {
                desiredWidth = Math.min(desiredWidth, widthSpecSize);
            }
        }

        if (heightSpecMode == MeasureSpec.EXACTLY) {
            desiredHeight = heightSpecSize;
        } else {
            desiredHeight = getSuggestedMinimumHeight();
            if (heightSpecMode == MeasureSpec.AT_MOST) {
                desiredHeight = Math.min(desiredHeight, heightSpecSize);
            }
        }

        setMeasuredDimension(desiredWidth, desiredHeight);
    }

    public void setProgress(int value) {
        this.value = value;
        invalidate();
    }
    public void setStrokeWidth(int strokeWidth) {
        this.strokeWidth = strokeWidth;
        paint.setStrokeWidth(CommonUtils.dip2px(strokeWidth));
        invalidate();
    }
}
