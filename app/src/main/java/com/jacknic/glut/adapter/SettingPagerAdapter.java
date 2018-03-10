package com.jacknic.glut.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.jacknic.glut.view.fragment.setting.AboutSettingsFragment;
import com.jacknic.glut.view.fragment.setting.AccountSettingsFragment;
import com.jacknic.glut.view.fragment.setting.BasicSettingsFragment;

/**
 * 设置页pager适配器
 */

public class SettingPagerAdapter extends FragmentPagerAdapter {
    private final Fragment[] settingPages = {new BasicSettingsFragment(), new AccountSettingsFragment(), new AboutSettingsFragment()};
    private final String[] titles = new String[]{"基本设置", "账户设置", "关于"};

    public SettingPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return settingPages[position];
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titles[position];
    }

    @Override
    public int getCount() {
        return settingPages.length;
    }
}
