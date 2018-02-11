package com.jacknic.glut.page;

import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.jacknic.glut.R;
import com.jacknic.glut.util.PageTool;
import com.jacknic.glut.util.PreferManager;
import com.jacknic.glut.util.ViewUtil;

import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 启动页
 */

public class StartPage extends BasePage {
    @BindView(R.id.start_btns)
    View startBtns;

    @Override
    protected int getLayoutId() {
        return R.layout.page_start;
    }

    @Override
    void initPage() {
        ViewUtil.showToolbar((AppCompatActivity) getContext(), false);
        boolean isLogin = PreferManager.getPrefer().getBoolean("login_flag", false);
        if (!isLogin) {
            start();
        } else {
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    //打开主页
                    PageTool.jumpFragment(getContext(), new HomePage());
                }
            }, 400);
        }
    }

    /**
     * 启动选项
     */
    public void start() {
        Animation loadAnimation = AnimationUtils.loadAnimation(getContext(), android.R.anim.fade_in);
        startBtns.startAnimation(loadAnimation);
        startBtns.setVisibility(View.VISIBLE);
    }

    /**
     * 直接进入
     */
    @OnClick(R.id.btn_enter)
    void enter() {
        PageTool.jumpFragment(getContext(), new HomePage());
    }

    /**
     * 用户登录
     */
    @OnClick(R.id.tv_login)
    void login() {
        PageTool.open(getContext(), new LoginPage());
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            ViewUtil.showToolbar((AppCompatActivity) getContext(), false);
        }
    }
}
