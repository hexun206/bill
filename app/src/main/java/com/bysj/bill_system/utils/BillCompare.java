package com.bysj.bill_system.utils;

import com.bysj.bill_system.bean.BillBean;

import java.util.Comparator;

public class BillCompare implements Comparator<BillBean> {
    @Override
    public int compare(BillBean o1, BillBean o2) {
        return o1.time < o2.time ? -1 : 1;
    }

    @Override
    public boolean equals(Object obj) {
        return false;
    }
}
