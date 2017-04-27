package com.jacknic.glut.fragments.main;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.jacknic.glut.R;
import com.jacknic.glut.activity.BrowserActivity;
import com.jacknic.glut.activity.LoginActivity;
import com.jacknic.glut.beans.financial.InfoBean;
import com.jacknic.glut.utils.Config;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;

import okhttp3.Call;
import okhttp3.Response;


/**
 * 财务
 */
public class Fragment_cw extends Fragment implements View.OnClickListener {
    private ImageView iv_yue_refresh;
    SharedPreferences prefer_cw;
    private String sid;
    private String studentid;
    private View fragment;
    private ImageView iv_bid_copy;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        prefer_cw = getContext().getSharedPreferences(Config.PREFER_CW, Context.MODE_PRIVATE);
        View toLogin = check(inflater, container);
        if (toLogin != null) return toLogin;
        fragment = inflater.inflate(R.layout.fragment_cw, container, false);
        sid = prefer_cw.getString("sid", "");
        studentid = prefer_cw.getString("studentid", "");
        initViews();
        getInfo();
        getYue();
        return fragment;
    }

    /**
     * 获取财务信息
     */
    private void getInfo() {
        OkGo.get(Config.getQueryUrlCW("getinfo", studentid))
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String res_json, Call call, Response response) {
                        if (null == res_json || "".equals(res_json)) return;
                        String msg = JSONObject.parseObject(res_json).getString("msg");
                        if (msg == null) {
                            return;
                        }
                        InfoBean infoBean = JSONObject.parseObject(msg, InfoBean.class);
                        findAndSetText(R.id.cw_tv_name, infoBean.getStudentName());
                        findAndSetText(R.id.cw_tv_card_no, infoBean.getBankId());
                        findAndSetText(R.id.cw_tv_college, infoBean.getAcademy());
                        findAndSetText(R.id.cw_tv_dkdz, infoBean.getDeferralDate());
                        findAndSetText(R.id.cw_tv_dkje, infoBean.getDeferralMoney() + "元");
                        findAndSetText(R.id.cw_tv_grade, infoBean.getGrade());
                        findAndSetText(R.id.cw_tv_number, infoBean.getSid());
                        findAndSetText(R.id.cw_tv_remark, infoBean.getRemark());
                        findAndSetText(R.id.cw_tv_yjfy, infoBean.getPayMoney() + "元");
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
//                        Toast.makeText(getContext(), "获取失败数据！", Toast.LENGTH_SHORT).show();
                        Log.d("okgo", "加载数据失败");
                    }
                });
    }

    /**
     * 检查用户是否已经登录
     *
     * @param inflater
     * @param container
     * @return
     */
    @Nullable
    private View check(LayoutInflater inflater, ViewGroup container) {
        boolean isLogin = prefer_cw.getBoolean("login_flag", false);
        if (!isLogin) {
            View view = inflater.inflate(R.layout.tips, container, false);
            view.findViewById(R.id.touch_to_login).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getContext(), LoginActivity.class);
                    intent.putExtra("flag", Config.LOGIN_FLAG_CW);
                    getContext().startActivity(intent);
                }
            });
            return view;
        }
        return null;
    }

    /**
     * 初始化视图
     */
    private void initViews() {
        ImageView iv_jyjl = (ImageView) fragment.findViewById(R.id.cw_iv_jyjl);
        ImageView iv_jfxm = (ImageView) fragment.findViewById(R.id.cw_iv_jfxm);
        ImageView iv_jfmx = (ImageView) fragment.findViewById(R.id.cw_iv_jfmx);
        ImageView iv_djfy = (ImageView) fragment.findViewById(R.id.cw_iv_yktcz);

        iv_jyjl.setOnClickListener(this);
        iv_djfy.setOnClickListener(this);
        iv_jfmx.setOnClickListener(this);
        iv_jfxm.setOnClickListener(this);

        iv_yue_refresh = (ImageView) fragment.findViewById(R.id.cw_iv_yue_refresh);
        iv_yue_refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refresh();
                getYue();
                getInfo();
            }
        });

        iv_bid_copy = (ImageView) fragment.findViewById(R.id.cw_iv_bid_copy);
        iv_bid_copy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager cm = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                TextView tv_card_no = (TextView) fragment.findViewById(R.id.cw_tv_card_no);
                cm.setPrimaryClip(ClipData.newPlainText("text", tv_card_no.getText()));
                Toast.makeText(getContext(), "复制银行卡号成功，可粘贴使用", Toast.LENGTH_SHORT).show();
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


    public static Fragment newInstance() {
        return new Fragment_cw();
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
            case R.id.cw_iv_jfmx:
                intent.setAction(urls[2]);
                break;
            case R.id.cw_iv_yktcz:
                intent.setAction(urls[3]);
                break;
        }
        getContext().startActivity(intent);
    }

    /**
     * 获取一卡通余额
     */
    private void getYue() {
        OkGo.get(Config.getQueryUrlCW("yikatongyue", "0") + String.format("&sid=%s", sid)).execute(new StringCallback() {
            @Override
            public void onSuccess(String res_json, Call call, Response response) {
                JSONObject json = JSONObject.parseObject(res_json);
                String msg = json.getString("msg");
                Integer result = json.getInteger("result");
                if (result.equals(0)) {
                    JSONObject data = JSONObject.parseObject(msg);
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
                super.onAfter(s, e);
                Animation side_left = AnimationUtils.loadAnimation(getContext(), android.R.anim.slide_in_left);
                side_left.setDuration(500L);
                View iv_refresh = fragment.findViewById(R.id.cw_tv_yktye);
                iv_refresh.startAnimation(side_left);
                iv_yue_refresh.clearAnimation();
            }
        });
    }

    /**
     * 刷新余额
     */
    private void refresh() {
        RotateAnimation rotateAnimation = new RotateAnimation(0, 360 * 2, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotateAnimation.setDuration(1000L);
        rotateAnimation.setRepeatMode(-1);
        iv_yue_refresh.startAnimation(rotateAnimation);
    }
}
