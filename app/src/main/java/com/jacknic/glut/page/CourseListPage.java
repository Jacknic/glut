package com.jacknic.glut.page;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;

import com.jacknic.glut.R;
import com.jacknic.glut.adapter.CourseListAdapter;
import com.jacknic.glut.model.entity.CourseEntity;
import com.jacknic.glut.model.entity.CourseEntityDao;
import com.jacknic.glut.model.entity.CourseInfoEntity;
import com.jacknic.glut.model.entity.CourseInfoEntityDao;
import com.jacknic.glut.util.Config;
import com.jacknic.glut.util.DataBase;
import com.jacknic.glut.util.Func;
import com.jacknic.glut.util.ViewUtil;
import com.jacknic.glut.view.widget.Dialogs;
import com.lzy.okgo.OkGo;

import java.util.Calendar;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

/**
 * 课程列表页
 */
public class CourseListPage extends Fragment {

    private View courseListPage;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        courseListPage = inflater.inflate(R.layout.page_all_course, container, false);
        ViewUtil.setTitle(getContext(), "课程列表");
        initView();
        return courseListPage;
    }


    /**
     * 初始化控件并添加事件监听
     */
    private void initView() {
        final ListView lv_course_list = (ListView) courseListPage.findViewById(R.id.lv_course_list);
        SharedPreferences prefer_jw = OkGo.getContext().getSharedPreferences(Config.PREFER, MODE_PRIVATE);
        int startYear = prefer_jw.getInt(Config.JW_SCHOOL_YEAR, Calendar.getInstance().get(Calendar.YEAR));
        int semester = prefer_jw.getInt(Config.JW_SEMESTER, 1);
        final List<CourseInfoEntity> courseInfoList = DataBase.getDaoSession()
                .getCourseInfoEntityDao()
                .queryBuilder()
                .where(CourseInfoEntityDao.Properties.SchoolYearStart.eq(startYear))
                .where(CourseInfoEntityDao.Properties.Semester.eq(semester))
                .orderAsc(CourseInfoEntityDao.Properties.CourseName)
                .list();
        final CourseListAdapter listAdapter = new CourseListAdapter(getContext(), courseInfoList);
        lv_course_list.setAdapter(listAdapter);
        lv_course_list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                final CourseInfoEntity courseInfoEntity = courseInfoList.get(position);
                AlertDialog alertDialog = new AlertDialog.Builder(getContext())
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
                                SharedPreferences prefer_jw = OkGo.getContext().getSharedPreferences(Config.PREFER, MODE_PRIVATE);
                                prefer_jw.edit().putBoolean(Config.IS_REFRESH, true).apply();
                                DataBase.getDaoSession().getCourseInfoEntityDao().delete(courseInfoEntity);
                                Func.updateWidget(getContext());
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
                List<CourseEntity> courseEntityList = DataBase.getDaoSession().getCourseEntityDao()
                        .queryBuilder()
                        .where(CourseEntityDao.Properties.CourseNum.eq(courseInfoEntity.getCourseNum()))
                        .list();
                CourseEntity courseEntity = new CourseEntity();
                StringBuilder courseArrange = new StringBuilder();
                StringBuilder classRooms = new StringBuilder();
                for (CourseEntity entity : courseEntityList) {
                    Integer dayOfWeek = entity.getDayOfWeek();
                    courseArrange.append(entity.getWeek() == null ? "" : entity.getWeek())
                            .append(dayOfWeek != null && dayOfWeek >= 1 ?
                                    ",周" + Config.weekNames[dayOfWeek - 1] + " "
                                            + Func.courseIndexToStr(entity.getStartSection()) + "-" + Func.courseIndexToStr(entity.getEndSection()) + "节;" : "");
                    classRooms.append(!TextUtils.isEmpty(entity.getClassRoom()) ? entity.getClassRoom() + ";" : "");
                }
                courseEntity.setWeek(courseArrange.toString());
                courseEntity.setClassRoom(classRooms.toString());
                courseEntity.setStartSection(null);
                courseEntity.setEndSection(null);
                AlertDialog courseInfoDialog = Dialogs.getCourseInfoDialog(getActivity(), courseEntity, courseInfoEntity).create();
                //设置对话框背景色透明
                Window window = courseInfoDialog.getWindow();
                if (window != null) {
                    window.setBackgroundDrawable(new ColorDrawable());
                }
                courseInfoDialog.show();
            }
        });
    }

}
