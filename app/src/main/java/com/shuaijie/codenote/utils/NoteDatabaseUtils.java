package com.shuaijie.codenote.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.shuaijie.codenote.bean.BmobNote;
import com.shuaijie.codenote.bean.Note;
import com.shuaijie.codenote.database.DatabaseHelper;
import com.shuaijie.codenote.database.NoteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 姜帅杰 on 2016/2/2.
 */
public class NoteDatabaseUtils {
    private DatabaseHelper databaseHelper;

    public NoteDatabaseUtils(Context context) {
        databaseHelper = new DatabaseHelper(context);
    }

    public void add(Note note) {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(NoteDatabase.NoteTable.TITLE, note.getTitle());
        values.put(NoteDatabase.NoteTable.CONTENT, note.getContent());
        values.put(NoteDatabase.NoteTable.TIME, note.getTime());
        db.insert(NoteDatabase.NoteTable.TABLE_NAME, null, values);//表名，可以为空的列名，contentvalues
        db.close();
    }

    public int addNotes(List<BmobNote> list) {
        int i = 0;
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        for (BmobNote bmobNote : list) {
            ContentValues values = new ContentValues();
            values.put(NoteDatabase.NoteTable._ID, bmobNote.getLocalId());
            values.put(NoteDatabase.NoteTable.TITLE, bmobNote.getTitle());
            values.put(NoteDatabase.NoteTable.CONTENT, bmobNote.getContent());
            values.put(NoteDatabase.NoteTable.TIME, bmobNote.getTime());
            if (findById(Integer.parseInt(bmobNote.getLocalId())) == null) {
                db.insert(NoteDatabase.NoteTable.TABLE_NAME, null, values);
                i++;
            }
        }
        db.close();
        return i;
    }

    public void delete(int id) {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        String whereClause = NoteDatabase.NoteTable._ID + "=?";
        String[] whereArgs = {String.valueOf(id)};
        db.delete(NoteDatabase.NoteTable.TABLE_NAME, whereClause, whereArgs);
        db.close();
    }

    public void update(Note note) {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(NoteDatabase.NoteTable.TITLE, note.getTitle());
        values.put(NoteDatabase.NoteTable.CONTENT, note.getContent());
        values.put(NoteDatabase.NoteTable.TIME, note.getTime());
        String whereClause = NoteDatabase.NoteTable._ID + "=?";
        String[] whereArgs = {String.valueOf(note.getId())};
        db.update(NoteDatabase.NoteTable.TABLE_NAME, values, whereClause, whereArgs);
        db.close();
    }

    public Note findById(int id) {
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        String[] columns = {NoteDatabase.NoteTable._ID, NoteDatabase.NoteTable.TITLE, NoteDatabase.NoteTable.CONTENT, NoteDatabase.NoteTable.TIME};
        //参数：是否去除重复记录，表名，查询的列，查询条件，查询条件的值，分组条件，分组条件的值，排序，分页条件
        Cursor cursor = db.query(true, NoteDatabase.NoteTable.TABLE_NAME, columns, NoteDatabase.NoteTable._ID + "=?", new String[]{String.valueOf(id)}, null, null, null, null);
        if (cursor.getCount() > 0) {
            Note note = null;
            while (cursor.moveToNext()) {
                note = new Note();
                note.setId(cursor.getInt(cursor.getColumnIndexOrThrow(NoteDatabase.NoteTable._ID)));
                note.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(NoteDatabase.NoteTable.TITLE)));
                note.setContent(cursor.getString(cursor.getColumnIndexOrThrow(NoteDatabase.NoteTable.CONTENT)));
                note.setTime(cursor.getString(cursor.getColumnIndexOrThrow(NoteDatabase.NoteTable.TIME)));
            }
            cursor.close();
            return note;
        } else {
            return null;
        }
    }

    public ArrayList<Note> findLike(String whereLike) {
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        String sql = "select _id,title,content,time from note where title like '%" + whereLike + "%' order by _id desc";
        Cursor cursor = db.rawQuery(sql, new String[]{});
        ArrayList<Note> notes = new ArrayList<>();
        Note note = null;
        while (cursor.moveToNext()) {
            note = new Note();
            note.setId(cursor.getInt(cursor.getColumnIndexOrThrow(NoteDatabase.NoteTable._ID)));
            note.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(NoteDatabase.NoteTable.TITLE)));
            note.setContent(cursor.getString(cursor.getColumnIndexOrThrow(NoteDatabase.NoteTable.CONTENT)));
            note.setTime(cursor.getString(cursor.getColumnIndexOrThrow(NoteDatabase.NoteTable.TIME)));
            notes.add(note);
        }
        cursor.close();
        db.close();
        return notes;
    }

    public ArrayList<Note> findAll() {
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        String[] columns = {NoteDatabase.NoteTable._ID, NoteDatabase.NoteTable.TITLE, NoteDatabase.NoteTable.CONTENT, NoteDatabase.NoteTable.TIME};
        //参数：是否去除重复记录，表名，查询的列，查询条件，查询条件的值，分组条件，分组条件的值，排序，分页条件
        Cursor cursor = db.query(true, NoteDatabase.NoteTable.TABLE_NAME, columns, null, null, null, null, "_id desc", null);
        ArrayList<Note> notes = new ArrayList<>();
        Note note = null;
        while (cursor.moveToNext()) {
            note = new Note();
            note.setId(cursor.getInt(cursor.getColumnIndexOrThrow(NoteDatabase.NoteTable._ID)));
            note.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(NoteDatabase.NoteTable.TITLE)));
            note.setContent(cursor.getString(cursor.getColumnIndexOrThrow(NoteDatabase.NoteTable.CONTENT)));
            note.setTime(cursor.getString(cursor.getColumnIndexOrThrow(NoteDatabase.NoteTable.TIME)));
            notes.add(note);
        }
        cursor.close();
        db.close();
        return notes;
    }
}
