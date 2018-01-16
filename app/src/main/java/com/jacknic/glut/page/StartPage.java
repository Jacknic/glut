package com.jacknic.glut.page;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.jacknic.glut.R;
import com.jacknic.glut.stacklibrary.PageTool;
import com.jacknic.glut.util.Config;
import com.jacknic.glut.util.ViewUtil;
import com.tencent.stat.StatService;

import java.util.Timer;
import java.util.TimerTask;

import static android.content.Context.MODE_PRIVATE;

/**
 * 启动页
 */

public class StartPage extends Fragment {
    SharedPreferences preferJw;
    private View page;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        StatService.trackBeginPage(getContext(), "启动页");
        page = inflater.inflate(R.layout.page_start, container, false);
        preferJw = getContext().getSharedPreferences(Config.PREFER, MODE_PRIVATE);
        ViewUtil.showToolbar((AppCompatActivity) getContext(), false);
        boolean is_login = preferJw.getBoolean("login_flag", false);
        if (!is_login) {
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
        return page;
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
                        Bundle bundle = new Bundle();
                        bundle.putInt("flag", Config.LOGIN_FLAG_JW);
                        PageTool.open(getContext(), new LoginPage(), bundle);
                        break;
                    case R.id.btn_enter:
                        PageTool.jumpFragment(getContext(), new HomePage());
//                        preferJw.edit().putBoolean("login_flag", true).apply();
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
        if (!hidden) {
            ViewUtil.showToolbar((AppCompatActivity) getContext(), false);
        }
    }
}
