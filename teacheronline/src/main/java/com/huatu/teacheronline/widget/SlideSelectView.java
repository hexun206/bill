package com.huatu.teacheronline.widget;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.NinePatch;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

import com.huatu.teacheronline.R;
import com.huatu.teacheronline.utils.CommonUtils;

import java.util.HashMap;

/**
 * 签到时间轴控件
 * Created by ljyu on 2017-5-2 16:20:34.
 */
public class SlideSelectView extends View {

    //未签到的圆半径
    private static final float RADIU_SMALL = 15;
    //当前签到的圆半径
    private static final float RADIU_BIG = 15;
    //线的高度
    private static float HEIGHT_LINE = 10;
    //线距离两头的边距
    private static float MARGEN_LINE = RADIU_BIG * 6;

    private final Bitmap backgroundBitmap;
    //当前天数的画笔
    private Paint mSelectPaint;
    //未签到圆的数量
    private int countOfSmallCircle;
    //未签到圆的横坐标
    private float circlesX[];
    private Context mContext;
    //小圆画笔
    private Paint mPaint;
    //上面文字画笔
    private TextPaint mTextPaint;
    //下面文字画笔
    private TextPaint mTextDownPaint;
    //"天"画笔
    private TextPaint dayPaint;
    //控件高度
    private float mHeight;
    //控件宽度
    private float mWidth;
    //签到圆的横坐标 也就是红点的位置
    private float bigCircleX = 0;
    //是否是手指跟随模式
    private boolean isFollowMode;
    //手指按下的x坐标
    private float startX;
    //文字大小
    private float textSize;
    //文字宽度
    private float textWidth;
    //当前大球距离最近的位置
    private int currentPosition;
    //小圆之间的间距
    private float distanceX;
    //线上面文字
    private String[] text4Rates;
    //线底部文字
    private String[] text5Rates;
    //依附效果实现
    private ValueAnimator valueAnimator;
    //用于纪录松手后的x坐标
    private float currentPositionX;

    private onSelectListener selectListener;

    //22天每天签到的日期的位置
    private HashMap<Integer, Float> coordinates = new HashMap<Integer, Float>();

    public SlideSelectView(Context context) {
        this(context, null);
    }

