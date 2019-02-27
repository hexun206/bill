package com.huatu.teacheronline.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.huatu.teacheronline.R;

public class RctView extends View {
    Paint paint;
    RectF area;
    float value = 60;

    public RctView(Context context) {
        super(context);

        init();
    }

    public RctView(Context context, AttributeSet attrs) {
        super(context, attrs);

        init();
    }

    public RctView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init();
    }

    public void init() {
        paint = new Paint();

        paint.setStyle(Paint.Style.STROKE);
        paint.setAntiAlias(true);
        area = new RectF();

    }

    @Override
    protected void onDraw(Canvas canvas) {
        //view的宽度和高度
        int height = getMeasuredHeight() - getPaddingLeft() * 2;
        int width = getMeasuredWidth() - getPaddingTop() * 2;
        int left = getPaddingLeft();
        int top = getPaddingTop();

        area.set(left, top, (left + width) * (value / 100), top + height);

        paint.setColor(getResources().getColor(R.color.green013));
        canvas.drawColor(getResources().getColor(R.color.gray021));
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        canvas.drawRect(area, paint);

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
}
