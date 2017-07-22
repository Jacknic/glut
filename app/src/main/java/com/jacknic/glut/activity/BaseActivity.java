package com.jacknic.glut.activity;

import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.jacknic.glut.R;
import com.jacknic.glut.util.ActivityUtil;
import com.jacknic.glut.util.Config;


/**
 * Activity基类
 */

public class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        selectTheme();
        super.onCreate(savedInstanceState);
    }

    /**
     * 设置主题
     */
    protected void selectTheme() {
        SharedPreferences setting = getSharedPreferences(Config.PREFER_SETTING, MODE_PRIVATE);
        int theme_id = setting.getInt(Config.SETTING_THEME_INDEX, 4);
        setTheme(Config.THEME_LIST[theme_id]);
    }

    /**
     * 设置沉浸式状态栏
     */
    protected void setStatusView() {
        // 设置状态栏透明
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //获取主题颜色
            TypedValue typedValue = new TypedValue();
            if (!getTheme().resolveAttribute(R.attr.colorPrimary, typedValue, true)) return;
            int primaryColor = typedValue.data;
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            // 添加 statusView 到布局中
            ViewGroup decorView = (ViewGroup) getWindow().getDecorView();
            // 设置根布局的参数
            ViewGroup rootView = (ViewGroup) ((ViewGroup) findViewById(android.R.id.content)).getChildAt(0);
            rootView.setFitsSystemWindows(true);
            rootView.setClipToPadding(true);
            // 获得状态栏高度
            int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
            int statusBarHeight = getResources().getDimensionPixelSize(resourceId);
            //设置状态栏控件
            View statusView = new View(getBaseContext());
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, statusBarHeight);
            statusView.setLayoutParams(params);
            statusView.setBackgroundColor(primaryColor);
            decorView.addView(statusView);
        }
    }

    @Override
    protected void onPause() {
        ActivityUtil.pushActivity(this);
        super.onPause();
    }

    @Override
    public void finish() {
        ActivityUtil.removeActivity(this);
        super.finish();
    }
}
