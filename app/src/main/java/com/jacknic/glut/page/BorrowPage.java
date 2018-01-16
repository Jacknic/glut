package com.jacknic.glut.page;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jacknic.glut.R;
import com.jacknic.glut.adapter.BorrowListAdapter;
import com.jacknic.glut.model.LoginModel;
import com.jacknic.glut.util.Config;
import com.jacknic.glut.util.SnackbarTool;
import com.jacknic.glut.util.ViewUtil;
import com.jacknic.glut.view.widget.Dialogs;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.AbsCallbackWrapper;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.request.BaseRequest;
import com.tencent.stat.StatService;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import okhttp3.Call;
import okhttp3.Response;

/**
 * 图书借阅页
 */
public class BorrowPage extends Fragment {

    private RecyclerView rl_borrow_list;
    private boolean isAuto = true;
    private View tips;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        StatService.trackBeginPage(getContext(), "图书借阅页");
        View page = inflater.inflate(R.layout.page_borrow, container, false);
        ViewUtil.setTitle(getContext(), "借阅查询");
        tips = page.findViewById(R.id.ts_tv_empty_tips);
        rl_borrow_list = (RecyclerView) page.findViewById(R.id.ts_rl_borrow_list);
        rl_borrow_list.setLayoutManager(new LinearLayoutManager(getContext()));
        return page;
    }

    @Override
    public void onStart() {
        super.onStart();
        getRenewList();
    }

    /**
     * 获取借书列表
     */
    private void getRenewList() {
        OkGo.get("http://202.193.80.181:8080/opac/loan/renewList").execute(new StringCallback() {

            @Override
            public void onBefore(BaseRequest request) {
                SnackbarTool.showShort("请求中...");
            }

            @Override
            public void onSuccess(String s, Call call, Response response) {
                Document dom = Jsoup.parse(s);
                Elements select = dom.select("#contentTable tr[id!=contentHeader]");
                if (select.isEmpty()) {
                    tips.setVisibility(View.VISIBLE);
                } else {
                    tips.setVisibility(View.GONE);
                    rl_borrow_list.setAdapter(new BorrowListAdapter(getContext(), select));
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
                        AlertDialog loginTs = Dialogs.getLoginTs(getActivity(), new AbsCallbackWrapper() {
                            @Override
                            public void onAfter(Object o, Exception e) {
                                getRenewList();
                            }
                        });
                        loginTs.show();
                    }

                } else {
                    SnackbarTool.showShort("网络错误");
                }
            }

            @Override
            public void onAfter(String s, Exception e) {
                super.onAfter(s, e);
                SnackbarTool.dismiss();
            }
        });
    }

    /**
     * 自动登录
     */
    private void autoLogin() {
        isAuto = false;
        Log.d("ts 图书", "autoLogin: 自动登录图书馆");
        SharedPreferences prefer = OkGo.getContext().getSharedPreferences(Config.PREFER, Context.MODE_PRIVATE);
        String sid = prefer.getString(Config.SID, "");
        String password = prefer.getString(Config.PASSWORD_TS, "");
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
