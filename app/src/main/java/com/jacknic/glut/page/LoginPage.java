package com.jacknic.glut.page;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;

import com.jacknic.glut.R;
import com.jacknic.glut.model.EduInfoModel;
import com.jacknic.glut.model.LoginModel;
import com.jacknic.glut.stacklibrary.PageTool;
import com.jacknic.glut.util.Config;
import com.jacknic.glut.util.SnackbarTool;
import com.jacknic.glut.util.ViewUtil;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.AbsCallback;
import com.lzy.okgo.callback.AbsCallbackWrapper;
import com.lzy.okgo.callback.BitmapCallback;
import com.lzy.okgo.callback.StringCallback;

import java.util.Timer;
import java.util.TimerTask;

import okhttp3.Call;
import okhttp3.Response;

import static android.content.Context.MODE_PRIVATE;

/**
 * 用户登录页
 */
public class LoginPage extends BasePage {
    private EditText et_sid, et_password;
    private EditText et_captcha;
    private ImageView iv_captcha;
    private final SharedPreferences prefer_jw = OkGo.getContext().getSharedPreferences(Config.PREFER, MODE_PRIVATE);
    private ImageView iv_show_pwd;
    private AlertDialog login_dialog;
    private EduInfoModel eduInfoModel = new EduInfoModel();
    private boolean loginSuccess;

    @Override
    protected int getLayoutId() {
        mTitle = "登录教务";
        return R.layout.page_login;
    }

    @Override
    public void onStart() {
        super.onStart();
        ViewUtil.showToolbar((AppCompatActivity) getContext(), true);
        et_sid = (EditText) page.findViewById(R.id.et_sid);
        et_password = (EditText) page.findViewById(R.id.et_password);
        et_sid.setText(prefer_jw.getString(Config.SID, ""));
        showCaptcha();
        iv_show_pwd = (ImageView) page.findViewById(R.id.iv_show_pwd);
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
        page.findViewById(R.id.tv_login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(((AppCompatActivity) getContext()).getWindow().getDecorView().getWindowToken(), 0);
                }
                login_dialog = new AlertDialog.Builder(getContext())
                        .setTitle("登录中 ")
                        .setMessage("验证用户信息...")
                        .setCancelable(false)
                        .create();
                login_dialog.show();
                //12秒超时设置
                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        if (login_dialog.isShowing()) {
                            login_dialog.dismiss();
                        }
                    }
                }, 12000);
                loginJw();
            }
        });
    }

    /**
     * 登录到教务
     */
    private void loginJw() {
        et_captcha.clearFocus();
        String sid = et_sid.getText().toString();
        String password = et_password.getText().toString();
        String captcha = et_captcha.getText().toString();
        LoginModel.loginJW(sid, password, captcha, new StringCallback() {

            @Override
            public void onSuccess(String s, Call call, Response response) {
                loginSuccess = true;
            }

            @Override
            public void onAfter(String s, Exception e) {
                if (loginSuccess) {
                    getCourses();
                } else {
                    iv_captcha.callOnClick();
                    iv_show_pwd.callOnClick();
                    login_dialog.dismiss();
                    SnackbarTool.showShort("登录失败!请检查输入信息");
                }
            }
        });

    }

    /**
     * 获取课表
     */
    private void getCourses() {
        login_dialog.setMessage("获取课表...");
        AbsCallback callback = new AbsCallbackWrapper() {
            @Override
            public void onAfter(Object o, Exception e) {
                getStudentInfo();
            }
        };
        eduInfoModel.getCourses(callback);
    }

    /**
     * 获取学生信息
     */
    private void getStudentInfo() {
        login_dialog.setMessage("获取学生信息...");
        AbsCallback callback = new AbsCallbackWrapper() {
            @Override
            public void onAfter(Object s, Exception e) {
                getHeaderImg();
            }
        };
        eduInfoModel.getStudentInfo(callback);
    }

    /**
     * 获取教务空间头像
     */
    private void getHeaderImg() {
        login_dialog.setMessage("获取用户头像...");
        AbsCallback callback = new AbsCallbackWrapper() {
            @Override
            public void onAfter(Object o, Exception e) {
                getStudyProcess();
            }
        };
        eduInfoModel.getHeaderImg(callback);
    }

    /**
     * 获取学业进度
     */
    private void getStudyProcess() {
        login_dialog.setMessage("获取学业进度...");
        AbsCallbackWrapper callbackWrapper = new AbsCallbackWrapper() {
            @Override
            public void onAfter(Object o, Exception e) {
                getWeekInfo();
            }
        };
        eduInfoModel.getStudyProcess(callbackWrapper);
    }


    /**
     * 获取教学运行周的情况
     */
    private void getWeekInfo() {
        login_dialog.setMessage("获取运行周...");
        AbsCallback callback = new AbsCallbackWrapper() {
            @Override
            public void onAfter(Object o, Exception e) {
                PageTool.open(getContext(), new HomePage());
                login_dialog.dismiss();
            }
        };
        eduInfoModel.getWeekInfo(callback);
    }

    /**
     * 显示验证码
     */

    private void showCaptcha() {
        SharedPreferences prefer = getContext().getSharedPreferences(Config.PREFER, MODE_PRIVATE);
        et_password.setText(prefer.getString(Config.PASSWORD_JW, ""));
        final View captcha_layout = page.findViewById(R.id.captcha_layout);
        captcha_layout.setVisibility(View.VISIBLE);
        et_captcha = (EditText) page.findViewById(R.id.et_captcha);
        final ImageView iv_check_captcha = (ImageView) page.findViewById(R.id.iv_check_captcha);
        iv_check_captcha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                et_captcha.setText("");
            }
        });
        //验证码检验监听
        et_captcha.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (et_captcha.getText().toString().length() < 4) return;
                StringCallback checkCallback = new StringCallback() {
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
                        }
                    }
                };
                OkGo.get(Config.URL_JW_CAPTCHA_CHECK + et_captcha.getText().toString()).execute(checkCallback);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        getCaptcha();
        iv_captcha = (ImageView) page.findViewById(R.id.iv_captcha);
        iv_captcha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(AnimationUtils.loadAnimation(getContext(), android.R.anim.fade_in));
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
                ((ImageView) page.findViewById(R.id.iv_captcha)).setImageBitmap(bitmap);
            }
        });
    }
}