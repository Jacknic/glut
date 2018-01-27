package com.jacknic.glut.page;

import android.content.Context;
import android.support.annotation.Keep;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import com.jacknic.glut.R;
import com.jacknic.glut.adapter.SettingPagerAdapter;
import com.jacknic.glut.event.ThemeChangeEvent;
import com.jacknic.glut.util.Config;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import butterknife.BindView;

/**
 * 设置
 */
public class SettingPage extends BasePage {

    @BindView(R.id.setting_tab)
    TabLayout tab;
    @BindView(R.id.setting_pager)
    ViewPager viewPager;

    @Override
    protected int getLayoutId() {
        mTitle = "设置";
        return R.layout.page_setting;
    }

    @Override
    void initPage() {
        EventBus.getDefault().register(this);
        setPagers();
    }

    /**
     * 设置pager
     */
    private void setPagers() {
        FragmentPagerAdapter settingPagerAdapter = new SettingPagerAdapter(getChildFragmentManager());
        viewPager.setAdapter(settingPagerAdapter);
        tab.setupWithViewPager(viewPager);
    }

    /**
     * 主题切换事件
     */
    @Keep
    @Subscribe
    public void themeChange(ThemeChangeEvent event) {
        int index = getContext().getSharedPreferences(Config.PREFER, Context.MODE_PRIVATE).getInt(Config.SETTING_THEME_INDEX, 4);
        tab.setBackgroundColor(getResources().getColor(Config.COLORS[index]));
        setPagers();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
    }
}
