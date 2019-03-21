package com.jacknic.glut;

import android.app.NotificationChannel;
import android.app.NotificationManager;
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
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;

import com.jacknic.glut.page.StartPage;
import com.jacknic.glut.service.YueWarningWorker;
import com.jacknic.glut.util.Config;
import com.jacknic.glut.util.PageManager;
import com.jacknic.glut.util.PreferManager;
import com.jacknic.glut.util.SnackbarTool;
import com.jacknic.glut.util.ViewUtil;
import com.lzy.okgo.OkGo;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import es.dmoral.toasty.Toasty;

public class MainActivity extends AppCompatActivity {

    public PageManager manager;
    public static final String WORKER_ID = "worker_request_id";
    public static final String CHANNEL_NAME = "notification_channel_name";
    public static final String CHANNEL_ID = "notification_channel_id";

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

        createNotificationChannel();

        initWarningWorker();
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
        final float posX = ev.getX();
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                boolean xRange = posX < ViewUtil.dip2px(20);
                boolean yRange = ev.getY() > getSupportActionBar().getHeight();
                handled = xRange && yRange && manager.getPages().size() > 1;
                break;
            }
            case MotionEvent.ACTION_MOVE:
                if (handled) {
                    View viewNow = manager.getPages().peek().getView();
                    Fragment nextPage = manager.getPages().get(manager.getPages().size() - 2);
                    View viewNext = nextPage.getView();
                    float percent = posX / viewNext.getWidth();
                    TranslateAnimation nowTransX = new TranslateAnimation(posX, posX, 0, 0);
                    float nextX = -viewNext.getWidth() / 3 * (1 - percent);
                    TranslateAnimation nextTranX = new TranslateAnimation(nextX, nextX, 0, 0);
                    AlphaAnimation nextAlpha = new AlphaAnimation(percent + 0.1f, percent + 0.1f);
                    AnimationSet animSet = new AnimationSet(true);
                    animSet.addAnimation(nextTranX);
                    animSet.addAnimation(nextAlpha);
                    animSet.setFillAfter(true);
                    nowTransX.setFillAfter(true);
                    viewNow.startAnimation(nowTransX);
                    viewNext.startAnimation(animSet);
                }
                break;
            case MotionEvent.ACTION_UP: {
                if (!handled) break;
                View currView = manager.getPages().peek().getView();
                //滑动大于1/3则视为返回动作
                final boolean goBack = posX > currView.getWidth() / 3;
                final float percent = posX / currView.getWidth();
                TranslateAnimation transX = new TranslateAnimation(posX, goBack ? currView.getWidth() : 0, 0, 0);
                transX.setDuration(200 + (goBack ? (long) (100 * (1 - percent)) : 0));
                transX.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                        if (goBack) {
                            Fragment peek = manager.getPages().get(manager.getPages().size() - 2);
                            View preView = peek.getView();
                            TranslateAnimation transX = new TranslateAnimation(-preView.getWidth() / 3 * (1 - percent), 0, 0, 0);
                            AlphaAnimation alpha = new AlphaAnimation(percent + 0.1f, 1);
                            AnimationSet animSet = new AnimationSet(true);
                            animSet.setDuration(animation.getDuration());
                            animSet.addAnimation(transX);
                            animSet.addAnimation(alpha);
                            preView.startAnimation(animSet);
                        }
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        //true执行返回操作，否则清除动画
                        if (goBack) {
                            getSupportFragmentManager()
                                    .beginTransaction()
                                    .remove(manager.getPages().pop())
                                    .show(manager.getPages().peek())
                                    .commit();
                        } else {
                            Fragment peek = manager.getPages().get(manager.getPages().size() - 2);
                            peek.getView().clearAnimation();
                        }
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                currView.startAnimation(transX);
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
        getWindow().getDecorView().postDelayed(new Runnable() {
            @Override
            public void run() {
                exit = false;
            }
        }, 2000);
        if (manager.getPages().size() <= 1) {
            if (!exit) {
                int index = PreferManager.getPrefer().getInt(Config.SETTING_THEME_INDEX, 4);

                Toasty.custom(this, "再次点击返回退出", null,
                        getResources().getColor(Config.COLORS[index]),
                        getResources().getColor(R.color.white), Toasty.LENGTH_SHORT,
                        false, true).show();

                exit = true;
            } else {
                OkGo.getInstance().cancelAll();
                super.onBackPressed();
            }
        } else {
            manager.onBackPressed();
        }
    }

    /**
     * 处理在后台检查一卡通余额的worker
     */
    public static void initWarningWorker() {
        boolean isWorkerEnqueued =
                !PreferManager.getPrefer().getString(MainActivity.WORKER_ID, "").equals("");
        boolean isWarning =
                PreferManager.getPrefer().getBoolean(Config.KEY_MONEY_WARNING, true);

        //提交worker
        if (isWarning && !isWorkerEnqueued) {
            PeriodicWorkRequest request =
                    new PeriodicWorkRequest.Builder(
                            YueWarningWorker.class, Config.WARNING_WORKER_INTERVAL, TimeUnit.HOURS)
                            .build();

            PreferManager.getPrefer()
                    .edit().putString(MainActivity.WORKER_ID, request.getId().toString()).apply();
            WorkManager.getInstance().enqueue(request);
        }

        //移除worker
        if (!isWarning && isWorkerEnqueued) {
            WorkManager.getInstance()
                    .cancelWorkById(
                            UUID.fromString(
                                    PreferManager.getPrefer().getString(MainActivity.WORKER_ID, "")));
            PreferManager.getPrefer().edit().putString(MainActivity.WORKER_ID, "").apply();
        }

    }

    /**
     * 适配8.0，添加通知channel
     */
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance);
            channel.setDescription("");
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}