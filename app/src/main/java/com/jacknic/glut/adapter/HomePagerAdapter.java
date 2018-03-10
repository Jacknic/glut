package com.jacknic.glut.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.jacknic.glut.view.fragment.home.CourseFragment;
import com.jacknic.glut.view.fragment.home.FinancialFragment;
import com.jacknic.glut.view.fragment.home.LibraryFragment;
import com.jacknic.glut.view.fragment.home.MineFragment;

/**
 * home页面Pager适配器
 */

public class HomePagerAdapter extends FragmentPagerAdapter {
    private final Fragment[] pages = {new CourseFragment(), new FinancialFragment(), new LibraryFragment(), new MineFragment()};

    public HomePagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return pages[position];
    }

    @Override
    public int getCount() {
        return pages.length;
    }
}
