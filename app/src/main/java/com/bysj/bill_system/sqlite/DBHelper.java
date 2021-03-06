package com.bysj.bill_system.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.File;
import java.util.List;

import androidx.annotation.Nullable;

public class DBHelper extends SQLiteOpenHelper {

    private List<String> mTableCreate;
    Context context;

    public DBHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version, List<String> createTableS) {
        super(context, name, factory, version);
        this.context = context;
        mTableCreate = createTableS;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        for (String s : mTableCreate) {
            db.execSQL(s);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//        for (String s : mTableCreate) {
//            db.execSQL("drop table if exists " + s);
//        }
//        onCreate(db);
    }

}
