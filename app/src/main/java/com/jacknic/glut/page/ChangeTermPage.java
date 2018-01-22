package com.jacknic.glut.page;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.jacknic.glut.R;
import com.jacknic.glut.model.CourseModel;
import com.jacknic.glut.model.dao.CourseDao;
import com.jacknic.glut.model.dao.CourseInfoDao;
import com.jacknic.glut.model.entity.CourseEntity;
import com.jacknic.glut.model.entity.CourseEntityDao;
import com.jacknic.glut.model.entity.CourseInfoEntity;
import com.jacknic.glut.model.entity.CourseInfoEntityDao;
import com.jacknic.glut.util.Config;
import com.jacknic.glut.util.DataBase;
import com.jacknic.glut.util.SnackbarTool;
import com.jacknic.glut.view.widget.Dialogs;
import com.jacknic.glut.view.widget.PickerView;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.AbsCallbackWrapper;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.request.BaseRequest;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

/**
 * 切换学期
 */

public class ChangeTermPage extends BasePage {

    private ListView lvTerms;
    private String year;
    private String term;
    private List<CourseEntity> termCourse;

    @Override
    protected int getLayoutId() {
        mTitle = "选择学期";
        return R.layout.page_change_term;
    }

    @Override
    public void onStart() {
        super.onStart();
        initView();
        setTerms();
    }

