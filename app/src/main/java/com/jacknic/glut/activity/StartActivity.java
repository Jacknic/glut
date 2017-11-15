package com.jacknic.glut.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.jacknic.glut.R;
import com.jacknic.glut.util.Config;

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
        boolean is_login = prefer_jw.getBoolean("login_flag", false);
        if (!is_login) {
            start();
        } else {
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    startActivity(new Intent(StartActivity.this, MainActivity.class));
                    finish();
                }
            }, 400);
        }
    }

    /**
     * 启动选项
     */
    private void start() {
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.btn_login:
                        Intent intent = new Intent(StartActivity.this, LoginActivity.class);
                        intent.putExtra("flag", Config.LOGIN_FLAG_JW);
                        startActivity(intent);
                        break;
                    case R.id.btn_enter:
                        startActivity(new Intent(StartActivity.this, MainActivity.class));
                        prefer_jw.edit().putBoolean("login_flag", true).apply();
                        break;
                    default:
                        break;
                }
            }
        };
        View start_btns = findViewById(R.id.start_btns);
        start_btns.setVisibility(View.VISIBLE);
        Animation loadAnimation = AnimationUtils.loadAnimation(this, R.anim.push_right_in);
        start_btns.startAnimation(loadAnimation);
        TextView btn_login = (TextView) findViewById(R.id.btn_login);
        TextView btn_enter = (TextView) findViewById(R.id.btn_enter);
        btn_login.setOnClickListener(listener);
        btn_enter.setOnClickListener(listener);
    }
}
