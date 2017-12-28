package com.jacknic.glut.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.jacknic.glut.view.fragment.main.CourseFragment;
import com.jacknic.glut.view.fragment.main.FinancialFragment;
import com.jacknic.glut.view.fragment.main.LibraryFragment;
import com.jacknic.glut.view.fragment.main.MineFragment;

import java.util.ArrayList;

/**
 * 主页面Pager适配器
 */

public class MainPagerAdapter extends FragmentPagerAdapter {
    private ArrayList<Fragment> fragments = new ArrayList<>();

    public MainPagerAdapter(FragmentManager fm) {
        super(fm);
        fragments.add(new CourseFragment());
        fragments.add(new FinancialFragment());
        fragments.add(new LibraryFragment());
        fragments.add(new MineFragment());
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
