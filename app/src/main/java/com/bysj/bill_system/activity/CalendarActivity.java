package com.bysj.bill_system.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.CalendarView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bysj.bill_system.R;

import butterknife.BindView;
import butterknife.OnClick;

public class CalendarActivity extends BaseActivity {

    @BindView(R.id.vStub)
    View vStub;
    @BindView(R.id.rlBack)
    RelativeLayout rlBack;
    @BindView(R.id.tvTitle)
    TextView tvTitle;
    @BindView(R.id.tvRight)
    TextView tvRight;
    @BindView(R.id.cvCalendarView)
    CalendarView cvCalendarView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_calendar);
    }

    @Override
    void initData() {
        tvTitle.setText("日历");
    }

    @OnClick(R.id.rlBack)
    public void onViewClicked() {
        finish();
    }
}
