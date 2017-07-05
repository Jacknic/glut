package com.jacknic.glut.activity.library;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.jacknic.glut.R;
import com.jacknic.glut.activity.BaseActivity;
import com.jacknic.glut.adapter.BorrowListAdapter;
import com.jacknic.glut.model.LoginModel;
import com.jacknic.glut.util.Config;
import com.jacknic.glut.view.widget.Dialogs;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.AbsCallbackWrapper;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.request.BaseRequest;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import okhttp3.Call;
import okhttp3.Response;

public class BorrowActivity extends BaseActivity {

    private ImageView top_setting;
    private RecyclerView rl_borrow_list;
    private boolean isAuto = true;
    private TextView tips;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_borrow);
        setStatusView();
        TextView title = (TextView) findViewById(R.id.tv_toolbar_title);
        top_setting = (ImageView) findViewById(R.id.iv_setting);
        top_setting.setVisibility(View.VISIBLE);
        top_setting.setImageResource(R.drawable.ic_autorenew);
        top_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RotateAnimation rotateAnimation = new RotateAnimation(0, 360 * 2, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                rotateAnimation.setDuration(1000L);
                rotateAnimation.setRepeatMode(-1);
                v.startAnimation(rotateAnimation);
                getRenewList();
            }
        });
        title.setText("借阅查询");
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        tips = (TextView) findViewById(R.id.ts_tv_empty_tips);
        rl_borrow_list = (RecyclerView) findViewById(R.id.ts_rl_borrow_list);
        rl_borrow_list.setLayoutManager(new LinearLayoutManager(BorrowActivity.this));
        getRenewList();
    }

    /**
     * 获取借书列表
     */
    private void getRenewList() {
        OkGo.get("http://202.193.80.181:8080/opac/loan/renewList").execute(new StringCallback() {
            private Snackbar make = Snackbar.make(rl_borrow_list, "请求中...", Snackbar.LENGTH_SHORT);

            @Override
            public void onBefore(BaseRequest request) {
                make.show();
            }

            @Override
            public void onSuccess(String s, Call call, Response response) {
                Document dom = Jsoup.parse(s);
                Elements select = dom.select("#contentTable tr[id!=contentHeader]");
                if (select.isEmpty()) {
                    tips.setVisibility(View.VISIBLE);
                    tips.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            top_setting.callOnClick();
                        }
                    });
                } else {
                    tips.setVisibility(View.GONE);
                    rl_borrow_list.setAdapter(new BorrowListAdapter(BorrowActivity.this, select));
                }
            }

            @Override
            public void onError(Call call, Response response, Exception e) {
                if (response != null) {
                    // 自动登录
                    if (isAuto) {
                        autoLogin();
                    } else {
                        //登录图书对话框
                        AlertDialog loginTs = Dialogs.getLoginTs(BorrowActivity.this, new AbsCallbackWrapper() {
                            @Override
                            public void onAfter(Object o, Exception e) {
                                getRenewList();
                            }
                        });
                        loginTs.show();
                    }

                } else {
                    Snackbar.make(rl_borrow_list, "网络错误", Snackbar.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onAfter(String s, Exception e) {
                super.onAfter(s, e);
                make.dismiss();
                top_setting.clearAnimation();
            }
        });
    }

    /**
     * 自动登录
     */
    private void autoLogin() {
        isAuto = false;
        Log.d("ts 图书", "autoLogin: 自动登录图书馆");
        SharedPreferences prefer_cw = OkGo.getContext().getSharedPreferences(Config.PREFER_CW, Context.MODE_PRIVATE);
        String sid = prefer_cw.getString(Config.SID, "");
        String password = prefer_cw.getString(Config.PASSWORD, "");
        if (!TextUtils.isEmpty(sid) && !TextUtils.isEmpty(password)) {
            LoginModel.loginTs(sid, password, new AbsCallbackWrapper() {
                @Override
                public void onAfter(Object o, Exception e) {
                    getRenewList();
                }
            });
        } else {
            getRenewList();
        }
    }
}
