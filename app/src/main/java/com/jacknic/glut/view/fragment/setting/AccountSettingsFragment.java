package com.jacknic.glut.view.fragment.setting;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jacknic.glut.MainActivity;
import com.jacknic.glut.R;
import com.jacknic.glut.model.entity.CourseEntity;
import com.jacknic.glut.model.entity.CourseInfoEntity;
import com.jacknic.glut.util.Config;
import com.jacknic.glut.util.DataBase;
import com.jacknic.glut.util.Func;
import com.jacknic.glut.util.PreferManager;
import com.lzy.okgo.OkGo;

import java.io.File;

import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.HttpUrl;


/**
 * 账户设置
 */
public class AccountSettingsFragment extends Fragment {

    private SharedPreferences prefer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View fragment = inflater.inflate(R.layout.frag_setting_account, container, false);
        ButterKnife.bind(this, fragment);
        prefer = PreferManager.getPrefer();
        return fragment;
    }


    //控件点击事件处理
    @OnClick({R.id.setting_btn_clear_cw, R.id.setting_btn_clear_ts, R.id.setting_btn_clear_all})
    void onClick(final View v) {
        AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setTitle("确定清除?")
                .setNegativeButton(android.R.string.cancel, null)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (v.getId()) {
                            case R.id.setting_btn_clear_cw:
                                OkGo.getInstance().getCookieJar().getCookieStore().removeCookie(HttpUrl.parse("http://cwwsjf.glut.edu.cn:8088"));
                                prefer.edit().remove(Config.PASSWORD_CW).remove(Config.STUDENT_ID).remove("info").apply();
                                break;
                            case R.id.setting_btn_clear_ts:
                                OkGo.getInstance().getCookieJar().getCookieStore().removeCookie(HttpUrl.parse("http://202.193.80.181:8080"));
                                prefer.edit().remove(Config.PASSWORD_TS).apply();
                                break;
                            case R.id.setting_btn_clear_all:
                                logout();
                                break;
                        }
                    }
                }).create();
        dialog.show();


    }


    /**
     * 清除登录信息
     */
    private void logout() {
        //清除数据表
        DataBase.getDaoSession().deleteAll(CourseEntity.class);
        DataBase.getDaoSession().deleteAll(CourseInfoEntity.class);
        File filesDir = getContext().getFilesDir();
        Func.deleteFile(filesDir);
        String sid = prefer.getString(Config.SID, "");
        String pwd = prefer.getString(Config.PASSWORD_JW, "");
        SharedPreferences.Editor editor = prefer.edit();
        editor.clear().apply();
        editor.putString(Config.SID, sid)
                .putString(Config.PASSWORD_JW, pwd)
                .apply();
        OkGo.getInstance().getCookieJar().getCookieStore().removeAllCookie();
        getActivity().finish();
        startActivity(new Intent(getContext(), MainActivity.class));
    }
}
