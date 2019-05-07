package com.bysj.bill_system.sqlite;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.bysj.bill_system.bean.ReplyBean;
import com.bysj.bill_system.bean.TiebaBean;
import com.bysj.bill_system.compare.TiebaCompare;
import com.bysj.bill_system.config.Config;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TiebaDao {

    private final String tableTie = "table_tieba";
    private final String tableReply = "table_reply";
    private final String CREATE_Tie = "create table " + tableTie + " (id integer primary key, title varchar, content varchar, style varchar,phone varchar,nickname varchar,headUrl varchar,time long)";
    private final String CREATE_Reply = "create table " + tableReply + " (id integer primary key, content varchar, toName varchar,owner varchar,sendTime long,ssid integer,pid integer,toNamePhone varchar,ownerPhone varchar)";
    private DBHelper dbHelper;
    private Context context;
    private static TiebaDao instance;

    public static TiebaDao getInstance(Context context) {
        if (instance == null)
            instance = new TiebaDao(context.getApplicationContext());
        return instance;
    }

    public TiebaDao(Context context) {
        ArrayList<String> strings = new ArrayList<>();
        strings.add(CREATE_Tie);
        strings.add(CREATE_Reply);
        this.dbHelper = new DBHelper(context, "tieba.db", null, Config.DBVERSON, strings);
    }

    public synchronized void createTie(TiebaBean tiebaBean) {
        SQLiteDatabase db = null;
        try {
            db = dbHelper.getWritableDatabase();
            db.execSQL("insert into " + tableTie + "(title,content,style,phone,nickname,headUrl,time)  values(?,?,?,?,?,?,?)", new Object[]{tiebaBean.title, tiebaBean.content, tiebaBean.style, tiebaBean.phone, tiebaBean.nickname, tiebaBean.headUrl, tiebaBean.time});
        } catch (Exception e) {
        }
        if (db != null)
            db.close();
    }

    public synchronized List<TiebaBean> query() {
        List<TiebaBean> billBeanList = new ArrayList<>();
        SQLiteDatabase readableDatabase = null;
        try {
            readableDatabase = dbHelper.getReadableDatabase();
            Cursor cursor = readableDatabase.rawQuery("select id,title,content,style,phone,nickname,headUrl,time from " + tableTie, null);
            while (cursor.moveToNext()) {
                billBeanList.add(new TiebaBean(cursor.getInt(0)
                        , cursor.getString(1)
                        , cursor.getString(2)
                        , cursor.getString(3)
                        , cursor.getString(4)
                        , cursor.getString(5)
                        , cursor.getString(6)
                        , cursor.getLong(7)
                ));
            }
        } catch (Exception e) {

        }
        if (readableDatabase != null)
            readableDatabase.close();
        Collections.sort(billBeanList, new TiebaCompare());
        return billBeanList;
    }

    public synchronized List<TiebaBean> queryMine(String phone) {
        List<TiebaBean> billBeanList = new ArrayList<>();
        SQLiteDatabase readableDatabase = dbHelper.getReadableDatabase();
        try {
            Cursor cursor = readableDatabase.rawQuery("select id,title,content,style,phone,nickname,headUrl,time from " + tableTie + " where phone = " + phone, null);
            while (cursor.moveToNext()) {
                billBeanList.add(new TiebaBean(cursor.getInt(0)
                        , cursor.getString(1)
                        , cursor.getString(2)
                        , cursor.getString(3)
                        , cursor.getString(4)
                        , cursor.getString(5)
                        , cursor.getString(6)
                        , cursor.getLong(7)
                ));
            }
        } catch (Exception e) {
        }
        readableDatabase.close();
        Collections.sort(billBeanList, new TiebaCompare());
        return billBeanList;
    }

    public synchronized List<TiebaBean> queryMinePart(String phone) {
        List<TiebaBean> billBeanList = new ArrayList<>();
        SQLiteDatabase readableDatabase = dbHelper.getReadableDatabase();
        try {
            //查询所有与我相关的回复
            Cursor cursor = readableDatabase.rawQuery("select * from " + tableReply + " where toNamePhone = " + phone + " or ownerPhone = " + phone, null);
            StringBuffer sb = new StringBuffer();
            while (cursor.moveToNext()) {
                sb.append(" id = " + cursor.getInt(6) + " or ");
            }
            String sq = "select id,title,content,style,phone,nickname,headUrl,time from " + tableTie + " where " + sb.toString().substring(0, sb.toString().length() - 3);
            if (sb.toString().length() != 0) {
                //查询所有我参与过的
                Cursor cursor1 = readableDatabase.rawQuery(sq, null);
                while (cursor1.moveToNext()) {
                    TiebaBean tiebaBean = new TiebaBean(cursor1.getInt(0)
                            , cursor1.getString(1)
                            , cursor1.getString(2)
                            , cursor1.getString(3)
                            , cursor1.getString(4)
                            , cursor1.getString(5)
                            , cursor1.getString(6)
                            , cursor1.getLong(7)
                    );
                    billBeanList.add(tiebaBean);
                }
            }
        } catch (Exception e) {
        }
        readableDatabase.close();
        Collections.sort(billBeanList, new TiebaCompare());
        return billBeanList;
    }

    public synchronized void replyTie(ReplyBean replyBean) {
        SQLiteDatabase db = null;
        try {
            db = dbHelper.getWritableDatabase();
            db.execSQL("insert into " + tableReply + " (content,toName,owner,sendTime,ssid,pid,toNamePhone,ownerPhone)  values(?,?,?,?,?,?,?,?)", new Object[]{replyBean.content, replyBean.toName, replyBean.owner, replyBean.sendTime, replyBean.ssid, replyBean.pid, replyBean.toNamePhone, replyBean.ownerPhone});
        } catch (Exception e) {

        }
        if (db != null)
            db.close();
    }

    public synchronized TiebaBean queryDetail(int id) {
        SQLiteDatabase readableDatabase = dbHelper.getReadableDatabase();
        TiebaBean tiebaBean = null;
        try {
            Cursor cursor = readableDatabase.rawQuery("select * from " + tableTie + " where id = " + id, null);
            cursor.moveToNext();
            tiebaBean = new TiebaBean(cursor.getInt(0)
                    , cursor.getString(1)
                    , cursor.getString(2)
                    , cursor.getString(3)
                    , cursor.getString(4)
                    , cursor.getString(5)
                    , cursor.getString(6)
                    , cursor.getLong(7)
            );
            tiebaBean.replys = getReplyListOne(readableDatabase, tiebaBean.id);
        } catch (Exception e) {
        }
        readableDatabase.close();
        return tiebaBean;
    }

    private synchronized List<ReplyBean> getReplyListOne(SQLiteDatabase readableDatabase, int pid) {
        Cursor cursor1 = readableDatabase.rawQuery("select * from " + tableReply + " where pid = " + pid + " and ssid = " + 0, null);
        List<ReplyBean> replyBeans = new ArrayList<>();
        while (cursor1.moveToNext()) {
            ReplyBean replyBean = new ReplyBean();
            replyBean.id = cursor1.getInt(0);
            replyBean.content = cursor1.getString(1);
            replyBean.toName = cursor1.getString(2);
            replyBean.owner = cursor1.getString(3);
            replyBean.sendTime = cursor1.getLong(4);
            replyBean.ssid = cursor1.getInt(5);
            replyBean.pid = cursor1.getInt(6);
            replyBean.replyBeanList = getReplyListSecond(readableDatabase, pid, replyBean.id);
            replyBeans.add(replyBean);
        }
        return replyBeans;
    }
    private synchronized List<ReplyBean> getReplyListSecond(SQLiteDatabase readableDatabase, int pid, int ssid) {
        Cursor cursor1 = readableDatabase.rawQuery("select * from " + tableReply + " where pid = " + pid + " and ssid = " + ssid, null);
        List<ReplyBean> replyBeans = new ArrayList<>();
        while (cursor1.moveToNext()) {
            ReplyBean replyBean = new ReplyBean();
            replyBean.id = cursor1.getInt(0);
            replyBean.content = cursor1.getString(1);
            replyBean.toName = cursor1.getString(2);
            replyBean.owner = cursor1.getString(3);
            replyBean.sendTime = cursor1.getLong(4);
            replyBean.ssid = cursor1.getInt(5);
            replyBean.pid = cursor1.getInt(6);
            replyBeans.add(replyBean);
        }
        return replyBeans;
    }
}
