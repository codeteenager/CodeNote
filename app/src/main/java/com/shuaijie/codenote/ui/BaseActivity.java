package com.shuaijie.codenote.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;

/**
 * 作者:姜帅杰
 * 版本:1.0
 * 创建日期:2016/8/19:14:47.
 */
public class BaseActivity extends AppCompatActivity {
    /**
     * 整个Activity视图的根视图
     */
    private View decorView;

    /**
     * 手指按下时的坐标
     */
    private float downX;

    /**
     * 手机屏幕的宽度
     */
    private float screenWidth;

    protected ActionBar actionBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 获得decorView
        decorView = getWindow().getDecorView();
        // 获得手机屏幕的宽度和高度，单位像素
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        screenWidth = metrics.widthPixels;
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(false);
        initView();
    }

    protected void initView() {
    }

    /**
     * 通过重写该方法，对触摸事件进行处理
     */

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {// 当按下时
            // 获得按下时的X坐标
            downX = event.getX();

        } else if (event.getAction() == MotionEvent.ACTION_MOVE) {// 当手指滑动时
            // 获得滑过的距离
            float moveDistanceX = event.getX() - downX;
            if (moveDistanceX > 0) {// 如果是向右滑动
                decorView.setX(moveDistanceX); // 设置界面的X到滑动到的位置
            }

        } else if (event.getAction() == MotionEvent.ACTION_UP) {// 当抬起手指时
            // 获得滑过的距离
            float moveDistanceX = event.getX() - downX;
            if (moveDistanceX > 0) {
                if (moveDistanceX > screenWidth / 2) {
                    // 如果滑动的距离超过了手机屏幕的一半, 结束当前Activity
                    continueMove(moveDistanceX);
                } else { // 如果滑动距离没有超过一半
                    // 恢复初始状态
                    rebackToLeft(moveDistanceX);
                }
            }

        }
        return super.dispatchTouchEvent(event);
    }

    private void continueMove(float moveDistanceX) {
        // 从当前位置移动到右侧。
        ValueAnimator anim = ValueAnimator.ofFloat(moveDistanceX, screenWidth);
        anim.setDuration(500); // 一秒的时间结束, 为了简单这里固定为1秒
        anim.start();

        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                // 位移
                float x = (float) (animation.getAnimatedValue());
                decorView.setX(x);
            }
        });

        // 动画结束时结束当前Activity
        anim.addListener(new AnimatorListenerAdapter() {

            @Override
            public void onAnimationEnd(Animator animation) {
                finish();
            }

        });
    }

    /**
     * Activity被滑动到中途时，滑回去~
     */
    private void rebackToLeft(float moveDistanceX) {
        ObjectAnimator.ofFloat(decorView, "X", moveDistanceX, 0).setDuration(300).start();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == android.view.KeyEvent.KEYCODE_BACK) {
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}
