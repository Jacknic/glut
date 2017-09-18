package com.jacknic.glut.view.widget;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.support.annotation.IdRes;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.jacknic.glut.R;
import com.jacknic.glut.model.LoginModel;
import com.jacknic.glut.model.entity.CourseEntity;
import com.jacknic.glut.model.entity.CourseInfoEntity;
import com.jacknic.glut.util.Config;
import com.jacknic.glut.util.Func;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.AbsCallbackWrapper;
import com.lzy.okgo.callback.BitmapCallback;

import java.util.Calendar;

import okhttp3.Call;
import okhttp3.Response;

import static android.content.Context.MODE_PRIVATE;

/**
 * 对话框
 */

public class Dialogs {
    /**
     * 课程详情对话框
     */
    public static AlertDialog.Builder getCourseInfoDialog(final Activity activity, CourseEntity courseEntity, CourseInfoEntity infoBean) {
        LayoutInflater inflater = LayoutInflater.from(activity);
        View dialogView = inflater.inflate(R.layout.dialog_course, null, false);
        findAndSet(dialogView, R.id.dc_tv_course_name, infoBean.getCourseName());
        findAndSet(dialogView, R.id.dc_tv_classroom, courseEntity.getClassRoom());
        findAndSet(dialogView, R.id.dc_tv_teacher, infoBean.getTeacher());
        findAndSet(dialogView, R.id.dc_tv_weeks, courseEntity.getWeek() == null ? "" : courseEntity.getWeek());
        findAndSet(dialogView, R.id.dc_tv_courses, Func.courseIndexToStr(courseEntity.getStartSection()) + "-"
                + Func.courseIndexToStr(courseEntity.getEndSection()));
        findAndSet(dialogView, R.id.dc_tv_grade, infoBean.getGrade());
        AlertDialog.Builder dialog = new AlertDialog.Builder(activity)
                .setView(dialogView);
        return dialog;
    }

    //设置文本
    private static void findAndSet(View parent, @IdRes int id, String text) {
        TextView textView = (TextView) parent.findViewById(id);
        textView.setText(text);
    }

