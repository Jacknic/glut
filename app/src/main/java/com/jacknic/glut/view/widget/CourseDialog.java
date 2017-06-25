package com.jacknic.glut.view.widget;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.IdRes;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.jacknic.glut.R;
import com.jacknic.glut.model.entity.CourseEntity;
import com.jacknic.glut.model.entity.CourseInfoEntity;
import com.jacknic.glut.util.Func;

/**
 * 课程详情对话框
 */

class CourseDialog {
    static Dialog getDialog(final Activity activity, CourseEntity courseEntity, CourseInfoEntity infoBean) {
        LayoutInflater inflater = LayoutInflater.from(activity);
        final View dialogView = inflater.inflate(R.layout.dialog_course, null, false);
        findAndSet(dialogView, R.id.dc_tv_course_name, courseEntity.getCourseName());
        findAndSet(dialogView, R.id.dc_tv_classroom, courseEntity.getClassRoom());
        findAndSet(dialogView, R.id.dc_tv_teacher, infoBean.getTeacher());
        findAndSet(dialogView, R.id.dc_tv_weeks, courseEntity.getWeek());
        findAndSet(dialogView, R.id.dc_tv_courses, Func.courseIndexToStr(courseEntity.getStartSection()) + "-" + Func.courseIndexToStr(courseEntity.getEndSection()));
        findAndSet(dialogView, R.id.dc_tv_grade, infoBean.getGrade());
        final Dialog dialog = new AlertDialog.Builder(activity)
                .setView(dialogView)
                .create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable());
        dialogView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        return dialog;
    }

    private static void findAndSet(View parent, @IdRes int id, String text) {
        TextView textView = (TextView) parent.findViewById(id);
        textView.setText(text);
    }
}
