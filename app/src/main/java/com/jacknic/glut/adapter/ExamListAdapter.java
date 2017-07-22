package com.jacknic.glut.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jacknic.glut.R;
import com.jacknic.glut.model.bean.ExamInfoBean;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ExamListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private LayoutInflater inflater;
    private List<ExamInfoBean> examInfoBeanList = new ArrayList<>();
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA);
    private Date date_now = new Date();

    public ExamListAdapter(Context context, List<ExamInfoBean> examInfoBeanList) {
        inflater = LayoutInflater.from(context);
        this.examInfoBeanList = examInfoBeanList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(inflater.inflate(R.layout.item_exam, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ViewHolder itemHolder = (ViewHolder) holder;
        itemHolder.bindHolder(examInfoBeanList.get(position));
    }

    @Override
    public int getItemCount() {
        return examInfoBeanList.size();
    }


    private class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_name, tv_time, tv_location;

        ViewHolder(View itemView) {
            super(itemView);
            setIsRecyclable(false);
            tv_name = (TextView) itemView.findViewById(R.id.tv_exam_name);
            tv_time = (TextView) itemView.findViewById(R.id.tv_exam_time);
            tv_location = (TextView) itemView.findViewById(R.id.tv_exam_location);
        }

        void bindHolder(ExamInfoBean examInfoBean) {
            tv_name.setText(examInfoBean.getName());
            tv_time.setText(examInfoBean.getTime());
            tv_location.setText(examInfoBean.getLocation());
            try {
                Date date_exam = dateFormat.parse(examInfoBean.getTime());
                if (date_exam.getTime() <= date_now.getTime()) {
                    //System.out.println("当前时间：" + date_now + "考试时间：" + date_exam);
                    int cary = Color.rgb(117, 117, 117);
                    tv_name.setTextColor(cary);
                    tv_time.setTextColor(cary);
                    tv_location.setTextColor(cary);
                }
            } catch (ParseException e) {
                Log.d("date fail", "bindHolder: 日期转换失败" + e.getLocalizedMessage());
            }
        }
    }
}
