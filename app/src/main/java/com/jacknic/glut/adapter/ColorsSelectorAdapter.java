package com.jacknic.glut.adapter;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.jacknic.glut.util.Config;
import com.jacknic.glut.util.ViewUtil;
import com.jacknic.glut.view.fragment.setting.ColorsDialogFragment;

import java.util.HashMap;
import java.util.Map;

/**
 * 颜色选择适配器
 */
public class ColorsSelectorAdapter extends BaseAdapter {

    private ColorsDialogFragment colorsDialogFragment;
    private Map<String, ColorDrawable> colorMap = new HashMap<String, ColorDrawable>();
    private int selectIndex;

    public ColorsSelectorAdapter(ColorsDialogFragment colorsDialogFragment, int selectIndex) {
        this.colorsDialogFragment = colorsDialogFragment;
        this.selectIndex = selectIndex;
    }

    @Override
    public int getCount() {
        return Config.COLORS.length;
    }

    @Override
    public Object getItem(int position) {
        return Config.COLORS[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            TextView textView = new TextView(parent.getContext());
            textView.setHeight(ViewUtil.dip2px(50));
            textView.setBackgroundColor(colorsDialogFragment.getResources().getColor(Config.COLORS[position]));
            textView.setGravity(Gravity.CENTER);
            textView.setTextColor(Color.WHITE);
            textView.setTextSize(ViewUtil.dip2px(10));
            convertView = textView;
        }
        if (position == selectIndex) {
            ((TextView) convertView).setText("✪");
        }
        if (!colorMap.containsKey(String.valueOf(position)))
            colorMap.put(String.valueOf(position), new ColorDrawable(colorsDialogFragment.getResources().getColor(Config.COLORS[position])));

        return convertView;
    }

}
