package com.jacknic.glut.page;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.jacknic.glut.R;
import com.jacknic.glut.stacklibrary.PageTool;
import com.jacknic.glut.util.Config;
import com.jacknic.glut.util.ViewUtil;

import java.util.Timer;
import java.util.TimerTask;

import static android.content.Context.MODE_PRIVATE;

/**
 * 启动页
 */

public class StartPage extends BasePage {

    @Override
    protected int getLayoutId() {
        return R.layout.page_start;
    }

    @Override
    void initPage() {
        ViewUtil.showToolbar((AppCompatActivity) getContext(), false);
        SharedPreferences prefer = getContext().getSharedPreferences(Config.PREFER, MODE_PRIVATE);
        boolean isLogin = prefer.getBoolean("login_flag", false);
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
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.tv_login:
                        PageTool.open(getContext(), new LoginPage());
                        break;
                    case R.id.btn_enter:
                        PageTool.jumpFragment(getContext(), new HomePage());
//                        prefer.edit().putBoolean("login_flag", true).apply();
                        break;
                    default:
                        break;
                }
            }
        };
        View startBtns = page.findViewById(R.id.start_btns);
        Animation loadAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.fade_in);
        startBtns.startAnimation(loadAnimation);
        startBtns.setVisibility(View.VISIBLE);
        TextView btnLogin = (TextView) page.findViewById(R.id.tv_login);
        TextView btnEnter = (TextView) page.findViewById(R.id.btn_enter);
        btnLogin.setOnClickListener(listener);
        btnEnter.setOnClickListener(listener);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            ViewUtil.showToolbar((AppCompatActivity) getContext(), false);
        }
    }
}
