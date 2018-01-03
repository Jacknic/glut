package com.jacknic.glut.view.fragment.setting;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;

import com.jacknic.glut.MainActivity;
import com.jacknic.glut.R;
import com.jacknic.glut.adapter.ColorsSelectorAdapter;
import com.jacknic.glut.page.HomePage;
import com.jacknic.glut.page.SettingPage;
import com.jacknic.glut.stacklibrary.StackManager;
import com.jacknic.glut.util.Config;

import static android.content.Context.MODE_PRIVATE;

/**
 * 设置颜色页弹窗
 */

public class ColorsDialogFragment extends DialogFragment implements AdapterView.OnItemClickListener {

    public static void launch(Activity context) {
        Fragment fragment = context.getFragmentManager().findFragmentByTag("ColorsDialogFragment");
        if (fragment != null) {
            context.getFragmentManager().beginTransaction().remove(fragment).commit();
        }
        ColorsDialogFragment dialogFragment = new ColorsDialogFragment();
        dialogFragment.show(context.getFragmentManager(), "ColorsDialogFragment");
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        setCancelable(true);
        View view = View.inflate(getActivity(), R.layout.frag_color_dialog, null);
        GridView gridView = (GridView) view.findViewById(R.id.grid_colors);
        gridView.setAdapter(new ColorsSelectorAdapter(this));
        gridView.setOnItemClickListener(this);
        return new AlertDialog.Builder(getActivity())
                .setView(view)
                .setNegativeButton(android.R.string.cancel, null)
                .create();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        SharedPreferences setting = getActivity().getSharedPreferences(Config.PREFER_SETTING, MODE_PRIVATE);
        setting.edit().putInt(Config.SETTING_THEME_INDEX, position).putBoolean(Config.IS_REFRESH, true).apply();
        getActivity().setTheme(Config.THEME_LIST[position]);
        ImageView iv_select_theme_color = (ImageView) getActivity().findViewById(R.id.setting_iv_select_theme_color);
        int theme_index = setting.getInt(Config.SETTING_THEME_INDEX, Config.SETTING_THEME_COLOR_INDEX);
        int color_res = Config.COLORS[theme_index];
        iv_select_theme_color.setImageResource(color_res);
        MainActivity activity = (MainActivity) getActivity();
        android.support.v7.app.ActionBar actionBar = activity.getSupportActionBar();
        if (actionBar != null) {
            actionBar.setBackgroundDrawable(getResources().getDrawable(color_res));
            StackManager stackManager = activity.manager;
            HomePage homePage = new HomePage();
            stackManager.setFragment(homePage);
            FragmentManager fragmentManager = activity.getSupportFragmentManager();
            SettingPage settingPage = new SettingPage();
            fragmentManager.beginTransaction()
                    .add(homePage, homePage.getClass().getName())
                    .add(settingPage, settingPage.getClass()
                            .getName())
                    .commit();
        }
        dismiss();
    }
}