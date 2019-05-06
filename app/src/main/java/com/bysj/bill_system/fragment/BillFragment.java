package com.bysj.bill_system.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bysj.bill_system.R;
import com.bysj.bill_system.adapter.BillAdapter;
import com.bysj.bill_system.bean.BillBean;
import com.bysj.bill_system.dialog.CalendarDialog;
import com.bysj.bill_system.listener.DateTimeUpdateListener;
import com.bysj.bill_system.listener.RemoveBillDataListener;
import com.bysj.bill_system.sqlite.BillDao;
import com.bysj.bill_system.utils.DateUtils;

import java.util.Calendar;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.OnClick;

public class BillFragment extends BaseFragment implements DateTimeUpdateListener, RemoveBillDataListener {
    @BindView(R.id.vStub)
    View vStub;
    @BindView(R.id.tvYear)
    TextView tvYear;
    @BindView(R.id.tvMonth)
    TextView tvMonth;
    @BindView(R.id.tvIcome)
    TextView tvIcome;
    @BindView(R.id.tvSpending)
    TextView tvSpending;
    @BindView(R.id.rvBill)
    RecyclerView rvBill;
    @BindView(R.id.tvEmpty)
    TextView tvEmpty;

    private BillAdapter billAdapter;
    private CalendarDialog mCalendarDialog;
    private Calendar calendar;

    @Override
    public int getLayoutId() {
        return R.layout.fragment_bill_layout;
    }

    @SuppressLint("WrongConstant")
    @Override
    public void initUI(View view, @Nullable Bundle savedInstanceState) {
//        setStatusBarHeight(view.findViewById(R.id.vStub));
        rvBill.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        billAdapter = new BillAdapter(getActivity(), this);
        rvBill.setAdapter(billAdapter);
        calendar = Calendar.getInstance();
        tvYear.setText(calendar.get(Calendar.YEAR) + "年");
        tvMonth.setText(DateUtils.fmt(calendar.get(Calendar.MONTH) + 1));
        mCalendarDialog = new CalendarDialog(getActivity());
        mCalendarDialog.setDateTimeUpdateListener(this);
    }

    private void getData() {
        showLoadingDialog();
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
                        billAdapter.setData(query);
                        double totalI = 0;
                        double totalS = 0;
                        for (BillBean billBean : query) {
                            if (billBean.isIncome)
                                totalI += billBean.money;
                            else
                                totalS += billBean.money;
                        }
                        tvIcome.setText(totalI + "");
                        tvSpending.setText(totalS + "");
                        tvEmpty.setVisibility(query.size() == 0 ? View.VISIBLE : View.GONE);
                        hiddenLoadingDialog();
                    }
                });
            }
        }).start();
    }

    @OnClick(R.id.rlDateChoose)
    public void onViewClicked() {
        showDateChooseView();
    }

    private void showDateChooseView() {
        mCalendarDialog.setSelectYear(calendar.get(Calendar.YEAR));
        mCalendarDialog.setSelectMonth(calendar.get(Calendar.MONTH) + 1);
        mCalendarDialog.initData();
        mCalendarDialog.show();
    }

    @Override
    public void dateUpdate(int selectYear, int selectMonth) {
        calendar.clear();
        calendar.set(Calendar.YEAR, selectYear);
        calendar.set(Calendar.MONTH, selectMonth - 1);
        tvYear.setText(calendar.get(Calendar.YEAR) + "年");
        tvMonth.setText(DateUtils.fmt(calendar.get(Calendar.MONTH) + 1));
        getData();
    }

    @Override
    public void onResume() {
        super.onResume();
        getData();
    }

    @Override
    public void removed(BillBean billBean) {
        BillDao.getInstance(getActivity()).delete(billBean);
        getData();
    }
}
