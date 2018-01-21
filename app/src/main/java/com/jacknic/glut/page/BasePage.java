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

/**
 * 带操作栏菜单frag
 */

public abstract class BasePage extends Fragment {
    protected String mTitle = "";
    protected View page;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        ViewUtil.showBackIcon(getContext(), true);
        super.onCreate(savedInstanceState);
    }

    @LayoutRes
    protected abstract int getLayoutId();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        page = inflater.inflate(getLayoutId(), container, false);
        ViewUtil.setTitle(getContext(), mTitle);
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
        SnackbarTool.dismiss();
    }
}
