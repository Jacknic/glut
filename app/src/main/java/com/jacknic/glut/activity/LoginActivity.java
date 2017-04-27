package com.jacknic.glut.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.jacknic.glut.R;
import com.jacknic.glut.beans.course.CourseBean;
import com.jacknic.glut.beans.course.CourseInfoBean;
import com.jacknic.glut.model.CourseDbModel;
import com.jacknic.glut.model.CourseInfoDbModel;
import com.jacknic.glut.model.CourseModel;
import com.jacknic.glut.model.StudentInfoModel;
import com.jacknic.glut.utils.ActivityUtil;
import com.jacknic.glut.utils.Config;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.AbsCallbackWrapper;
import com.lzy.okgo.callback.BitmapCallback;
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
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Pattern;

import okhttp3.Call;
import okhttp3.Response;

/**
 * 用户登录
 */
public class LoginActivity extends BaseActivity {
    private EditText et_sid, et_password;
    TextView tv_toolbar_title;
    private int flag;
    private EditText et_captcha;
    private ImageView iv_captcha;
    private final SharedPreferences prefer_jw = OkGo.getContext().getSharedPreferences(Config.PREFER_JW, Context.MODE_PRIVATE);
    private ImageView iv_show_pwd;
    private AlertDialog login_dialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        et_sid = (EditText) findViewById(R.id.et_sid);
        et_password = (EditText) findViewById(R.id.et_password);
        et_sid.setText(prefer_jw.getString(Config.SID, ""));
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        tv_toolbar_title = (TextView) findViewById(R.id.tv_toolbar_title);
        setStatusView();
        flag = getIntent().getIntExtra("flag", 0);
        int[] tips_id = new int[]{R.string.txt_jw, R.string.txt_financial, R.string.txt_library,};
        tv_toolbar_title.setText("登录" + getString(tips_id[flag]));
        if (flag == Config.LOGIN_FLAG_JW) {
            showCaptcha();
        }
        iv_show_pwd = (ImageView) findViewById(R.id.iv_show_pwd);
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
        findViewById(R.id.btn_login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login_dialog = new AlertDialog.Builder(LoginActivity.this)
                        .setTitle("登录中 ")
                        .setMessage("验证用户信息...")
                        .setCancelable(false)
                        .create();
                login_dialog.show();
                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        if (login_dialog.isShowing()) {
                            login_dialog.dismiss();
                        }
                    }
                }, 12000);
                switch (flag) {
                    case Config.LOGIN_FLAG_JW:
                        login_jw();
                        break;
                    case Config.LOGIN_FLAG_CW:
                        login_cw();
                        break;
                    case Config.LOGIN_FLAG_TS:
                        login_ts();
                        break;
                }
            }
        });
    }

    /**
     * 登录到图书
     * <br/>暂时不做这部分
     */
    private void login_ts() {
    }

    /**
     * 登录到财务处
     */
    private void login_cw() {
        OkGo.post(Config.getQueryUrlCW("login", "0") + String.format("&sid=%s&spassword=%s", et_sid.getText().toString(), et_password.getText().toString()))
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        JSONObject json = JSONObject.parseObject(s);
                        Integer result = json.getInteger("result");
                        String msg = json.getString("msg");
                        if (result == 0) {
                            SharedPreferences prefer_cw = getSharedPreferences(Config.PREFER_CW, MODE_PRIVATE);
                            SharedPreferences.Editor editor = prefer_cw.edit();
                            editor.putString(Config.STUDENTID, msg);
                            editor.putString(Config.SID, et_sid.getText().toString());
                            editor.putString(Config.PASSWORD, et_password.getText().toString());
                            editor.putBoolean(Config.LOGIN_FLAG, true);
                            editor.apply();
                            ActivityUtil.lunchActivity(LoginActivity.this, MainActivity.class);
                            ActivityUtil.cleanActivities();
                            finish();
                        } else {
                            iv_show_pwd.callOnClick();
                            Toast.makeText(LoginActivity.this, getString(R.string.error_login_fail), Toast.LENGTH_SHORT).show();
                        }
                        login_dialog.dismiss();
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        Toast.makeText(LoginActivity.this, "连接服务器失败", Toast.LENGTH_SHORT).show();
                        login_dialog.dismiss();
                    }
                });
    }

    /**
     * 登录到教务
     */
    private void login_jw() {
        et_captcha.clearFocus();
        String sid = et_sid.getText().toString();
        String password = et_password.getText().toString();
        String captcha = et_captcha.getText().toString();
//        登录操作
        OkGo.post(Config.URL_JW_LOGIN_CHECK)
                .params("groupId", "")
                .params("j_username", sid)
                .params("j_password", password)
                .params("j_captcha", captcha).execute(new AbsCallbackWrapper<Object>() {
            @Override
            public void onError(Call call, Response response, Exception e) {
                getCourses();
            }

            /**
             * 获取课表
             */
            private void getCourses() {
                OkGo.get("http://202.193.80.58:81/academic/student/currcourse/currcourse.jsdo").execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        /**
                         * 登录成功后执行的操作
                         */
                        SharedPreferences.Editor editor = prefer_jw.edit();
                        CourseModel courseModel = new CourseModel(s);
                        editor.putInt(Config.JW_SCHOOL_YEAR, courseModel.getSchoolYear());
                        editor.putInt(Config.JW_SEMESTER, courseModel.getSemester());
                        editor.putString(Config.SID, et_sid.getText().toString());
                        editor.putString(Config.PASSWORD, et_password.getText().toString());
                        editor.apply();
                        //插入课表
                        ArrayList<CourseBean> courses = courseModel.getCourses();
                        CourseDbModel courseDbModel = new CourseDbModel(OkGo.getContext());
                        for (CourseBean course : courses) {
//                            Log.d("log", course.toString());
                            courseDbModel.insertCourse(course);
                        }
                        login_dialog.setMessage("获取课表...");
                        courseDbModel.close();
                        //课表信息
                        CourseInfoDbModel infoDbModel = new CourseInfoDbModel(getApplicationContext());
                        for (CourseInfoBean courseInfoBean : courseModel.getCourseInfoList()) {
                            System.out.println(courseInfoBean);
                            infoDbModel.insertCourseInfo(courseInfoBean);
                        }
                        infoDbModel.close();
//                        ArrayList<CourseInfoBean> courseInfoList =

                        getStudentInfo();
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        iv_captcha.callOnClick();
                        iv_show_pwd.callOnClick();
                        login_dialog.dismiss();
                        Snackbar snackbar = Snackbar.make(et_sid, "登录失败!请检查输入信息", Snackbar.LENGTH_SHORT);
                        snackbar.show();
                    }
                });
            }

            /**
             * 获取教务空间头像
             */
            private void getHeaderImg() {
                FileCallback fileCallback = new FileCallback() {
                    @Override
                    public void onSuccess(File file, Call call, Response response) {
                        login_dialog.setMessage("获取头像...");
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
                        getStudyProcess();
                    }

                };
                OkGo.get("http://202.193.80.58:81/academic/manager/studentinfo/showStudentImage.jsp").execute(fileCallback);
            }

            /**
             * 获取学业进度
             */
            private void getStudyProcess() {
                login_dialog.setMessage("获取学业进度...");
                OkGo.get("http://202.193.80.58:81/academic/manager/score/studentStudyProcess.do").execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        Document document = Jsoup.parse(s);
                        Element table = document.select("table.datalist").get(0);
                        File study_process = new File(getFilesDir(), "study_process");
                        try {
                            FileOutputStream fout = new FileOutputStream(study_process, false);
                            fout.write(table.toString().getBytes());
                        } catch (IOException e) {
                            Log.d("okgo.get", "读写错误、无法写入学业进度信息");
                        }
                        getWeekInfo();
                    }
                });
            }

            /**
             * 获取教学运行周的情况
             */
            private void getWeekInfo() {
                StringCallback stringCallback = new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        SharedPreferences.Editor editor = prefer_jw.edit();
                        Document document = Jsoup.parse(s);
                        login_dialog.setMessage("获取运行周...");
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
                        editor.putBoolean(Config.LOGIN_FLAG, true);
                        editor.apply();
                        ActivityUtil.lunchActivity(LoginActivity.this, MainActivity.class);
                        login_dialog.dismiss();
                    }
                };
                OkGo.get("http://202.193.80.58:81/academic/calendarinfo/viewCalendarInfo.do").execute(stringCallback);
            }

            /**
             * 获取学生信息
             */
            private void getStudentInfo() {
                StringCallback stringCallback = new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        login_dialog.setMessage("获取学生信息...");
                        StudentInfoModel infoModel = new StudentInfoModel();
                        infoModel.saveToPrefer(infoModel.getStudentInfo(s));
                    }

                    @Override
                    public void onAfter(String s, Exception e) {
                        getHeaderImg();
                    }
                };
                OkGo.get(Config.URL_JW_STUDENT_INFO).execute(stringCallback);
            }
        });

    }


    /**
     * 显示验证码
     */

    private void showCaptcha() {
        SharedPreferences prefer_jw = getSharedPreferences(Config.PREFER_JW, MODE_PRIVATE);
        et_password.setText(prefer_jw.getString(Config.PASSWORD, ""));
        final View captcha_layout = findViewById(R.id.captcha_layout);
        captcha_layout.setVisibility(View.VISIBLE);
        et_captcha = (EditText) findViewById(R.id.et_captcha);
        final ImageView iv_check_captcha = (ImageView) findViewById(R.id.iv_check_captcha);
        iv_check_captcha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                et_captcha.setText("");
            }
        });
        //验证码检验监听
        et_captcha.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    if (et_captcha.getText().toString().length() == 0) return;
                    OkGo.get(Config.URL_JW_CAPTCHA_CHECK + et_captcha.getText().toString()).execute(new StringCallback() {
                        @Override
                        public void onSuccess(String s, Call call, Response response) {
                            if ("true".equalsIgnoreCase(s)) {
                                iv_check_captcha.setImageResource(R.drawable.ic_check_circle);
                                iv_check_captcha.setColorFilter(getResources().getColor(R.color.green));
                                iv_check_captcha.setClickable(false);
                            } else {
                                iv_check_captcha.setImageResource(R.drawable.ic_cancel);
                                iv_check_captcha.setColorFilter(getResources().getColor(R.color.red));
                                iv_check_captcha.setClickable(true);
                                iv_captcha.callOnClick();
                                Toast.makeText(LoginActivity.this, "验证码有误！", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
        getCaptcha();
        iv_captcha = (ImageView) findViewById(R.id.iv_captcha);
        iv_captcha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(AnimationUtils.loadAnimation(LoginActivity.this, android.R.anim.fade_in));
                getCaptcha();
            }
        });
    }

    /**
     * 获取验证码
     */
    private void getCaptcha() {
        OkGo.get(Config.URL_JW_CAPTCHA).execute(new BitmapCallback() {
            @Override
            public void onSuccess(Bitmap bitmap, Call call, Response response) {
                ((ImageView) findViewById(R.id.iv_captcha)).setImageBitmap(bitmap);
            }
        });
    }
}

