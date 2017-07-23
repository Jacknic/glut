package com.jacknic.glut.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.TextView;

import com.jacknic.glut.R;
import com.jacknic.glut.adapter.SettingPagerAdapter;
import com.jacknic.glut.util.ActivityUtil;
import com.jacknic.glut.util.Config;

/**
 * 设置
 */
public class SettingActivity extends BaseActivity {

    private TabLayout tab;
    private ViewPager pager_container;
    private SharedPreferences prefer_setting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        setStatusView();
        tab = (TabLayout) findViewById(R.id.setting_tab);
        pager_container = (ViewPager) findViewById(R.id.setting_pager);
        prefer_setting = getSharedPreferences(Config.PREFER_SETTING, MODE_PRIVATE);
        setPagers();
        TextView tv_toolbar_title = (TextView) findViewById(R.id.tv_toolbar_title);
        tv_toolbar_title.setText("返回");
        tv_toolbar_title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    /**
     * 设置pager
     */
    private void setPagers() {
        FragmentPagerAdapter settingPagerAdapter = new SettingPagerAdapter(getSupportFragmentManager());
        pager_container.setAdapter(settingPagerAdapter);
        tab.setupWithViewPager(pager_container);
    }

    @Override
    public void onBackPressed() {
        boolean is_refresh = prefer_setting.getBoolean(Config.IS_REFRESH, false);
        if (is_refresh) {
            prefer_setting.edit().putBoolean(Config.IS_REFRESH, false).apply();
            ActivityUtil.lunchActivity(this, MainActivity.class);
            ActivityUtil.finishAllActivity();
            finish();
        }
        super.onBackPressed();
    }
}
