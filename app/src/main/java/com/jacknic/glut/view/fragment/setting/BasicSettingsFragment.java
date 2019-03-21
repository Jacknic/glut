package com.jacknic.glut.view.fragment.setting;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Keep;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SwitchCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jacknic.glut.BuildConfig;
import com.jacknic.glut.MainActivity;
import com.jacknic.glut.R;
import com.jacknic.glut.event.MoneyLimitChangeEvent;
import com.jacknic.glut.page.FeedbackPage;
import com.jacknic.glut.util.Config;
import com.jacknic.glut.util.PageTool;
import com.jacknic.glut.util.PreferManager;
import com.jacknic.glut.util.UpdateUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;



/**
 * 基本设置
 */
public class BasicSettingsFragment extends Fragment implements View.OnClickListener {
    private TextView tvMoneyLimit;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View fragment = inflater.inflate(R.layout.frag_setting_basic, container, false);

        TextView tvSelectThemeColor = (TextView) fragment.findViewById(R.id.setting_tv_select_theme_color);
        final SharedPreferences prefer = PreferManager.getPrefer();
        tvSelectThemeColor.setOnClickListener(this);
        TextView tvFeedback = (TextView) fragment.findViewById(R.id.setting_tv_feedback);
        tvFeedback.setOnClickListener(this);
        TextView tvFeedbackDeal = (TextView) fragment.findViewById(R.id.setting_tv_feedbackDeal);
        tvFeedbackDeal.setOnClickListener(this);
        TextView tvCheckUpdate = (TextView) fragment.findViewById(R.id.setting_tv_checkUpdate);
        tvCheckUpdate.setOnClickListener(this);
        LinearLayout llWarnLimit = (LinearLayout) fragment.findViewById(R.id.setting_ll_warn_limit);
        llWarnLimit.setOnClickListener(this);

        tvMoneyLimit = (TextView) fragment.findViewById(R.id.setting_tv_money_limit);
        setMoneyLimitText();

        boolean autoCheckUpdate = prefer.getBoolean(Config.KEY_AUTO_CHECK_UPDATE, true);
        SwitchCompat swAutoCheck = (SwitchCompat) fragment.findViewById(R.id.setting_sw_auto_check);
        swAutoCheck.setChecked(autoCheckUpdate);
        swAutoCheck.setOnCheckedChangeListener( new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                prefer.edit().putBoolean(Config.KEY_AUTO_CHECK_UPDATE, isChecked).apply();
            }
        });

        boolean isMoneyWarning = prefer.getBoolean(Config.KEY_MONEY_WARNING, true);
        SwitchCompat swMoneyWarning = (SwitchCompat) fragment.findViewById(R.id.setting_sw_money_warning);
        swMoneyWarning.setChecked(isMoneyWarning);
        swMoneyWarning.setOnCheckedChangeListener( new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                prefer.edit().putBoolean(Config.KEY_MONEY_WARNING, isChecked).apply();
                MainActivity.initWarningWorker();
            }
        });

        TextView tvUpdateTips = (TextView) fragment.findViewById(R.id.setting_tv_updateTips);
        tvUpdateTips.setOnClickListener(this);
        tvCheckUpdate.append(BuildConfig.VERSION_NAME);
        return fragment;
    }

    @Override
    public void onClick(View v) {
        String url = "";
        switch (v.getId()) {
            case R.id.setting_tv_select_theme_color:
                ColorsDialogFragment.launch(getActivity());
                break;
            case R.id.setting_tv_feedback:
                PageTool.open(getContext(), new FeedbackPage());
                break;
            case R.id.setting_tv_feedbackDeal:
                url = "https://github.com/Jacknic/glut/blob/master/feedback.md";
                break;
            case R.id.setting_tv_checkUpdate:
                UpdateUtil.checkUpdate(getActivity(), true);
                break;
            case R.id.setting_tv_updateTips:
                url = "https://github.com/Jacknic/glut/blob/master/update.md";
                break;
            case R.id.setting_ll_warn_limit:
                MoneyLimitDialogFragment.launch(getActivity());
                break;
        }
        if (!TextUtils.isEmpty(url)) {
            PageTool.openWebPage(getActivity(), url);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    /**
     * 一卡通余额提醒额度改变事件
     */
    @Keep
    @Subscribe
    public void MoneyLimitChange(MoneyLimitChangeEvent event) {
        setMoneyLimitText();
    }

    public void setMoneyLimitText() {
        tvMoneyLimit.setText(
                PreferManager.getPrefer().getInt(Config.KEY_MONEY_LIMIT, Config.DEFAULT_MONEY_LIMIT) + "元");
    }
}
