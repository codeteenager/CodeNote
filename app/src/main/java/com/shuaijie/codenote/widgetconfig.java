package com.shuaijie.codenote;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;


public class widgetconfig extends AppWidgetProvider {
    //widget从屏幕移除
    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
    }

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

//        CharSequence widgetText = context.getString(R.string.appwidget_text);
//        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widgetconfig);
//        views.setTextViewText(R.id.appwidget_text, widgetText);
//
//        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
    }

    //刷新执行
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    //widget添加到屏幕上执行
    @Override
    public void onEnabled(Context context) {
    }

    //最后一个widget从屏幕移除执行
    @Override
    public void onDisabled(Context context) {

    }
}

