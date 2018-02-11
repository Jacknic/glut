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
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Toast;

import com.jacknic.glut.page.StartPage;
import com.jacknic.glut.util.Config;
import com.jacknic.glut.util.PageManager;
import com.jacknic.glut.util.PreferManager;
import com.jacknic.glut.util.SnackbarTool;
import com.jacknic.glut.util.ViewUtil;
import com.lzy.okgo.OkGo;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    public PageManager manager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        selectTheme();
        setContentView(R.layout.activity_main);
        manager = new PageManager(this);
        Fragment startPage = new StartPage();
        manager.setFragment(startPage);
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
     * 是否拦截处理
     */
    private boolean handled = false;

    /**
     * 侧滑返回
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                handled = ev.getX() < ViewUtil.dip2px(20) && manager.getPages().size() > 1;
                break;
            }
            case MotionEvent.ACTION_MOVE:
                if (handled) {
                    View viewNow = manager.getPages().peek().getView();
                    Fragment nextPage = manager.getPages().get(manager.getPages().size() - 2);
                    View viewNext = nextPage.getView();
                    viewNext.setAlpha((ev.getX()) / viewNext.getWidth());
                    TranslateAnimation currAnim = new TranslateAnimation(ev.getX(), ev.getX(), 0, 0);
                    TranslateAnimation nextAnim = new TranslateAnimation(0, 0, 0, 0);
                    currAnim.setFillAfter(true);
                    nextAnim.setFillAfter(true);
                    viewNow.startAnimation(currAnim);
                    viewNext.startAnimation(nextAnim);
                }
                break;
            case MotionEvent.ACTION_UP: {
                if (!handled) break;
                View currView = manager.getPages().peek().getView();
                //滑动大于1/3则视为返回动作
                final boolean goBack = ev.getX() > currView.getWidth() / 3;
                TranslateAnimation currAnim = new TranslateAnimation(ev.getX(), goBack ? currView.getWidth() : 0, 0, 0);
                currAnim.setDuration(200);
                currAnim.setFillAfter(true);
                currAnim.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        Fragment peek;
                        if (goBack) {
                            manager.closeFragment(manager.getPages().pop());
                            peek = manager.getPages().peek();
                            getSupportFragmentManager()
                                    .beginTransaction()
                                    .show(peek)
                                    .commit();
                        } else {
                            peek = manager.getPages().get(manager.getPages().size() - 2);
                        }
                        peek.getView().setAlpha(1.0f);
                        handled = false;

                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                currView.startAnimation(currAnim);
                handled = goBack;
                break;
            }
        }
        return handled || super.dispatchTouchEvent(ev);
    }


    /**
     * 设置主题
     */
    public void selectTheme() {
        SharedPreferences prefer = PreferManager.getPrefer();
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