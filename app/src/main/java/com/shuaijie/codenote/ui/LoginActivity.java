package com.shuaijie.codenote.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.shuaijie.codenote.R;
import com.shuaijie.codenote.bean.User;
import com.shuaijie.codenote.utils.CommonUtils;
import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.bean.SHARE_MEDIA;

import org.json.JSONObject;

import java.io.File;
import java.util.Map;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.DownloadFileListener;
import cn.bmob.v3.listener.LogInListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

public class LoginActivity extends BaseActivity {
    private EditText et_username, et_password;
    private ProgressDialog pd;//正在登录进度框
    public static LoginActivity loginActivity;
    private UMShareAPI mShareAPI;

    @Override
    protected void initView() {
        super.initView();
        setContentView(R.layout.activity_login);
        et_password = (EditText) findViewById(R.id.et_password);
        et_username = (EditText) findViewById(R.id.et_username);
        mShareAPI = UMShareAPI.get(this);
        loginActivity = this;
        actionBar.setTitle("用户登录");
    }

    /**
     * 跳转到找回密码界面
     *
     * @param view
     */
    public void resetPassword(View view) {
        Intent intent = new Intent(this, ResetPasswordActivity.class);
        startActivity(intent);
    }

    /**
     * qq登录
     *
     * @param view
     */
    public void qqLogin(View view) {
        SHARE_MEDIA platform = SHARE_MEDIA.QQ;
        mShareAPI.doOauthVerify(this, platform, new UMAuthListener() {
            @Override
            public void onComplete(final SHARE_MEDIA platform, int action, Map<String, String> data) {
                String access_token = data.get("access_token");
                String expires_in = data.get("expires_in");
                String openid = data.get("openid");
                BmobUser.BmobThirdUserAuth userAuth = new BmobUser.BmobThirdUserAuth("qq", access_token, expires_in, openid);
                BmobUser.loginWithAuthData(userAuth, new LogInListener<JSONObject>() {
                    @Override
                    public void done(JSONObject jsonObject, BmobException e) {
                        if (e == null) {
                            pd = new ProgressDialog(LoginActivity.this);
                            pd.setTitle("正在登录");
                            pd.setMessage("玩命加载中");
                            pd.show();
                            //保存用户在云端的id
                            final BmobUser user = BmobUser.getCurrentUser();
                            mShareAPI.getPlatformInfo(LoginActivity.this, platform, new UMAuthListener() {
                                @Override
                                public void onComplete(SHARE_MEDIA share_media, int i, Map<String, String> map) {
                                    String username = map.get("screen_name");
                                    String avatorUrl = map.get("profile_image_url");
                                    BmobUser bmobUser = new BmobUser();
                                    bmobUser.setUsername(username);
                                    bmobUser.update(user.getObjectId(), new UpdateListener() {
                                        @Override
                                        public void done(BmobException e) {
                                            if (e == null) {
                                                CommonUtils.setSpData(getApplicationContext(), "currentUserId", user.getObjectId());
                                                downloadAvator();//下载头像
                                                CommonUtils.showTip(getApplicationContext(), "登录成功");
                                            } else {
                                                pd.dismiss();
                                                CommonUtils.showTip(getApplicationContext(), "登录失败");
                                            }
                                        }
                                    });
                                }

                                @Override
                                public void onError(SHARE_MEDIA share_media, int i, Throwable throwable) {

                                }

                                @Override
                                public void onCancel(SHARE_MEDIA share_media, int i) {

                                }
                            });
                        } else {
                            pd.dismiss();
                            CommonUtils.showTip(getApplicationContext(), "登录失败");
                        }
                    }
                });
            }

            @Override
            public void onError(SHARE_MEDIA platform, int action, Throwable t) {
                Toast.makeText(getApplicationContext(), "授权失败", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancel(SHARE_MEDIA platform, int action) {
                Toast.makeText(getApplicationContext(), "取消授权", Toast.LENGTH_SHORT).show();
            }
        });

    }

    /**
     * 登录操作
     *
     * @param view
     */
    public void btnLogin(View view) {
        //判断当前网络状态
        if (!CommonUtils.isNetworkConnected(this)) {
            CommonUtils.showTip(getApplicationContext(), "网络未连接");
            return;
        }
        String username = et_username.getText().toString().trim();
        String password = et_password.getText().toString().trim();
        if (TextUtils.isEmpty(username)) {
            CommonUtils.showTip(getApplicationContext(), "用户名不能为空");
            return;
        }
        if (TextUtils.isEmpty(password)) {
            CommonUtils.showTip(getApplicationContext(), "密码不能为空");
            return;
        }
        pd = new ProgressDialog(this);
        pd.setTitle("正在登录");
        pd.setMessage("玩命加载中");
        pd.show();
        if (CommonUtils.isEmail(username)) {
            BmobUser.loginByAccount(username, password, new LogInListener<User>() {

                @Override
                public void done(User user, BmobException e) {
                    if (user != null) {//登录成功时
                        //保存用户在云端的id
                        CommonUtils.setSpData(getApplicationContext(), "currentUserId", user.getObjectId());
                        downloadAvator();//下载头像
                        CommonUtils.showTip(getApplicationContext(), "登录成功");
                    } else {
                        pd.dismiss();
                        CommonUtils.showTip(getApplicationContext(), "登录失败");
                    }
                }
            });
        } else {
            User user = new User();
            user.setUsername(username);
            user.setPassword(password);
            user.login(new SaveListener<BmobUser>() {
                @Override
                public void done(BmobUser bmobUser, BmobException e) {
                    if (e == null) {//登录成功时
                        //保存用户在云端的id
                        CommonUtils.setSpData(getApplicationContext(), "currentUserId", bmobUser.getObjectId());
                        downloadAvator();//下载头像
                        CommonUtils.showTip(getApplicationContext(), "登录成功");
                    } else {
                        pd.dismiss();
                        CommonUtils.showTip(getApplicationContext(), "登录失败");
                    }
                }
            });
        }
    }

    /**
     * 从云端下载头像到本地
     */
    private void downloadAvator() {
        User user = BmobUser.getCurrentUser(User.class);
        //如果用户没有头像就不用下载
        if (user.getFileName() != null) {
            //用户头像路径
            final String picPath = CommonUtils.DIR_PIC + "/" + CommonUtils.avatorName(getApplicationContext());
            File avatorFile = new File(picPath);
            //判断当前用户头像是否存在，不存在则下载，存在则不下载
            if (!avatorFile.exists()) {
                BmobFile downFile = new BmobFile(user.getFileName(), "", user.getFileUrl());
                downFile.download(new DownloadFileListener() {
                    @Override
                    public void done(String s, BmobException e) {
                        if (e == null) {
                            //将下载的缓存头像复制到指定路径中
                            CommonUtils.copyFile(s, picPath);
                            //设置头像路径
                            CommonUtils.setSpData(getApplicationContext(), "avatorPath", picPath);
                        } else {
                            pd.dismiss();
                        }
                    }

                    @Override
                    public void onProgress(Integer value, long l) {
                        //当下载完成时，对话框消失，并结束当前activity
                        if (value == 100) {
                            pd.dismiss();
                            finish();
                        }
                    }
                });
            } else {
                //设置头像路径，并结束activity
                CommonUtils.setSpData(getApplicationContext(), "avatorPath", picPath);
                finish();
            }
        } else {
            finish();
        }
    }

    /**
     * 跳转到注册页面
     *
     * @param view
     */
    public void btnRegister(View view) {
        Intent intent = new Intent(this, RegisterUserActivity.class);
        startActivity(intent);
    }

    /**
     * 社会化分享返回结果
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mShareAPI.onActivityResult(requestCode, resultCode, data);
    }
}
