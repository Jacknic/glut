package com.jacknic.glut.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jacknic.glut.R;
import com.jacknic.glut.beans.educational.ExamInfoBean;

import java.util.ArrayList;
import java.util.List;

public class ExamListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private LayoutInflater inflater;
    private List<ExamInfoBean> examInfoBeanList = new ArrayList<>();

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
            tv_name = (TextView) itemView.findViewById(R.id.tv_exam_name);
            tv_time = (TextView) itemView.findViewById(R.id.tv_exam_time);
            tv_location = (TextView) itemView.findViewById(R.id.tv_exam_location);
        }

        void bindHolder(ExamInfoBean examInfoBean) {
            tv_name.setText(examInfoBean.getName());
            tv_time.setText(examInfoBean.getTime());
            tv_location.setText(examInfoBean.getLocation());
        }
    }
}
