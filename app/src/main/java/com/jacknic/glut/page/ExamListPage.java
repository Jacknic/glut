package com.jacknic.glut.page;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jacknic.glut.R;
import com.jacknic.glut.adapter.ExamListAdapter;
import com.jacknic.glut.model.bean.ExamInfoBean;
import com.jacknic.glut.stacklibrary.RootFragment;
import com.jacknic.glut.util.SnackbarTool;
import com.jacknic.glut.util.ViewUtil;
import com.jacknic.glut.view.widget.Dialogs;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.AbsCallbackWrapper;
import com.lzy.okgo.callback.StringCallback;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Response;

/**
 * 考试列表查询
 */

public class ExamListPage extends RootFragment {

    private ArrayList<ExamInfoBean> examInfoBeenList = new ArrayList<>();
    private ExamListAdapter examListAdapter;
    private boolean isGot = false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View page = inflater.inflate(R.layout.page_exam_list, container, false);
        ViewUtil.setTitle(getRoot(), "考试安排");
        RecyclerView rv_exam_list = (RecyclerView) page.findViewById(R.id.jw_rv_exam_list);
        rv_exam_list.setLayoutManager(new LinearLayoutManager(getContext()));
        examListAdapter = new ExamListAdapter(getContext(), examInfoBeenList);
        rv_exam_list.setAdapter(examListAdapter);
        getDataPre();
        return page;
    }


    /**
     * 获取数据
     */
    private void getData() {
        OkGo.get("http://202.193.80.58:81/academic/manager/examstu/studentQueryAllExam.do?pagingNumberPerVLID=1000").execute(new StringCallback() {
            @Override
            public void onSuccess(String s, Call call, Response response) {
                Document document = Jsoup.parse(s);
                Elements elements = document.select("table.datalist tr");
                elements.remove(0);
                for (Element element : elements) {
                    ExamInfoBean bean = new ExamInfoBean(element.child(1).text(), element.child(2).text(), element.child(3).text());
                    examInfoBeenList.add(bean);
                }
                isGot = true;
                getDataPre();
            }
        });
    }

    /**
     * 获取数据前操作
     */
    private void getDataPre() {
        if (!isGot) {
            SnackbarTool.showShort("数据获取中...");
            OkGo.get("http://202.193.80.58:81/academic/student/currcourse/currcourse.jsdo").execute(
                    new StringCallback() {
                        @Override
                        public void onSuccess(String s, Call call, Response response) {
                            //如果成功获取则就无需再次登录
                            getData();
                        }

                        @Override
                        public void onError(Call call, Response response, Exception e) {
                            login();
                        }
                    }
            );

        } else {
            examListAdapter.notifyDataSetChanged();
        }
    }

    /**
     * 登录验证
     */
    private void login() {
        final AlertDialog login_dialog = Dialogs.getLoginJw((Activity) getContext(), new AbsCallbackWrapper() {
            @Override
            public void onAfter(Object o, Exception e) {
                getDataPre();
            }
        });
        login_dialog.show();
    }
}
