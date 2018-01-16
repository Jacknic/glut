package com.jacknic.glut.page;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.MenuItem;

import com.jacknic.glut.R;
import com.jacknic.glut.util.SnackbarTool;

/**
 * 带操作栏菜单frag
 */

public class BasePage extends Fragment {
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
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
        SnackbarTool.dismiss();
        super.onStop();
    }
}
