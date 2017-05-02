package com.shuaijie.codenote.ui;

import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.shuaijie.codenote.R;
import com.shuaijie.codenote.bean.Note;
import com.shuaijie.codenote.utils.NoteDatabaseUtils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class AddNoteActivity extends BaseActivity {

    private EditText newNoteTitle;
    private EditText newNoteContent;

    @Override
    protected void initView() {
        super.initView();
        setContentView(R.layout.activity_add_note);
        newNoteTitle = (EditText) findViewById(R.id.addNoteTitle);
        newNoteContent = (EditText) findViewById(R.id.addNoteContent);
        actionBar.setTitle("新建笔记");
    }

    public void finishNewNote(View view) {
        addNote();
    }

    private void addNote() {
        String title = newNoteTitle.getText().toString().trim();
        String content = newNoteContent.getText().toString().trim();
        if (TextUtils.isEmpty(title) && TextUtils.isEmpty(content)) {
            finish();
        } else {
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String time = df.format(new Date());
            Note note = new Note();
            note.setTitle(title);
            note.setContent(content);
            note.setTime(time);
            NoteDatabaseUtils noteDatabaseUtils = new NoteDatabaseUtils(this);
            noteDatabaseUtils.add(note);
            finish();
        }

    }
}