    /**
     * 改变当前周对话框
     */
    public static AlertDialog.Builder getChangeWeekDialog(Activity activity) {
        LinearLayout dialogView = (LinearLayout) activity.getLayoutInflater().inflate(R.layout.dialog_change_week, null);
        final SharedPreferences prefer_jw = OkGo.getContext().getSharedPreferences(Config.PREFER_JW, Context.MODE_PRIVATE);
        int jw_week_end = prefer_jw.getInt(Config.JW_WEEK_END, 30);
        int select_week = prefer_jw.getInt(Config.JW_WEEK_SELECT, 30);
        final Calendar calendar = Calendar.getInstance();
        int year_week_old = prefer_jw.getInt(Config.JW_YEAR_WEEK_OLD, calendar.get(Calendar.WEEK_OF_YEAR));
        int year_week_now = calendar.get(Calendar.WEEK_OF_YEAR);
        int week_now = select_week + (year_week_now - year_week_old);
        if (year_week_now > year_week_old && calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
            week_now -= 1;
        }
        final NumberPicker max_week = (NumberPicker) dialogView.findViewById(R.id.max_week);
        max_week.setMinValue(1);
        max_week.setMaxValue(50);
        max_week.setValue(jw_week_end);
        final NumberPicker curr_week = (NumberPicker) dialogView.findViewById(R.id.curr_week);
        curr_week.setMinValue(1);
        curr_week.setMaxValue(jw_week_end);
        curr_week.setValue(week_now);
        max_week.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                curr_week.setMaxValue(newVal);
            }
        });
        AlertDialog.Builder builder = new AlertDialog.Builder(activity)
                .setView(dialogView);
        builder.setNegativeButton(android.R.string.cancel, null);
        builder.setPositiveButton("修改", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int select_week = curr_week.getValue();
                int end_week = max_week.getValue();
                SharedPreferences.Editor edit_jw = prefer_jw.edit();
                edit_jw.putInt(Config.JW_WEEK_SELECT, select_week);
                int year_week_old = calendar.get(Calendar.WEEK_OF_YEAR);
                if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
                    year_week_old -= 1;
                }
                edit_jw.putInt(Config.JW_YEAR_WEEK_OLD, year_week_old);
                edit_jw.putBoolean(Config.IS_REFRESH, true);
                edit_jw.putInt(Config.JW_WEEK_END, end_week);
                edit_jw.apply();
            }
        });
        return builder;
    }

    /**
     * 教务登录对话框
     */
    public static AlertDialog getLoginJw(Activity activity, final AbsCallbackWrapper callback) {
        LinearLayout login_view = (LinearLayout) LayoutInflater.from(activity).inflate(R.layout.dialog_login, null, false);
        SharedPreferences prefer_jw = activity.getSharedPreferences(Config.PREFER_JW, MODE_PRIVATE);
        String sid = prefer_jw.getString(Config.SID, "");
        String password = prefer_jw.getString(Config.PASSWORD, "");
        final EditText et_sid = (EditText) login_view.findViewById(R.id.et_sid);
        final EditText et_password = (EditText) login_view.findViewById(R.id.et_password);
        et_sid.setText(sid);
        et_password.setText(password);
        final EditText et_captcha = (EditText) login_view.findViewById(R.id.et_captcha);
        final AlertDialog.Builder builder = new AlertDialog.Builder(activity)
                .setView(login_view);
        final ImageView iv_captcha = (ImageView) login_view.findViewById(R.id.iv_captcha);

        iv_captcha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OkGo.get(Config.URL_JW_CAPTCHA).execute(new BitmapCallback() {
                    @Override
                    public void onSuccess(Bitmap bitmap, Call call, Response response) {
                        iv_captcha.setImageBitmap(bitmap);
                    }
                });
            }
        });
        iv_captcha.callOnClick();

        final ImageView iv_show_pwd = (ImageView) login_view.findViewById(R.id.iv_show_pwd);
        //显示、隐藏密码
        iv_show_pwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int inputType = et_password.getInputType();
                if (inputType == (InputType.TYPE_TEXT_VARIATION_WEB_PASSWORD | 1)) {
                    et_password.setInputType(InputType.TYPE_TEXT_VARIATION_WEB_EDIT_TEXT);
                    iv_show_pwd.setColorFilter(0xff222222);
                } else {
                    iv_show_pwd.setColorFilter(0xffd9d9d9);
                    et_password.setInputType(InputType.TYPE_TEXT_VARIATION_WEB_PASSWORD | 1);
                }
                et_password.setSelection(et_password.getText().length());
            }
        });
        Button btn_login = (Button) login_view.findViewById(R.id.btn_login);
        final AlertDialog dialog = builder.create();
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String sid = et_sid.getText().toString();
                String password = et_password.getText().toString();
                String captcha = et_captcha.getText().toString();
                //  登录操作
                LoginModel.loginJW(sid, password, captcha, callback);
                dialog.dismiss();
            }
        });
        return dialog;
    }

    /**
     * 图书登录对话框
     */
    public static AlertDialog getLoginTs(final Activity activity, final AbsCallbackWrapper callback) {
        LinearLayout login_view = (LinearLayout) LayoutInflater.from(activity).inflate(R.layout.dialog_login, null, false);
        login_view.findViewById(R.id.captcha_layout).setVisibility(View.GONE);
        SharedPreferences prefer_jw = activity.getSharedPreferences(Config.PREFER_JW, MODE_PRIVATE);
        String sid = prefer_jw.getString(Config.SID, "");
        final SharedPreferences prefer_ts = activity.getSharedPreferences(Config.PREFER_TS, MODE_PRIVATE);
        String password = prefer_ts.getString(Config.PASSWORD, "");
        final EditText et_sid = (EditText) login_view.findViewById(R.id.et_sid);
        final EditText et_password = (EditText) login_view.findViewById(R.id.et_password);
        et_sid.setText(sid);
        et_password.setHint("默认密码123456");
        et_password.setText(password);
        final AlertDialog.Builder builder = new AlertDialog.Builder(activity)
                .setView(login_view);
        final ImageView iv_show_pwd = (ImageView) login_view.findViewById(R.id.iv_show_pwd);
        //显示、隐藏密码
        iv_show_pwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int inputType = et_password.getInputType();
                if (inputType == (InputType.TYPE_TEXT_VARIATION_WEB_PASSWORD | 1)) {
                    et_password.setInputType(InputType.TYPE_TEXT_VARIATION_WEB_EDIT_TEXT);
                    iv_show_pwd.setColorFilter(0xff222222);
                } else {
                    iv_show_pwd.setColorFilter(0xffd9d9d9);
                    et_password.setInputType(InputType.TYPE_TEXT_VARIATION_WEB_PASSWORD | 1);
                }
                et_password.setSelection(et_password.getText().length());
            }
        });
        Button btn_login = (Button) login_view.findViewById(R.id.btn_login);
        final AlertDialog dialog = builder.create();
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String sid = et_sid.getText().toString();
                final String password = et_password.getText().toString();
                LoginModel.loginTs(sid, password, callback);
                dialog.dismiss();
            }
        });
        return dialog;
    }
}
