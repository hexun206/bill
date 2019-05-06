package com.bysj.bill_system.activity;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bysj.bill_system.R;
import com.bysj.bill_system.adapter.TiebaFragmentAdapter;
import com.bysj.bill_system.fragment.TMineFragment;
import com.flyco.tablayout.SlidingTabLayout;

import java.util.ArrayList;
import java.util.List;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TMineActivity extends BaseActivity {

    @BindView(R.id.vStub)
    View vStub;
    @BindView(R.id.rlBack)
    RelativeLayout rlBack;
    @BindView(R.id.tvTitle)
    TextView tvTitle;
    @BindView(R.id.tvRight)
    TextView tvRight;
    @BindView(R.id.tabLayout)
    SlidingTabLayout tabLayout;
    @BindView(R.id.vpViewPager)
    ViewPager vpViewPager;

    List<Fragment> list = new ArrayList<>();
    List<String> titles = new ArrayList<>();
    TiebaFragmentAdapter adapter;

    @Override
    void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_tmine);
    }

    @Override
    void initData() {
        tvTitle.setText("我的社区");
        list.add(new TMineFragment(0));
        list.add(new TMineFragment(1));
        titles.add("我的");
        titles.add("我参与的");
        adapter = new TiebaFragmentAdapter(getSupportFragmentManager(), list, titles);
        vpViewPager.setAdapter(adapter);
        tabLayout.setViewPager(vpViewPager);
    }

    @OnClick({R.id.rlBack, R.id.tvPosting})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rlBack:
                finish();
                break;
            case R.id.tvPosting:
                startActivity(new Intent(this, PostingActivity.class));
                break;
        }
    }
}
