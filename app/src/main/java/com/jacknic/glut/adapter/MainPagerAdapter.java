package com.jacknic.glut.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.jacknic.glut.view.fragment.main.Fragment_cw;
import com.jacknic.glut.view.fragment.main.Fragment_kc;
import com.jacknic.glut.view.fragment.main.Fragment_ts;
import com.jacknic.glut.view.fragment.main.Fragment_wd;

import java.util.ArrayList;

/**
 * 主页面Pager适配器
 */

public class MainPagerAdapter extends FragmentPagerAdapter {
    private ArrayList<Fragment> fragments = new ArrayList<>();

    public MainPagerAdapter(FragmentManager fm) {
        super(fm);
        fragments.add(new Fragment_kc());
        fragments.add(new Fragment_cw());
        fragments.add(new Fragment_ts());
        fragments.add(new Fragment_wd());
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }
}
