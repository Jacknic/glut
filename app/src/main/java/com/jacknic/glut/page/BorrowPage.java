package com.jacknic.glut.page;

import android.content.SharedPreferences;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;

import com.jacknic.glut.R;
import com.jacknic.glut.adapter.BorrowListAdapter;
import com.jacknic.glut.model.LoginModel;
import com.jacknic.glut.util.Config;
import com.jacknic.glut.util.PreferManager;
import com.jacknic.glut.util.SnackbarTool;
import com.jacknic.glut.view.widget.Dialogs;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.AbsCallbackWrapper;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.request.BaseRequest;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Response;

/**
 * 图书借阅页
 */
public class BorrowPage extends BasePage {
    @BindView(R.id.rl_borrow_list)
    RecyclerView rl_borrow_list;
    @BindView(R.id.tv_empty_tips)
    View tips;
    private boolean isAuto = true;

    @Override
    protected int getLayoutId() {
        mTitle = "图书借阅";
        return R.layout.page_borrow;
    }


    @Override
    void initPage() {
        rl_borrow_list.setLayoutManager(new LinearLayoutManager(getContext()));
        getRenewList();
    }

    /**
     * 点击刷新
     */
    @OnClick(R.id.tv_empty_tips)
    void tipsClick() {
        getRenewList();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_refresh, menu);
    }

    @Override
    void refresh() {
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
                if (getContext() == null) return;
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
                if (getContext() == null) return;
                if (response != null) {
                    // 自动登录
                    if (isAuto) {
                        autoLogin();
                    } else {
                        //登录图书对话框
                        Dialogs.showLoginTs(getActivity(), new AbsCallbackWrapper() {
                            @Override
                            public void onAfter(Object o, Exception e) {
                                getRenewList();
                            }
                        });
                    }

                } else {
                    SnackbarTool.showShort("网络错误");
                }
            }
        });
    }

    /**
     * 自动登录
     */
    private void autoLogin() {
        isAuto = false;
        Log.d("ts 图书", "autoLogin: 自动登录图书馆");
        SharedPreferences prefer = PreferManager.getPrefer();
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
