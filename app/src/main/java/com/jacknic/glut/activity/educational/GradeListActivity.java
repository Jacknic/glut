package com.jacknic.glut.activity.educational;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.jacknic.glut.R;
import com.jacknic.glut.activity.BaseActivity;
import com.jacknic.glut.adapter.GradeListAdapter;
import com.jacknic.glut.util.Func;
import com.jacknic.glut.util.ViewUtil;
import com.jacknic.glut.view.widget.Dialogs;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.AbsCallbackWrapper;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.request.BaseRequest;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Calendar;

import okhttp3.Call;
import okhttp3.Response;

/**
 * 成绩查询
 */

public class GradeListActivity extends BaseActivity {

    private Elements grade_list;
    private RecyclerView rlv_grade_list;
    private Spinner sp_year;
    private Spinner sp_semester;
    private ArrayList<String> years;
    private Snackbar snackbar;
    private AlertDialog loginDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grade_list);
        ViewUtil.showBackIcon(this);
        TextView title = (TextView) findViewById(R.id.tv_toolbar_title);
        title.setText("成绩查询");
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        sp_year = (Spinner) findViewById(R.id.sp_select_year);
        sp_semester = (Spinner) findViewById(R.id.sp_select_semester);
        rlv_grade_list = (RecyclerView) findViewById(R.id.rlv_grade_list);
        rlv_grade_list.setLayoutManager(new LinearLayoutManager(this));
        ArrayList<String> semesters = new ArrayList<>();
        semesters.add("全部");
        semesters.add("春季");
        semesters.add("秋季");
        years = new ArrayList<>();
        years.add("全部");
        for (int i = year; i > year - 10; i--) {
            years.add("" + i);
        }
        ArrayAdapter<String> adapter_years = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, years);
        sp_year.setAdapter(adapter_years);
        sp_year.setSelection(1);
        ArrayAdapter<String> adapter_semesters = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, semesters);
        sp_semester.setAdapter(adapter_semesters);
        sp_semester.setSelection(calendar.get(Calendar.MONTH) > Calendar.SEPTEMBER ? 2 : 1);
        snackbar = Snackbar.make(rlv_grade_list, "数据获取中...", Snackbar.LENGTH_INDEFINITE);
        showGrade();
    }

    @Override
    protected void onStart() {
        super.onStart();
        AdapterView.OnItemSelectedListener onItemSelectedListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                System.out.println("触发选择事件");
                showGrade();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        };
        sp_semester.setOnItemSelectedListener(onItemSelectedListener);
        sp_year.setOnItemSelectedListener(onItemSelectedListener);
    }

    /**
     * 获取成绩
     */
    private void getGrade() {
        snackbar.show();
        StringCallback callback = new StringCallback() {
            @Override
            public void onSuccess(String s, Call call, Response response) {
                //如果成功获取则就无需再次登录
                getAllGrade();
            }

            @Override
            public void onError(Call call, Response response, Exception e) {
//                        toLogin(GradeListActivity.this);
                if (response == null && e instanceof SocketTimeoutException) {
                    snackbar.setText("连接服务器失败").setDuration(Snackbar.LENGTH_SHORT).show();
                } else {
                    login();
                }
            }
        };
        Func.checkLoginStatus(callback);
    }

    /**
     * 用户登录
     */
    private void login() {
        if (loginDialog == null) {
            loginDialog = Dialogs.getLoginJw(this, new AbsCallbackWrapper() {
                @Override
                public void onAfter(Object o, Exception e) {
                    showGrade();
                }
            });
        }
        if (loginDialog.isShowing() || isFinishing()) return;
        else {
            loginDialog.show();
            snackbar.dismiss();
        }
    }

    //获取所有成绩
    private void getAllGrade() {
        if (grade_list == null) {

            OkGo.get("http://202.193.80.58:81/academic/manager/score/studentOwnScore.do?year=&term=&para=0&sortColumn=&Submit=%E6%9F%A5%E8%AF%A2").execute(new StringCallback() {
                @Override
                public void onBefore(BaseRequest request) {
                    snackbar.show();
                }

                @Override
                public void onSuccess(String s, Call call, Response response) {
                    Document document = Jsoup.parse(s);
                    grade_list = document.select("table.datalist tr");
                    showGrade();
                }

                @Override
                public void onAfter(String s, Exception e) {
                    snackbar.setDuration(Snackbar.LENGTH_SHORT);
                    snackbar.show();
                }
            });
        }
    }

    /**
     * 显示成绩
     */
    private void showGrade() {
        if (grade_list == null) {
            getGrade();
        } else {
            //过滤数据
            ArrayList<Element> select_list = new ArrayList<>();
            int year_position = sp_year.getSelectedItemPosition();
            if (year_position != 0) {
                String str_year = years.get(year_position);
//                Toast.makeText(GradeListActivity.this, "选择学年" + str_year, Toast.LENGTH_SHORT).show();
                for (int i = 0; i < grade_list.size(); i++) {
                    Element element = grade_list.get(i);
                    if (str_year.equals(element.children().get(0).text().trim())) {
                        select_list.add(element);
                    }
                }
            } else {
                select_list = grade_list.clone();
                select_list.remove(0);
            }
//            Toast.makeText(GradeListActivity.this, "选择学期" + sp_semester.getSelectedItemPosition(), Toast.LENGTH_SHORT).show();
            switch (sp_semester.getSelectedItemPosition()) {
                case 1:
                    ArrayList<Element> tpl_list = new ArrayList<>();
                    for (int i = 0; i < select_list.size(); i++) {
                        Element element = select_list.get(i);
                        if (element.children().get(1).text().contains("春")) {
                            tpl_list.add(element);
                        }
                    }
                    select_list.clear();
                    select_list = tpl_list;
                    break;
                case 2:
                    ArrayList<Element> tpl_list2 = new ArrayList<>();
                    for (int i = 0; i < select_list.size(); i++) {
                        Element element = select_list.get(i);
                        if (element.children().get(1).text().contains("秋")) {
                            tpl_list2.add(element);
                        }
                    }
                    select_list.clear();
                    select_list = tpl_list2;
                    break;

            }
            if (select_list.isEmpty()) {
                snackbar.setText("该学期成绩列表为空...");
            } else {
                snackbar.setText(select_list.size() + "条成绩信息");
            }
            snackbar.show();
            select_list.add(0, grade_list.first());
            rlv_grade_list.setAdapter(new GradeListAdapter(GradeListActivity.this, select_list));
        }
    }
}
