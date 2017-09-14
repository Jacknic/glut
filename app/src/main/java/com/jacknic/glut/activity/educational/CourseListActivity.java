package com.jacknic.glut.activity.educational;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.jacknic.glut.R;
import com.jacknic.glut.activity.BaseActivity;
import com.jacknic.glut.adapter.CourseListAdapter;
import com.jacknic.glut.model.entity.CourseEntity;
import com.jacknic.glut.model.entity.CourseEntityDao;
import com.jacknic.glut.model.entity.CourseInfoEntity;
import com.jacknic.glut.model.entity.CourseInfoEntityDao;
import com.jacknic.glut.util.Config;
import com.jacknic.glut.util.DataBase;
import com.jacknic.glut.util.Func;
import com.jacknic.glut.view.widget.Dialogs;
import com.lzy.okgo.OkGo;

import java.util.Calendar;
import java.util.List;

public class CourseListActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_course);
        TextView title = (TextView) findViewById(R.id.tv_toolbar_title);
        final ListView lv_course_list = (ListView) findViewById(R.id.lv_course_list);
        SharedPreferences prefer_jw = OkGo.getContext().getSharedPreferences(Config.PREFER_JW, MODE_PRIVATE);
        int startYear = prefer_jw.getInt(Config.JW_SCHOOL_YEAR, Calendar.getInstance().get(Calendar.YEAR));
        int semester = prefer_jw.getInt(Config.JW_SEMESTER, 1);
        final List<CourseInfoEntity> courseInfoList = DataBase.getDaoSession()
                .getCourseInfoEntityDao()
                .queryBuilder()
                .where(CourseInfoEntityDao.Properties.SchoolYearStart.eq(startYear))
                .where(CourseInfoEntityDao.Properties.Semester.eq(semester))
                .orderAsc(CourseInfoEntityDao.Properties.CourseName)
                .list();
        final CourseListAdapter listAdapter = new CourseListAdapter(this, courseInfoList);
        lv_course_list.setAdapter(listAdapter);
        lv_course_list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                final CourseInfoEntity courseInfoEntity = courseInfoList.get(position);
                AlertDialog alertDialog = new AlertDialog.Builder(CourseListActivity.this)
                        .setTitle("删除课程?")
                        .setMessage("  删除课程《" + courseInfoEntity.getCourseName() + "》,并移除相关课表信息")
                        .setNegativeButton(android.R.string.cancel, null)
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                DataBase.getDaoSession().getCourseEntityDao()
                                        .queryBuilder()
                                        .where(CourseEntityDao.Properties.CourseNum.eq(courseInfoEntity.getCourseNum()))
                                        .buildDelete().executeDeleteWithoutDetachingEntities();
                                SharedPreferences prefer_jw = OkGo.getContext().getSharedPreferences(Config.PREFER_JW, MODE_PRIVATE);
                                prefer_jw.edit().putBoolean(Config.IS_REFRESH, true).apply();
                                DataBase.getDaoSession().getCourseInfoEntityDao().delete(courseInfoEntity);
                                Func.updateWidget(CourseListActivity.this);
                                courseInfoList.remove(position);
                                listAdapter.notifyDataSetChanged();
                            }
                        }).create();
                alertDialog.show();
                return true;
            }
        });
        lv_course_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CourseInfoEntity courseInfoEntity = courseInfoList.get(position);
                CourseEntity courseEntity = DataBase.getDaoSession().getCourseEntityDao()
                        .queryBuilder()
                        .where(CourseEntityDao.Properties.CourseNum.eq(courseInfoEntity.getCourseNum()))
                        .limit(1).unique();
                Dialogs.getCourseInfoDialog(CourseListActivity.this, courseEntity, courseInfoEntity).show();
            }
        });
        title.setText("课程列表");
    }

}
