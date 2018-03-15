package com.jacknic.glut.page;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.jacknic.glut.R;
import com.jacknic.glut.util.SnackbarTool;
import com.jacknic.glut.util.ViewUtil;
import com.lzy.okgo.OkGo;
import com.tencent.stat.StatService;

import butterknife.ButterKnife;

/**
 * 带操作栏菜单frag
 */

public abstract class BasePage extends Fragment {
    protected String mTitle = "";
    protected View page;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        ViewUtil.showBackIcon(getContext(), true);
    }

    /**
     * 页面布局ID
     */
    @LayoutRes
    protected abstract int getLayoutId();

    /**
     * 初始化页面
     */
    void initPage() {
        //do not thing
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        page = inflater.inflate(getLayoutId(), container, false);
        ButterKnife.bind(this, page);
        ViewUtil.setTitle(getContext(), mTitle);
        initPage();
        StatService.trackBeginPage(getContext(), this.getClass().getSimpleName());
        return page;
    }

    /**
     * 刷新操作
     */
    void refresh() {
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_refresh:
                refresh();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStop() {
        super.onStop();
        OkGo.getInstance().cancelTag(this);
        StatService.trackEndPage(getContext(), this.getClass().getSimpleName());
        SnackbarTool.dismiss();
    }
}
