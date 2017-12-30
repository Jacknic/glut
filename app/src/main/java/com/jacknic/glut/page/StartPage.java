package com.jacknic.glut.page;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.jacknic.glut.R;
import com.jacknic.glut.stacklibrary.RootFragment;
import com.jacknic.glut.util.Config;
import com.jacknic.glut.util.ViewUtil;

import java.util.Timer;
import java.util.TimerTask;

import static android.content.Context.MODE_PRIVATE;

/**
 * 启动页
 */

public class StartPage extends RootFragment {
    SharedPreferences preferJw;
    private View page;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        page = inflater.inflate(R.layout.page_start, container, false);
        preferJw = getContext().getSharedPreferences(Config.PREFER, MODE_PRIVATE);
        ViewUtil.showStatusView(getRoot(), false);
        boolean is_login = preferJw.getBoolean("login_flag", false);
        if (!is_login) {
            start();
        } else {
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    //打开主页
                    dialogFragment(new HomePage());
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
                    case R.id.btn_login:
                        Bundle bundle = new Bundle();
                        bundle.putInt("flag", Config.LOGIN_FLAG_JW);
                        open(new LoginPage(), bundle);
                        break;
                    case R.id.btn_enter:
                        dialogFragment(new HomePage());
                        preferJw.edit().putBoolean("login_flag", true).apply();
                        break;
                    default:
                        break;
                }
            }
        };
        View startBtns = page.findViewById(R.id.start_btns);
        startBtns.setVisibility(View.VISIBLE);
        Animation loadAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.push_right_in);
        startBtns.startAnimation(loadAnimation);
        TextView btnLogin = (TextView) page.findViewById(R.id.btn_login);
        TextView btnEnter = (TextView) page.findViewById(R.id.btn_enter);
        btnLogin.setOnClickListener(listener);
        btnEnter.setOnClickListener(listener);
    }
}
