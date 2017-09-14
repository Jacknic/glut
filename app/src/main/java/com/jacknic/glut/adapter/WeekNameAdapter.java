package com.jacknic.glut.adapter;

import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.jacknic.glut.util.Config;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * 星期适配器
 */

public class WeekNameAdapter extends BaseAdapter {
    private ArrayList<Integer> weekdays;
    private int day_of_week = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);

    public WeekNameAdapter(ArrayList<Integer> weekdays) {
        this.weekdays = weekdays;
    }

    @Override
    public int getCount() {
        return weekdays.size();
    }

    @Override
    public Object getItem(int position) {
        return "周" + Config.weekNames[position] + "\n" + weekdays.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView tv_weekName = new TextView(parent.getContext());
        String str_weekName = (String) getItem(position);
        tv_weekName.setText(str_weekName);
        //0123456->2345678->2345671
        int weekday = (position + 2) % 7;
        if (weekday == 0) weekday = 7;
        if (weekday == day_of_week) {
            //当前星期几着重显示
            tv_weekName.setBackgroundColor(0xDDDEDEDE);
        }
        tv_weekName.setGravity(Gravity.CENTER);
        return tv_weekName;
    }
}