package com.bysj.bill_system.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DBHelper extends SQLiteOpenHelper {

    private String mTableCreate;

    public DBHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version,String createTableS) {
        super(context, name, factory, version);
        mTableCreate = createTableS;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(mTableCreate);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(mTableCreate);
    }

}
