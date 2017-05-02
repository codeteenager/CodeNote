package com.shuaijie.codenote.ui;

import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.shuaijie.codenote.R;
import com.shuaijie.codenote.utils.CommonUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;

/**
 * 作者:姜帅杰
 * 版本:1.0
 * 创建日期:2016/8/19:22:44.
 */
public class ResetPasswordActivity extends BaseActivity {
    private EditText reset_email;

    @Override
    protected void initView() {
        super.initView();
        setContentView(R.layout.activity_reset_password);
        reset_email = (EditText) findViewById(R.id.reset_email);
        actionBar.setTitle("重置密码");
    }

    public void reset(View view) {
        final String email = reset_email.getText().toString().trim();
        if (TextUtils.isEmpty(email)) {
            CommonUtils.showTip(getApplicationContext(), "请输入注册时所用邮箱");
        } else {
            if (CommonUtils.isEmail(email)) {
                BmobUser.resetPasswordByEmail(email, new UpdateListener() {

                    @Override
                    public void done(BmobException e) {
                        if (e == null) {
                            CommonUtils.showTip(getApplicationContext(), "重置密码请求成功，请到" + email + "邮箱进行密码重置操作");
                            finish();
                        } else {
                            CommonUtils.showTip(getApplicationContext(), "失败：" + e.getMessage());
                        }
                    }
                });
            } else {
                CommonUtils.showTip(getApplicationContext(), "邮箱格式不正确");
            }

        }
    }
}
