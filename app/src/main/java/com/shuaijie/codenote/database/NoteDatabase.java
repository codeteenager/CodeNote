package com.shuaijie.codenote.database;

import android.provider.BaseColumns;

/**
 * Created by 姜帅杰 on 2016/2/2.
 */
public final class NoteDatabase {
    public static abstract class NoteTable implements BaseColumns {
        public static final String TABLE_NAME = "note";
        public static final String TITLE = "title";
        public static final String CONTENT = "content";
        public static final String TIME = "time";
    }
}
