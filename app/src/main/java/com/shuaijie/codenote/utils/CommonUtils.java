package com.shuaijie.codenote.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.shuaijie.codenote.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.bmob.v3.BmobUser;

/**
 * Created by 姜帅杰 on 2016/2/7.
 */
public class CommonUtils {
    public static final String DIR_PIC = Environment.getExternalStorageDirectory() + "/codeNote";

    public static String avatorName(Context context) {
        BmobUser currentUser = BmobUser.getCurrentUser();
        String userId;
        if (currentUser.getObjectId() == null) {
            userId = CommonUtils.getSpData(context, "currentUserId");
        } else {
            userId = currentUser.getObjectId();
        }
        return userId + ".png";
    }

    //判断网络连接
    public static boolean isNetworkConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable();
            }
        }
        return false;
    }

    public static void setSpData(Context context, String key, String value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("codenote", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public static String getSpData(Context context, String key) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("codenote", Context.MODE_PRIVATE);
        return sharedPreferences.getString(key, null);
    }

    public static void delSpData(Context context, String key) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("codenote", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(key);
        editor.commit();
    }

    public static void showTip(Context context, String content) {
        Toast toast = new Toast(context);
        ImageView iv = new ImageView(context);
        iv.setImageResource(R.mipmap.toastlogo);
        TextView tv = new TextView(context);
        tv.setText(content);
        tv.setTextColor(context.getResources().getColor(R.color.white));
        LinearLayout tipLayout = new LinearLayout(context);
        tipLayout.setGravity(Gravity.CENTER);
        tipLayout.setPadding(CommonUtils.dp2px(context, 10), CommonUtils.dp2px(context, 10), CommonUtils.dp2px(context, 10), CommonUtils.dp2px(context, 10));
        tipLayout.setBackgroundColor(Color.GRAY);
        tipLayout.setOrientation(LinearLayout.HORIZONTAL);
        tipLayout.addView(iv);
        tipLayout.addView(tv);
        toast.setView(tipLayout);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.show();
    }

    public static int dp2px(Context context, float dpValue) {
        float density = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * density + 0.5f);
    }

    /**
     * 文件复制方法
     *
     * @param oldPath 原文件路径
     * @param newPath 目标文件路径
     */
    public static void copyFile(String oldPath, String newPath) {
        try {
            int byteread = 0;
            File oldfile = new File(oldPath);
            if (oldfile.exists()) { //文件存在时
                InputStream inStream = new FileInputStream(oldPath); //读入原文件
                FileOutputStream fs = new FileOutputStream(newPath);
                byte[] buffer = new byte[1024];
                while ((byteread = inStream.read(buffer)) != -1) {
                    fs.write(buffer, 0, byteread);
                }
                inStream.close();
            }
        } catch (Exception e) {
            System.out.println("复制单个文件操作出错");
            e.printStackTrace();
        }

    }

    public static boolean isEmail(String email) {
        String check = "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
        Pattern regex = Pattern.compile(check);
        Matcher matcher = regex.matcher(email);
        boolean isMatched = matcher.matches();
        return isMatched;
    }
}
