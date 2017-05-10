package com.jacknic.glut.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

import com.jacknic.glut.R;

import java.util.List;

/**
 * 学业进度适配器
 */
public class StudyProcessAdapter extends BaseAdapter {
    private List<String> dataList;

    public StudyProcessAdapter(List<String> dataList) {
        this.dataList = dataList;
    }

    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public Object getItem(int position) {
        return dataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_study_process, parent, false);
            GridView.LayoutParams params = new GridView.LayoutParams(GridView.LayoutParams.MATCH_PARENT, GridView.LayoutParams.MATCH_PARENT);
            convertView.setLayoutParams(params);
        }
        TextView tv = (TextView) convertView.findViewById(R.id.item_text);
        String text = dataList.get(position);
        if ("通过".equals(text)) tv.setTextColor(parent.getResources().getColor(R.color.green));
        tv.setText(text);
        return convertView;
    }
}
