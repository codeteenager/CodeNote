package com.shuaijie.codenote.ui;

import android.view.View;
import android.widget.EditText;

import com.shuaijie.codenote.R;
import com.shuaijie.codenote.bean.Note;
import com.shuaijie.codenote.utils.NoteDatabaseUtils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ShowNoteActivity extends BaseActivity {

    private NoteDatabaseUtils noteDatabaseUtils;
    private EditText tvTitle, tvContent;
    private String id;

    @Override
    protected void initView() {
        super.initView();
        setContentView(R.layout.activity_show_note);
        actionBar.setTitle("查看笔记");
        id = getIntent().getStringExtra("id");
        noteDatabaseUtils = new NoteDatabaseUtils(this);
        Note note = noteDatabaseUtils.findById(Integer.parseInt(id));
        tvContent = (EditText) findViewById(R.id.showNoteContent);
        tvTitle = (EditText) findViewById(R.id.showNoteTitle);
        tvTitle.setText(note.getTitle());
        tvContent.setText(note.getContent());
    }

    public void finishUpdateNote(View view) {
        Note note = new Note();
        String title = tvTitle.getText().toString().trim();
        String content = tvContent.getText().toString().trim();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String time = df.format(new Date());
        note.setId(Integer.parseInt(id));
        note.setTime(time);
        note.setTitle(title);
        note.setContent(content);
        noteDatabaseUtils.update(note);
        finish();
    }
}
