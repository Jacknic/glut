package com.jacknic.glut.activity.educational;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.GridView;
import android.widget.TextView;

import com.jacknic.glut.R;
import com.jacknic.glut.activity.BaseActivity;
import com.jacknic.glut.adapter.StudyProcessAdapter;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * 学业进度
 */

public class ProcessActivity extends BaseActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_process);
        setStatusView();
        TextView title = (TextView) findViewById(R.id.tv_toolbar_title);
        title.setText("学业进度");
        GridView gv_grade_list = (GridView) findViewById(R.id.gv_grade_list);
        ArrayList<String> dataList = new ArrayList<>();
        File study_process = new File(getFilesDir(), "study_process");
        if (study_process.exists()) {
            try {
                Document document = Jsoup.parse(study_process, "UTF-8");
                Elements trs = document.getElementsByTag("tr");
                for (Element tr : trs) {
                    tr.child(0).remove();
                    for (Element element : tr.children()) {
                        dataList.add(element.text());
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
//        for (int i = 1; i < 35; i++) {
//            dataList.add("实践教学环节");
//        }
        gv_grade_list.setAdapter(new StudyProcessAdapter(dataList));
    }
}

