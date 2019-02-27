package com.huatu.teacheronline.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.huatu.teacheronline.utils.StringUtils;

/**
 * Created by kinndann on 2018/9/27.
 * description:
 */
public class AssCodeView extends View {


    private final int numLength = 6;//密码个数
    private int numSize = dp2px(30);
    private int numPadding = dp2px(5);
    private int borderColor = Color.BLACK;
    private int numTextSize = sp2px(24);
    private Paint paint;
    private String[] textArray = new String[numLength];

    private final String DEFAULT_TEXT = "*";


    public AssCodeView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {

        paint = new Paint();
        paint.setAntiAlias(true);
    }


    private int dp2px(float dp) {
        float scale = getContext().getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

    private int sp2px(float spValue) {
        float fontScale = getContext().getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    public void setText(@NonNull String text) {
        textArray = new String[6];
        for (int i = 0; i < text.trim().length(); i++) {
            textArray[i] = String.valueOf(text.charAt(i));
        }
        invalidate();


    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int width = 0;
        switch (widthMode) {
            case MeasureSpec.UNSPECIFIED:
            case MeasureSpec.AT_MOST:
                width = numLength * numSize + numPadding * (numLength - 1);
                break;
            case MeasureSpec.EXACTLY:
                width = MeasureSpec.getSize(widthMeasureSpec);

                numPadding = (int) (width * 0.024);
                numSize = (width - (numPadding * (numLength - 1))) / numLength;


                break;
        }
        setMeasuredDimension(width, numSize);

    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //绘制方框
        drawRect(canvas, paint);
        //绘制文本
        drawText(canvas, paint);
    }

    private void drawText(Canvas canvas, Paint paint) {

        if (isEnabled()) {
            paint.setColor(Color.BLACK);
        } else {
            paint.setColor(Color.GRAY);
        }


        paint.setTextSize(numTextSize);
        paint.setTypeface(Typeface.DEFAULT_BOLD);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setStyle(Paint.Style.FILL);


        for (int i = 0; i < textArray.length; i++) {

            if (StringUtils.isEmpty(textArray[i])) {
                textArray[i] = DEFAULT_TEXT;
            }

            //文字居中的处理
            Rect r = new Rect();
            canvas.getClipBounds(r);
            int cHeight = r.height();
            paint.getTextBounds(textArray[i], 0, textArray[i].length(), r);
            float y = cHeight / 2f + r.height() / 2f - r.bottom;


            canvas.drawText(textArray[i],
                    (getPaddingLeft() + numSize / 2) + (numSize + numPadding) * i,
                    getPaddingTop() + y, paint);


        }


    }

    private void drawRect(Canvas canvas, Paint paint) {
        paint.setColor(borderColor);
        paint.setStrokeWidth(0);
        paint.setStyle(Paint.Style.STROKE);
        Rect rect;
        for (int i = 0; i < numLength; i++) {
            int startX = getPaddingLeft() + (numSize + numPadding) * i;
            int startY = getPaddingTop();
            int stopX = getPaddingLeft() + (numSize + numPadding) * i + numSize;
            int stopY = getPaddingTop() + numSize;
            rect = new Rect(startX, startY, stopX, stopY);
            canvas.drawRect(rect, paint);
        }
    }


}
