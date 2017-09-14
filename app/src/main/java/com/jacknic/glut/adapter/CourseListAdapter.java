package com.jacknic.glut.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.jacknic.glut.R;
import com.jacknic.glut.model.entity.CourseInfoEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * 课程列表适配器
 */
public class CourseListAdapter extends ArrayAdapter {
    private List<CourseInfoEntity> courseEntityList;

    public CourseListAdapter(@NonNull Context context, List<CourseInfoEntity> courseList) {
        super(context, R.layout.item_course);
        courseEntityList = courseList;
        if (courseEntityList == null) {
            courseEntityList = new ArrayList<>();
        }
    }

    @Override
    public int getCount() {
        return courseEntityList.size();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        CourseInfoEntity infoEntity = courseEntityList.get(position);
        View view;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_course, parent, false);
            convertView.setTag(convertView);
            view = convertView;
        } else {
            view = (View) convertView.getTag();
        }
        TextView tv_course_name = (TextView) view.findViewById(R.id.item_tv_course_name);
        tv_course_name.setText(infoEntity.getCourseName());
        TextView tv_teacher = (TextView) view.findViewById(R.id.item_tv_teacher);
        tv_teacher.setText(infoEntity.getTeacher());
        return view;
    }


}