    /**
     * 初始化控件
     */
    private void initView() {
        lvTerms = (ListView) page.findViewById(R.id.ch_lv_terms);
        TextView tvImportTerm = (TextView) page.findViewById(R.id.ch_tv_import_term);
        tvImportTerm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog().show();
            }
        });
    }

    /**
     * 设置可选学期
     */
    private void setTerms() {
        if (getContext() == null) return;
        final List<String> termList = getTerms();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, termList);
        lvTerms.setAdapter(adapter);
        lvTerms.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                new AlertDialog.Builder(getContext())
                        .setMessage("当前学期修改为：" + ((TextView) view).getText())
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                CourseEntity courseEntity = termCourse.get(position);
                                SharedPreferences.Editor editor = OkGo.getContext().getSharedPreferences(Config.PREFER, Context.MODE_PRIVATE).edit();
                                editor.putInt(Config.JW_SCHOOL_YEAR, courseEntity.getSchoolStartYear())
                                        .putInt(Config.JW_SEMESTER, courseEntity.getSemester())
                                        .putBoolean(Config.IS_REFRESH, true)
                                        .putInt(Config.JW_YEAR_WEEK_OLD, Calendar.getInstance().get(Calendar.WEEK_OF_YEAR))
                                        .apply();
                                setTerms();
                            }
                        })
                        .setNegativeButton(android.R.string.cancel, null)
                        .show();
            }
        });
        lvTerms.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                new AlertDialog.Builder(getActivity())
                        .setMessage("删除" + ((TextView) view).getText() + "课程？")
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                CourseEntity courseEntity = termCourse.get(position);
                                deleteTerm(courseEntity);
                            }
                        })
                        .setNegativeButton(android.R.string.cancel, null)
                        .show();
                return true;
            }
        });
    }

    /**
     * 删除相同学期的所以数据记录
     */
    private void deleteTerm(CourseEntity courseEntity) {
        EventBus.getDefault().post(this.getClass().getName());
        // 删除课程
        DataBase.getDaoSession().getCourseEntityDao().queryBuilder()
                .where(CourseEntityDao.Properties.SchoolStartYear.eq(courseEntity.getSchoolStartYear()),
                        CourseEntityDao.Properties.Semester.eq(courseEntity.getSemester()))
                .buildDelete().executeDeleteWithoutDetachingEntities();
        // 删除课程信息
        DataBase.getDaoSession().getCourseInfoEntityDao().queryBuilder()
                .where(CourseInfoEntityDao.Properties.SchoolYearStart.eq(courseEntity.getSchoolStartYear()),
                        CourseInfoEntityDao.Properties.Semester.eq(courseEntity.getSemester()))
                .buildDelete().executeDeleteWithoutDetachingEntities();
        setTerms();
    }

    /**
     * 数据库中可选学期
     */
    @NonNull
    private List<String> getTerms() {
        SharedPreferences prefer = getContext().getSharedPreferences(Config.PREFER, Context.MODE_PRIVATE);
        termCourse = new CourseDao().getTermsCourse();
        final List<String> termList = new ArrayList<>();
        int select_year = prefer.getInt(Config.JW_SCHOOL_YEAR, Calendar.getInstance().get(Calendar.YEAR));
        int select_semester = prefer.getInt(Config.JW_SEMESTER, Calendar.getInstance().get(Calendar.YEAR));

        for (CourseEntity courseEntity : termCourse) {
            Integer schoolStartYear = courseEntity.getSchoolStartYear();
            Integer semester = courseEntity.getSemester();
            String item = schoolStartYear + "年" + (Integer.valueOf(1).equals(semester) ? "春" : "秋") + "季学期";
            if (schoolStartYear == select_year && select_semester == semester) {
                item = item.concat(" (已选中)");
            }
            termList.add(item);
        }
        return termList;
    }

    /**
     * 对话框
     */
    private AlertDialog dialog() {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        final LinearLayout dialogView = (LinearLayout) inflater.inflate(R.layout.dialog_import_term, null);
        final PickerView pvYear = (PickerView) dialogView.findViewById(R.id.ch_term_year);
        PickerView pvTerm = (PickerView) dialogView.findViewById(R.id.ch_term_term);
        ArrayList<String> years = new ArrayList<>();
        int year_now = Calendar.getInstance().get(Calendar.YEAR);
        for (int i = 0; i <= 10; i++) {
            years.add("" + (year_now - i));
        }
        List<String> terms = new ArrayList<>();
        terms.add("春");
        terms.add("秋");
        pvYear.setData(years);
        pvYear.setSelected(0);
        pvTerm.setData(terms);
        pvTerm.setSelected(0);
        year = year_now + "";
        term = "春";
        pvYear.setOnSelectListener(new PickerView.onSelectListener() {
            @Override
            public void onSelect(String text) {
                year = text;
            }
        });
        pvTerm.setOnSelectListener(new PickerView.onSelectListener() {
            @Override
            public void onSelect(String text) {
                term = text;
            }
        });
        AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setView(dialogView)
                .setPositiveButton("导入", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        CourseEntityDao courseEntityDao = DataBase.getDaoSession().getCourseEntityDao();
                        String real_term = "春".equals(term) ? "1" : "2";
                        long count = courseEntityDao.queryBuilder()
                                .where(CourseEntityDao.Properties.SchoolStartYear.eq(year), CourseEntityDao.Properties.Semester.eq(real_term))
                                .count();
                        if (count > 0) {
                            SnackbarTool.showShort("该学期已存在");
                        } else {
                            importTerm(year, real_term);
                        }
                    }
                })
                .setNegativeButton("取消", null)
                .create();
        return dialog;
    }

    /**
     * 导入新学期
     *
     * @param year 学年
     * @param term 学期
     */
    private void importTerm(final String year, final String term) {
        int year_int = Integer.parseInt(year) - 1980;
        OkGo.get(Config.URL_JW_COURSE + String.format("?year=%s&term=%s", year_int + "", term))
                .execute(new StringCallback() {

                    @Override
                    public void onBefore(BaseRequest request) {
                        SnackbarTool.showShort("课表获取中...");
                    }

                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        CourseModel courseModel = new CourseModel(s);
                        ArrayList<CourseEntity> courses = courseModel.getCourses();
                        if (courses.size() == 0) {
                            SnackbarTool.showShort("暂时无法获取该学期课表");
                            return;
                        }
                        ArrayList<CourseInfoEntity> courseInfoList = courseModel.getCourseInfoList();
                        new CourseInfoDao().insertCourseInfoList(courseInfoList);
                        CourseDao courseDao = new CourseDao();
                        courseDao.insertCourses(courses);
                        SnackbarTool.showShort("导入课表成功");
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        Dialogs.showLoginJw(getContext(), new AbsCallbackWrapper() {
                            @Override
                            public void onAfter(Object o, Exception e) {
                                importTerm(year, term);
                            }
                        });
                    }

                    @Override
                    public void onAfter(String s, Exception e) {
                        setTerms();
                    }
                });
    }

}
