package com.bysj.bill_system.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;

import com.bysj.bill_system.R;
import com.bysj.bill_system.adapter.TiebaAdapter;
import com.bysj.bill_system.bean.TiebaBean;
import com.bysj.bill_system.sqlite.TiebaDao;
import com.bysj.bill_system.utils.DataUtils;

import java.util.List;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;

@SuppressLint("ValidFragment")
public class TMineFragment extends BaseFragment {
    @BindView(R.id.rvRecyclerView)
    RecyclerView rvRecyclerView;
    @BindView(R.id.tvNull)
    View tvNull;

    TiebaAdapter tiebaAdapter;

    private int type;

    public TMineFragment(int type) {
        this.type = type;
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_tieba_mine;
    }

    @SuppressLint("WrongConstant")
    @Override
    public void initUI(View view, @Nullable Bundle savedInstanceState) {
        rvRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        tiebaAdapter = new TiebaAdapter(getActivity());
        rvRecyclerView.setAdapter(tiebaAdapter);
    }

    private void getData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<TiebaBean> query;
                String phone = DataUtils.getLoginAccount(getActivity()).phone;
                if (type == 0)
                    query = TiebaDao.getInstance(getActivity()).queryMine(phone);
                else
                    query = TiebaDao.getInstance(getActivity()).queryMinePart(phone);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        hiddenLoadingDialog();
                        tiebaAdapter.setData(query);
                        tvNull.setVisibility(query.size() == 0 ? View.VISIBLE : View.GONE);
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
