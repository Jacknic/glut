package com.jacknic.glut.view.fragment.setting;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.jacknic.glut.R;
import com.jacknic.glut.util.Config;

import java.util.HashMap;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

/**
 * 设置颜色页弹窗
 */

public class ColorsDialogFragment extends DialogFragment implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {

    private String which = Config.SETTING_THEME_INDEX;

    public String getWhich() {
        return which;
    }

    public void setWhich(String which) {
        this.which = which;
    }

    public static void launch(Activity context, String which) {
        Fragment fragment = context.getFragmentManager().findFragmentByTag("ColorsDialogFragment");
        if (fragment != null) {
            context.getFragmentManager().beginTransaction().remove(fragment).commit();
        }

        ColorsDialogFragment dialogFragment = new ColorsDialogFragment();
        dialogFragment.setWhich(which);
        dialogFragment.show(context.getFragmentManager(), "ColorsDialogFragment");
    }

    private Map<String, ColorDrawable> colorMap = new HashMap<String, ColorDrawable>();

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        setCancelable(true);

        View view = View.inflate(getActivity(), R.layout.fragment_color_dialog, null);

        GridView gridView = (GridView) view.findViewById(R.id.grid_colors);
        gridView.setAdapter(new ColorsAdapter());
        gridView.setOnItemClickListener(this);
        gridView.setOnItemLongClickListener(this);

        return new AlertDialog.Builder(getActivity())
                .setView(view)
                .setNegativeButton(android.R.string.cancel, null)
                .create();
    }

    private class ColorsAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return Config.colors.length;
        }

        @Override
        public Object getItem(int position) {
            return Config.colors[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                TextView textView = new TextView(parent.getContext());
                textView.setHeight(100);
                textView.setBackgroundColor(getResources().getColor(Config.colors[position]));
                convertView = textView;
            }
            if (!colorMap.containsKey(String.valueOf(position)))
                colorMap.put(String.valueOf(position), new ColorDrawable(getResources().getColor(Config.colors[position])));

            return convertView;
        }

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        SharedPreferences setting = getActivity().getSharedPreferences(Config.PREFER_SETTING, MODE_PRIVATE);
        setting.edit().putInt(which, position).putBoolean(Config.IS_REFRESH, true).apply();
        ImageView iv_select_theme_color = (ImageView) getActivity().findViewById(R.id.setting_iv_select_theme_color);
        ImageView iv_select_btn_color = (ImageView) getActivity().findViewById(R.id.setting_iv_select_btn_color);
        int theme_index = setting.getInt(Config.SETTING_THEME_INDEX, Config.SETTING_THEME_COLOR_INDEX);
        iv_select_theme_color.setImageResource(Config.colors[theme_index]);
        int btn_color_index = setting.getInt(Config.SETTING_COLOR_INDEX, Config.SETTING_THEME_COLOR_INDEX);
        iv_select_btn_color.setImageResource(Config.colors[btn_color_index]);
        dismiss();
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        return true;
    }
}