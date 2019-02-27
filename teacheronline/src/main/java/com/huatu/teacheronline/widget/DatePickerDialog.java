package com.huatu.teacheronline.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;

import com.huatu.teacheronline.R;

import java.util.Calendar;

/**
 * 日期选择控件
 */
public class DatePickerDialog extends Dialog implements View.OnClickListener{

	private NumberPicker npYear;
	private NumberPicker npMonth;
	private NumberPicker npDay;
	private Context context;
	private static String str1="" ;
	private static String str2="" ;
	private static String str3="" ;

	private DatePickerDialogClickListener listener;
	
	
	public DatePickerDialog(Context context) {
		super(context);
		this.context = context;
	}

	public DatePickerDialog(Context context , DatePickerDialogClickListener listener) {
		super(context);
		this.context = context;
		this.listener = listener;
	}
	public DatePickerDialog(Context context , int style,DatePickerDialogClickListener listener) {
	    super(context,style);
	    this.context = context;
	    this.listener = listener;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

//		this.setContentView(R.layout.datepickerdialog);
		View inflate = getLayoutInflater().inflate(R.layout.datepickerdialog, null);
//        addContentView(inflate, params);
		setContentView(inflate);
		npYear = (NumberPicker) inflate.findViewById(R.id.np_year);
		npMonth = (NumberPicker) inflate.findViewById(R.id.np_month);
		npDay = (NumberPicker) inflate.findViewById(R.id.np_day);
		npYear.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS); 
		npMonth.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS); 
		npDay.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS); 
		Calendar cal = Calendar.getInstance();
		// 当前年
		int year = cal.get(Calendar.YEAR);
		// 当前月
		int month = (cal.get(Calendar.MONTH)) + 1;
		// 当前月的第几天：即当前日
		int day_of_month = cal.get(Calendar.DAY_OF_MONTH);
//		// 当前时：HOUR_OF_DAY-24小时制；HOUR-12小时制
//		int hour = cal.get(Calendar.HOUR_OF_DAY);
//		// 当前分
//		int minute = cal.get(Calendar.MINUTE);
//		// 当前秒
//		int second = cal.get(Calendar.SECOND);
//		// 0-上午；1-下午
//		int ampm = cal.get(Calendar.AM_PM);
//		// 当前年的第几周
//		int week_of_year = cal.get(Calendar.WEEK_OF_YEAR);
//		// 当前月的第几周
//		int week_of_month = cal.get(Calendar.WEEK_OF_MONTH);
//		// 当前年的第几天
//		int day_of_year = cal.get(Calendar.DAY_OF_YEAR);
		str1=String.valueOf(year);
		str2=String.valueOf(month);
		str3=String.valueOf(day_of_month);
		
		npYear.setMaxValue(2299);
		npYear.setMinValue(1970);
		npYear.setValue(year);
		npYear.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
			@Override
			public void onValueChange(NumberPicker arg0, int arg1, int arg2) {
				str1 = npYear.getValue() + "";
				if (Integer.parseInt(str1) % 4 == 0 && Integer.parseInt(str1) % 100 != 0 || Integer.parseInt(str1) % 400 == 0) {
					if (str2.equals("1") || str2.equals("3") || str2.equals("5") || str2.equals("7") || str2.equals("8") || str2.equals("10") || str2.equals("12")) {
						npDay.setMaxValue(31);
						npDay.setMinValue(1);
					} else if (str2.equals("4") || str2.equals("6") || str2.equals("9") || str2.equals("11")) {
						npDay.setMaxValue(30);
						npDay.setMinValue(1);
					} else {
						npDay.setMaxValue(29);
						npDay.setMinValue(1);
					}
				} else {
					if (str2.equals("1") || str2.equals("3") || str2.equals("5") || str2.equals("7") || str2.equals("8") || str2.equals("10") || str2.equals("12")) {
						npDay.setMaxValue(31);
						npDay.setMinValue(1);
					} else if (str2.equals("4") || str2.equals("6") || str2.equals("9") || str2.equals("11")) {
						npDay.setMaxValue(30);
						npDay.setMinValue(1);
					} else {
						npDay.setMaxValue(28);
						npDay.setMinValue(1);
					}
				}

			}
		});

		npMonth.setMaxValue(12);
		npMonth.setMinValue(1);
		npMonth.setValue(month);
		npMonth.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
			@Override
			public void onValueChange(NumberPicker arg0, int arg1, int arg2) {
				str2 = npMonth.getValue() + "";
				if (str2.equals("1") || str2.equals("3") || str2.equals("5") || str2.equals("7") || str2.equals("8") || str2.equals("10") || str2.equals("12")) {
					npDay.setMaxValue(31);
					npDay.setMinValue(1);
				} else if (str2.equals("4") || str2.equals("6") || str2.equals("9") || str2.equals("11")) {
					npDay.setMaxValue(30);
					npDay.setMinValue(1);
				} else {
					if (Integer.parseInt(str1) % 4 == 0 && Integer.parseInt(str1) % 100 != 0 || Integer.parseInt(str1) % 400 == 0) {
						npDay.setMaxValue(29);
						npDay.setMinValue(1);
					} else {
						npDay.setMaxValue(28);
						npDay.setMinValue(1);
					}
				}
			}
		});

		npDay.setMaxValue(31);
		npDay.setMinValue(1);
		npDay.setValue(day_of_month);
		npDay.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
			@Override
			public void onValueChange(NumberPicker arg0, int arg1, int arg2) {
				str3 = npDay.getValue() + "";
			}
		});
		
		
/*		Button cancelBtn = (Button) findViewById(R.id.btn_cancel);
		cancelBtn.setOnClickListener(this);*/
		
		Button okBtn = (Button) inflate.findViewById(R.id.btn_ok);
		okBtn.setOnClickListener(this);
		
	}
	
	public interface DatePickerDialogClickListener{
		void onClick(View v);
	}
	/**
	 * 获取日期
	 */
	public static String getDate(){
		if(str2.length()!=2){
			str2="0"+str2;
		}
		if(str3.length()!=2){
			str3="0"+str3;
		}
		return str1+"-"+str2+"-"+str3;  
	}

	@Override
	public void onClick(View v) {
		this.dismiss();
		listener.onClick(v);
	}

	
}
