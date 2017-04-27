package com.jacknic.glut.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.jacknic.glut.R;
import com.jacknic.glut.fragments.main.Fragment_cw;
import com.jacknic.glut.fragments.main.Fragment_kc;
import com.jacknic.glut.fragments.main.Fragment_ts;
import com.jacknic.glut.fragments.main.Fragment_wd;
import com.jacknic.glut.utils.ActivityUtil;
import com.jacknic.glut.utils.Config;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends BaseActivity implements View.OnClickListener {

    private TextView tv_toolbar_titlt;
    private ViewPager page_container;
    private ArrayList<Fragment> fragments;
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
    private ImageView iv_setting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv_toolbar_titlt = (TextView) findViewById(R.id.tv_toolbar_title);
        iv_setting = (ImageView) findViewById(R.id.iv_setting);
        tv_toolbar_titlt.setText(((TextView) findViewById(tabs_tv[select_index])).getText());
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        page_container = (ViewPager) findViewById(R.id.page_container);
        initFragments();
        setOnClick();
        setStatusView();
        //  选择课程页作为默认显示页面
        findViewById(R.id.bottom_tabs_iv_course).callOnClick();
    }

    private void setOnClick() {
        //   设置事件监听
        for (int iv_id : tabs_iv) {
            ImageView imageView = (ImageView) findViewById(iv_id);
            imageView.setOnClickListener(this);
        }
        iv_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityUtil.lunchActivity(MainActivity.this, SettingActivity.class);
            }
        });

    }

    /**
     * 初始化Fragment
     */
    private void initFragments() {
        fragments = new ArrayList<>();
        fragments.add(Fragment_kc.newInstance());
        fragments.add(Fragment_cw.newInstance());
        fragments.add(Fragment_ts.newInstance());
        fragments.add(Fragment_wd.newInstance());
        FragmentPagerAdapter pagerAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return fragments.get(position);
            }

            @Override
            public int getCount() {
                return fragments.size();
            }
        };
        page_container.setOffscreenPageLimit(fragments.size() - 1);
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
        if (select_index == fragments.size() - 1) {
            iv_setting.setVisibility(View.VISIBLE);
        } else {
            iv_setting.setVisibility(View.GONE);
        }
        ImageView imageView = (ImageView) findViewById(tabs_iv[select_index]);
        TextView textView = (TextView) findViewById(tabs_tv[select_index]);
        tv_toolbar_titlt.setText(textView.getText());
        int color_index = getSharedPreferences(Config.PREFER_SETTING, MODE_PRIVATE).getInt(Config.SETTING_COLOR_INDEX, Config.SETTING_THEME_COLOR_INDEX);
        int color = getResources().getColor(Config.colors[color_index]);
        page_container.setCurrentItem(select_index, false);
        imageView.setColorFilter(color);
        textView.setTextColor(color);
    }


    private boolean is_exit = false;

    @Override
    public void onBackPressed() {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                is_exit = false;
            }
        }, 2000);
        if (!is_exit) {
            Toast.makeText(MainActivity.this, "再次返回退出应用", Toast.LENGTH_SHORT).show();
            is_exit = true;
        } else {
            ActivityUtil.cleanActivities();
            finish();
            System.exit(0);
        }
    }

}
