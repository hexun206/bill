package com.bysj.bill_system.activity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.bysj.bill_system.R;
import com.bysj.bill_system.bean.BillBean;
import com.bysj.bill_system.config.Config;
import com.bysj.bill_system.sqlite.BillDao;
import com.bysj.bill_system.utils.DateUtils;
import com.bysj.bill_system.utils.ToastUtils;
import com.flyco.tablayout.SegmentTabLayout;
import com.flyco.tablayout.listener.OnTabSelectListener;

import java.util.Calendar;

import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ActionActivity extends AppCompatActivity {

    @BindView(R.id.tvDay)
    TextView tvDay;
    @BindView(R.id.tvWeek)
    TextView tvWeek;
    @BindView(R.id.tvMonth)
    TextView tvMonth;
    @BindView(R.id.ivClose)
    ImageView ivClose;
    @BindView(R.id.rlBacView)
    RelativeLayout rlBacView;
    Context mContext;
    @BindView(R.id.stlTab)
    SegmentTabLayout stlTab;
    @BindView(R.id.etMoney)
    EditText etMoney;
    @BindView(R.id.sType)
    Spinner sType;
    @BindView(R.id.tvTime)
    TextView tvTime;
    @BindView(R.id.etRemark)
    EditText etRemark;
    @BindView(R.id.tvConfirm)
    TextView tvConfirm;

    Calendar calendar;
    ArrayAdapter arr_adapter;
    BillBean billBean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_action);
        ButterKnife.bind(this);
        mContext = this;
        initView();
    }

    private void initView() {
        billBean = new BillBean();
        calendar = Calendar.getInstance();
        tvDay.setText(calendar.get(Calendar.DAY_OF_MONTH) + "");
        tvMonth.setText((calendar.get(Calendar.MONTH) + 1) + "/" + calendar.get(Calendar.YEAR));
        tvWeek.setText(getWeekString(calendar.get(Calendar.DAY_OF_WEEK)) + "");
        tvTime.setText(DateUtils.format(calendar.getTimeInMillis()));
        billBean.time = calendar.getTimeInMillis();
        stlTab.setTabData(new String[]{"支出", "收入"});
        stlTab.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelect(int position) {
                billBean.isIncome = position == 1;
                initSpinner(position == 1);
            }

            @Override
            public void onTabReselect(int position) {
            }
        });
        initSpinner(false);
    }

    private void initSpinner(boolean isIncome) {
        //适配器
        arr_adapter = new ArrayAdapter<String>(this, R.layout.spinner_item, isIncome ? Config.BILL_TYPE_INCOME : Config.BILL_TYPE_SPENDING);
        //设置样式
        arr_adapter.setDropDownViewResource(R.layout.spinner_item);
        sType.setAdapter(arr_adapter);
        sType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                billBean.type = arr_adapter.getItem(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        billBean.type = arr_adapter.getItem(0).toString();
    }

    @OnClick({R.id.ivClose, R.id.rlBacView, R.id.tvTime, R.id.tvConfirm, R.id.rlContentLayout})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rlBacView:
            case R.id.ivClose:
                finishActivity();
                break;
            case R.id.tvTime:
                DatePickerDialog datePicker = new DatePickerDialog(this, DatePickerDialog.THEME_HOLO_LIGHT, new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear,
                                          int dayOfMonth) {
                        calendar.clear();
                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.MONTH, monthOfYear);
                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        tvTime.setText(DateUtils.format(calendar.getTimeInMillis()));
                        billBean.time = calendar.getTimeInMillis();
                    }
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                datePicker.show();
                break;
            case R.id.tvConfirm:
                if (etMoney.getText().toString().isEmpty() || Double.parseDouble(etMoney.getText().toString()) == 0)
                    ToastUtils.showToast(this, "请输入记账金额");
                else {
                    billBean.money = Double.parseDouble(etMoney.getText().toString());
                    billBean.remark = etRemark.getText().toString() + " ";
                    BillDao.getInstance(this).insert(billBean);
                    setResult(20001);
                    finishActivity();
                }
                break;
            case R.id.rlContentLayout:
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.action_activity_int, R.anim.action_activity_out);
    }

    private String getWeekString(int week) {
        week -= 1;
        String dateStr = "";
        switch (week) {
            case 0:
                dateStr = "星期日";
                break;
            case 1:
                dateStr = "星期一";
                break;
            case 2:
                dateStr = "星期二";
                break;
            case 3:
                dateStr = "星期三";
                break;
            case 4:
                dateStr = "星期四";
                break;
            case 5:
                dateStr = "星期五";
                break;
            case 6:
                dateStr = "星期六";
                break;
            default:
                break;
        }
        return dateStr;
    }

    private void finishActivity() {
        finish();
        overridePendingTransition(R.anim.action_activity_int, R.anim.action_activity_out);
    }


    public static void intentActionActivity(Activity context) {
        context.startActivityForResult(new Intent(context, ActionActivity.class), 2001);
        context.overridePendingTransition(R.anim.action_activity_int, R.anim.action_activity_out);
    }

}
