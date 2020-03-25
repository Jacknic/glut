package com.jacknic.glut.service

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.jacknic.glut.MainActivity
import com.jacknic.glut.R
import com.jacknic.glut.util.Preferences
import com.jacknic.glut.util.TimeUtils
import java.util.*

/**
 * 课程列表桌面小部件
 *
 * @author Jacknic
 */
class CourseWidgetProvider : AppWidgetProvider() {

    private val prefer = Preferences.getInstance()

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        val remoteViews = RemoteViews(context.packageName, R.layout.course_widget)
        val term = TimeUtils.term2text(prefer.term)
        val calendar = Calendar.getInstance()
        val dayOfWeek = TimeUtils.getFixDayOfWeek(calendar.get(Calendar.DAY_OF_WEEK))
        val schoolYear = prefer.schoolYear
        val currWeek = prefer.currWeek
        val weekDay = TimeUtils.getWeekDay(dayOfWeek)
        remoteViews.setTextViewText(R.id.tvWeek, "${schoolYear}年${term}第${currWeek}周 周$weekDay")
        remoteViews.setEmptyView(R.id.lvCourseList, R.id.emptyView)
        val adapterIntent = Intent(context, CourseWidgetService::class.java)
        remoteViews.setRemoteAdapter(R.id.lvCourseList, adapterIntent)
        val mainIntent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(context, 0, mainIntent, 0)
        remoteViews.setOnClickPendingIntent(R.id.courseWidget, pendingIntent)
        remoteViews.setPendingIntentTemplate(R.id.lvCourseList, pendingIntent)
        appWidgetManager.updateAppWidget(appWidgetIds, remoteViews)
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.lvCourseList)
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        if (intent.action == ACTION_UPDATE_COURSE_WIDGET) {
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val cn = ComponentName(context, CourseWidgetProvider::class.java)
            val widgetIds = appWidgetManager.getAppWidgetIds(cn)
            onUpdate(context, appWidgetManager, widgetIds)
        }
    }

    companion object {
        const val ACTION_UPDATE_COURSE_WIDGET = "ACTION_UPDATE_COURSE_WIDGET"
    }
}