    public SlideSelectView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SlideSelectView);
        countOfSmallCircle = a.getInt(R.styleable.SlideSelectView_circleCount, 5);
        textSize = a.getInt(R.styleable.SlideSelectView_textSize, (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_SP, 10, getResources().getDisplayMetrics()));
        a.recycle();

        mPaint = new Paint();
        mPaint.setColor(Color.parseColor("#00B38A"));
        mPaint.setAntiAlias(true);
        mSelectPaint = new Paint();
        mSelectPaint.setColor(Color.RED);
        mSelectPaint.setAntiAlias(true);

        backgroundBitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.jfjb_2);

        textSize = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_SP, 10, getResources().getDisplayMetrics());

        mTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        mTextPaint.setColor(Color.WHITE);
        mTextPaint.setTextSize(textSize);

        mTextDownPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        //底部文字颜色
        mTextDownPaint.setColor(Color.parseColor("#00B38A"));
        mTextDownPaint.setTextSize(CommonUtils.dip2px(13));
        dayPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        //底部文字颜色
        dayPaint.setColor(Color.parseColor("#00B38A"));
        dayPaint.setTextSize(textSize);

        currentPosition = countOfSmallCircle / 2;

    }

    /**
     * 设置显示文本
     *
     * @param strings
     */
    public void setString(String[] strings) {
        text4Rates = strings;
        textWidth = mTextPaint.measureText(text4Rates[0]);

        if (countOfSmallCircle != text4Rates.length) {
            throw new IllegalArgumentException("the count of small circle must be equal to the " +
                    "text array length !");
        }

    }

    /**
     * 设置底部显示文本
     *
     * @param strings
     */
    public void setDownString(String[] strings) {
        text5Rates = strings;
    }

    /**
     * 设置目前到哪一天
     *
     * @param
     */
    public void setCurrentSign(int sign) {
        if (sign > 22 || sign < 2) {
            bigCircleX = 0;
            return;
        }
        bigCircleX = coordinates.get(sign);
        invalidate();
    }

    /**
     * 设置监听器
     *
     * @param listener
     */
    public void setOnSelectListener(onSelectListener listener) {
        selectListener = listener;
    }


    @Override
    protected void onDraw(Canvas canvas) {

        //画中间的线
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(HEIGHT_LINE);
        canvas.drawLine(MARGEN_LINE, mHeight * 2 / 3, mWidth - MARGEN_LINE, mHeight * 2 / 3,
                mPaint);
        //画小圆
        mPaint.setStyle(Paint.Style.FILL);
        for (int i = 0; i < countOfSmallCircle; i++) {
            mPaint.setColor(Color.parseColor("#5dc9b1"));
            canvas.drawCircle(circlesX[i], mHeight * 2 / 3, RADIU_SMALL, mPaint);
            mPaint.setColor(Color.parseColor("#00B38A"));
            canvas.drawCircle(circlesX[i], mHeight * 2 / 3, RADIU_SMALL - 5, mPaint);
        }
        //画大圆的默认位置
        if (bigCircleX != 0) {
            mSelectPaint.setColor(Color.parseColor("#ff6060"));
            canvas.drawCircle(bigCircleX, mHeight * 2 / 3, RADIU_BIG, mSelectPaint);
            mSelectPaint.setColor(Color.RED);
            canvas.drawCircle(bigCircleX, mHeight * 2 / 3, RADIU_BIG - 5, mSelectPaint);
        }
        for (int i = 0; i < text4Rates.length; i++) {
            // 画背景图片的位置
            if(bigCircleX != 0 && circlesX[i] <=bigCircleX){
                Bitmap bmp_9 = BitmapFactory.decodeResource(getResources(), R.drawable.jfjb);
                NinePatch np = new NinePatch(bmp_9, bmp_9.getNinePatchChunk(), null);
                Rect rect = new Rect((int) (circlesX[i]) - CommonUtils.dip2px(21),
                        (int) ((mHeight * 2 / 3) - RADIU_BIG * 2 -
                                RADIU_SMALL) - CommonUtils.dip2px(27), (int) (circlesX[i]) + CommonUtils.dip2px(23), (int) ((mHeight * 2 / 3) - RADIU_BIG * 2 -
                        RADIU_SMALL) + CommonUtils.dip2px(10));
                np.draw(canvas, rect);
            }else {
                Bitmap bmp_9 = BitmapFactory.decodeResource(getResources(), R.drawable.jfjb_f);
                NinePatch np = new NinePatch(bmp_9, bmp_9.getNinePatchChunk(), null);
                Rect rect = new Rect((int) (circlesX[i]) - CommonUtils.dip2px(21),
                        (int) ((mHeight * 2 / 3) - RADIU_BIG * 2 -
                                RADIU_SMALL) - CommonUtils.dip2px(27), (int) (circlesX[i]) + CommonUtils.dip2px(23), (int) ((mHeight * 2 / 3) - RADIU_BIG * 2 -
                        RADIU_SMALL) + CommonUtils.dip2px(10));
                np.draw(canvas, rect);
            }
            //画底部文字
            canvas.drawText(text5Rates[i], circlesX[i] - CommonUtils.dip2px(6),
                    (mHeight * 7 / 8),
                    mTextDownPaint);
            if (text5Rates[i].length() == 1) {
                canvas.drawText("天", circlesX[i] + CommonUtils.dip2px(2),
                        (mHeight * 7 / 8),
                        dayPaint);
            } else {
                canvas.drawText("天", circlesX[i] + CommonUtils.dip2px(8),
                        (mHeight * 7 / 8),
                        dayPaint);
            }
            Point point = new Point((int) (circlesX[i]),
                    (int) ((mHeight * 2 / 3) - RADIU_BIG * 2 -
                            RADIU_SMALL) - CommonUtils.dip2px(9));
            //画上面文字
            textCenter(text4Rates[i], mTextPaint, canvas, point, CommonUtils.dip2px(45), Layout.Alignment.ALIGN_CENTER, 1.2f, 0, false);
        }

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:

                startX = event.getX();
                //如果手指按下的x坐标与大圆的x坐标的距离小于半径，则是follow模式
                if (Math.abs(startX - bigCircleX) <= RADIU_BIG) {
                    isFollowMode = true;
                } else {
                    isFollowMode = false;
                }

                break;
            case MotionEvent.ACTION_MOVE:

                //如果是follow模式，则大圆跟随手指移动
                if (isFollowMode) {
                    //防止滑出边界
                    if (event.getX() >= MARGEN_LINE && event.getX() <= (mWidth - MARGEN_LINE)) {
                        //Log.d("TAG", "event.getX()=" + event.getX() + "__mWidth=" + mWidth);
                        bigCircleX = event.getX();
                        int position = (int) ((event.getX() - MARGEN_LINE) / (distanceX / 2));
                        //更新当前位置
                        currentPosition = (position + 1) / 2;
                        invalidate();
                    }

                }

                break;
            case MotionEvent.ACTION_UP:
                break;
        }


        return false;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mHeight = h;
        mWidth = w;
        //计算每个小圆点的x坐标
        circlesX = new float[countOfSmallCircle];
        distanceX = (mWidth - MARGEN_LINE * 2) / (countOfSmallCircle - 1);
        for (int i = 0; i < countOfSmallCircle; i++) {
            //设置每个点的位置
            if (i == 0) {
                circlesX[i] = MARGEN_LINE;
                //设置第二天坐标
                coordinates.put(2, circlesX[0]);
            }
            if (i == 1) {
                circlesX[i] = (float) 0.1668 * mWidth + MARGEN_LINE;
                //设置第三天坐标
                coordinates.put(3, circlesX[1]);
            }
            if (i == 2) {
                circlesX[i] = (float) 0.3335 * mWidth + MARGEN_LINE;
            }
            if (i == 3) {
                circlesX[i] = (float) 0.5457 * mWidth + MARGEN_LINE;

            }
            if (i == 4) {
                circlesX[i] = mWidth - MARGEN_LINE;
                //设置第二十二天坐标
                coordinates.put(22, circlesX[4]);
            }
        }
        calculateCoordinate();


