package com.jacknic.glut.page;

import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import com.jacknic.glut.R;
import com.jacknic.glut.adapter.SettingPagerAdapter;

/**
 * 设置
 */
public class SettingPage extends BasePage {

    private TabLayout tab;
    private ViewPager viewPager;

    @Override
    protected int getLayoutId() {
        mTitle = "设置";
        return R.layout.page_setting;
    }

    @Override
    public void onStart() {
        super.onStart();
        tab = (TabLayout) page.findViewById(R.id.setting_tab);
        viewPager = (ViewPager) page.findViewById(R.id.setting_pager);
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

}
