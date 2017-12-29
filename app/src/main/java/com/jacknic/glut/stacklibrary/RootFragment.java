package com.jacknic.glut.stacklibrary;

import android.os.Bundle;
import android.support.annotation.AnimRes;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;

/**
 * 拓展Fragment
 */
public abstract class RootFragment extends Fragment {

    /**
     * 打开新Fragment
     */
    public void open(@NonNull RootFragment fragment) {
        getRoot().manager.addFragment(this, fragment, null);
    }

    /**
     * 打开新fragment（带参数）
     */
    public void open(@NonNull RootFragment fragment, Bundle bundle) {
        getRoot().manager.addFragment(this, fragment, bundle);
    }

    /**
     * 跳转fragment，不隐藏当前页
     */
    public void dialogFragment(Fragment to) {
        getRoot().manager.dialogFragment(to);
    }

    /**
     * 设置对话框式fragment动画
     */
    public void setDialogAnim(@AnimRes int dialog_in, @AnimRes int dialog_out) {
        getRoot().manager.setDialogAnim(dialog_in, dialog_out);
    }

    /**
     * 关闭当前fragment
     */
    public void close() {
        getRoot().manager.close(this);
    }

    /**
     * 关闭fragment
     */
    public void close(RootFragment fragment) {
        getRoot().manager.close(fragment);
    }


    /**
     * 获取Activity
     */
    public RootActivity getRoot() {
        return (RootActivity) getContext();
    }
}
