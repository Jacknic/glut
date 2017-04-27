package com.jacknic.glut.fragments.setting;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.jacknic.glut.R;
import com.jacknic.glut.utils.Config;


/**
 * 基本设置
 */
public class BasicSettingsFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View fragment = inflater.inflate(R.layout.fragment_setting_basic, container, false);
        TextView tv_select_theme_color = (TextView) fragment.findViewById(R.id.setting_tv_select_theme_color);
        ImageView iv_select_btn_color = (ImageView) fragment.findViewById(R.id.setting_iv_select_btn_color);
        TextView tv_select_btn_color = (TextView) fragment.findViewById(R.id.setting_tv_select_btn_color);
        SharedPreferences prefer_setting = getContext().getSharedPreferences(Config.PREFER_SETTING, Context.MODE_PRIVATE);
        int index = prefer_setting.getInt(Config.SETTING_COLOR_INDEX, 4);
        iv_select_btn_color.setImageResource(Config.colors[index]);
        tv_select_theme_color.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ColorsDialogFragment.launch(getActivity(), Config.SETTING_THEME_INDEX);
            }
        });
        tv_select_btn_color.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ColorsDialogFragment.launch(getActivity(), Config.SETTING_COLOR_INDEX);
            }
        });
        return fragment;
    }

}
