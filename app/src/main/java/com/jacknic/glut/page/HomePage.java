package com.jacknic.glut.page;

import android.content.SharedPreferences;
import android.support.annotation.Keep;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.jacknic.glut.R;
import com.jacknic.glut.adapter.HomePagerAdapter;
import com.jacknic.glut.event.ThemeChangeEvent;
import com.jacknic.glut.util.Config;
import com.jacknic.glut.util.PageTool;
import com.jacknic.glut.util.PreferManager;
import com.jacknic.glut.util.UpdateUtil;
import com.jacknic.glut.util.ViewUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import butterknife.BindView;
import butterknife.OnClick;

import static com.jacknic.glut.util.Config.SETTING_THEME_COLOR_INDEX;
import static com.jacknic.glut.util.Config.SETTING_THEME_INDEX;

/**
 * 起始页
 */

public class HomePage extends BasePage {

    @BindView(R.id.page_container)
    ViewPager pageContainer;
    //按钮组
    private final int[] TAB_IDS = new int[]{
            R.id.bottom_tab_course,
            R.id.bottom_tab_financial,
            R.id.bottom_tab_library,
            R.id.bottom_tab_mine,
    };
    //默认选中课表页
    @BindView(R.id.bottom_tab_course)
    ViewGroup selectTab;
    private int colorInactive;
    private int colorActive;

    @Override
    protected int getLayoutId() {
        mTitle = getString(R.string.txt_course);
        return R.layout.page_home;
    }

    @Override
    void initPage() {
        EventBus.getDefault().register(this);
        colorInactive = getResources().getColor(R.color.inactive);
        int colorIndex = PreferManager.getPrefer().getInt(SETTING_THEME_INDEX, SETTING_THEME_COLOR_INDEX);
        colorActive = getResources().getColor(Config.COLORS[colorIndex]);
        ViewUtil.showToolbar((AppCompatActivity) getContext(), true);
        initFragments();
        SharedPreferences prefer = PreferManager.getPrefer();
        boolean auto_check_update = prefer.getBoolean("auto_check_update", true);
        if (auto_check_update) UpdateUtil.checkUpdate((FragmentActivity) getContext(), false);
    }


    /**
     * 初始化Fragment
     */
    private void initFragments() {
        HomePagerAdapter pagerAdapter = new HomePagerAdapter(getChildFragmentManager());
        pageContainer.setOffscreenPageLimit(pagerAdapter.getCount());
        pageContainer.setAdapter(pagerAdapter);
        pageContainer.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                page.findViewById(TAB_IDS[position]).callOnClick();
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    @OnClick({R.id.bottom_tab_course, R.id.bottom_tab_financial, R.id.bottom_tab_library, R.id.bottom_tab_mine})
    void tabClick(View v) {
        if (selectTab == v) {
            return;
        }
        ViewGroup preTab = selectTab;
        changeColor(preTab, colorInactive);
        //选中tab设置样式
        ViewGroup tab = (ViewGroup) v;
        selectTab = tab;
        TextView textView = (TextView) tab.getChildAt(1);
        mTitle = textView.getText().toString();
        ViewUtil.setTitle(getContext(), mTitle);
        changeColor(tab, colorActive);
        for (int i = 0; i < TAB_IDS.length; i++) {
            if (selectTab.getId() == TAB_IDS[i]) {
                pageContainer.setCurrentItem(i, true);
            }
        }
        ScaleAnimation scale = new ScaleAnimation(0.39F, 1.1F, 0.39F, 1.1F, Animation.RELATIVE_TO_SELF, 0.5F, Animation.RELATIVE_TO_SELF, 0.5F);
        scale.setDuration(200L);
        scale.setInterpolator(new DecelerateInterpolator());
        tab.getChildAt(0).startAnimation(scale);
        tab.getChildAt(1).startAnimation(scale);
    }

    /**
     * 切换颜色
     */
    private void changeColor(ViewGroup tab, int color) {
        ImageView imageView = (ImageView) tab.getChildAt(0);
        TextView textView = (TextView) tab.getChildAt(1);
        imageView.setColorFilter(color);
        textView.setTextColor(color);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        ViewUtil.setTitle(getContext(), mTitle);
        ViewUtil.showBackIcon(getContext(), false);
        menu.clear();
        inflater.inflate(R.menu.menu_page_home, menu);
    }

    /**
     * 主题切换事件
     */
    @Keep
    @Subscribe
    public void themeChange(ThemeChangeEvent event) {
        int colorIndex = PreferManager.getPrefer().getInt(SETTING_THEME_INDEX, SETTING_THEME_COLOR_INDEX);
        colorActive = getResources().getColor(Config.COLORS[colorIndex]);
        pageContainer.setAdapter(new HomePagerAdapter(getChildFragmentManager()));
        pageContainer.setCurrentItem(3);
        changeColor(selectTab, colorActive);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_all_courses:
                PageTool.open(getContext(), new CourseListPage());
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
    }
}
