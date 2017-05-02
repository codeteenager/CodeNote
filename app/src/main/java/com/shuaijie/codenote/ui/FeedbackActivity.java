package com.shuaijie.codenote.ui;

import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.shuaijie.codenote.R;
import com.shuaijie.codenote.utils.CommonUtils;

public class FeedbackActivity extends BaseActivity {
    private EditText et_suggest;
    private Button btn_commit;

    @Override
    protected void initView() {
        super.initView();
        setContentView(R.layout.activity_feedback);
        actionBar.setTitle("意见反馈");
        et_suggest = (EditText) findViewById(R.id.et_suggest);
        btn_commit = (Button) findViewById(R.id.btn_commit);
        btn_commit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = et_suggest.getText().toString().trim();
                if (TextUtils.isEmpty(content)) {
                    CommonUtils.showTip(getApplicationContext(), "请输入您的宝贵意见");
                } else {
                    CommonUtils.showTip(getApplicationContext(), "提交成功");
                    finish();
                }
            }
        });
    }

}
