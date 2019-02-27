package com.huatu.teacheronline.personal.custom;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.huatu.teacheronline.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MonthDateView extends View {
	private static final int NUM_COLUMNS = 7;
	private static final int NUM_ROWS = 6;
	private Paint mPaint;
	private int mDayColor = Color.parseColor("#666666");
	private int mSelectDayColor = Color.parseColor("#666666");
	private int mSelectBGColor = Color.parseColor("#1FC2F3");
	private int mCurrentColor = Color.parseColor("#00b38a");
	private int mCurrYear,mCurrMonth,mCurrDay;
	private int mSelYear,mSelMonth,mSelDay;
	private int mColumnSize,mRowSize;
	private DisplayMetrics mDisplayMetrics;
	private int mDaySize = 16;
	private TextView tv_date,tv_week;
	private int weekRow;
	private int [][] daysString;
	private int mCircleRadius = 6;
	private DateClick dateClick;
	private int mCircleColor = Color.parseColor("#00b38a");
	private int mCircleColor1 = Color.parseColor("#dddddd");
	private List<Integer> daysHasThingList;
	private List<Integer> daysHasThingList1;
	private Rect mIconRect;
	private final String date;

	public MonthDateView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mDisplayMetrics = getResources().getDisplayMetrics();
		Calendar calendar = Calendar.getInstance();
		SimpleDateFormat  sDateFormat = new SimpleDateFormat("yyyy年MM月");
		date = sDateFormat.format(new Date());
		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		mCurrYear = calendar.get(Calendar.YEAR);
		mCurrMonth = calendar.get(Calendar.MONTH);
		mCurrDay = calendar.get(Calendar.DATE);
		setSelectYearMonth(mCurrYear, mCurrMonth, mCurrDay);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		initSize();
		daysString = new int[6][7];
		mPaint.setTextSize(mDaySize*mDisplayMetrics.scaledDensity);
		String dayString;
		mIconRect = new Rect();
		int mMonthDays = DateUtils.getMonthDays(mSelYear, mSelMonth);
		int weekNumber = DateUtils.getFirstDayWeek(mSelYear, mSelMonth);
		for(int day = 0;day < mMonthDays;day++){
			dayString = (day + 1) + "";
			int column = (day+weekNumber - 1) % 7;
			int row = (day+weekNumber - 1) / 7;
			daysString[row][column]=day + 1;
			int startX = (int) (mColumnSize * column + (mColumnSize - mPaint.measureText(dayString))/2);
			int startY = (int) (mRowSize * row + mRowSize/2 - (mPaint.ascent() + mPaint.descent())/2);
			if(dayString.equals(mSelDay+"")){
				//绘制背景色矩形
				int startRecX = mColumnSize * column;
				int startRecY = mRowSize * row;
				int endRecX = startRecX + mColumnSize;
				int endRecY = startRecY + mRowSize;
//				mPaint.setColor(mSelectBGColor);
				Resources res=getResources();
				Bitmap bmp= BitmapFactory.decodeResource(res, R.drawable.cal_today);
				if (bmp!=null){//加点击后的背景图片
					startRecX=startRecX+(mColumnSize-mRowSize)/2;
					mIconRect.set(startRecX, startRecY, startRecX+mRowSize, endRecY);
					canvas.drawBitmap(bmp, null, mIconRect, mPaint);
				}
//				canvas.drawBitmap(bmp,startX,startY,mPaint);
//				canvas.drawRect(startRecX, startRecY, endRecX, endRecY, mPaint);
//				//记录第几行，即第几周
				weekRow = row + 1;
			}
			//绘制事务圆形标志
			drawCircle(row, column, day + 1, canvas);
			drawCircle1(row, column, day + 1, canvas);
			if (day+1>mSelDay&&mSelMonth==mCurrMonth){//根据月份和日期限制点击
				if(dayString.equals(mCurrDay+"") && mCurrDay != mSelDay && mCurrMonth == mSelMonth){
					mPaint.setColor(mCurrentColor);
				}else{
					if (day<mCurrDay){//根据月份和日期限制点击
						mPaint.setColor(Color.parseColor("#666666"));
					}else{
						mPaint.setColor(Color.parseColor("#dddddd"));
					}
				}
			}else{
				if(dayString.equals(mSelDay+"")){//判断如果是选中并且是今日 字体为绿色 否则为黑色
					if(dayString.equals(mCurrDay+"") && mCurrDay == mSelDay && mCurrMonth == mSelMonth){
						mPaint.setColor(mCurrentColor);
					}else {
						mPaint.setColor(mSelectDayColor);
					}
				}else if(dayString.equals(mCurrDay+"") && mCurrDay != mSelDay && mCurrMonth == mSelMonth){
					//正常月，选中其他日期，则今日为绿色
					mPaint.setColor(mCurrentColor);
				} else{
					mPaint.setColor(mDayColor);
				}
			}

//			if (date.equals(s)){//如果在当月就不能点击下个月的日历
//				if (day>=mSelDay){
//					mPaint.setColor(Color.parseColor("#dddddd"));
//				}
//			}
			canvas.drawText(dayString, startX, startY, mPaint);
			if (tv_date != null) {
				tv_date.setText(mSelYear + "年" + (mSelMonth + 1) + "月");
			}
		}
	}
	//画绿点6172
	private void drawCircle(int row,int column,int day,Canvas canvas){
		if(daysHasThingList != null && daysHasThingList.size() >0){
			if(!daysHasThingList.contains(day))return;
			mPaint.setColor(mCircleColor);
			float circleX = (float) (mColumnSize * column  +mColumnSize*0.5);
			float circley = (float) ((mRowSize *(row+1) ));
			canvas.drawCircle(circleX, circley, mCircleRadius, mPaint);
		}
	}
	//画灰点64877
	private void drawCircle1(int row,int column,int day,Canvas canvas){
		if(daysHasThingList1 != null && daysHasThingList1.size() >0){
			if(!daysHasThingList1.contains(day))return;
			mPaint.setColor(mCircleColor1);
			float circleX = (float) (mColumnSize * column  +mColumnSize*0.5);
			float circley = (float) ((mRowSize * (row + 1)));
			canvas.drawCircle(circleX, circley, mCircleRadius, mPaint);
		}
	}
	@Override
	public boolean performClick() {
		return super.performClick();
	}

	private int downX = 0,downY = 0;
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		int eventCode=  event.getAction();
		switch(eventCode){
		case MotionEvent.ACTION_DOWN:
			downX = (int) event.getX();
			downY = (int) event.getY();
			break;
		case MotionEvent.ACTION_MOVE:
			break;
		case MotionEvent.ACTION_UP:
			int upX = (int) event.getX();
			int upY = (int) event.getY();
			if(Math.abs(upX-downX) < 10 && Math.abs(upY - downY) < 10){//点击事件
				performClick();
				doClickAction((upX + downX)/2,(upY + downY)/2);
			}
			break;
		}
		return true;
	}

	/**
	 * 初始化列宽行高
	 */
	private void initSize(){
		mColumnSize = getWidth() / NUM_COLUMNS;
		mRowSize = getHeight() / NUM_ROWS-5;
	}

	/**
	 * 设置年月
	 * @param year
	 * @param month
	 */
	private void setSelectYearMonth(int year,int month,int day){
		if (day>mCurrDay&&month==mCurrMonth){//根据月份和日期限制点击
			return;
		}
		mSelYear = year;
		mSelMonth = month;
		mSelDay = day;
	}
	/**
	 * 执行点击事件
	 * @param x
	 * @param y
	 */
	private void doClickAction(int x,int y){
		int row = y / mRowSize;
		int column = x / mColumnSize;
		if(row<6&&column<7){
			if(daysString[row][column]!=0){
				setSelectYearMonth(mSelYear,mSelMonth,daysString[row][column]);
				invalidate();
				//执行activity发送过来的点击处理事件
				if(dateClick != null){
					dateClick.onClickOnDate();
				}
			}
		}



	}

	/**
	 * 左点击，日历向后翻页
	 */
	public void onLeftClick(){
		int year = mSelYear;
		int month = mSelMonth;
		int day = mSelDay;
		if(month == 0){//若果是1月份，则变成12月份
			year = mSelYear-1;
			month = 11;
		}else if(DateUtils.getMonthDays(year, month) == day){
			//如果当前日期为该月最后一点，当向前推的时候，就需要改变选中的日期
			month = month-1;
			day = DateUtils.getMonthDays(year, month);
		}else{
			month = month-1;
		}
		setSelectYearMonth(year, month, 1);
		invalidate();
	}

	/**
	 * 右点击，日历向前翻页
	 */
	public void onRightClick(){
		int year = mSelYear;
		int month = mSelMonth;
		int day = mSelDay;
		if(month == 11){//若果是12月份，则变成1月份
			year = mSelYear+1;
			month = 0;
		}else if(DateUtils.getMonthDays(year, month) == day){
			//如果当前日期为该月最后一点，当向前推的时候，就需要改变选中的日期
			month = month + 1;
			day = DateUtils.getMonthDays(year, month);
		}else{
			month = month + 1;
		}
		Log.e("TAG","year==="+year+"month==="+month+"day==="+day);
		setSelectYearMonth(year, month, 1);
		invalidate();
	}

	/**
	 * 获取选择的年份
	 * @return
	 */
	public int getmSelYear() {
		return mSelYear;
	}
	/**
	 * 获取选择的月份
	 * @return
	 */
	public int getmSelMonth() {
		return mSelMonth;
	}
	/**
	 * 获取选择的日期
	 * @param
	 */
	public int getmSelDay() {
		return this.mSelDay;
	}
	/**
	 * 普通日期的字体颜色，默认黑色
	 * @param mDayColor
	 */
		public void setmDayColor(int mDayColor) {
		this.mDayColor = mDayColor;
	}

	/**
	 * 选择日期的颜色，默认为白色
	 * @param mSelectDayColor
	 */
	public void setmSelectDayColor(int mSelectDayColor) {
		this.mSelectDayColor = mSelectDayColor;
	}

	/**
	 * 选中日期的背景颜色，默认蓝色
	 * @param mSelectBGColor
	 */
	public void setmSelectBGColor(int mSelectBGColor) {
		this.mSelectBGColor = mSelectBGColor;
	}
	/**
	 * 当前日期不是选中的颜色，默认红色
	 * @param mCurrentColor
	 */
	public void setmCurrentColor(int mCurrentColor) {
		this.mCurrentColor = mCurrentColor;
	}

	/**
	 * 日期的大小，默认18sp
	 * @param mDaySize
	 */
	public void setmDaySize(int mDaySize) {
		this.mDaySize = mDaySize;
	}
	/**
	 * 设置显示当前日期的控件
	 * @param tv_date
	 * 		显示日期
	 * @param tv_week
	 * 		显示周
	 */
	public void setTextView(TextView tv_date,TextView tv_week){
		this.tv_date = tv_date;
		this.tv_week = tv_week;
		invalidate();
	}
	public void setTextView1(TextView tv_date){
		this.tv_date = tv_date;
		invalidate();
	}
	/**
	 * 设置圆点出现的日期
	 * @param daysHasThingList
	 */
	public void setDaysHasThingList(List<Integer> daysHasThingList) {
		this.daysHasThingList = daysHasThingList;
		//内部刷新
		invalidate();
	}
	public void setDaysHasThingList1(List<Integer> daysHasThingList1) {
		this.daysHasThingList1 = daysHasThingList1;
		invalidate();
	}
	/***
	 * 设置圆圈的半径，默认为6
	 * @param mCircleRadius
	 */
	public void setmCircleRadius(int mCircleRadius) {
		this.mCircleRadius = mCircleRadius;
	}

	/**
	 * 设置圆圈的半径
	 * @param mCircleColor
	 */
	public void setmCircleColor(int mCircleColor) {
		this.mCircleColor = mCircleColor;
	}

	/**
	 * 设置日期的点击回调事件
	 * @author shiwei.deng
	 *
	 */
	public interface DateClick{
		void onClickOnDate();
	}

	/**
	 * 设置日期点击事件
	 * @param dateClick
	 */
	public void setDateClick(DateClick dateClick) {

		this.dateClick = dateClick;
	}

	/**
	 * 跳转至今天
	 */
	public void setTodayToView(){
		setSelectYearMonth(mCurrYear,mCurrMonth,mCurrDay);
		invalidate();
	}
}
