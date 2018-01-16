package com.jacknic.glut.stacklibrary;

import android.os.Bundle;
import android.support.annotation.AnimRes;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.jacknic.glut.R;

import java.util.Stack;


/**
 * 栈管理器
 */
public class StackManager {
    private final FragmentActivity context;
    private Stack<Fragment> pages = new Stack<>();
    private int nextIn;
    private int nextOut;
    private int quitIn;
    private int quitOut;
    private Animation next_in;
    private Animation next_out;
    private int dialog_in;
    private int dialog_out;

    public StackManager(FragmentActivity context) {
        this.context = context;
    }

    public Stack<Fragment> getPages() {
        return pages;
    }

    /**
     * 设置栈底frag
     */
    public void setFragment(@NonNull Fragment mTargetFragment) {
        FragmentTransaction transaction = context.getSupportFragmentManager().beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .replace(R.id.frame_container, mTargetFragment, mTargetFragment.getClass().getName())
                .commit();
        pages.clear();
        pages.add(mTargetFragment);
    }

    /**
     * 跳转到指定frag
     */
    public void addFragment(@NonNull final Fragment from, @NonNull final Fragment to) {
        FragmentTransaction transaction = context.getSupportFragmentManager().beginTransaction();
        if (nextIn != 0 && nextOut != 0 && quitIn != 0 && quitOut != 0) {
            transaction
                    .setCustomAnimations(nextIn, nextOut)
                    .add(R.id.frame_container, to, to.getClass().getName())
                    .setCustomAnimations(nextIn, nextOut)
                    .hide(from)
                    .commit();
        } else {
            transaction
                    .add(R.id.frame_container, to, to.getClass().getName())
                    .hide(from)
                    .commit();
        }
        pages.push(to);
    }

    /**
     * 设置页面跳转动画
     */
    public void setAnim(@AnimRes int nextIn, @AnimRes int nextOut, @AnimRes int quitIn, @AnimRes int quitOut) {
        this.nextIn = nextIn;
        this.nextOut = nextOut;
        this.quitIn = quitIn;
        this.quitOut = quitOut;
        next_in = AnimationUtils.loadAnimation(context, quitIn);
        next_out = AnimationUtils.loadAnimation(context, quitOut);
    }


    /**
     * 打开新frag
     */
    public void openFragment(Fragment from, Fragment to) {
        addFragment(from, to);
    }

    /**
     * 带参数跳转frag
     */
    public void addFragment(Fragment from, Fragment to, Bundle bundle) {
        to.setArguments(bundle);
        addFragment(from, to);
    }

    /**
     * 跳转页面,清空旧栈
     */
    public void jumpFragment(Fragment to) {
        pages.clear();
        pages.push(to);
        FragmentTransaction transaction = context.getSupportFragmentManager().beginTransaction();
        if (!to.isAdded()) {
            if (dialog_in != 0 && dialog_out != 0) {
                transaction
                        .setCustomAnimations(dialog_in, dialog_out)
                        .add(R.id.frame_container, to, to.getClass().getName())
                        .commit();
            } else {
                transaction
                        .add(R.id.frame_container, to, to.getClass().getName())
                        .commit();
            }
        }
    }

    /**
     * 设置对话框弹出动画
     */
    public void setDialogAnim(@AnimRes int dialog_in, @AnimRes int dialog_out) {
        this.dialog_in = dialog_in;
        this.dialog_out = dialog_out;
    }

    /**
     * 关闭自定frag
     */
    public void closeFragment(Fragment mTargetFragment) {
        if (!context.isFinishing()) {
            FragmentTransaction transaction = context.getSupportFragmentManager().beginTransaction();
            transaction.remove(mTargetFragment).commit();
        } else {
            closeAllFragment();
        }
    }

    /**
     * 关闭所有 fragment
     */
    public void closeAllFragment() {
        int backStackCount = context.getSupportFragmentManager().getBackStackEntryCount();
        for (int i = 0; i < backStackCount; i++) {
            int backStackId = context.getSupportFragmentManager().getBackStackEntryAt(i).getId();
            context.getSupportFragmentManager().popBackStack(backStackId, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
    }

    /**
     * 返回键(退栈处理）
     */
    public void onBackPressed() {
        FragmentManager fragmentManager = context.getSupportFragmentManager();
        final Fragment from = pages.pop();
        Fragment to = null;
        if (pages.size() >= 1) {
            to = pages.peek();
        }
        if (from != null) {
            if (to != null) {
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.show(to).commit();
            }
            View fromVie = from.getView();
            if (fromVie != null && next_out != null) {
                fromVie.startAnimation(next_out);
                next_out.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        closeFragment(from);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });

            } else {
                closeFragment(from);
            }
        }
        if (to != null) {
            View toView = to.getView();
            if (toView != null && next_in != null) {
                toView.startAnimation(next_in);
            }
        } else {
            closeAllFragment();
            context.finish();
        }
    }

    public static boolean isFirstClose = true;

    /**
     * 关闭指定frag
     */
    public void close(final Fragment fragment) {
        if (isFirstClose) {
            View view = fragment.getView();
            if (view != null) {
                if (next_out != null) {
                    view.startAnimation(next_out);
                    next_out.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            closeFragment(fragment);
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });
                } else {
                    closeFragment(fragment);
                }
            }
            isFirstClose = false;
        } else {
            closeFragment(fragment);
        }

    }
}
