package com.jacknic.glut.activity.educational;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TextView;

import com.bigkoo.pickerview.OptionsPickerView;
import com.jacknic.glut.R;
import com.jacknic.glut.activity.BaseActivity;
import com.jacknic.glut.model.entity.CourseInfoEntity;
import com.jacknic.glut.util.Config;
import com.jacknic.glut.util.DataBase;
import com.jacknic.glut.util.Func;

import java.util.ArrayList;
import java.util.List;

public class AddCourseActivity extends BaseActivity {
    int weekStart = 1;
    int weekEnd = 1;
    int weekType = 0;
    final String showWeek = "星期%s  %s - %s节";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_course);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        TextView title = (TextView) findViewById(R.id.tv_toolbar_title);
        Intent intent = getIntent();
        int start = intent.getIntExtra("start", 1);
        weekStart = start;
        weekEnd = start;
        int weekDay = intent.getIntExtra("weekDay", 1);
        title.setText("添加课程");
        AutoCompleteTextView act_course_name = (AutoCompleteTextView) findViewById(R.id.act_course_name);
        List<CourseInfoEntity> courseInfoEntities = DataBase.getDaoSession().getCourseInfoEntityDao().loadAll();
        List<String> courseNames = new ArrayList<>();
        for (CourseInfoEntity courseInfoEntity : courseInfoEntities) {
            courseNames.add(courseInfoEntity.getCourseName());
        }
        final TextView tv_course_class = (TextView) findViewById(R.id.tv_course_class);
        tv_course_class.setText(String.format(showWeek, Config.weekNames[weekDay - 1], Func.courseIndexToStr(weekStart - 1), Func.courseIndexToStr(weekEnd - 1)));
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
                }
            }
        });
        np_week_end.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                weekEnd = newVal;
                if (weekEnd < weekStart) np_week_start.setValue(weekEnd);
            }
        });


        final OptionsPickerView pvOptions = new OptionsPickerView.Builder(this, new OptionsPickerView.OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int option2, int options3, View v) {
                //返回的分别是三个级别的选中位置
                if (option2 > options3)
                    options3 = option2;
                String tx = String.format(showWeek, Config.weekNames[options1], classes.get(option2), classes.get(options3));
                tv_course_class.setText(tx);
            }
        })
                .isDialog(true)
                .setLabels("", "到", "")
                .build();
        pvOptions.setNPicker(weekDays, classes, classes);
        pvOptions.setSelectOptions(weekDay - 1, start, start);
        tv_course_class.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pvOptions.show();
            }
        });
    }
}
