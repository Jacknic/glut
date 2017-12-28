package com.jacknic.glut.service;

import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.jacknic.glut.MainActivity;
import com.jacknic.glut.R;
import com.jacknic.glut.model.dao.CourseDao;
import com.jacknic.glut.model.entity.CourseEntity;
import com.jacknic.glut.util.Func;

import java.util.Calendar;
import java.util.List;

/**
 * 更新小部件
 */
public class CourseWidgetService extends RemoteViewsService {


    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        return START_REDELIVER_INTENT;
    }

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new ListRemoteViewsFactory(this, intent);
    }
}

/**
 * 生成并绑定listView条目
 */
class ListRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {

    private Context mContext;
    private List<CourseEntity> courseList;

    ListRemoteViewsFactory(Context context, Intent intent) {
        mContext = context;
        courseList = new CourseDao().getCourse();
        //输出数据
        for (CourseEntity courseEntity : courseList) {
            System.out.println(courseEntity);
        }
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onDataSetChanged() {
        courseList = new CourseDao().getCourse();
    }

    @Override
    public void onDestroy() {
        courseList.clear();
    }

    @Override
    public int getCount() {
        return courseList.size();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        if (position < 0 || position >= courseList.size())
            return new RemoteViews(mContext.getPackageName(), R.layout.item_course_empty);
        final RemoteViews rv = new RemoteViews(mContext.getPackageName(), R.layout.item_course_widget);
        System.out.println("----->在service刷新界面" + Calendar.getInstance().getTime());
        CourseEntity courseEntity = courseList.get(position);
        System.out.println(courseEntity);
        rv.setTextViewText(R.id.item_tv_course_name, courseEntity.getCourseName());
        rv.setTextViewText(R.id.item_tv_location, courseEntity.getClassRoom());
        rv.setTextViewText(R.id.item_tv_start_end, Func.courseIndexToStr(courseEntity.getStartSection())
                + "-" + Func.courseIndexToStr(courseEntity.getEndSection()));
        //添加点击事件
        rv.setOnClickFillInIntent(R.id.item_course_widget, new Intent(mContext, MainActivity.class));
        return rv;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}