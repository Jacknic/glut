package com.jacknic.glut.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.jacknic.glut.beans.course.CourseBean;
import com.jacknic.glut.utils.Config;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.FileCallback;
import com.lzy.okgo.callback.StringCallback;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.regex.Pattern;

import okhttp3.Call;
import okhttp3.Response;

/**
 * 登录到教务处
 */

public class LoginModel_jw {
    private String sid;
    private String password;
    private final SharedPreferences prefer_jw = OkGo.getContext().getSharedPreferences(Config.PREFER_JW, Context.MODE_PRIVATE);


    /**
     * 登录到教务处并执行相应流程
     *
     * @param sid      学号
     * @param password 密码
     * @param captcha  验证码
     */
    public void login(String sid, String password, String captcha, StringCallback callback) {
        this.sid = sid;
        this.password = password;
//        登录操作
        OkGo.post(Config.URL_JW_LOGIN_CHECK)
                .params("groupId", "")
                .params("j_username", sid)
                .params("j_password", password)
                .params("j_captcha", captcha).execute(callback);
    }


    /**
     * 获取课程信息
     *
     * @return 请求
     */
    public void getCourses(final StringCallback callback) {
        StringCallback stringCallback = new StringCallback() {
            @Override
            public void onSuccess(String s, Call call, Response response) {
                /**
                 * 登录成功后执行的操作
                 */
                SharedPreferences.Editor editor = prefer_jw.edit();
                editor.putString(Config.SID, sid);
                editor.putString(Config.PASSWORD, password);
                editor.putBoolean(Config.LOGIN_FLAG, true);
                CourseModel courseModel = new CourseModel(s);
                editor.putInt(Config.JW_SCHOOL_YEAR, courseModel.getSchoolYear());
                editor.putInt(Config.JW_SEMESTER, courseModel.getSemester());
                editor.apply();
                ArrayList<CourseBean> courses = courseModel.getCourses();
                CourseDbModel courseDbModel = new CourseDbModel(OkGo.getContext());
                for (CourseBean course : courses) {
//                            Log.d("log", course.toString());
                    courseDbModel.insertCourse(course);
                }
                if (callback != null) {
                    callback.onSuccess(s, call, response);
                }
            }

            @Override
            public void onError(Call call, Response response, Exception e) {
                if (callback != null) {
                    callback.onError(call, response, e);
                }
                super.onError(call, response, e);
            }
        };
        OkGo.get("http://202.193.80.58:81/academic/student/currcourse/currcourse.jsdo").execute(callback);
    }

    /**
     * 获取教务空间头像
     */
    public void getHeaderImg(final FileCallback callback) {
        FileCallback fileCallback = new FileCallback() {
            @Override
            public void onSuccess(File file, Call call, Response response) {
                try {
                    Log.d("week", "------------------------------------------------");
                    Log.d("week", "获取头像");
                    String sid = prefer_jw.getString(Config.SID, "0");
                    File appPath = OkGo.getContext().getFilesDir();
                    File image = new File(appPath, sid + ".jpg");
                    FileInputStream fin = new FileInputStream(file);
                    FileOutputStream fout = new FileOutputStream(image);
                    System.out.println("源文件路径" + file.getAbsolutePath());
                    System.out.println("源文件大小" + file.length());
                    fin.getChannel().transferTo(0, fin.getChannel().size(), fout.getChannel());
                    Log.d("week", "文件保存路径" + image.getAbsolutePath());
                } catch (IOException e) {
                    Log.e("week", "获取学生头像失败");
                } finally {
                    file.delete();
                }
                if (callback != null) {
                    callback.onSuccess(file, call, response);
                }
            }

            @Override
            public void onError(Call call, Response response, Exception e) {
                if (callback != null) {
                    callback.onError(call, response, e);
                }
                super.onError(call, response, e);
            }
        };
        OkGo.get("http://202.193.80.58:81/academic/manager/studentinfo/showStudentImage.jsp").execute(fileCallback);
    }

    /**
     * 获取教学运行周的情况
     */

    public void getWeekInfo(final StringCallback callback) {
        StringCallback stringCallback = new StringCallback() {
            @Override
            public void onSuccess(String s, Call call, Response response) {
                SharedPreferences.Editor editor = prefer_jw.edit();
                Document document = Jsoup.parse(s);
                //获取当前周数
                Element curweek = document.select(".curweek strong").get(0);
                String str_week = curweek.text();
                int week = Pattern.matches("\\d+", str_week) ? Integer.parseInt(str_week) : 1;
                editor.putInt(Config.JW_WEEK_SELECT, week);
                Calendar calendar_now = Calendar.getInstance();
                int year_week_old = calendar_now.get(Calendar.WEEK_OF_YEAR);
                if (calendar_now.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
                    year_week_old -= 1;
                }
                editor.putInt(Config.JW_YEAR_WEEK_OLD, year_week_old);
                //获取最后周数
                Element ele_lastWeek = document.select(".week table tbody tr td").last();
                String str_lastWeek = ele_lastWeek.text();
                int lastWeek = Pattern.matches("\\d+", str_lastWeek) ? Integer.parseInt(str_lastWeek) : 25;
                editor.putInt(Config.JW_WEEK_END, lastWeek);
                Log.d("week", "当前周数" + str_week);
                Log.d("week", "最后一周" + str_lastWeek);
                editor.apply();
                if (callback != null) {
                    callback.onSuccess(s, call, response);
                }
            }

            @Override
            public void onError(Call call, Response response, Exception e) {
                if (callback != null) {
                    callback.onError(call, response, e);
                }
                super.onError(call, response, e);
            }
        };
        OkGo.get("http://202.193.80.58:81/academic/calendarinfo/viewCalendarInfo.do").execute(stringCallback);
    }

    /**
     * 获取学生信息
     */
    private void getStudentInfo(final StringCallback callback) {
        StringCallback stringCallback = new StringCallback() {
            @Override
            public void onSuccess(String s, Call call, Response response) {
                SharedPreferences.Editor editor = prefer_jw.edit();
                Log.d("okok", s);
                Document dom = Jsoup.parse(s);
                editor.apply();
                if (callback != null) {
                    callback.onSuccess(s, call, response);
                }
            }

            @Override
            public void onError(Call call, Response response, Exception e) {
                if (callback != null) {
                    callback.onError(call, response, e);
                }
                super.onError(call, response, e);
            }
        };
        OkGo.get(Config.URL_JW_STUDENT_INFO).setCallback(stringCallback);
    }

}
