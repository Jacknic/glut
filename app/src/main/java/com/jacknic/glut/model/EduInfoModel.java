package com.jacknic.glut.model;

import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.util.Log;

import com.jacknic.glut.model.dao.CourseDao;
import com.jacknic.glut.model.dao.CourseInfoDao;
import com.jacknic.glut.model.entity.CourseEntity;
import com.jacknic.glut.util.Config;
import com.jacknic.glut.util.Func;
import com.jacknic.glut.util.PreferManager;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.AbsCallback;
import com.lzy.okgo.callback.FileCallback;
import com.lzy.okgo.callback.StringCallback;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.regex.Pattern;

import okhttp3.Call;
import okhttp3.Response;

/**
 * 教务系统信息获取
 */

public class EduInfoModel {
    private SharedPreferences prefer = PreferManager.getPrefer();

    /**
     * 获取课表
     */
    public void getCourses(@NonNull final AbsCallback callback) {
        OkGo.get(Config.URL_JW_COURSE).execute(new StringCallback() {
            @Override
            public void onSuccess(String s, Call call, Response response) {
                CourseModel courseModel = new CourseModel(s);
                SharedPreferences.Editor editor = prefer.edit();
                editor.putInt(Config.JW_SCHOOL_YEAR, courseModel.getSchoolYear());
                editor.putInt(Config.JW_SEMESTER, courseModel.getSemester());
                editor.apply();
                //插入课表
                ArrayList<CourseEntity> courses = courseModel.getCourses();
                new CourseDao().insertCourses(courses);
                //课表信息
                new CourseInfoDao().insertCourseInfoList(courseModel.getCourseInfoList());
                callback.onSuccess(s, call, response);
            }

            @Override
            public void onError(Call call, Response response, Exception e) {
                callback.onError(call, response, e);
            }

            @Override
            public void onAfter(String s, Exception e) {
                callback.onAfter(s, e);
            }
        });
    }

    /**
     * 获取教务空间头像
     */
    public void getHeaderImg(@NonNull final AbsCallback callback) {
        String sid = prefer.getString(Config.SID, "0");
        File filesDir = OkGo.getContext().getFilesDir();
        FileCallback fileCallback = new FileCallback(filesDir.getAbsolutePath(), sid + ".jpg") {
            @Override
            public void onError(Call call, Response response, Exception e) {
                System.err.println("获取头像产生了错误----");
                System.err.println("err" + e.getMessage());
                super.onError(call, response, e);
            }

            @Override
            public void onSuccess(File file, Call call, Response response) {
                Log.d("week", "------------------------------------------------");
                Log.d("week", "获取头像");
                System.out.println("源文件路径" + file.getAbsolutePath());
                System.out.println("源文件大小" + file.length());
                Log.d("week", "文件保存路径" + file.getAbsolutePath());
            }

            @Override
            public void onAfter(File file, Exception e) {
                callback.onAfter(file, e);
            }
        };
        OkGo.get("http://jw.glut.edu.cn/academic/manager/studentinfo/showStudentImage.jsp").execute(fileCallback);
    }

    /**
     * 获取学业进度
     */
    public void getStudyProcess(@NonNull final AbsCallback callback) {
        OkGo.get("http://jw.glut.edu.cn/academic/manager/score/studentStudyProcess.do").execute(new StringCallback() {
            @Override
            public void onSuccess(String s, Call call, Response response) {
                Document document = Jsoup.parse(s);
                Element table = document.select("table.datalist").get(0);
                File study_process = new File(OkGo.getContext().getFilesDir(), "study_process");
                try {
                    FileOutputStream fout = new FileOutputStream(study_process, false);
                    fout.write(table.toString().getBytes());
                } catch (IOException e) {
                    Log.d("okgo.get", "读写错误、无法写入学业进度信息");
                }
                callback.onSuccess(s, call, response);
            }

            @Override
            public void onError(Call call, Response response, Exception e) {
                callback.onError(call, response, e);
            }

            @Override
            public void onAfter(String s, Exception e) {
                callback.onAfter(s, e);
            }
        });
    }

    /**
     * 获取教学运行周的情况
     */
    public void getWeekInfo(@NonNull final AbsCallback callback) {
        StringCallback stringCallback = new StringCallback() {
            @Override
            public void onSuccess(String s, Call call, Response response) {
                SharedPreferences.Editor editor = prefer.edit();
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
                int lastWeek = Func.getInt(str_lastWeek, 30);
                editor.putInt(Config.JW_WEEK_END, lastWeek);
                Log.d("week", "当前周数" + str_week);
                Log.d("week", "最后一周" + str_lastWeek);
                editor.putBoolean(Config.LOGIN_FLAG, true);
                editor.apply();
                callback.onSuccess(s, call, response);
            }

            @Override
            public void onError(Call call, Response response, Exception e) {
                callback.onError(call, response, e);
            }

            @Override
            public void onAfter(String s, Exception e) {
                callback.onAfter(s, e);
            }
        };
        OkGo.get(Config.URL_JW_CALENDAR).execute(stringCallback);
    }

    /**
     * 获取学生信息
     */
    public void getStudentInfo(@NonNull final AbsCallback callback) {
        StringCallback stringCallback = new StringCallback() {
            @Override
            public void onSuccess(String s, Call call, Response response) {
                StudentInfoModel infoModel = new StudentInfoModel();
                infoModel.saveToPrefer(infoModel.getStudentInfo(s));
                callback.onSuccess(s, call, response);
            }

            @Override
            public void onError(Call call, Response response, Exception e) {
                callback.onError(call, response, e);
            }

            @Override
            public void onAfter(String s, Exception e) {
                callback.onAfter(s, e);
            }
        };
        OkGo.get(Config.URL_JW_STUDENT_INFO).execute(stringCallback);
    }
}