//        bigCircleX = circlesX[currentPosition];
    }

    /**
     * 计算22天每天的坐标
     */
    private void calculateCoordinate() {
        //3-7天的 4等分
        float vF = (circlesX[2] - circlesX[1]) / 4;
        int f = 0;
        //7-14天的 7等分
        float vS = (circlesX[3] - circlesX[2]) / 7;
        int s = 0;
        //14-22天的 8等分
        float vE = (circlesX[4] - circlesX[3]) / 8;
        int e = 0;
        for (int j = 3; j < 23; j++) {
            if (j >= 3 && j < 7) {
                coordinates.put(j, vF * f + circlesX[1]);
                f++;
            }
            if (j >= 7 && j < 14) {
                coordinates.put(j, vS * s + circlesX[2]);
                s++;
            }
            if (j >= 14 && j < 22) {
                coordinates.put(j, vE * e + circlesX[3]);
                e++;
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int screenSize[] = getScreenSize((Activity) mContext);

        int resultWidth;
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);

        if (widthMode == MeasureSpec.EXACTLY) {
            resultWidth = widthSize;
        } else {
            resultWidth = screenSize[0];

            if (widthMode == MeasureSpec.AT_MOST) {
                resultWidth = Math.min(widthSize, screenSize[0]);
            }
        }

        int resultHeight;
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        if (heightMode == MeasureSpec.EXACTLY) {
            resultHeight = heightSize;
        } else {
            //设置高度
            resultHeight = (int) (RADIU_BIG * 12);

            if (heightMode == MeasureSpec.AT_MOST) {
                resultHeight = Math.min(heightSize, resultHeight);
            }
        }

        setMeasuredDimension(resultWidth, resultHeight);

    }

    private static int[] getScreenSize(Activity activity) {
        DisplayMetrics metrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        return new int[]{metrics.widthPixels, metrics.heightPixels};
    }

    public interface onSelectListener {
        public void onSelect(int index);
    }

    /**
     * 文本自动换行居中
     *
     * @param string      文字
     * @param textPaint   画笔
     * @param canvas      画布
     * @param point       点
     * @param width       宽
     * @param align
     * @param spacingmult
     * @param spacingadd
     * @param includepad
     */
    private void textCenter(String string, TextPaint textPaint, Canvas canvas, Point point, int width,
                            Layout.Alignment align, float spacingmult, float spacingadd, boolean includepad) {
        StaticLayout staticLayout = new StaticLayout(string, textPaint, width, align, spacingmult, spacingadd, includepad);
        canvas.save();
        canvas.translate(-staticLayout.getWidth() / 2 + point.x, -staticLayout.getHeight() / 2 + point.y);
        staticLayout.draw(canvas);
        canvas.restore();

//		String mString = "Idtk是一个小学生";
//		TextPaint tp = new TextPaint();
//		tp.setColor(Color.BLUE);
//		tp.setStyle(Paint.Style.FILL);
//		tp.setTextSize(50);
//		Point point = new Point(0,0);
//		textCenter(mString,tp,canvas,point,150,Layout.Alignment.ALIGN_CENTER,1.5f,0,false);
    }

}