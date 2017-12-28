package com.jacknic.glut.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jacknic.glut.R;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;

/**
 * 成绩列表适配器
 */
public class GradeListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private LayoutInflater inflater;
    private ArrayList<Element> elements = new ArrayList<>();
    private Element header = new Element("<mark></mark>");

    public GradeListAdapter(Context context) {
        inflater = LayoutInflater.from(context);
    }

    public ArrayList<Element> getElements() {
        return elements;
    }

    public void setElements(ArrayList<Element> elements) {
        header = elements.remove(0);
        this.elements = elements;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewHolder viewHolder = new ViewHolder(inflater.inflate(R.layout.item_score, parent, false));
        viewHolder.setIsRecyclable(false);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ViewHolder itemHolder = (ViewHolder) holder;
        itemHolder.bindHolder(elements.get(position));

    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return elements.size();
    }


    private class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView item_score_course_name, item_score_score;
        private LinearLayout item_score_details;

        ViewHolder(View itemView) {
            super(itemView);
            item_score_course_name = (TextView) itemView.findViewById(R.id.item_score_course_name);
            item_score_score = (TextView) itemView.findViewById(R.id.item_score_score);
            item_score_details = (LinearLayout) itemView.findViewById(R.id.item_score_details);
            itemView.setOnClickListener(this);
        }

        void bindHolder(Element element) {
            Elements allElements = element.children();
            item_score_course_name.setText(allElements.get(4).text());
            item_score_score.setText(allElements.get(7).text());
            StringBuilder stringBuilder = new StringBuilder();
            for (int i = 0; i < header.children().size(); i++) {
                stringBuilder.append(header.child(i).text())
                        .append("：")
                        .append(allElements.get(i).text())
                        .append("\n");
            }
            TextView textView = new TextView(item_score_details.getContext());
            textView.setText(stringBuilder.toString());
            item_score_details.removeAllViews();
            item_score_details.addView(textView);
        }

        @Override
        public void onClick(View v) {
            if (item_score_details.getVisibility() == View.GONE) {
                item_score_details.setVisibility(View.VISIBLE);
            } else {
                item_score_details.setVisibility(View.GONE);
            }
        }
    }
}
