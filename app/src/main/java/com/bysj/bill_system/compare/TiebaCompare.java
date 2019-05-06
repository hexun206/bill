package com.bysj.bill_system.compare;

import com.bysj.bill_system.bean.TiebaBean;

import java.util.Comparator;

public class TiebaCompare implements Comparator<TiebaBean> {
    @Override
    public int compare(TiebaBean o1, TiebaBean o2) {
        return o1.time > o2.time ? -1 : 1;
    }

    @Override
    public boolean equals(Object obj) {
        return false;
    }
}
