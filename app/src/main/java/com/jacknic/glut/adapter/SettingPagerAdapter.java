package com.jacknic.glut.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.jacknic.glut.view.fragment.setting.AboutSettingsFragment;
import com.jacknic.glut.view.fragment.setting.AccountSettingsFragment;
import com.jacknic.glut.view.fragment.setting.BasicSettingsFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * 设置页pager适配器
 */

public class SettingPagerAdapter extends FragmentPagerAdapter {
    private List<Fragment> settingPages = new ArrayList<>();
    private final String[] tabs = new String[]{"基本设置", "账户设置", "关于"};

    public SettingPagerAdapter(FragmentManager fm) {
        super(fm);
        settingPages.add(new BasicSettingsFragment());
        settingPages.add(new AccountSettingsFragment());
        settingPages.add(new AboutSettingsFragment());
    }

    @Override
    public Fragment getItem(int position) {
        return settingPages.get(position);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabs[position];
    }

    @Override
    public int getCount() {
        return settingPages.size();
    }
}
