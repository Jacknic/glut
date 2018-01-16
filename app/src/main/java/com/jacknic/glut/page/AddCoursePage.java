package com.jacknic.glut.page;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bigkoo.pickerview.OptionsPickerView;
import com.jacknic.glut.R;
import com.jacknic.glut.model.entity.CourseEntity;
import com.jacknic.glut.model.entity.CourseInfoEntity;
import com.jacknic.glut.util.Config;
import com.jacknic.glut.util.DataBase;
import com.jacknic.glut.util.Func;
import com.jacknic.glut.util.ViewUtil;
import com.tencent.stat.StatService;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

/**
 * 添加课程页
 */
public class AddCoursePage extends Fragment {
    int courseStart = 1;
    int courseEnd = 1;
    int weekStart = 1;
    int weekEnd = 1;
    int weekType = 0;
    final String showWeek = "星期%s  %s - %s节";
    private int weekDay = 1;
    private AutoCompleteTextView actCourseName;
    private TextView tvCourseClass;
    private View page;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        StatService.trackBeginPage(getContext(), "添加课程页");
        page = inflater.inflate(R.layout.page_add_course, container, false);
        Bundle bundle = getArguments();
        int start = bundle.getInt("start", 1);
        courseStart = start;
        courseEnd = start;
        weekDay = bundle.getInt("weekDay", 1);
        ViewUtil.setTitle(getContext(), "添加课程");
        actCourseName = (AutoCompleteTextView) page.findViewById(R.id.act_course_name);
        List<CourseInfoEntity> courseInfoEntities = DataBase.getDaoSession().getCourseInfoEntityDao().loadAll();
        List<String> courseNames = new ArrayList<>();
        for (CourseInfoEntity courseInfoEntity : courseInfoEntities) {
            courseNames.add(courseInfoEntity.getCourseName());
        }
        tvCourseClass = (TextView) page.findViewById(R.id.tv_course_class);
        tvCourseClass.setText(String.format(showWeek, Config.weekNames[weekDay - 1], courseStart, courseEnd));
        ArrayAdapter arrayAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, courseNames);
        actCourseName.setAdapter(arrayAdapter);
        //条件选择器
        final List<String> weekDays = new ArrayList<String>();
        for (int i = 0; i < Config.weekNames.length; i++) {
            weekDays.add("星期" + Config.weekNames[i]);
        }
        final List<String> classes = new ArrayList<>();
        for (int i = 1; i <= 14; i++) {
            classes.add(Func.courseIndexToStr(i));
        }

        final NumberPicker npWeekStart = (NumberPicker) page.findViewById(R.id.np_week_start);
        final NumberPicker npWeekEnd = (NumberPicker) page.findViewById(R.id.np_week_end);
        Spinner sp_week_type = (Spinner) page.findViewById(R.id.sp_week_type);
        sp_week_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                weekType = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        npWeekStart.setMinValue(1);
        npWeekStart.setMaxValue(50);
        npWeekEnd.setMinValue(1);
        npWeekEnd.setMaxValue(50);
        npWeekStart.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                weekStart = newVal;
                if (weekEnd < weekStart) {
                    npWeekEnd.setValue(weekStart);
                    weekEnd = weekStart;
                }
            }
        });
        npWeekEnd.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                weekEnd = newVal;
                if (weekEnd < weekStart) {
                    npWeekStart.setValue(weekEnd);
                    weekStart = weekEnd;
                }
            }
        });
        int weekNow = Func.getWeekNow();
        npWeekStart.setValue(weekNow);
        npWeekEnd.setValue(weekNow);
        final OptionsPickerView pvOptions = new OptionsPickerView.Builder(getContext(), new OptionsPickerView.OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int option2, int options3, View v) {
                //返回的分别是三个级别的选中位置
                if (option2 > options3)
                    options3 = option2;
                weekDay = options1 + 1;
                courseStart = option2;
                courseEnd = options3;
                String tx = String.format(showWeek, Config.weekNames[options1], classes.get(option2), classes.get(options3));
                tvCourseClass.setText(tx);
            }
        })
                .setLabels("", "到", "")
                .build();
        pvOptions.setNPicker(weekDays, classes, classes);
        pvOptions.setSelectOptions(weekDay - 1, start - 1, start - 1);
        tvCourseClass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pvOptions.show();
            }
        });
        return page;
    }


    /**
     * 保存课程
     */
    private void save() {
        String courseName = actCourseName.getText().toString();
        if (TextUtils.isEmpty(courseName)) {
            Toast.makeText(getContext(), "课程名称不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        EditText et_location = (EditText) page.findViewById(R.id.et_location);
        String location = et_location.getText().toString();
        EditText et_teacher = (EditText) page.findViewById(R.id.et_teacher);
        String teacher = et_teacher.getText().toString();
        EditText et_grade = (EditText) page.findViewById(R.id.et_grade);
        String grade = et_grade.getText().toString();
        String courseNum = System.currentTimeMillis() + "";
        SharedPreferences prefer_jw = getContext().getSharedPreferences(Config.PREFER, MODE_PRIVATE);
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
        actCourseName.getText().clear();
        et_teacher.setText("");
        et_grade.setText("");
        et_location.setText("");
        prefer_jw.edit().putBoolean(Config.IS_REFRESH, true).apply();
        Toast.makeText(getContext(), "保存课程" + courseName + "成功", Toast.LENGTH_SHORT).show();
    }
}
