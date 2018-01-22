package com.jacknic.glut.page;

import android.content.SharedPreferences;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.jacknic.glut.R;
import com.jacknic.glut.adapter.MainPagerAdapter;
import com.jacknic.glut.stacklibrary.PageTool;
import com.jacknic.glut.util.Config;
import com.jacknic.glut.util.UpdateUtil;
import com.jacknic.glut.util.ViewUtil;

import static android.content.Context.MODE_PRIVATE;
import static com.jacknic.glut.util.Config.PREFER;
import static com.jacknic.glut.util.Config.SETTING_THEME_COLOR_INDEX;
import static com.jacknic.glut.util.Config.SETTING_THEME_INDEX;

/**
 * 起始页
 */

public class HomePage extends BasePage implements View.OnClickListener {

    private ViewPager pageContainer;

    //按钮组
    private int[] TAB_IDS = new int[]{
            R.id.bottom_tab_course,
            R.id.bottom_tab_financial,
            R.id.bottom_tab_library,
            R.id.bottom_tab_mine,
    };
    private ViewGroup selectTab;

    @Override
    protected int getLayoutId() {
        return R.layout.page_home;
    }

    @Override
    void initPage() {
        pageContainer = (ViewPager) page.findViewById(R.id.page_container);
        ViewUtil.showToolbar((AppCompatActivity) getContext(), true);
        initFragments();
        for (int id : TAB_IDS) {
            page.findViewById(id).setOnClickListener(this);
        }
        //  选择课程页作为默认显示页面
        page.findViewById(R.id.bottom_tab_course).callOnClick();
        SharedPreferences prefer = getContext().getSharedPreferences(PREFER, MODE_PRIVATE);
        boolean auto_check_update = prefer.getBoolean("auto_check_update", true);
        if (auto_check_update) UpdateUtil.checkUpdate((FragmentActivity) getContext(), false);
    }


    /**
     * 初始化Fragment
     */
    private void initFragments() {
        MainPagerAdapter pagerAdapter = new MainPagerAdapter(getChildFragmentManager());
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

    @Override
    public void onClick(View v) {
        if (selectTab == null) {
            selectTab = (ViewGroup) v;
        } else if (selectTab == v) {
            return;
        }
        int color = getResources().getColor(R.color.inactive);
        ViewGroup preTab = selectTab;
        changeColor(preTab, color);
        //选中tab设置样式
        ViewGroup tab = (ViewGroup) v;
        selectTab = tab;
        TextView textView = (TextView) tab.getChildAt(1);
        mTitle = textView.getText().toString();
        ViewUtil.setTitle(getContext(), mTitle);
        int colorIndex = getContext().getSharedPreferences(PREFER, MODE_PRIVATE).getInt(SETTING_THEME_INDEX, SETTING_THEME_COLOR_INDEX);
        color = getResources().getColor(Config.COLORS[colorIndex]);
        changeColor(tab, color);
        for (int i = 0; i < TAB_IDS.length; i++) {
            if (selectTab.getId() == TAB_IDS[i]) {
                pageContainer.setCurrentItem(i, true);
            }
        }
        ScaleAnimation scale = new ScaleAnimation(0.39F, 1.1F, 0.39F, 1.1F, Animation.RELATIVE_TO_SELF, 0.5F, Animation.RELATIVE_TO_SELF, 0.5F);
        scale.setDuration(200L);
        scale.setInterpolator(new AccelerateDecelerateInterpolator());
        tab.startAnimation(scale);
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_all_courses:
                PageTool.open(getContext(), new CourseListPage());
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
