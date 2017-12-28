package com.jacknic.glut.page;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.TextView;

import com.jacknic.glut.R;
import com.jacknic.glut.adapter.MainPagerAdapter;
import com.jacknic.glut.stacklibrary.RootFragment;
import com.jacknic.glut.util.Config;
import com.jacknic.glut.util.UpdateUtil;

import static android.content.Context.MODE_PRIVATE;
import static com.jacknic.glut.util.Config.PREFER_SETTING;
import static com.jacknic.glut.util.Config.SETTING_THEME_COLOR_INDEX;
import static com.jacknic.glut.util.Config.SETTING_THEME_INDEX;

/**
 * 起始页
 */

public class HomePage extends RootFragment implements View.OnClickListener {

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
    private ActionBar actionBar;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        page = inflater.inflate(R.layout.page_home, container, false);
        pageContainer = (ViewPager) page.findViewById(R.id.page_container);
        actionBar = getRoot().getSupportActionBar();
        if (actionBar != null) {
            actionBar.show();
        }
        initFragments();
        setOnClick();
        //  选择课程页作为默认显示页面
        page.findViewById(tabsIv[selectIndex]).callOnClick();
        SharedPreferences prefer_setting = getContext().getSharedPreferences(PREFER_SETTING, MODE_PRIVATE);
        boolean auto_check_update = prefer_setting.getBoolean("auto_check_update", true);
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
        MainPagerAdapter pagerAdapter = new MainPagerAdapter(getFragmentManager());
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
                ImageView imageView = (ImageView) page.findViewById(tabsIv[selectIndex]);
                int color = getResources().getColor(R.color.inactive);
                imageView.setColorFilter(color);
                TextView textView = (TextView) page.findViewById(tabsTv[selectIndex]);
                textView.setTextColor(color);
                selectIndex = i;
                break;
            }
        }
        if (selectIndex == 0) {
//            打开全部课程
        } else {
//            关闭全部课程
        }
        ImageView imageView = (ImageView) page.findViewById(tabsIv[selectIndex]);
        TextView textView = (TextView) page.findViewById(tabsTv[selectIndex]);
        actionBar.setTitle(textView.getText());
        int color_index = getContext().getSharedPreferences(PREFER_SETTING, MODE_PRIVATE).getInt(SETTING_THEME_INDEX, SETTING_THEME_COLOR_INDEX);
        int color = getResources().getColor(Config.COLORS[color_index]);
        pageContainer.setCurrentItem(selectIndex, false);
        imageView.setColorFilter(color);
        textView.setTextColor(color);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        if (!hidden) {
            page.findViewById(tabsIv[selectIndex]).callOnClick();
        }
    }

    @Override
    public void onNewIntent() {
        super.onNewIntent();
        Log.d(this.getClass().getName(), "onNewIntent");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(this.getClass().getName(), "onActivityResult");
    }

    @Override
    public void onInflate(Context context, AttributeSet attrs, Bundle savedInstanceState) {
        super.onInflate(context, attrs, savedInstanceState);
        Log.d(this.getClass().getName(), "onInflate");
    }

    @Override
    public void onAttachFragment(Fragment childFragment) {
        super.onAttachFragment(childFragment);
        Log.d(this.getClass().getName(), "onAttachFragment");
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.d(this.getClass().getName(), "onAttach");

    }

    @Override
    public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {
        Log.d(this.getClass().getName(), "onCreateAnimation");
        return super.onCreateAnimation(transit, enter, nextAnim);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(this.getClass().getName(), "onCreate");

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(this.getClass().getName(), "onViewCreated");

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(this.getClass().getName(), "onActivityCreated");
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        Log.d(this.getClass().getName(), "super.onViewStateRestored(savedInstanceState)");
        super.onViewStateRestored(savedInstanceState);
    }

    @Override
    public void onStart() {
        Log.d(this.getClass().getName(), "super.onStart()");
        super.onStart();
    }

    @Override
    public void onResume() {
        Log.d(this.getClass().getName(), "super.onResume()");
        super.onResume();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        Log.d(this.getClass().getName(), "super.onSaveInstanceState(outState)");
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onMultiWindowModeChanged(boolean isInMultiWindowMode) {
        Log.d(this.getClass().getName(), "super.onMultiWindowModeChanged(isInMultiWindowMode)");
        super.onMultiWindowModeChanged(isInMultiWindowMode);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        Log.d(this.getClass().getName(), "super.onConfigurationChanged(newConfig)");
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onPause() {
        Log.d(this.getClass().getName(), "super.onPause()");
        super.onPause();
    }

    @Override
    public void onStop() {
        Log.d(this.getClass().getName(), "super.onStop()");
        super.onStop();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    @Override
    public void onDestroyView() {
        Log.d(this.getClass().getName(), "super.onDestroyView()");
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        Log.d(this.getClass().getName(), "super.onDestroy()");
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        Log.d(this.getClass().getName(), "super.onDetach()");
        super.onDetach();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        Log.d(this.getClass().getName(), "super.onCreateOptionsMenu(menu, inflater)");
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onDestroyOptionsMenu() {
        Log.d(this.getClass().getName(), "super.onDestroyOptionsMenu()");
        super.onDestroyOptionsMenu();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d(this.getClass().getName(), "return super.onOptionsItemSelected(item)");
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onOptionsMenuClosed(Menu menu) {
        Log.d(this.getClass().getName(), "super.onOptionsMenuClosed(menu)");
        super.onOptionsMenuClosed(menu);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        Log.d(this.getClass().getName(), "super.onCreateContextMenu(menu, v, menuInfo)");
        super.onCreateContextMenu(menu, v, menuInfo);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        Log.d(this.getClass().getName(), "return super.onContextItemSelected(item)");
        return super.onContextItemSelected(item);
    }
}
