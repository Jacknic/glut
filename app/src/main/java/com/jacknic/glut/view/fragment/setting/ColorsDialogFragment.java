package com.jacknic.glut.view.fragment.setting;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.jacknic.glut.MainActivity;
import com.jacknic.glut.R;
import com.jacknic.glut.adapter.ColorsSelectorAdapter;
import com.jacknic.glut.page.HomePage;
import com.jacknic.glut.page.SettingPage;
import com.jacknic.glut.util.Config;

import java.util.Stack;

import static android.content.Context.MODE_PRIVATE;

/**
 * 设置颜色页弹窗
 */

public class ColorsDialogFragment extends DialogFragment implements AdapterView.OnItemClickListener {

    public static void launch(Activity context) {
        ColorsDialogFragment dialogFragment = (ColorsDialogFragment) context.getFragmentManager().findFragmentByTag(ColorsDialogFragment.class.getName());
        if (dialogFragment == null) {
            dialogFragment = new ColorsDialogFragment();
        }
        dialogFragment.show(context.getFragmentManager(), ColorsDialogFragment.class.getName());
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
        SharedPreferences prefer = getActivity().getSharedPreferences(Config.PREFER, MODE_PRIVATE);
        int oldIndex = prefer.getInt(Config.SETTING_THEME_INDEX, Config.SETTING_THEME_COLOR_INDEX);
        if (oldIndex != position) {
            prefer.edit().putInt(Config.SETTING_THEME_INDEX, position).putBoolean(Config.IS_REFRESH, true).apply();
            MainActivity mainActivity = (MainActivity) getActivity();

            mainActivity.selectTheme();
            FragmentManager fragmentManager = mainActivity.getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            HomePage homePage = new HomePage();
            SettingPage settingPage = new SettingPage();
            transaction.add(R.id.frame_container, homePage, homePage.getClass().getName());
            transaction.add(R.id.frame_container, settingPage, settingPage.getClass().getName());
            transaction.hide(homePage);
            transaction.show(settingPage);
            Stack<android.support.v4.app.Fragment> pages = mainActivity.manager.getPages();
            while (!pages.empty()) {
                android.support.v4.app.Fragment fragment = pages.pop();
                transaction.remove(fragment);
            }
            transaction.commit();
            ActionBar actionBar = mainActivity.getSupportActionBar();
            if (actionBar != null) {
                actionBar.setBackgroundDrawable(new ColorDrawable(getActivity().getResources().getColor(Config.COLORS[position])));
            }
            pages.add(homePage);
            pages.add(settingPage);
        }
        dismiss();
    }
}