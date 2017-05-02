package com.shuaijie.codenote.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.TextView;

import com.shuaijie.codenote.R;

import java.lang.ref.WeakReference;
import java.util.Random;

public class SplashActivity extends Activity {
    private static final int START_MAIN = 100;
    private TextView tip;
    private String[] tips = {"左侧侧滑菜单有惊喜哟！", "点击头像可以进入设置界面哟", "往右划可以关闭窗口哟！", "+按钮可以添加笔记哟！", "你可以将笔记上传到云端哟！", "你可以将云端笔记下载下来哟！"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        tip = (TextView) findViewById(R.id.tv_tip);
        Random random = new Random();
        int index = random.nextInt(tips.length);
        tip.setText(tips[index]);
        myHandler.sendEmptyMessageDelayed(START_MAIN, 2000);
    }

    private MyHandler myHandler = new MyHandler(this);

    private class MyHandler extends Handler {
        private WeakReference<SplashActivity> weakReference;

        public MyHandler(SplashActivity splashActivity) {
            this.weakReference = new WeakReference<SplashActivity>(splashActivity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            SplashActivity splashActivity = weakReference.get();
            switch (msg.what) {
                case START_MAIN:
                    Intent intent = new Intent(splashActivity, MainActivity.class);
                    startActivity(intent);
                    finish();
                    break;
            }
        }
    }
}
