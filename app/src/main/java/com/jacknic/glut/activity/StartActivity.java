package com.jacknic.glut.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;

import com.jacknic.glut.R;
import com.jacknic.glut.util.Config;
import com.tencent.stat.StatConfig;

import java.util.Timer;
import java.util.TimerTask;

/**
 * 启动页
 */

public class StartActivity extends AppCompatActivity {
    SharedPreferences prefer_jw;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefer_jw = getSharedPreferences(Config.PREFER_JW, MODE_PRIVATE);
//        // 隐藏标题栏
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        // 隐藏状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_start);
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                start();
            }
        }, 400);
    }

    /**
     * 启动
     */
    private void start() {
        boolean is_login = prefer_jw.getBoolean("login_flag", false);
        if (!is_login) {
            StatConfig.setCustomUserId(getBaseContext(), prefer_jw.getString(Config.SID, ""));
            Intent intent = new Intent(this, LoginActivity.class);
            intent.putExtra("flag", Config.LOGIN_FLAG_JW);
            startActivity(intent);
        } else {
            startActivity(new Intent(this, MainActivity.class));
        }
        finish();
    }
}
