package com.jacknic.glut.view.widget;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.jacknic.glut.R;
import com.jacknic.glut.util.Config;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.AbsCallbackWrapper;
import com.lzy.okgo.callback.BitmapCallback;
import com.lzy.okgo.callback.StringCallback;

import okhttp3.Call;
import okhttp3.Response;
import okhttp3.internal.Util;

import static android.content.Context.MODE_PRIVATE;

/**
 * 用户登录窗
 */

public class LoginDialog {
    /**
     * 教务登录对话框
     *
     * @param activity
     * @param callback
     * @return
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
                OkGo.post(Config.URL_JW_LOGIN_CHECK)
                        .params("groupId", "")
                        .params("j_username", sid)
                        .params("j_password", password)
                        .params("j_captcha", captcha).execute(callback);
                dialog.dismiss();
            }
        });
        return dialog;
    }

    /**
     * 图书登录对话框
     *
     * @param activity
     * @param callback
     * @return
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
                //  登录操作
                OkGo.post("http://202.193.80.181:8080/opac/reader/doLogin")
                        .params("rdid", sid)
                        .params("rdPasswd", Util.md5Hex(password))
                        .params("returnUrl", "")
                        .params("password", "")
                        .execute(new StringCallback() {
                            @Override
                            public void onSuccess(String s, Call call, Response response) {
                                iv_show_pwd.callOnClick();
                                callback.onError(call, response, new Exception("登录失败"));
                                Toast.makeText(activity, "登录失败", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onError(Call call, Response response, Exception e) {
                                super.onError(call, response, e);
                                if (response != null && "http://202.193.80.181:8080/opac/reader/space".equals(response.header("location"))) {
                                    Toast.makeText(activity, "登录成功", Toast.LENGTH_SHORT).show();
                                    prefer_ts.edit().putString(Config.SID, sid).putString(Config.PASSWORD, password).apply();
                                    callback.onSuccess(call, call, response);
                                }
                                callback.onError(call, response, e);
                            }

                            @Override
                            public void onAfter(String s, Exception e) {
                                super.onAfter(s, e);
                                dialog.dismiss();
                                callback.onAfter(s, e);
                            }
                        });
            }
        });
        return dialog;
    }

}
