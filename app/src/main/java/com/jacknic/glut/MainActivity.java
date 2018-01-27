package com.jacknic.glut;

import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.jacknic.glut.page.StartPage;
import com.jacknic.glut.stacklibrary.StackManager;
import com.jacknic.glut.util.Config;
import com.jacknic.glut.util.SnackbarTool;
import com.jacknic.glut.util.ViewUtil;
import com.lzy.okgo.OkGo;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    public StackManager manager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        selectTheme();
        setContentView(R.layout.activity_main);
        manager = new StackManager(this);
        Fragment startPage = new StartPage();
        manager.setFragment(startPage);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        setStatusView();
        SnackbarTool.init(this);
        manager.setAnim(R.anim.push_right_in, R.anim.push_left_out, R.anim.push_left_in, R.anim.push_right_out);
    }

    /**
     * 滑动初始点x坐标
     */
    private float posX = 0;

    /**
     * 侧滑返回
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                posX = ev.getX();
                if (posX < ViewUtil.dip2px(20)) {
                    return true;
                }
                break;
            }
            case MotionEvent.ACTION_UP: {
                float x = ev.getX();
                if (isBack(x)) return true;
                break;
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    /**
     * 检验是否执行返回操作
     */
    private boolean isBack(float x) {
        if (posX < ViewUtil.dip2px(20)) {
            if (x > getWindow().getDecorView().getWidth() / 3) {
                if (manager.getPages().size() > 1) {
                    onBackPressed();
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 设置主题
     */
    public void selectTheme() {
        SharedPreferences prefer = getSharedPreferences(Config.PREFER, MODE_PRIVATE);
        int themeIndex = prefer.getInt(Config.SETTING_THEME_INDEX, Config.SETTING_THEME_COLOR_INDEX);
        setTheme(Config.THEME_LIST[themeIndex]);
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
        if (manager.getPages().size() <= 1) {
            if (!exit) {
                Toast.makeText(MainActivity.this, "再次返回退出应用", Toast.LENGTH_SHORT).show();
                exit = true;
            } else {
                OkGo.getInstance().cancelAll();
                finish();
            }
        } else {
            manager.onBackPressed();
        }
    }
}