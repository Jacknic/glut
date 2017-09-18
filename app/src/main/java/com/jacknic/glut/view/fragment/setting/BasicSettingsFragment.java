package com.jacknic.glut.view.fragment.setting;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.jacknic.glut.R;
import com.jacknic.glut.activity.BrowserActivity;
import com.jacknic.glut.activity.FeedbackActivity;
import com.jacknic.glut.util.Config;
import com.jacknic.glut.util.UpdateUtil;

import static android.content.Context.MODE_PRIVATE;


/**
 * 基本设置
 */
public class BasicSettingsFragment extends Fragment implements View.OnClickListener {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View fragment = inflater.inflate(R.layout.fragment_setting_basic, container, false);
        TextView tv_select_theme_color = (TextView) fragment.findViewById(R.id.setting_tv_select_theme_color);
        ImageView iv_select_btn_color = (ImageView) fragment.findViewById(R.id.setting_iv_select_btn_color);
        TextView tv_select_btn_color = (TextView) fragment.findViewById(R.id.setting_tv_select_btn_color);
        final SharedPreferences prefer_setting = getContext().getSharedPreferences(Config.PREFER_SETTING, MODE_PRIVATE);
        int index = prefer_setting.getInt(Config.SETTING_COLOR_INDEX, 4);
        iv_select_btn_color.setImageResource(Config.COLORS[index]);
        tv_select_theme_color.setOnClickListener(this);
        tv_select_btn_color.setOnClickListener(this);
        TextView tv_feedback = (TextView) fragment.findViewById(R.id.setting_tv_feedback);
        tv_feedback.setOnClickListener(this);
        TextView tv_feedbackDeal = (TextView) fragment.findViewById(R.id.setting_tv_feedbackDeal);
        tv_feedbackDeal.setOnClickListener(this);
        TextView tv_checkUpdate = (TextView) fragment.findViewById(R.id.setting_tv_checkUpdate);
        tv_checkUpdate.setOnClickListener(this);
        boolean auto_check_update = prefer_setting.getBoolean("auto_check_update", true);
        SwitchCompat sw_auto_check = (SwitchCompat) fragment.findViewById(R.id.setting_sw_auto_check);
        sw_auto_check.setChecked(auto_check_update);
        sw_auto_check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                prefer_setting.edit().putBoolean("auto_check_update", isChecked).apply();
            }
        });
        TextView tv_updateTips = (TextView) fragment.findViewById(R.id.setting_tv_updateTips);
        tv_updateTips.setOnClickListener(this);
        tv_checkUpdate.append(getString(R.string.versionName));
        return fragment;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.setting_tv_select_theme_color:
                ColorsDialogFragment.launch(getActivity(), Config.SETTING_THEME_INDEX);
                break;
            case R.id.setting_tv_select_btn_color:
                ColorsDialogFragment.launch(getActivity(), Config.SETTING_COLOR_INDEX);
                break;
            case R.id.setting_tv_feedback:
                startActivity(new Intent(getContext(), FeedbackActivity.class));
                break;
            case R.id.setting_tv_feedbackDeal:
                Intent intent = new Intent(getContext(), BrowserActivity.class);
                intent.setAction("https://github.com/Jacknic/glut/blob/master/feedback.md");
                getActivity().startActivity(intent);
                break;
            case R.id.setting_tv_checkUpdate:
                UpdateUtil.checkUpdate(getActivity(), true);
                break;
            case R.id.setting_tv_updateTips:
                Intent updateTips = new Intent(getContext(), BrowserActivity.class);
                updateTips.setAction("https://github.com/Jacknic/glut/blob/master/update.md");
                getActivity().startActivity(updateTips);
                break;

        }
    }
}
