package com.jacknic.glut;

import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.WindowManager;
import android.widget.Toast;

import com.jacknic.glut.page.StartPage;
import com.jacknic.glut.stacklibrary.RootActivity;
import com.jacknic.glut.stacklibrary.RootFragment;
import com.jacknic.glut.util.Config;
import com.jacknic.glut.util.SnackBarTool;
import com.lzy.okgo.OkGo;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends RootActivity {

    private StartPage startPage;

    @NonNull
    @Override
    public RootFragment getRootFragment() {
        if (startPage == null) {
            startPage = new StartPage();
        }
        return startPage;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        selectTheme();
        super.onCreate(savedInstanceState);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setStatusView();
        SnackBarTool.init(this);
        setAnim(R.anim.push_right_in, R.anim.push_left_out, R.anim.push_left_in, R.anim.push_right_out);
    }


    /**
     * 设置主题
     */
    public void selectTheme() {
        SharedPreferences setting = getSharedPreferences(Config.PREFER_SETTING, MODE_PRIVATE);
        int theme_index = setting.getInt(Config.SETTING_THEME_INDEX, Config.SETTING_THEME_COLOR_INDEX);
        setTheme(Config.THEME_LIST[theme_index]);
    }

    /**
     * 设置沉浸式状态栏
     */
    private void setStatusView() {
        // 设置状态栏透明
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }


    private boolean exit = false;

    @Override
    public void onBackPressed() {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                exit = false;
            }
        }, 2000);
        if (!exit) {
            Toast.makeText(MainActivity.this, "再次返回退出应用", Toast.LENGTH_SHORT).show();
            exit = true;
        } else {
            this.overridePendingTransition(R.anim.scale_fade_out, R.anim.scale_fade_out);
            OkGo.getInstance().cancelAll();
            finish();
        }
    }
}