package com.jacknic.glut.view.fragment.home;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.CookieManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jacknic.glut.R;
import com.jacknic.glut.model.bean.FinancialInfoBean;
import com.jacknic.glut.util.Config;
import com.jacknic.glut.util.PageTool;
import com.jacknic.glut.util.PreferManager;
import com.jacknic.glut.view.widget.Dialogs;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.AbsCallbackWrapper;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.cookie.store.CookieStore;

import java.util.List;

import okhttp3.Call;
import okhttp3.Cookie;
import okhttp3.HttpUrl;
import okhttp3.Response;

import static android.content.ContentValues.TAG;
import static com.jacknic.glut.util.Config.URL_CW_API;


/**
 * 财务
 */
public class FinancialFragment extends Fragment implements View.OnClickListener {
    SharedPreferences prefer;
    private String sid;
    private View fragment;
    private SwipeRefreshLayout refreshLayout;
    private boolean isLogin = false;
    private String password;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        fragment = inflater.inflate(R.layout.frag_financial, container, false);
        prefer = PreferManager.getPrefer();
        initViews();
        sid = prefer.getString(Config.SID, "");
        password = prefer.getString(Config.PASSWORD_CW, "");
        if (TextUtils.isEmpty(sid) || TextUtils.isEmpty(password)) {
            fragment.findViewById(R.id.cw_tv_yktye).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    toLogin();
                }
            });
        } else {
            showData();
        }
        getYue();
        return fragment;
    }

    /**
     * 显示数据
     */
    private void showData() {
        String info = prefer.getString("info", "");
        FinancialInfoBean financialInfoBean = JSON.parseObject(info, FinancialInfoBean.class);
        if (TextUtils.isEmpty(info) || financialInfoBean == null) {
            getInfo();
        } else {
            isLogin = true;
            setText(financialInfoBean);
            Log.d(TAG, "showData: 读取缓存信息");
        }
    }

    /**
     * 登录财务处
     */
    private void toLogin() {
        Dialogs.showLoginCw(getContext(), new AbsCallbackWrapper() {
            @Override
            public void onSuccess(Object o, Call call, Response response) {
                isLogin = true;
                fragment.findViewById(R.id.cw_tv_yktye).setOnClickListener(null);
                sid = prefer.getString(Config.SID, "");
                password = prefer.getString(Config.PASSWORD_CW, "");
                showData();
                autoLogin();
            }

            @Override
            public void onError(Call call, Response response, Exception e) {
                Toast.makeText(getContext(), "登录财务处失败", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * 获取财务信息
     */
    private void getInfo() {
        OkGo.post(URL_CW_API).tag(this)
                .params("method", "getinfo")
                .params("stuid", "1")
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String res_json, Call call, Response response) {
                        try {
                            String data = JSONObject.parseObject(res_json).getString("data");
                            FinancialInfoBean financialInfoBean = JSONObject.parseObject(data, FinancialInfoBean.class);
                            Log.d(this.getClass().getName(), "获取最新信息");
                            setText(financialInfoBean);
                            isLogin = true;
                            prefer.edit().putString("info", JSON.toJSONString(financialInfoBean)).apply();
                        } catch (Exception e) {
                            toLogin();
                            Log.d(this.getClass().getName(), "获取财务信息失败" + e.getMessage());
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
//                        Toast.makeText(getContext(), "获取失败数据！", Toast.LENGTH_SHORT).show();
                        Log.d("okgo", "加载数据失败");
                    }
                });
    }

    /**
     * 文本控件赋值
     */
    private void setText(FinancialInfoBean financialInfoBean) {
        if (financialInfoBean == null) return;
        findAndSetText(R.id.cw_tv_name, financialInfoBean.getStudentName());
        findAndSetText(R.id.cw_tv_card_no, financialInfoBean.getBankId());
        findAndSetText(R.id.cw_tv_college, financialInfoBean.getAcademy());
        findAndSetText(R.id.cw_tv_dkdz, financialInfoBean.getDeferralDate());
        findAndSetText(R.id.cw_tv_dkje, financialInfoBean.getDeferralMoney() + "元");
        findAndSetText(R.id.cw_tv_grade, financialInfoBean.getGrade());
        findAndSetText(R.id.cw_tv_number, financialInfoBean.getSid());
        findAndSetText(R.id.cw_tv_remark, financialInfoBean.getRemark());
        findAndSetText(R.id.cw_tv_yjfy, financialInfoBean.getPayMoney() + "元");
    }

    /**
     * 初始化视图
     */
    private void initViews() {
        int[] cw_iv_ids = {R.id.cw_iv_jyjl, R.id.cw_iv_jfxm, R.id.cw_iv_jfmx, R.id.cw_iv_yktcz, R.id.cw_iv_cwzx};
        for (int id : cw_iv_ids) {
            fragment.findViewById(id).setOnClickListener(this);
        }

        refreshLayout = (SwipeRefreshLayout) fragment.findViewById(R.id.cw_refreshLayout);
        int[] colors = {0xff00ddff, 0xff99cc00, 0xffffbb33, 0xffff4444};
        refreshLayout.setColorSchemeColors(colors);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (!isLogin) {
                    toLogin();
                    refreshLayout.setRefreshing(false);
                } else {
                    getYue();
                    getInfo();
                }

            }


        });
        ImageView iv_bid_copy = (ImageView) fragment.findViewById(R.id.cw_iv_bid_copy);
        iv_bid_copy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView tv_card_no = (TextView) fragment.findViewById(R.id.cw_tv_card_no);
                CharSequence text_card_no = tv_card_no.getText();
                if (!TextUtils.isEmpty(text_card_no)) {
                    ClipboardManager cm = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                    if (cm != null) {
                        cm.setPrimaryClip(ClipData.newPlainText("text", text_card_no));
                        Toast.makeText(getContext(), "复制成功，可粘贴使用", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), "复制失败（无法访问剪切板）", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

    }

    /**
     * 寻找并设置文字
     *
     * @param view_id TextView id
     * @param text    文本内容
     */
    private void findAndSetText(int view_id, String text) {
        final TextView textView = (TextView) fragment.findViewById(view_id);
        textView.setText(text);
    }


    /**
     * 控件点击事件处理
     *
     * @param v 被点击的控件
     */
    @Override
    public void onClick(View v) {
        if (!isLogin) {
            autoLogin();
            return;
        }
        String[] urls = new String[]{
                //交易记录
                "http://cwjf.glut.edu.cn/mobile/jyjl",
                //学费项目
                "http://cwjf.glut.edu.cn/mobile/xfxm",
                //财务处主页
                "http://cwjf.glut.edu.cn/mobile/index",
                //缴费明细
                "http://cwjf.glut.edu.cn/mobile/jfmx",
                //一卡通充值
                "http://cwjf.glut.edu.cn/mobile/yktzxcz",
        };
        String url = "";
        switch (v.getId()) {
            case R.id.cw_iv_jyjl:
                url = urls[0];
                break;
            case R.id.cw_iv_jfxm:
                url = urls[1];
                break;
            case R.id.cw_iv_cwzx:
                url = urls[2];
                break;
            case R.id.cw_iv_jfmx:
                url = urls[3];
                break;
            case R.id.cw_iv_yktcz:
                url = urls[4];
                break;
        }
        PageTool.openWebPage(getActivity(), url);
    }

    /**
     * 自动登录认证
     */
    private void autoLogin() {
        if (TextUtils.isEmpty(sid) || TextUtils.isEmpty(password)) {
            toLogin();
            return;
        }
        // 执行自动登录
        StringCallback callback = new StringCallback() {
            @Override
            public void onSuccess(String s, Call call, Response response) {
                JSONObject json = JSON.parseObject(s);
                boolean success = json.getBooleanValue("success");
                String data = json.getString("data");
                if (success) {
                    isLogin = true;
                    syncStatus();
                    showData();
                } else {
                    Toast.makeText(OkGo.getContext(), "登录财务失败：" + data, Toast.LENGTH_SHORT).show();
                    toLogin();
                }
            }
        };
        String cookie = CookieManager.getInstance().getCookie(URL_CW_API);
        if (TextUtils.isEmpty(cookie)) {
            OkGo.post(Config.URL_CW_LOGIN).tag(this)
                    .params("sid", sid)
                    .params("passWord", password)
                    .execute(callback);
        }


    }

    /**
     * 同步登录状态到webkit
     */
    private void syncStatus() {
        CookieManager cookieManager = CookieManager.getInstance();
        CookieStore cookieStore = OkGo.getInstance().getCookieJar().getCookieStore();
        HttpUrl httpUrl = HttpUrl.parse(URL_CW_API);
        List<Cookie> cookies = cookieStore.getCookie(httpUrl);
        for (Cookie cookie : cookies) {
            cookieManager.setCookie(URL_CW_API, cookie.toString());
        }
    }

    /**
     * 获取一卡通余额
     */
    private void getYue() {
        OkGo.post(URL_CW_API).tag(this)
                .params("method", "getecardinfo")
                .params("stuid", "0")
                .params("carno", sid)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String res_json, Call call, Response response) {
                        JSONObject json;
                        try {
                            json = JSONObject.parseObject(res_json);
                        } catch (Exception e) {
                            Log.d(this.getClass().getName(), "获取财务数据失败");
                            return;
                        }
                        boolean success = json.getBoolean("success");
                        if (success) {
                            JSONObject data = json.getJSONObject("data");
                            findAndSetText(R.id.cw_tv_yktye, data.getString("Balance"));
                        } else {
                            findAndSetText(R.id.cw_tv_yktye, "获取失败");
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        findAndSetText(R.id.cw_tv_yktye, "获取失败");
                    }

                    @Override
                    public void onAfter(String s, Exception e) {
                        refreshLayout.setRefreshing(false);
                        if (getContext() == null) return;
                        Animation side_left = AnimationUtils.loadAnimation(getContext(), android.R.anim.slide_in_left);
                        View iv_refresh = fragment.findViewById(R.id.cw_tv_yktye);
                        iv_refresh.startAnimation(side_left);
                    }
                });
    }
}
