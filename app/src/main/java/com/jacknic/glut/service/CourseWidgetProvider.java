package com.jacknic.glut.service;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.jacknic.glut.MainActivity;
import com.jacknic.glut.R;
import com.jacknic.glut.util.Func;

import java.util.Calendar;
import java.util.Date;

/**
 * 课表小部件
 */
public class CourseWidgetProvider extends AppWidgetProvider {


    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        if (appWidgetIds == null || appWidgetIds.length == 0) return;
        System.out.println("执行更新小部件--------" + new Date());
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.course_widget);
        char[] weekNum = {' ', '日', '一', '二', '三', '四', '五', '六'};
        char weekDay = weekNum[Calendar.getInstance().get(Calendar.DAY_OF_WEEK)];
        remoteViews.setTextViewText(R.id.tv_week, "第" + Func.getWeekNow() + "周 星期" + weekDay);
        ComponentName thisWidget = new ComponentName(context, CourseWidgetProvider.class);

        //添加点击事件
        Intent updateViews = new Intent(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        PendingIntent refresh = PendingIntent
                .getBroadcast(context, 0, updateViews, 0);
        remoteViews.setOnClickPendingIntent(R.id.iv_refresh, refresh);


        Intent openActivity = new Intent(context, MainActivity.class);
        PendingIntent open = PendingIntent
                .getActivity(context, 0, openActivity, 0);
        remoteViews.setOnClickPendingIntent(R.id.wg_container, open);


        //设置适配器
        Intent adapterIntent = new Intent(context, CourseWidgetService.class);
        remoteViews.setRemoteAdapter(R.id.lv_course_list, adapterIntent);
        remoteViews.setEmptyView(R.id.lv_course_list, R.layout.item_course_empty);
        remoteViews.setPendingIntentTemplate(R.id.lv_course_list, open);


        AppWidgetManager manager = AppWidgetManager.getInstance(context);
        manager.notifyAppWidgetViewDataChanged(appWidgetIds[0], R.id.lv_course_list);
        //更新小部件
        appWidgetManager.updateAppWidget(thisWidget, remoteViews);

        System.out.println("执行更新小部件结束--------" + new Date());
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (AppWidgetManager.ACTION_APPWIDGET_UPDATE.equals(intent.getAction())) {
            // 刷新Widget
            AppWidgetManager manager = AppWidgetManager.getInstance(context);
            ComponentName cn = new ComponentName(context, CourseWidgetProvider.class);
            manager.notifyAppWidgetViewDataChanged(manager.getAppWidgetIds(cn), R.id.lv_course_list);
            onUpdate(context, manager, manager.getAppWidgetIds(cn));
        }
        super.onReceive(context, intent);
    }
}