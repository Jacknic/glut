package com.jacknic.glut.page;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.jacknic.glut.R;
import com.jacknic.glut.adapter.StudyProcessAdapter;
import com.jacknic.glut.model.EduInfoModel;
import com.jacknic.glut.stacklibrary.RootFragment;
import com.jacknic.glut.util.Func;
import com.jacknic.glut.util.SnackbarTool;
import com.jacknic.glut.util.ViewUtil;
import com.jacknic.glut.view.widget.Dialogs;
import com.lzy.okgo.callback.AbsCallbackWrapper;
import com.lzy.okgo.callback.StringCallback;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Response;

/**
 * 学业进度
 */

public class ProcessPage extends RootFragment {

    private GridView gv_grade_list;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View page = inflater.inflate(R.layout.page_process, container, false);
        ViewUtil.setTitle(getRoot(), "学业进度");
        gv_grade_list = (GridView) page.findViewById(R.id.gv_grade_list);
        showData();
        return page;
    }


    /**
     * 展示用户数据
     */
    private void showData() {
        ArrayList<String> dataList = new ArrayList<>();
        File study_process = new File(getContext().getFilesDir(), "study_process");
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

    /**
     * 刷新数据
     */
    private void refresh() {
        SnackbarTool.showShort("刷新中...");
        AbsCallbackWrapper callbackCheck = new AbsCallbackWrapper() {
            @Override
            public void onError(Call call, Response response, Exception e) {
                System.out.println("用户未登录");
                Dialogs.getLoginJw(getActivity(), new AbsCallbackWrapper() {
                    @Override
                    public void onAfter(Object o, Exception e) {
                        refresh();
                    }
                }).show();
            }

            @Override
            public void onSuccess(Object o, Call call, Response response) {
                System.out.println("用户已登录-----------------");
                getData();
            }
        };
        Func.checkLoginStatus(callbackCheck);
    }

    /**
     * 获取用户数据
     */
    private void getData() {
        System.out.println("执行获取用户数据");
        new EduInfoModel().getStudyProcess(new StringCallback() {
            @Override
            public void onSuccess(String s, Call call, Response response) {
                SnackbarTool.showShort("刷新成功");
                showData();
            }
        });
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_page_browser, menu);
    }
}

