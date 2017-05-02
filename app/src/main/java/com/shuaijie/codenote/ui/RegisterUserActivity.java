package com.shuaijie.codenote.ui;

import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.shuaijie.codenote.R;
import com.shuaijie.codenote.bean.User;
import com.shuaijie.codenote.utils.CommonUtils;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

public class RegisterUserActivity extends BaseActivity {
    private EditText reg_username, reg_password, reg_prepassword, reg_email;

    @Override
    protected void initView() {
        super.initView();
        setContentView(R.layout.activity_resiter_user);
        actionBar.setTitle("用户注册");
        reg_email = (EditText) findViewById(R.id.et_reg_email);
        reg_username = (EditText) findViewById(R.id.et_reg_username);
        reg_password = (EditText) findViewById(R.id.et_reg_password);
        reg_prepassword = (EditText) findViewById(R.id.et_reg_prepassword);
    }

    public void registerUser(View view) {
        final String username = reg_username.getText().toString().trim();
        String password = reg_password.getText().toString().trim();
        String prepassword = reg_prepassword.getText().toString().trim();
        final String email = reg_email.getText().toString().trim();
        if (TextUtils.isEmpty(username)) {
            CommonUtils.showTip(getApplicationContext(), "用户名不能为空");
            return;
        }
        if (TextUtils.isEmpty(email)) {
            CommonUtils.showTip(getApplicationContext(), "用户名不能为空");
            return;
        }
        if (TextUtils.isEmpty(password)) {
            CommonUtils.showTip(getApplicationContext(), "密码不能为空");
            return;
        }

        if (TextUtils.isEmpty(prepassword)) {
            CommonUtils.showTip(getApplicationContext(), "确认密码不能为空");
            return;
        }
        if (!CommonUtils.isNetworkConnected(this)) {
            CommonUtils.showTip(getApplicationContext(), "网络未连接");
            return;
        }
        if (!password.equals(prepassword)) {
            CommonUtils.showTip(getApplicationContext(), "两次密码不一致");
            return;
        }
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setEmail(email);
        user.signUp(new SaveListener<BmobUser>() {
            @Override
            public void done(BmobUser bmobUser, BmobException e) {
                if (e == null) {
                    LoginActivity.loginActivity.finish();
                    CommonUtils.setSpData(getApplicationContext(), "currentUserId", bmobUser.getObjectId());
                    CommonUtils.showTip(getApplicationContext(), "注册成功");
                    finish();
                } else {
                    CommonUtils.showTip(getApplicationContext(), "用户名或邮箱已被注册");
                }
            }
        });
    }

}
