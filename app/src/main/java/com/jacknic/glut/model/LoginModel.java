package com.jacknic.glut.model;

import android.content.SharedPreferences;

import com.alibaba.fastjson.JSONObject;
import com.jacknic.glut.util.Config;
import com.jacknic.glut.util.Func;
import com.jacknic.glut.util.PreferManager;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.AbsCallback;
import com.lzy.okgo.callback.StringCallback;

import okhttp3.Call;
import okhttp3.Response;
import okhttp3.internal.Util;

/**
 * 登录到教务处
 */

public class LoginModel {
    /**
     * 私有化
     */
    private LoginModel() {
    }

    /**
     * 登录到教务处并执行相应流程
     *
     * @param sid      学号
     * @param password 密码
     * @param captcha  验证码
     */
    public static void loginJW(final String sid, final String password, String captcha, final AbsCallback callback) {
//        登录操作
        OkGo.post(Config.URL_JW_LOGIN_CHECK)
                .params("groupId", "")
                .params("j_username", sid)
                .params("j_password", password)
                .params("j_captcha", captcha).execute(new StringCallback() {
            @Override
            public void onSuccess(String s, Call call, Response response) {
                //登录失败的情况
                callback.onError(call, response, null);
            }

            @Override
            public void onError(Call call, Response response, Exception e) {
                SharedPreferences.Editor editor = PreferManager.getPrefer().edit();
                editor.putString(Config.SID, sid);
                editor.putString(Config.PASSWORD_JW, password);
                editor.apply();
                Func.checkLoginStatus(callback);
            }
        });
    }


    /**
     * 登录图书馆
     *
     * @param sid      学号
     * @param password 密码
     * @param callback 回调
     */

    public static void loginTs(final String sid, final String password, final AbsCallback callback) {
        //  登录操作
        OkGo.post("http://202.193.80.181:8080/opac/reader/doLogin")
                .params("rdid", sid)
                //实际提交的密码是经MD5加密后的
                .params("rdPasswd", Util.md5Hex(password))
                .params("returnUrl", "")
                .params("password", "")
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        //登录失败回调
                        callback.onError(call, response, null);
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        //判断是否跳转，如果跳转则登录成功
                        if (response != null && "http://202.193.80.181:8080/opac/reader/space".equals(response.header("location"))) {
                            SharedPreferences prefer = PreferManager.getPrefer();
                            prefer.edit().putString(Config.SID, sid).putString(Config.PASSWORD_TS, password).apply();
                            callback.onSuccess("", call, response);
                        }
                        callback.onError(call, response, e);
                    }

                    @Override
                    public void onAfter(String s, Exception e) {
                        callback.onAfter(s, e);
                    }
                });
    }

    /**
     * 登录财务处
     *
     * @param sid      学号
     * @param password 密码
     * @param callback 回调
     */
    public static void loginCw(final String sid, final String password, final AbsCallback callback) {
        OkGo.post(Config.getQueryUrlCW("login", "0") + String.format("&sid=%s&spassword=%s", sid, password))
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        JSONObject json = null;
                        try {
                            json = JSONObject.parseObject(s);
                        } catch (Exception e) {
                            return;
                        }
                        Integer result = json.getInteger("result");
                        String msg = json.getString("msg");
                        //判断是否登录成功
                        if (result == 0) {
                            SharedPreferences.Editor editor = PreferManager.getPrefer().edit();
                            editor.putString(Config.STUDENT_ID, msg);
                            editor.putString(Config.SID, sid);
                            editor.putString(Config.PASSWORD_CW, password);
                            editor.putBoolean(Config.LOGIN_FLAG, true);
                            editor.apply();
                            callback.onSuccess(s, call, response);
                        } else {
                            onError(call, response, null);
                        }
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

}
