package com.jacknic.glut.view.fragment.setting;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.jacknic.glut.MainActivity;
import com.jacknic.glut.R;
import com.jacknic.glut.adapter.ColorsSelectorAdapter;
import com.jacknic.glut.event.ThemeChangeEvent;
import com.jacknic.glut.util.Config;
import com.lzy.okgo.OkGo;

import org.greenrobot.eventbus.EventBus;

import static android.content.Context.MODE_PRIVATE;

/**
 * 设置颜色页弹窗
 */

public class ColorsDialogFragment extends DialogFragment implements AdapterView.OnItemClickListener {

    private final int oldIndex;
    private final SharedPreferences prefer;

    public static void launch(Activity context) {
        ColorsDialogFragment dialogFragment = (ColorsDialogFragment) context.getFragmentManager().findFragmentByTag(ColorsDialogFragment.class.getName());
        if (dialogFragment == null) {
            dialogFragment = new ColorsDialogFragment();
        }
        dialogFragment.show(context.getFragmentManager(), ColorsDialogFragment.class.getName());
    }

    public ColorsDialogFragment() {
        prefer = OkGo.getContext().getSharedPreferences(Config.PREFER, MODE_PRIVATE);
        oldIndex = prefer.getInt(Config.SETTING_THEME_INDEX, Config.SETTING_THEME_COLOR_INDEX);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        setCancelable(true);
        View view = View.inflate(getActivity(), R.layout.frag_color_dialog, null);
        GridView gridView = (GridView) view.findViewById(R.id.grid_colors);
        gridView.setAdapter(new ColorsSelectorAdapter(this, oldIndex));
        gridView.setOnItemClickListener(this);
        return new AlertDialog.Builder(getActivity())
                .setView(view)
                .setNegativeButton("关闭", null)
                .create();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (oldIndex != position) {
            prefer.edit().putInt(Config.SETTING_THEME_INDEX, position).apply();
            MainActivity mainActivity = (MainActivity) getActivity();
            mainActivity.selectTheme();
            ActionBar actionBar = mainActivity.getSupportActionBar();
            if (actionBar != null) {
                actionBar.setBackgroundDrawable(new ColorDrawable(getActivity().getResources().getColor(Config.COLORS[position])));
            }
            EventBus.getDefault().post(new ThemeChangeEvent());
        }
        dismiss();
    }
}