package com.bysj.bill_system.adapter;

import java.util.List;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class TiebaFragmentAdapter extends FragmentPagerAdapter {
    List<Fragment> list;
    List<String> titles;

    public TiebaFragmentAdapter(FragmentManager fm, List<Fragment> list, List<String> titles) {
        super(fm);
        this.list = list;
        this.titles = titles;
    }

    @Override
    public Fragment getItem(int position) {
        return list.get(position);
    }

    @Override
    public int getCount() {
        return list.size();
    }
    public String getPageTitle(int position){
        return titles.get(position);
    }
}
