package com.huahua.weread.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Administrator on 2016/12/12.
 */

public class WeReadDataBase extends SQLiteOpenHelper {

    // 三个表名
    public static final String TABLE_WEIXINJINGXUAN = "weixinjingxuan"; // 微信精选
    public static final String TABLE_GUOKERIMEN = "guokerimen"; // 果壳热门
    public static final String TABLE_ZHIHURIBAO = "zhihuribao"; // 知乎日报

    // 两个字段
    private static final String ID = "id";
    private static final String KEY = "key";

    private static final String CREATE_TABLE_IF_NOT_EXISTS =
            "create table %s " +
                    "(id integer  primary key autoincrement,key text unique)";


    public WeReadDataBase(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version); // 库名在这个name参数里
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(String.format(CREATE_TABLE_IF_NOT_EXISTS, TABLE_GUOKERIMEN));
        db.execSQL(String.format(CREATE_TABLE_IF_NOT_EXISTS, TABLE_WEIXINJINGXUAN));
        db.execSQL(String.format(CREATE_TABLE_IF_NOT_EXISTS, TABLE_ZHIHURIBAO));
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}





















