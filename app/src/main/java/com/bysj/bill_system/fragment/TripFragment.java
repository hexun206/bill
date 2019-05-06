package com.bysj.bill_system.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bysj.bill_system.R;
import com.bysj.bill_system.bean.BillBean;
import com.bysj.bill_system.dialog.CalendarDialog;
import com.bysj.bill_system.listener.DateTimeUpdateListener;
import com.bysj.bill_system.sqlite.BillDao;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.utils.Legend;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.Nullable;
import butterknife.BindView;
import butterknife.OnClick;

public class TripFragment extends BaseFragment implements DateTimeUpdateListener {
    @BindView(R.id.vStub)
    View vStub;
    @BindView(R.id.mPieChartS)
    PieChart mPieChartS;
    @BindView(R.id.llSpending)
    LinearLayout llSpending;
    @BindView(R.id.mPieChartI)
    PieChart mPieChartI;
    @BindView(R.id.llIncome)
    LinearLayout llIncome;
    @BindView(R.id.tvMonth)
    TextView tvMonth;
    @BindView(R.id.llMonth)
    LinearLayout llMonth;

    private CalendarDialog mCalendarDialog;
    private Calendar calendar;
    private double totalI = 0;
    private double totalS = 0;
    private Map<String, Double> mIncomeMap;
    private Map<String, Double> mSpendingMap;

    @Override
    public int getLayoutId() {
        return R.layout.fragment_trip_layout;
    }

    @Override
    public void initUI(View view, @Nullable Bundle savedInstanceState) {
        mIncomeMap = new HashMap<>();
        mSpendingMap = new HashMap<>();
        calendar = Calendar.getInstance();
        tvMonth.setText(calendar.get(Calendar.YEAR) + "年" + (calendar.get(Calendar.MONTH) + 1) + "月");
        mCalendarDialog = new CalendarDialog(getActivity());
        mCalendarDialog.setDateTimeUpdateListener(this);
    }

    @OnClick(R.id.llMonth)
    public void onViewClicked() {
        showDateChooseView();
    }

    @Override
    public void dateUpdate(int selectYear, int selectMonth) {
        calendar.clear();
        calendar.set(Calendar.YEAR, selectYear);
        calendar.set(Calendar.MONTH, selectMonth - 1);
        tvMonth.setText(calendar.get(Calendar.YEAR) + "年" + (calendar.get(Calendar.MONTH) + 1) + "月");
        showLoadingDialog();
        getData();
    }

    private void showDateChooseView() {
        mCalendarDialog.setSelectYear(calendar.get(Calendar.YEAR));
        mCalendarDialog.setSelectMonth(calendar.get(Calendar.MONTH) + 1);
        mCalendarDialog.initData();
        mCalendarDialog.show();
    }

    private void getData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<BillBean> query = BillDao.getInstance(getActivity()).queryInMonth(calendar);
                synchronized (Thread.currentThread()) {
                    try {
                        Thread.currentThread().wait(300);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        hiddenLoadingDialog();
                        llSpending.removeAllViews();
                        llIncome.removeAllViews();
                        totalI = 0;
                        totalS = 0;
                        mSpendingMap.clear();
                        mIncomeMap.clear();
                        for (BillBean billBean : query) {
                            if (billBean.isIncome) {
                                totalI += billBean.money;
                                mIncomeMap.put(billBean.type, (mIncomeMap.get(billBean.type) == null ? 0 : mIncomeMap.get(billBean.type)) + billBean.money);
                            } else {
                                totalS += billBean.money;
                                mSpendingMap.put(billBean.type, (mSpendingMap.get(billBean.type) == null ? 0 : mSpendingMap.get(billBean.type)) + billBean.money);
                            }
                        }
                        showChart(mPieChartS, getPieData(mSpendingMap, llSpending), totalS == 0 ? "快去记录支出信息以展示图表" : "总:" + totalS);
                        showChart(mPieChartI, getPieData(mIncomeMap, llIncome), totalI == 0 ? "快去记录收入信息以展示图表" : "总:" + totalI);
                    }
                });
            }
        }).start();
    }

    //关键方法在这里-主要用于饼图的画图
    private void showChart(PieChart pieChart, PieData pieData, String centerString) {

//            pieChart.setHoleColorTransparent(true);

        pieChart.setHoleRadius(50f);//半径
//        pieChart.setTransparentCircleRadius(40f); // 半透明圈
//        pieChart.setHoleRadius(0);  //实心圆
        pieChart.setDescription(""); //添加右下角备注
        pieChart.setDrawCenterText(true);  //饼状图中间可以添加文字
        pieChart.setDrawHoleEnabled(true);
        pieChart.setRotationAngle(90); // 初始旋转角度
        pieChart.setRotationEnabled(true); // 可以手动旋转
        pieChart.setUsePercentValues(true);  //显示成百分比
        pieChart.setCenterText(centerString);  //饼状图中间的文字
        pieChart.setValueTextSize(11);
        //设置数据
        pieChart.setData(pieData);

        Legend mLegend = pieChart.getLegend();  //设置比例图
//        mLegend.setPosition(Legend.LegendPosition.RIGHT_OF_CHART);  //最右边显示
//        mLegend.setForm(Legend.LegendForm.LINE);  //设置比例图的形状,默认是方形
        mLegend.setXEntrySpace(7f);
        mLegend.setYEntrySpace(5f);

        pieChart.animateXY(1000, 1000);  //设置动画
        pieChart.spin(2000, 0, 360);
    }

    private PieData getPieData(Map<String, Double> data, ViewGroup parent) {

        int index = 0;
        ArrayList<Entry> numbs = new ArrayList<>();
        ArrayList<String> titles = new ArrayList<>();
        for (Map.Entry<String, Double> entry : data.entrySet()) {
            titles.add(entry.getKey());
            numbs.add(new Entry((float) ((double) entry.getValue()), index));
            addContentView(parent, entry.getKey() + ":" + entry.getValue() + "元");
        }
        PieDataSet pieDataSet = new PieDataSet(numbs, null);
        //设置饼状图之间的距离
        pieDataSet.setSliceSpace(0f);
        //设置饼状图之间的颜色
        pieDataSet.setColors(initColor(numbs.size()));
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        float px = 5 * (metrics.densityDpi / 160f);
        pieDataSet.setSelectionShift(px); // 选中态多出的长度
        PieData pieData = new PieData(titles, pieDataSet);

        return pieData;
    }

    private ArrayList<Integer> initColor(int size) {
        // 饼图颜色
        ArrayList<Integer> colors = new ArrayList<Integer>();
        if (size > 0)
            colors.add(Color.rgb(255, 0, 0));
        if (size > 1)
            colors.add(Color.rgb(255, 125, 0));
        if (size > 2)
            colors.add(Color.rgb(255, 70, 180));
        if (size > 3)
            colors.add(Color.rgb(70, 255, 200));
        if (size > 4)
            colors.add(Color.rgb(90, 0, 255));
        if (size > 5)
            colors.add(Color.rgb(0, 255, 255));
        if (size > 6)
            colors.add(Color.rgb(255, 125, 255));
        return colors;
    }

    private void addContentView(ViewGroup parent, String content) {
        TextView t = new TextView(getActivity());
        t.setText(content);
        t.setTextColor(getContext().getResources().getColor(R.color.black_5B6C8A));
        t.setTextSize(13);
        t.setPadding(0, 14, 0, 14);
        parent.addView(t);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden)
            getData();
    }

    @Override
    public void onResume() {
        super.onResume();
        getData();
    }
}