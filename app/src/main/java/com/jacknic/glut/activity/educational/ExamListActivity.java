package com.jacknic.glut.activity.educational;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.jacknic.glut.R;
import com.jacknic.glut.activity.BaseActivity;
import com.jacknic.glut.adapter.ExamListAdapter;
import com.jacknic.glut.model.bean.ExamInfoBean;
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

public class ExamListActivity extends BaseActivity {

    private RecyclerView rv_exam_list;
    private ArrayList<ExamInfoBean> examInfoBeenList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exam_list);
        TextView title = (TextView) findViewById(R.id.tv_toolbar_title);
        title.setText("考试安排");
        ViewUtil.showBackIcon(this);
        ViewUtil.showRefreshView(this, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDataPre();
            }
        });
        rv_exam_list = (RecyclerView) findViewById(R.id.jw_rv_exam_list);
        rv_exam_list.setLayoutManager(new LinearLayoutManager(this));
        getDataPre();
    }


    /**
     * 获取数据
     */
    void getData() {
        OkGo.get("http://202.193.80.58:81/academic/manager/examstu/studentQueryAllExam.do?pagingNumberPerVLID=1000").execute(new StringCallback() {
            @Override
            public void onSuccess(String s, Call call, Response response) {
                Document document = Jsoup.parse(s);
                Elements elements = document.select("table.datalist tr");
                elements.remove(0);
                examInfoBeenList = new ArrayList<ExamInfoBean>();
                for (Element element : elements) {
                    ExamInfoBean bean = new ExamInfoBean(element.child(1).text(), element.child(2).text(), element.child(3).text());
                    examInfoBeenList.add(bean);
                }
                getDataPre();
            }
        });
    }

    /**
     * 获取数据前操作
     */
    private void getDataPre() {
        if (examInfoBeenList == null) {
            Snackbar.make(rv_exam_list, "数据获取中...", Snackbar.LENGTH_LONG).show();
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
            ExamListAdapter adapter = new ExamListAdapter(this, examInfoBeenList);
            rv_exam_list.setAdapter(adapter);
        }
    }

    /**
     * 登录验证
     */
    private void login() {
        final AlertDialog login_dialog = Dialogs.getLoginJw(this, new AbsCallbackWrapper() {
            @Override
            public void onAfter(Object o, Exception e) {
                getDataPre();
            }
        });
        login_dialog.show();
    }
}
