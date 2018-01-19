package com.jacknic.glut.page;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
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
import com.tencent.stat.StatService;

import static android.content.Context.MODE_PRIVATE;
import static com.jacknic.glut.util.Config.PREFER;
import static com.jacknic.glut.util.Config.SETTING_THEME_COLOR_INDEX;
import static com.jacknic.glut.util.Config.SETTING_THEME_INDEX;

/**
 * 起始页
 */

public class HomePage extends BasePage implements View.OnClickListener {

    private View page;
    private ViewPager pageContainer;
    //按钮图标ID
    private int[] tabsIv = new int[]{
            R.id.bottom_tabs_iv_course,
            R.id.bottom_tabs_iv_financial,
            R.id.bottom_tabs_iv_library,
            R.id.bottom_tabs_iv_mine,
    };
    //按钮下部文字ID
    private int[] tabsTv = new int[]{
            R.id.bottom_tabs_tv_course,
            R.id.bottom_tabs_tv_financial,
            R.id.bottom_tabs_tv_library,
            R.id.bottom_tabs_tv_mine,
    };
    private int selectIndex = 0;//按钮选中位置

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        StatService.trackBeginPage(getContext(), "起始页");
        page = inflater.inflate(R.layout.page_home, container, false);
        pageContainer = (ViewPager) page.findViewById(R.id.page_container);
        ViewUtil.showToolbar((AppCompatActivity) getContext(), true);
        initFragments();
        setOnClick();
        //  选择课程页作为默认显示页面
        page.findViewById(tabsIv[selectIndex]).callOnClick();
        SharedPreferences prefer = getContext().getSharedPreferences(PREFER, MODE_PRIVATE);
        boolean auto_check_update = prefer.getBoolean("auto_check_update", true);
        if (auto_check_update) UpdateUtil.checkUpdate((FragmentActivity) getContext(), false);
        return page;
    }


    private void setOnClick() {
        //   设置事件监听
        for (int iv_id : tabsIv) {
            ImageView imageView = (ImageView) page.findViewById(iv_id);
            imageView.setOnClickListener(this);
        }
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
                page.findViewById(tabsIv[position]).callOnClick();
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    @Override
    public void onClick(View v) {
        int view_id = v.getId();
        for (int i = 0; selectIndex < tabsIv.length; i++) {
            if (view_id == tabsIv[i]) {
                int color = getResources().getColor(R.color.inactive);
                ImageView imageView = (ImageView) page.findViewById(tabsIv[selectIndex]);
                imageView.setColorFilter(color);
                TextView textView = (TextView) page.findViewById(tabsTv[selectIndex]);
                textView.setTextColor(color);
                selectIndex = i;
                break;
            }
        }
        ImageView imageView = (ImageView) page.findViewById(tabsIv[selectIndex]);
        TextView textView = (TextView) page.findViewById(tabsTv[selectIndex]);
        ViewUtil.setTitle(getContext(), textView.getText().toString());
        int color_index = getContext().getSharedPreferences(PREFER, MODE_PRIVATE).getInt(SETTING_THEME_INDEX, SETTING_THEME_COLOR_INDEX);
        int color = getResources().getColor(Config.COLORS[color_index]);
        pageContainer.setCurrentItem(selectIndex, true);
        ScaleAnimation scale = new ScaleAnimation(0.39F, 1.1F, 0.39F, 1.1F, Animation.RELATIVE_TO_SELF, 0.5F, Animation.RELATIVE_TO_SELF, 0.5F);
        scale.setDuration(200L);
        scale.setInterpolator(new AccelerateDecelerateInterpolator());
        ((ViewGroup) imageView.getParent()).startAnimation(scale);
        imageView.setColorFilter(color);
        textView.setTextColor(color);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        TextView textView = (TextView) page.findViewById(tabsTv[selectIndex]);
        ViewUtil.setTitle(getContext(), textView.getText().toString());
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
