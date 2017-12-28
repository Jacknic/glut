package com.jacknic.glut.page;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.jacknic.glut.R;
import com.jacknic.glut.model.EduInfoModel;
import com.jacknic.glut.model.LoginModel;
import com.jacknic.glut.stacklibrary.RootFragment;
import com.jacknic.glut.util.Config;
import com.jacknic.glut.util.Func;
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
 * 用户登录
 */
public class LoginPage extends RootFragment {
    private EditText et_sid, et_password;
    private int flag;
    private EditText et_captcha;
    private ImageView iv_captcha;
    private final SharedPreferences prefer_jw = OkGo.getContext().getSharedPreferences(Config.PREFER_JW, MODE_PRIVATE);
    private ImageView iv_show_pwd;
    private AlertDialog login_dialog;
    private EduInfoModel eduInfoModel = new EduInfoModel();
    private boolean loginSuccess;
    private View page;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        page = inflater.inflate(R.layout.page_login, container, false);
        et_sid = (EditText) page.findViewById(R.id.et_sid);
        et_password = (EditText) page.findViewById(R.id.et_password);
        et_sid.setText(prefer_jw.getString(Config.SID, ""));
        flag = 0;//getIntent().getIntExtra("flag", 0);
        int[] tips_id = new int[]{R.string.txt_jw, R.string.txt_cw};
        ViewUtil.setTitle(getRoot(), "登录" + getString(tips_id[flag]));
        if (flag == Config.LOGIN_FLAG_JW) {
            showCaptcha();
        } else {
            // 只剩财务了
            et_password.setHint("默认密码，身份证号后6位");
        }
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
        page.findViewById(R.id.btn_login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                switch (flag) {
                    case Config.LOGIN_FLAG_JW:
                        loginJw();
                        break;
                    case Config.LOGIN_FLAG_CW:
                        login_cw();
                        break;
                }
            }
        });
        return page;
    }


    /**
     * 登录到财务处
     */
    private void login_cw() {
        final String sid = et_sid.getText().toString();
        final String password = et_password.getText().toString();
        AbsCallback callback = new StringCallback() {
            /**
             * 登录成功后进行的操作
             */
            @Override
            public void onSuccess(String s, Call call, Response response) {
                RootFragment rootFragment = Func.getRootFragment(getActivity());
                rootFragment.open(new HomePage());
            }

            @Override
            public void onError(Call call, Response response, Exception e) {
                iv_show_pwd.callOnClick();
                Toast.makeText(getContext(), "登录失败！", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAfter(String s, Exception e) {
                if (login_dialog.isShowing()) {
                    login_dialog.dismiss();
                }
            }
        };
        //登录
        LoginModel.loginCw(sid, password, callback);
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
                    Snackbar snackbar = Snackbar.make(et_sid, "登录失败!请检查输入信息", Snackbar.LENGTH_SHORT);
                    snackbar.show();
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
                Func.getRootFragment(getActivity()).dialogFragment(new HomePage());
                login_dialog.dismiss();
            }
        };
        eduInfoModel.getWeekInfo(callback);
    }

    /**
     * 显示验证码
     */

    private void showCaptcha() {
        SharedPreferences prefer_jw = getContext().getSharedPreferences(Config.PREFER_JW, MODE_PRIVATE);
        et_password.setText(prefer_jw.getString(Config.PASSWORD, ""));
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
        et_captcha.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    if (et_captcha.getText().toString().length() == 0) return;
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
                                iv_captcha.callOnClick();
                                Toast.makeText(getContext(), "验证码有误！", Toast.LENGTH_SHORT).show();
                            }
                        }
                    };
                    OkGo.get(Config.URL_JW_CAPTCHA_CHECK + et_captcha.getText().toString()).execute(checkCallback);
                }
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