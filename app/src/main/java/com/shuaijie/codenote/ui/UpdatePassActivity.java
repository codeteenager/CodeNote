package com.shuaijie.codenote.ui;

import android.view.View;
import android.widget.EditText;

import com.shuaijie.codenote.R;
import com.shuaijie.codenote.utils.CommonUtils;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;

public class UpdatePassActivity extends BaseActivity {
    private EditText oldPassword, newPassword, preNewPassword;

    @Override
    protected void initView() {
        super.initView();
        setContentView(R.layout.activity_update_pass);
        oldPassword = (EditText) findViewById(R.id.oldPassword);
        newPassword = (EditText) findViewById(R.id.newPassword);
        preNewPassword = (EditText) findViewById(R.id.preNewPassword);
        actionBar.setTitle("修改密码");
    }

    public void updatePass(View view) {
        if (!CommonUtils.isNetworkConnected(getApplicationContext())) {
            CommonUtils.showTip(getApplicationContext(), "网络未连接");
            return;
        }
        String strOldPassword = oldPassword.getText().toString().trim();
        String strNewPassword = newPassword.getText().toString().trim();
        String strPreNewPassword = preNewPassword.getText().toString().trim();
        if (!strNewPassword.equals(strPreNewPassword)) {
            CommonUtils.showTip(getApplicationContext(), "两次新密码不一致");
            return;
        }
        BmobUser.updateCurrentUserPassword(strOldPassword, strNewPassword, new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    CommonUtils.showTip(getApplicationContext(), "修改成功");
                    finish();
                } else {
                    CommonUtils.showTip(getApplicationContext(), "旧密码错误");
                }
            }
        });
    }
}
