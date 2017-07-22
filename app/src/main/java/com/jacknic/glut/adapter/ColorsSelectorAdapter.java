package com.jacknic.glut.adapter;

import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.jacknic.glut.util.Config;
import com.jacknic.glut.view.fragment.setting.ColorsDialogFragment;

import java.util.HashMap;
import java.util.Map;

/**
 * 颜色选择适配器
 */
public class ColorsSelectorAdapter extends BaseAdapter {

    private ColorsDialogFragment colorsDialogFragment;
    private Map<String, ColorDrawable> colorMap = new HashMap<String, ColorDrawable>();

    public ColorsSelectorAdapter(ColorsDialogFragment colorsDialogFragment) {
        this.colorsDialogFragment = colorsDialogFragment;
    }

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
            textView.setBackgroundColor(colorsDialogFragment.getResources().getColor(Config.colors[position]));
            convertView = textView;
        }
        if (!colorMap.containsKey(String.valueOf(position)))
            colorMap.put(String.valueOf(position), new ColorDrawable(colorsDialogFragment.getResources().getColor(Config.colors[position])));

        return convertView;
    }

}
