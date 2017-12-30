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
public class StackManager implements CloseFragment {
    private final FragmentActivity context;
    private Stack<RootFragment> pages = new Stack<>();
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

    public Stack<RootFragment> getPages() {
        return pages;
    }

    /**
     * Set the bottom of the fragment
     *
     * @param mTargetFragment bottom of the fragment
     */
    public void setFragment(@NonNull RootFragment mTargetFragment) {
        FragmentTransaction transaction = context.getSupportFragmentManager().beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .replace(R.id.frame_container, mTargetFragment, mTargetFragment.getClass().getName())
                .commit();
        pages.add(mTargetFragment);
    }

    /**
     * Jump to the specified fragment
     *
     * @param from current fragment
     * @param to   next fragment
     */
    public void addFragment(@NonNull final RootFragment from, @NonNull final RootFragment to) {
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
     * Set page switch animation
     *
     * @param nextIn  The next page to enter the animation
     * @param nextOut The next page out of the animation
     * @param quitIn  The current page into the animation
     * @param quitOut Exit animation for the current page
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
     * open this fragment
     *
     * @param from current fragment
     * @param to   next fragment
     */
    public void openFragment(RootFragment from, RootFragment to) {
        addFragment(from, to);
    }

    /**
     * Jump to the specified fragment with a parameter form
     *
     * @param from   current fragment
     * @param to     next fragment
     * @param bundle Parameter carrier
     */
    public void addFragment(RootFragment from, RootFragment to, Bundle bundle) {
        to.setArguments(bundle);
        addFragment(from, to);
    }

    /**
     * Jump to the specified fragment and do not hide the current page.
     *
     * @param to To jump to the page
     */
    public void dialogFragment(Fragment to) {
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
     * Set the animation to add fragment in dialog mode
     *
     * @param dialog_in  The next page to enter the animation
     * @param dialog_out The next page out of the animation
     */
    public void setDialogAnim(@AnimRes int dialog_in, @AnimRes int dialog_out) {
        this.dialog_in = dialog_in;
        this.dialog_out = dialog_out;
    }

    /**
     * Closes the specified fragment
     *
     * @param mTargetFragment fragment
     */
    public void closeFragment(Fragment mTargetFragment) {
        if (!context.isFinishing()) {
            FragmentTransaction transaction = context.getSupportFragmentManager().beginTransaction();
            transaction.remove(mTargetFragment).commit();
            pages.pop();
        } else {
            closeAllFragment();
        }
    }

    /**
     * Close all fragment
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
        Fragment from = pages.peek();
        Fragment to = null;
        if (pages.size() > 1) {
            to = pages.get(pages.size() - 2);
        }
        if (from != null && from != pages.firstElement()) {
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
                        closeFragment(pages.peek());
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

    @Override
    public void close(final RootFragment fragment) {
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

    @Override
    public void show(RootFragment fragment) {
        FragmentTransaction transaction = context.getSupportFragmentManager().beginTransaction();
        transaction.show(fragment).commit();
        View view = fragment.getView();
        if (view != null && next_in != null) {
            view.startAnimation(next_in);
        }
    }

}
