package com.jacknic.glut.page;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.jacknic.glut.R;
import com.jacknic.glut.adapter.GradeListAdapter;
import com.jacknic.glut.util.Func;
import com.jacknic.glut.util.SnackbarTool;
import com.jacknic.glut.view.widget.Dialogs;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.AbsCallbackWrapper;
import com.lzy.okgo.callback.StringCallback;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Calendar;

import butterknife.BindView;
import okhttp3.Call;
import okhttp3.Response;

/**
 * 成绩查询
 */

public class GradeListPage extends BasePage {

    private Elements grade_list;
    @BindView(R.id.sp_select_year)
    Spinner sp_year;
    @BindView(R.id.sp_select_semester)
    Spinner sp_semester;
    @BindView(R.id.rv_grade_list)
    RecyclerView rv_grade_list;
    private ArrayList<String> years;
    private GradeListAdapter gradeListAdapter;
    private AdapterView.OnItemSelectedListener onItemSelectedListener;

    @Override
    protected int getLayoutId() {
        mTitle = "成绩查询";
        return R.layout.page_grade_list;
    }

    @Override
    void initPage() {
        Calendar calendar = Calendar.getInstance();
        boolean springSession = calendar.get(Calendar.MONTH) < Calendar.JULY;
        int year = calendar.get(Calendar.YEAR);
        rv_grade_list.setLayoutManager(new LinearLayoutManager(getContext()));
        ArrayList<String> semesters = new ArrayList<>();
        semesters.add("全部");
        semesters.add("春季");
        semesters.add("秋季");
        years = new ArrayList<>();
        years.add("全部");
        for (int i = year; i > year - 7; i--) {
            years.add("" + i);
        }
        ArrayAdapter<String> adapter_years = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_dropdown_item, years);
        sp_year.setAdapter(adapter_years);
        sp_year.setSelection(springSession ? 2 : 1);
        ArrayAdapter<String> adapter_semesters = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_dropdown_item, semesters);
        sp_semester.setAdapter(adapter_semesters);
        sp_semester.setSelection(springSession ? 2 : 1);
        gradeListAdapter = new GradeListAdapter(getContext());
        rv_grade_list.setAdapter(gradeListAdapter);
        showGrade();
    }

    /**
     * 获取成绩
     */
    private void getGrade() {
        SnackbarTool.showIndefinite("数据获取中...");
        StringCallback callback = new StringCallback() {
            @Override
            public void onSuccess(String s, Call call, Response response) {
                //如果成功获取则就无需再次登录
                getAllGrade();
            }

            @Override
            public void onError(Call call, Response response, Exception e) {
//                        toLogin(GradeListPage.this);
                if (response == null && e instanceof SocketTimeoutException) {
                    SnackbarTool.showShort("连接服务器失败");
                } else {
                    login();
                }
            }

            @Override
            public void onAfter(String s, Exception e) {
                if (onItemSelectedListener == null) {
                    setListener();
                }
            }
        };
        Func.checkLoginStatus(callback);
    }

    private void setListener() {
        onItemSelectedListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
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
     * 用户登录
     */
    private void login() {
        Dialogs.showLoginJw(getContext(), new AbsCallbackWrapper() {
            @Override
            public void onAfter(Object o, Exception e) {
                showGrade();
            }
        });
        SnackbarTool.dismiss();
    }

    //获取所有成绩
    private void getAllGrade() {
        OkGo.get("http://jw.glut.edu.cn/academic/manager/score/studentOwnScore.do?year=&term=&para=0&sortColumn=&Submit=%E6%9F%A5%E8%AF%A2")
                .tag(this)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        Document document = Jsoup.parse(s);
                        grade_list = document.select("table.datalist tr");
                        showGrade();
                    }
                });
    }

    /**
     * 显示成绩
     */
    private void showGrade() {
        if (getContext() == null) return;
        if (grade_list == null) {
            getGrade();
        } else {
            //过滤数据
            ArrayList<Element> select_list = new ArrayList<>();
            int year_position = sp_year.getSelectedItemPosition();
            if (year_position != 0) {
                String str_year = years.get(year_position);
//                Toast.makeText(GradeListPage.this, "选择学年" + str_year, Toast.LENGTH_SHORT).show();
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
//            Toast.makeText(GradeListPage.this, "选择学期" + sp_semester.getSelectedItemPosition(), Toast.LENGTH_SHORT).show();
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
                SnackbarTool.showShort("该学期成绩列表为空...");
            } else {
                SnackbarTool.showShort(select_list.size() + "条成绩信息");
            }
            select_list.add(0, grade_list.first());
            gradeListAdapter.setElements(select_list);
            gradeListAdapter.notifyDataSetChanged();
        }
    }
}
