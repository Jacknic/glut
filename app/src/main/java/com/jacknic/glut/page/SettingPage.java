package com.jacknic.glut.page;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jacknic.glut.R;
import com.jacknic.glut.adapter.SettingPagerAdapter;
import com.jacknic.glut.util.ViewUtil;

/**
 * 设置
 */
public class SettingPage extends Fragment {

    private TabLayout tab;
    private ViewPager pager_container;
    private View page;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        page = inflater.inflate(R.layout.page_setting, container, false);
        tab = (TabLayout) page.findViewById(R.id.setting_tab);
        pager_container = (ViewPager) page.findViewById(R.id.setting_pager);
        setPagers();
        ViewUtil.setTitle(getContext(), "设置");
        return page;

    }


    /**
     * 设置pager
     */
    private void setPagers() {
        FragmentPagerAdapter settingPagerAdapter = new SettingPagerAdapter(getChildFragmentManager());
        pager_container.setAdapter(settingPagerAdapter);
        tab.setupWithViewPager(pager_container);
    }

}
