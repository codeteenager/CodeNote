package com.shuaijie.codenote.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by 姜帅杰 on 2016/2/2.
 */
public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "note.db";
    private static final int VERSION = 1;
    private static final String CREATE_TABLE_NOTE = "CREATE TABLE note(_id INTEGER PRIMARY KEY AUTOINCREMENT,"
            + "title TEXT,content,TEXT,time TEXT)";
    private static final String DROP_NOTE = "DROP TABLE IF EXISTS note";

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_NOTE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DROP_NOTE);
        db.execSQL(CREATE_TABLE_NOTE);
    }

}
