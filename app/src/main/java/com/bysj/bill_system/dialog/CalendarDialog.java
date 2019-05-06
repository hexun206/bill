package com.bysj.bill_system.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;


import com.bysj.bill_system.R;
import com.bysj.bill_system.adapter.CalendarAdapter;
import com.bysj.bill_system.listener.CalendarItemClickListener;
import com.bysj.bill_system.listener.DateTimeUpdateListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CalendarDialog implements View.OnClickListener, CalendarItemClickListener {

    Activity mActivity;
    GridView gridView;
    CalendarAdapter calendarAdapter;
    List<Map<String, Integer>> datas;
    int year = 0;
    int selectYear;
    int selectMonth = 0;//0为当前年全部
    Dialog dialog;
    DateTimeUpdateListener dateTimeUpdateListener;
    ImageView mLast;
    TextView mYear;
    ImageView mNext;
    TextView all;

    public void setSelectYear(int selectYear) {
        this.selectYear = selectYear;
    }

    public void setSelectMonth(int selectMonth) {
        this.selectMonth = selectMonth;
    }

    public int getSelectYear() {

        return selectYear;
    }

    public int getSelectMonth() {
        return selectMonth;
    }

    public void setDateTimeUpdateListener(DateTimeUpdateListener dateTimeUpdateListener) {
        this.dateTimeUpdateListener = dateTimeUpdateListener;
    }

    public CalendarDialog(Activity activity) {
        initView(activity);
    }


    private void initView(Activity activity) {
        this.mActivity = activity;
        year = Calendar.getInstance().get(Calendar.YEAR);
        selectYear = year;
        selectMonth = 0;
        datas = new ArrayList<>();
        dialog = new Dialog(mActivity, R.style.dialog);
        View content = LayoutInflater.from(mActivity).inflate(R.layout.distribution_calendar_choose_layout, null, false);
        View backgroundLayout = content.findViewById(R.id.bacground_layout);
        View contentLayout = content.findViewById(R.id.content_layout);
        gridView = content.findViewById(R.id.grid_view);
        mLast = content.findViewById(R.id.last);
        mYear = content.findViewById(R.id.year);
        mNext = content.findViewById(R.id.next);
        all = content.findViewById(R.id.all);
        mLast.setOnClickListener(this);
        mNext.setOnClickListener(this);
        all.setOnClickListener(this);
        mYear.setText(year + "");
        gridView.setSelector(new ColorDrawable(Color.TRANSPARENT));
        calendarAdapter = new CalendarAdapter(activity, datas);
        gridView.setAdapter(calendarAdapter);
        calendarAdapter.setCalendarItemClickListener(this);
        backgroundLayout.setOnClickListener(this);
        contentLayout.setOnClickListener(this);
        dialog.addContentView(content, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        initData();
    }

    public void initData() {
        int currentMonth = Calendar.getInstance().get(Calendar.MONTH) + 1;
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);

        datas.clear();
        for (int i = 0; i < 12; i++) {
            int month = i + 1;
            Map<String, Integer> map = new HashMap<>();
            map.put("month", month);
            if (currentYear == year && month > currentMonth)
                map.put("table_status", 2);
            else if (selectYear == year && selectMonth == month)
                map.put("table_status", 1);
            else if (currentYear == year && currentMonth == month)
                map.put("table_status", 3);
            else
                map.put("table_status", 0);
            datas.add(map);
        }
        calendarAdapter.notifyDataSetChanged();
    }

    public void show() {
        dialog.show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bacground_layout:
                dialog.dismiss();
                break;
            case R.id.content_layout:
                break;
            case R.id.last:
                year -= 1;
                mYear.setText(year + "");
                mNext.setAlpha(1.0f);
                selectYear = year;
                initData();
                break;
            case R.id.next:
                int currentYear = Calendar.getInstance().get(Calendar.YEAR);
                year += 1;
                if (year > currentYear) {
                    year = currentYear;
                    return;
                }
                if (year == currentYear)
                    mNext.setAlpha(0.2f);
                mYear.setText(year + "");
                selectYear = year;
                initData();
                break;
            case R.id.all:
                selectMonth = 0;
                initData();
                dialog.dismiss();
                if (dateTimeUpdateListener != null) {
                    dateTimeUpdateListener.dateUpdate(selectYear, selectMonth);
                }
                break;
        }
    }

    @Override
    public void onItemClick(int position) {
        selectYear = year;
        selectMonth = datas.get(position).get("month");
        initData();
        dialog.dismiss();
        if (dateTimeUpdateListener != null) {
//            Calendar calendar = Calendar.getInstance();
//            calendar.set(Calendar.YEAR, selectYear);
//            calendar.set(Calendar.MONTH, selectMonth == 0 ? 12 : selectMonth - 1);
            dateTimeUpdateListener.dateUpdate(selectYear, selectMonth);
        }
    }
}
