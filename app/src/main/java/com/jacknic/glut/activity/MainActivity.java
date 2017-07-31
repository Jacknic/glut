package com.jacknic.glut.activity;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.jacknic.glut.R;
import com.jacknic.glut.adapter.MainPagerAdapter;
import com.jacknic.glut.util.ActivityManager;
import com.jacknic.glut.util.Config;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends BaseActivity implements View.OnClickListener {

    private TextView tv_toolbar_title;
    private ViewPager page_container;
    //按钮图标ID
    private int[] tabs_iv = new int[]{
            R.id.bottom_tabs_iv_course,
            R.id.bottom_tabs_iv_financial,
            R.id.bottom_tabs_iv_library,
            R.id.bottom_tabs_iv_mine,
    };
    //按钮下部文字ID
    private int[] tabs_tv = new int[]{
            R.id.bottom_tabs_tv_course,
            R.id.bottom_tabs_tv_financial,
            R.id.bottom_tabs_tv_library,
            R.id.bottom_tabs_tv_mine,
    };
    private int select_index = 0;//按钮选中位置

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv_toolbar_title = (TextView) findViewById(R.id.tv_toolbar_title);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        page_container = (ViewPager) findViewById(R.id.page_container);
        initFragments();
        setOnClick();
        //  选择课程页作为默认显示页面
        findViewById(R.id.bottom_tabs_iv_course).callOnClick();
    }

    private void setOnClick() {
        //   设置事件监听
        for (int iv_id : tabs_iv) {
            ImageView imageView = (ImageView) findViewById(iv_id);
            imageView.setOnClickListener(this);
        }
    }

    /**
     * 初始化Fragment
     */
    private void initFragments() {
        MainPagerAdapter pagerAdapter = new MainPagerAdapter(getSupportFragmentManager());
        page_container.setOffscreenPageLimit(pagerAdapter.getCount());
        page_container.setAdapter(pagerAdapter);
        page_container.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                findViewById(tabs_iv[position]).callOnClick();
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    @Override
    public void onClick(View v) {
        int view_id = v.getId();
        for (int i = 0; select_index < tabs_iv.length; i++) {
            if (view_id == tabs_iv[i]) {
                ImageView imageView = (ImageView) findViewById(tabs_iv[select_index]);
                int color = getResources().getColor(R.color.inactive);
                imageView.setColorFilter(color);
                TextView textView = (TextView) findViewById(tabs_tv[select_index]);
                textView.setTextColor(color);
                select_index = i;
                break;
            }
        }
        ImageView imageView = (ImageView) findViewById(tabs_iv[select_index]);
        TextView textView = (TextView) findViewById(tabs_tv[select_index]);
        tv_toolbar_title.setText(textView.getText());
        int color_index = getSharedPreferences(Config.PREFER_SETTING, MODE_PRIVATE).getInt(Config.SETTING_COLOR_INDEX, Config.SETTING_THEME_COLOR_INDEX);
        int color = getResources().getColor(Config.COLORS[color_index]);
        page_container.setCurrentItem(select_index, false);
        imageView.setColorFilter(color);
        textView.setTextColor(color);
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
            ActivityManager.finishAll();
            finish();
        }
    }

}
