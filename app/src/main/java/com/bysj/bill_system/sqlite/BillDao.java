package com.bysj.bill_system.sqlite;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.bysj.bill_system.bean.BillBean;
import com.bysj.bill_system.config.Config;
import com.bysj.bill_system.compare.BillCompare;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;


public class BillDao {
    private final String tableName = "table_bill";
    private final String CREATE_BILL = "create table " + tableName + " (id integer primary key, type varchar(20), money double, isIncome integer,remark varchar,time long)";
    private DBHelper dbHelper;
    private Context context;
    private static BillDao instance;

    public static BillDao getInstance(Context context) {
        if (instance == null)
            instance = new BillDao(context.getApplicationContext());
        return instance;
    }

    public BillDao(Context context) {
        ArrayList<String> objects = new ArrayList<>();
        objects.add(CREATE_BILL);
        this.dbHelper = new DBHelper(context, "bill.db", null, Config.DBVERSON, objects);
    }

    //    public String type;      //账单类型
//    public double money;
//    public boolean isIncome; //是否是收入 否则是支出
//    public String remark;    //备注信息
//    public long time;        //时间
    public void insert(BillBean billBean) {
        SQLiteDatabase writableDatabase = dbHelper.getWritableDatabase();
        try {
            writableDatabase.execSQL("insert into " + tableName + "(type,money,isIncome,remark,time)  values(?,?,?,?,?)"
                    , new Object[]{billBean.type, billBean.money, billBean.isIncome ? 1 : 0, billBean.remark, billBean.time});
        } catch (Exception e) {
        }
        writableDatabase.close();
    }

    public List<BillBean> query() {
        List<BillBean> billBeanList = new ArrayList<>();
        SQLiteDatabase readableDatabase = dbHelper.getReadableDatabase();
        try {
            Cursor cursor = readableDatabase.rawQuery("select id,type,money,isIncome,remark,time from " + tableName, null);
            while (cursor.moveToNext()) {
                billBeanList.add(new BillBean(cursor.getInt(0)
                        , cursor.getString(1)
                        , cursor.getDouble(2)
                        , cursor.getInt(3) == 1
                        , cursor.getString(4)
                        , cursor.getLong(5)
                ));
            }
        } catch (Exception e) {
        }
        readableDatabase.close();
        return billBeanList;
    }

    public List<BillBean> queryInMonth(Calendar calendar) {
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        long startMonthTime = calendar.getTimeInMillis();
        calendar.set(Calendar.DAY_OF_MONTH,
                calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        long endMonthTime = calendar.getTimeInMillis();
        List<BillBean> billBeanList = new ArrayList<>();
        SQLiteDatabase readableDatabase = dbHelper.getReadableDatabase();
        try {
            Cursor cursor = readableDatabase.rawQuery("select * from " + tableName + " where time>" + startMonthTime + " and time<" + endMonthTime, null);
            while (cursor.moveToNext()) {
                billBeanList.add(new BillBean(cursor.getInt(0)
                        , cursor.getString(1)
                        , cursor.getDouble(2)
                        , cursor.getInt(3) == 1
                        , cursor.getString(4)
                        , cursor.getLong(5)
                ));
            }
            Collections.sort(billBeanList, new BillCompare());
        } catch (Exception e) {
        }
        readableDatabase.close();
        return billBeanList;
    }

    public void delete(BillBean billBean) {
        SQLiteDatabase writableDatabase = dbHelper.getWritableDatabase();
        try {
            writableDatabase.execSQL("delete from " + tableName + " where id = " + billBean.id);
        } catch (Exception e) {
        }
        writableDatabase.close();
    }
}
