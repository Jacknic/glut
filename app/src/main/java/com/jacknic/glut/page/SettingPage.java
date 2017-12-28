package com.jacknic.glut.page;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jacknic.glut.R;
import com.jacknic.glut.adapter.SettingPagerAdapter;
import com.jacknic.glut.stacklibrary.RootFragment;
import com.jacknic.glut.util.Config;
import com.jacknic.glut.util.ViewUtil;

import static android.content.Context.MODE_PRIVATE;

/**
 * 设置
 */
public class SettingPage extends RootFragment {

    private TabLayout tab;
    private ViewPager pager_container;
    private SharedPreferences prefer_setting;
    private View page;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        page = inflater.inflate(R.layout.page_setting, container, false);
        tab = (TabLayout) page.findViewById(R.id.setting_tab);
        pager_container = (ViewPager) page.findViewById(R.id.setting_pager);
        prefer_setting = getContext().getSharedPreferences(Config.PREFER_SETTING, MODE_PRIVATE);
        setPagers();
        ViewUtil.setTitle(getRoot(), "设置");

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

    public void onBackPressed() {
        boolean is_refresh = prefer_setting.getBoolean(Config.IS_REFRESH, false);
        if (is_refresh) {
            prefer_setting.edit().putBoolean(Config.IS_REFRESH, false).apply();

        }
    }

}
