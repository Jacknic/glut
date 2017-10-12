package com.jacknic.glut.activity.educational;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bigkoo.pickerview.OptionsPickerView;
import com.jacknic.glut.R;
import com.jacknic.glut.activity.BaseActivity;
import com.jacknic.glut.model.entity.CourseEntity;
import com.jacknic.glut.model.entity.CourseInfoEntity;
import com.jacknic.glut.util.Config;
import com.jacknic.glut.util.DataBase;
import com.jacknic.glut.util.Func;
import com.jacknic.glut.util.ViewUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class AddCourseActivity extends BaseActivity {
    int courseStart = 1;
    int courseEnd = 1;
    int weekStart = 1;
    int weekEnd = 1;
    int weekType = 0;
    final String showWeek = "星期%s  %s - %s节";
    private int weekDay = 1;
    private AutoCompleteTextView act_course_name;
    private TextView tv_course_class;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_course);
        ViewUtil.showBackIcon(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        TextView title = (TextView) findViewById(R.id.tv_toolbar_title);
        Intent intent = getIntent();
        int start = intent.getIntExtra("start", 1);
        ViewUtil.showRefreshView(this, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save();
            }
        });
        ImageView iv_save = (ImageView) findViewById(R.id.iv_setting);
        iv_save.setImageResource(R.drawable.ic_add);
        courseStart = start;
        courseEnd = start;
        weekDay = intent.getIntExtra("weekDay", 1);
        title.setText("添加课程");
        act_course_name = (AutoCompleteTextView) findViewById(R.id.act_course_name);
        List<CourseInfoEntity> courseInfoEntities = DataBase.getDaoSession().getCourseInfoEntityDao().loadAll();
        List<String> courseNames = new ArrayList<>();
        for (CourseInfoEntity courseInfoEntity : courseInfoEntities) {
            courseNames.add(courseInfoEntity.getCourseName());
        }
        tv_course_class = (TextView) findViewById(R.id.tv_course_class);
        tv_course_class.setText(String.format(showWeek, Config.weekNames[weekDay - 1], courseStart, courseEnd));
        ArrayAdapter arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, courseNames);
        act_course_name.setAdapter(arrayAdapter);
        //条件选择器
        final List<String> weekDays = new ArrayList<String>();
        for (int i = 0; i < Config.weekNames.length; i++) {
            weekDays.add("星期" + Config.weekNames[i]);
        }
        final List<String> classes = new ArrayList<>();
        for (int i = 1; i <= 14; i++) {
            classes.add(Func.courseIndexToStr(i));
        }

        final NumberPicker np_week_start = (NumberPicker) findViewById(R.id.np_week_start);
        final NumberPicker np_week_end = (NumberPicker) findViewById(R.id.np_week_end);
        Spinner sp_week_type = (Spinner) findViewById(R.id.sp_week_type);
        sp_week_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                weekType = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        np_week_start.setMinValue(1);
        np_week_start.setMaxValue(50);
        np_week_end.setMinValue(1);
        np_week_end.setMaxValue(50);
        np_week_start.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                weekStart = newVal;
                if (weekEnd < weekStart) {
                    np_week_end.setValue(weekStart);
                    weekEnd = weekStart;
                }
            }
        });
        np_week_end.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                weekEnd = newVal;
                if (weekEnd < weekStart) {
                    np_week_start.setValue(weekEnd);
                    weekStart = weekEnd;
                }
            }
        });
        int weekNow = Func.getWeekNow();
        np_week_start.setValue(weekNow);
        np_week_end.setValue(weekNow);
        final OptionsPickerView pvOptions = new OptionsPickerView.Builder(this, new OptionsPickerView.OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int option2, int options3, View v) {
                //返回的分别是三个级别的选中位置
                if (option2 > options3)
                    options3 = option2;
                weekDay = options1 + 1;
                courseStart = option2;
                courseEnd = options3;
                String tx = String.format(showWeek, Config.weekNames[options1], classes.get(option2), classes.get(options3));
                tv_course_class.setText(tx);
            }
        })
                .setLabels("", "到", "")
                .build();
        pvOptions.setNPicker(weekDays, classes, classes);
        pvOptions.setSelectOptions(weekDay - 1, start - 1, start - 1);
        tv_course_class.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pvOptions.show();
            }
        });
    }

    /**
     * 保存课程
     */
    private void save() {
        String courseName = act_course_name.getText().toString();
        if (TextUtils.isEmpty(courseName)) {
            Toast.makeText(this, "课程名称不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        EditText et_location = (EditText) findViewById(R.id.et_location);
        String location = et_location.getText().toString();
        EditText et_teacher = (EditText) findViewById(R.id.et_teacher);
        String teacher = et_teacher.getText().toString();
        EditText et_grade = (EditText) findViewById(R.id.et_grade);
        String grade = et_grade.getText().toString();
        String courseNum = System.currentTimeMillis() + "";
        SharedPreferences prefer_jw = getSharedPreferences(Config.PREFER_JW, MODE_PRIVATE);
        int semester = prefer_jw.getInt(Config.JW_SEMESTER, 1);
        int schoolYearStart = prefer_jw.getInt(Config.JW_SCHOOL_YEAR, Calendar.getInstance().get(Calendar.YEAR));
        /*上课安排信息*/
        CourseEntity courseEntity = new CourseEntity();
        courseEntity.setCourseName(courseName);
        courseEntity.setClassRoom(location);
        courseEntity.setStartSection(courseStart);
        courseEntity.setEndSection(courseEnd);
        courseEntity.setDayOfWeek(weekDay);
        courseEntity.setWeekType(weekType);
        courseEntity.setCourseNum(courseNum);
        courseEntity.setSchoolStartYear(schoolYearStart);
        courseEntity.setSemester(semester);
        StringBuilder sb_smartPeriod = new StringBuilder(" ");
        if (weekType == 0) {
            for (int begin = weekStart; begin <= weekEnd; begin++) {
                sb_smartPeriod.append(begin).append(" ");
            }
        } else {
            if (weekType == 1 && weekStart % 2 == 0) weekStart += 1;
            if (weekType == 2 && weekStart % 2 != 0) weekStart += 1;
            for (int begin = weekStart; begin <= weekEnd; begin += 2) {
                sb_smartPeriod.append(begin).append(" ");
            }
        }
        courseEntity.setSmartPeriod(sb_smartPeriod.toString());
        courseEntity.setWeek(weekStart + "-" + weekEnd + (weekType == 1 ? "单" : weekType == 2 ? "双" : "") + "周");
        /*课程描述信息*/
        CourseInfoEntity infoEntity = new CourseInfoEntity();
        infoEntity.setCourseName(courseName);
        infoEntity.setCourseNum(courseNum);
        infoEntity.setGrade(grade);
        infoEntity.setTeacher(teacher);
        infoEntity.setSemester(semester);
        infoEntity.setSchoolYearStart(schoolYearStart);
        DataBase.getDaoSession().getCourseEntityDao().save(courseEntity);
        DataBase.getDaoSession().getCourseInfoEntityDao().save(infoEntity);
        act_course_name.getText().clear();
        et_teacher.setText("");
        et_grade.setText("");
        et_location.setText("");
        prefer_jw.edit().putBoolean(Config.IS_REFRESH, true).apply();
        Toast.makeText(this, "保存课程" + courseName + "成功", Toast.LENGTH_SHORT).show();
    }
}
