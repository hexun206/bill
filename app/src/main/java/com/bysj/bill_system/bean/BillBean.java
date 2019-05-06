package com.bysj.bill_system.bean;

public class BillBean {
    public int id;
    public String type;      //账单类型
    public double money;
    public boolean isIncome; //是否是收入 否则是支出
    public String remark;    //备注信息
    public long time;    //备注信息
    public double totalIncome;
    public double totalSpending;

    public BillBean() {
    }

    public BillBean(int id, String type, double money, boolean isIncome, String remark, long time) {
        this.id = id;
        this.type = type;
        this.money = money;
        this.isIncome = isIncome;
        this.remark = remark;
        this.time = time;
    }
}
