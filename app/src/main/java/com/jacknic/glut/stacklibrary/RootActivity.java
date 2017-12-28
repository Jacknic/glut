package com.jacknic.glut.stacklibrary;

import android.os.Bundle;
import android.support.annotation.AnimRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;

import com.jacknic.glut.R;


/**
 * extends  this Activity to facilitate the management of multiple fragment instances
 * User: chengwangyong(cwy545177162@163.com)
 * Date: 2016-01-19
 * Time: 18:32
 */
public abstract class RootActivity extends AppCompatActivity {

    public StackManager manager;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        RootFragment fragment = getRootFragment();
        manager = new StackManager(this);
        manager.setFragment(fragment);
    }

    /**
     * Set the bottom of the fragment
     *
     * @return fragment
     */
    protected abstract
    @NonNull
    RootFragment getRootFragment();

    /**
     * Set page switch animation
     *
     * @param nextIn  The next page to enter the animation
     * @param nextOut The next page out of the animation
     * @param quitIn  The current page into the animation
     * @param quitOut Exit animation for the current page
     */
    public void setAnim(@AnimRes int nextIn, @AnimRes int nextOut, @AnimRes int quitIn, @AnimRes int quitOut) {
        manager.setAnim(nextIn, nextOut, quitIn, quitOut);
    }


    @Override
    public final boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                manager.onBackPressed();
                return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}
