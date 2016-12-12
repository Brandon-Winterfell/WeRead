package com.huahua.weread.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by Administrator on 2016/12/12.
 */

public class DBUtils {

    private static DBUtils sDBUtils;
    private SQLiteDatabase mDataBase;

    /**
     * 创建数据库
     * @param context
     * @param name
     * @param factory
     * @param version
     */
    private DBUtils(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        // 拿到写数据库权限
        mDataBase = new WeReadDataBase(context, name, factory, version).getWritableDatabase();
    }

    public static synchronized DBUtils getDB(Context context) {
        if (sDBUtils == null) {
            sDBUtils = new DBUtils(context, "weread.db", null, 1);
        }
        return sDBUtils;
    }

    /**
     * 将已读条目的标识插入数据库
     *
     * @param table
     * @param key
     */
    public void insertHasRead(String table, String key) {
        Cursor cursor = mDataBase.query(table, null, null, null, null, null, "id asc");
        // 这是最多保持两百条数据吧
        if (cursor.getCount() > 200 && cursor.moveToNext()) {
            mDataBase.delete(table, "id=?", new String[]{String.valueOf(cursor.getInt(cursor.getColumnIndex("id")))});
        }
        cursor.close();

        ContentValues contentValues = new ContentValues();
        contentValues.put("key", key);
        mDataBase.insertWithOnConflict(table, null, contentValues, SQLiteDatabase.CONFLICT_REPLACE);

    }

    /**
     * 移除某条已读的条目
     * @param table
     * @param key
     */
    public void deleteHasRead(String table, String key) {
        mDataBase.delete(table, "key=?", new String[]{key});
    }

    /**
     * 查询某条条目是否已读
     * @param table
     * @param key
     * @return
     */
    public boolean isRead(String table, String key) {
        boolean isRead = false;
        Cursor cursor = mDataBase.query(table, null, "key=?", new String[]{key}, null, null, null);
        if(cursor.moveToFirst()) {
            isRead = true;
        }
        cursor.close();
        return isRead;
    }
}

































