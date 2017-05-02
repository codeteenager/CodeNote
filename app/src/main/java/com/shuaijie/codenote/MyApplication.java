package com.shuaijie.codenote;

import android.app.Application;

import com.umeng.socialize.PlatformConfig;

import cn.bmob.v3.Bmob;

/**
 * Created by 姜帅杰 on 2016/2/7.
 */
public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Bmob.initialize(this, "88adf4a1da919f71b385e29fccbd8cca");
        PlatformConfig.setQQZone("1105175934", "EO3H4ejaaxw35wVb");
        PlatformConfig.setSinaWeibo("2841121667", "45c1642e045e529b4e81a20aba6e9fff");
        PlatformConfig.setWeixin("wxd3257802e4f7de95", "6da37cd2a422b7b2e91c7f6c30b19838");
    }
}
