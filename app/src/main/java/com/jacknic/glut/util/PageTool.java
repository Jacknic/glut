package com.jacknic.glut.util;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;

import com.jacknic.glut.MainActivity;
import com.jacknic.glut.page.BrowserPage;

/**
 * 页面管理工具
 */

public class PageTool {


    /**
     * 打开新Fragment
     */
    public static void open(Context context, @NonNull Fragment fragment) {
        getRoot(context).manager.openFragment(getRoot(context).manager.getPages().peek(), fragment, null);
    }

    /**
     * 打开新fragment（带参数）
     */
    public static void open(Context context, @NonNull Fragment fragment, Bundle bundle) {
        getRoot(context).manager.openFragment(getRoot(context).manager.getPages().peek(), fragment, bundle);
    }

    /**
     * 跳转fragment
     */
    public static void jumpFragment(Context context, Fragment to) {
        getRoot(context).manager.jumpFragment(to);
    }


    /**
     * 关闭fragment
     */
    public static void close(Context context, Fragment fragment) {
        getRoot(context).manager.close(fragment);
    }

    /**
     * 关闭fragment
     */
    public static void close(Fragment fragment) {
        getRoot(fragment.getContext()).manager.close(fragment);
    }

    /**
     * 获取Activity
     */
    private static MainActivity getRoot(Context context) {
        return (MainActivity) context;
    }

    /**
     * 打开网页
     */
    public static void openWebPage(Context context, String url) {
        Fragment browserPage = new BrowserPage();
        Bundle bundle = new Bundle();
        bundle.putString("url", url);
        open(context, browserPage, bundle);
    }

}
