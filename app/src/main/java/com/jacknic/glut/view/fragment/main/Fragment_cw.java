package com.jacknic.glut.view.fragment.main;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jacknic.glut.R;
import com.jacknic.glut.activity.BrowserActivity;
import com.jacknic.glut.activity.LoginActivity;
import com.jacknic.glut.model.bean.FinancialInfoBean;
import com.jacknic.glut.util.Config;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.Timer;
import java.util.TimerTask;

import okhttp3.Call;
import okhttp3.Response;

import static android.content.ContentValues.TAG;


/**
 * 财务
 */
public class Fragment_cw extends Fragment implements View.OnClickListener {
    SharedPreferences prefer_cw;
    private String sid;
    private String student_id;
    private View fragment;
    private SwipeRefreshLayout refreshLayout;
    private boolean isLogin;
    private boolean isFirst = true;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        prefer_cw = getContext().getSharedPreferences(Config.PREFER_CW, Context.MODE_PRIVATE);
        fragment = inflater.inflate(R.layout.fragment_cw, container, false);
        isLogin = prefer_cw.getBoolean(Config.LOGIN_FLAG, false);
        initViews();
        if (!isLogin) {
            fragment.findViewById(R.id.cw_tv_yktye).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    toLogin();
                }
            });
            return fragment;
        }
        sid = prefer_cw.getString(Config.SID, "");
        student_id = prefer_cw.getString(Config.STUDENT_ID, "");
        showData();
        //登录网页
        autoLogin(null);
        return fragment;
    }

    /**
     * 显示数据
     */
    private void showData() {
        String info = prefer_cw.getString("info", "");
        FinancialInfoBean financialInfoBean = JSON.parseObject(info, FinancialInfoBean.class);
        if (TextUtils.isEmpty(info) || financialInfoBean == null) {
            getInfo();
        } else {
            setText(financialInfoBean);
            Log.d(TAG, "showData: 读取缓存信息");
        }
        getYue();
    }

    /**
     * 登录财务处
     */
    private void toLogin() {
        Intent intent = new Intent(getContext(), LoginActivity.class);
        intent.putExtra("flag", Config.LOGIN_FLAG_CW);
        getContext().startActivity(intent);
    }

    /**
     * 获取财务信息
     */
    private void getInfo() {
        OkGo.get(Config.getQueryUrlCW("getinfo", student_id))
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String res_json, Call call, Response response) {
                        try {
                            String msg = JSONObject.parseObject(res_json).getString("msg");
                            FinancialInfoBean financialInfoBean = JSONObject.parseObject(msg, FinancialInfoBean.class);
                            Log.d(this.getClass().getName(), "获取最新信息");
                            setText(financialInfoBean);
                            prefer_cw.edit().putString("info", JSON.toJSONString(financialInfoBean)).apply();
                        } catch (Exception e) {
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
     *
     * @param financialInfoBean
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
        refreshLayout.setColorSchemeColors(getResources().getColor(android.R.color.holo_blue_bright), getResources().getColor(android.R.color.holo_green_light),
                getResources().getColor(android.R.color.holo_orange_light), getResources().getColor(android.R.color.holo_red_light));
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (!isLogin) {
                    toLogin();
                    refreshLayout.setRefreshing(false);
                } else {
                    getYue();
                    getInfo();
                    //10s超时设置
                    new Timer().schedule(new TimerTask() {
                        @Override
                        public void run() {
                            if (refreshLayout.isRefreshing()) {
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        refreshLayout.setRefreshing(false);
                                    }
                                });
                            }
                        }
                    }, 10000L);
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
                    cm.setPrimaryClip(ClipData.newPlainText("text", text_card_no));
                    Toast.makeText(getContext(), "复制银行卡号成功，可粘贴使用", Toast.LENGTH_SHORT).show();
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
        String[] urls = new String[]{
                //交易记录
                "http://cwwsjf.glut.edu.cn:8088/chargeonline/MborderList.aspx",
                //学费项目
                "http://cwwsjf.glut.edu.cn:8088/chargeonline/MbstudentChargeItems.aspx",
                //财务处主页
                "http://cwwsjf.glut.edu.cn:8088/chargeonline/Mbindex.aspx",
                //缴费明细
                "http://cwwsjf.glut.edu.cn:8088/chargeonline/MbchargeDetail.aspx",
                //一卡通充值
                "http://cwwsjf.glut.edu.cn:8088/chargeonline/Mbrecharge.aspx",
        };
        Intent intent = new Intent(getContext(), BrowserActivity.class);
        switch (v.getId()) {
            case R.id.cw_iv_jyjl:
                intent.setAction(urls[0]);
                break;
            case R.id.cw_iv_jfxm:
                intent.setAction(urls[1]);
                break;
            case R.id.cw_iv_cwzx:
                intent.setAction(urls[2]);
                break;
            case R.id.cw_iv_jfmx:
                intent.setAction(urls[3]);
                break;
            case R.id.cw_iv_yktcz:
                intent.setAction(urls[4]);
                break;
        }
        startActivity(intent);

    }

    /**
     * 自动登录认证
     */
    private void autoLogin(@Nullable final Intent intent) {
        long lastLogin = prefer_cw.getLong("last_login", 0);
        boolean isLogged = (System.currentTimeMillis() - lastLogin) < 30 * 60 * 1000;
        if (isLogged) {
            if (intent != null)
                startActivity(intent);
            return;
        }
        final String sid = prefer_cw.getString(Config.SID, "");
        final String password = prefer_cw.getString(Config.PASSWORD, "");
        if (TextUtils.isEmpty(sid) || TextUtils.isEmpty(password)) {
            toLogin();
            return;
        }
        final String url_login = "http://cwwsjf.glut.edu.cn:8088/ChargeOnline/Mblogin.aspx";
        OkGo.get(url_login).execute(new StringCallback() {
            String state = "";
            String validation = "";
            String logon = "";

            @Override
            public void onSuccess(String s, Call call, Response response) {
                Document document = Jsoup.parse(s);
                Element viewState = document.getElementById("__VIEWSTATE");
                if (viewState != null)
                    state = viewState.val();
                Element eventValidation = document.getElementById("__EVENTVALIDATION");
                if (eventValidation != null)
                    validation = eventValidation.val();
                Element logon_ele = document.getElementById("logon");
                if (logon_ele != null)
                    logon = logon_ele.val();
            }

            @Override
            public void onAfter(String s, Exception e) {
                WebView webView = new WebView(getContext());
                WebViewClient client = new WebViewClient() {
                    @Override
                    public void onPageStarted(WebView view, String url, Bitmap favicon) {
                        //登录后会自动重定向，如果不是首次请求，则直接打开activity
                        if (isFirst) {
                            isFirst = false;
                        } else {
                            view.stopLoading();
                            prefer_cw.edit().putLong("last_login", System.currentTimeMillis()).apply();
                            System.out.println("自动登录");
                            if (intent != null)
                                startActivity(intent);
                        }
                    }
                };
                webView.setWebViewClient(client);
                //没有这串字符，服务器不认
                String verify_data = String.format("__VIEWSTATE=%s&__EVENTVALIDATION=%s&TextBox_SID=%s&TextBox_Password=%s&logon=%s",
                        state, validation, sid, password, logon);
                //post请求登录
                webView.postUrl(url_login, verify_data.getBytes());
            }
        });


    }

    /**
     * 获取一卡通余额
     */
    private void getYue() {
        OkGo.get(Config.getQueryUrlCW("yikatongyue", "0") + String.format("&sid=%s", sid)).execute(new StringCallback() {
            @Override
            public void onSuccess(String res_json, Call call, Response response) {
                JSONObject json = null;
                try {
                    json = JSONObject.parseObject(res_json);
                } catch (Exception e) {
                    Log.d(this.getClass().getName(), "获取财务数据失败");
                    return;
                }
                Integer result = json.getInteger("result");
                if (result != null && result.equals(0)) {
                    JSONObject data = json.getJSONObject("msg");
                    findAndSetText(R.id.cw_tv_yktye, data.getString("SurplusMoney"));
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
                side_left.setDuration(500L);
                View iv_refresh = fragment.findViewById(R.id.cw_tv_yktye);
                iv_refresh.startAnimation(side_left);
            }
        });
    }
}
