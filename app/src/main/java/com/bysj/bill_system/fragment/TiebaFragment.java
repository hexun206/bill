package com.bysj.bill_system.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import com.bysj.bill_system.R;
import com.bysj.bill_system.activity.TMineActivity;
import com.bysj.bill_system.adapter.TiebaAdapter;
import com.bysj.bill_system.bean.BillBean;
import com.bysj.bill_system.bean.TiebaBean;
import com.bysj.bill_system.sqlite.BillDao;
import com.bysj.bill_system.sqlite.TiebaDao;

import java.util.List;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.OnClick;

public class TiebaFragment extends BaseFragment {
    @BindView(R.id.vStub)
    View vStub;
    @BindView(R.id.rlMine)
    RelativeLayout rlMine;
    @BindView(R.id.rvTieba)
    RecyclerView rvTieba;

    TiebaAdapter adapter;

    @Override
    public int getLayoutId() {
        return R.layout.fragment_tieba_layout;
    }

    @SuppressLint("WrongConstant")
    @Override
    public void initUI(View view, @Nullable Bundle savedInstanceState) {
        adapter = new TiebaAdapter(getActivity());
        rvTieba.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        rvTieba.setAdapter(adapter);
    }

    @OnClick(R.id.rlMine)
    public void onViewClicked() {
        startActivity(new Intent(getActivity(), TMineActivity.class));
    }

    private void getData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<TiebaBean> query = TiebaDao.getInstance(getActivity()).query();
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        hiddenLoadingDialog();
                        adapter.setData(query);
                    }
                });
            }
        }).start();
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
