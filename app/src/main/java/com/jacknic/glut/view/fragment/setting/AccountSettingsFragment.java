package com.jacknic.glut.view.fragment.setting;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jacknic.glut.R;
import com.jacknic.glut.model.entity.CourseEntity;
import com.jacknic.glut.model.entity.CourseInfoEntity;
import com.jacknic.glut.page.StartPage;
import com.jacknic.glut.util.Config;
import com.jacknic.glut.util.DataBase;
import com.jacknic.glut.util.Func;
import com.lzy.okgo.OkGo;

import java.io.File;

import okhttp3.HttpUrl;

import static android.content.Context.MODE_PRIVATE;


/**
 * 账户设置
 */
public class AccountSettingsFragment extends Fragment implements View.OnClickListener {

    private View fragment;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        fragment = inflater.inflate(R.layout.frag_setting_account, container, false);
        setOnClick();
        return fragment;
    }

    private void setOnClick() {
        fragment.findViewById(R.id.setting_btn_clear_cw).setOnClickListener(this);
        fragment.findViewById(R.id.setting_btn_clear_ts).setOnClickListener(this);
        fragment.findViewById(R.id.setting_btn_clear_all).setOnClickListener(this);
    }

    //控件点击事件处理
    @Override
    public void onClick(final View v) {
        AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setTitle("确定清除?")
                .setNegativeButton(android.R.string.cancel, null)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (v.getId()) {
                            case R.id.setting_btn_clear_cw:
                                clear_cw();
                                break;
                            case R.id.setting_btn_clear_ts:
                                clear_ts();
                                break;
                            case R.id.setting_btn_clear_all:
                                logout();
                                break;
                        }
                        if (v.getId() != R.id.setting_btn_clear_all) {
                            SharedPreferences setting = getActivity().getSharedPreferences(Config.PREFER_SETTING, MODE_PRIVATE);
                            setting.edit().putBoolean(Config.IS_REFRESH, true).apply();
                        }
                    }
                }).create();
        dialog.show();


    }

    /**
     * 清除财务信息
     */
    private void clear_cw() {
        getContext().getSharedPreferences(Config.PREFER_CW, Context.MODE_PRIVATE).edit().clear().apply();
    }

    /**
     * 清除图书信息
     */
    private void clear_ts() {
        OkGo.getInstance().getCookieJar().getCookieStore().removeCookie(HttpUrl.parse("http://202.193.80.181:8080"));
        getContext().getSharedPreferences(Config.PREFER_TS, Context.MODE_PRIVATE).edit().clear().apply();
    }

    /**
     * 清除登录信息
     */
    private void logout() {
        //清除数据表
        DataBase.getDaoSession().deleteAll(CourseEntity.class);
        DataBase.getDaoSession().deleteAll(CourseInfoEntity.class);
        SharedPreferences prefer_jw = getContext().getSharedPreferences(Config.PREFER_JW, Context.MODE_PRIVATE);
        clear_cw();
        clear_ts();
        File filesDir = getContext().getFilesDir();
        Func.deleteFile(filesDir);
        prefer_jw.edit().putBoolean(Config.LOGIN_FLAG, false).apply();
        OkGo.getInstance().getCookieJar().getCookieStore().removeAllCookie();
        startActivity(new Intent(getContext(), StartPage.class));
        getActivity().finish();
    }

}
