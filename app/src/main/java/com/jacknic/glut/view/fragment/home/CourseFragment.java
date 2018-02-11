package com.jacknic.glut.view.fragment.home;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.annotation.Keep;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jacknic.glut.R;
import com.jacknic.glut.adapter.WeekNameAdapter;
import com.jacknic.glut.event.UpdateCourseEvent;
import com.jacknic.glut.model.dao.CourseDao;
import com.jacknic.glut.model.entity.CourseEntity;
import com.jacknic.glut.page.ChangeTermPage;
import com.jacknic.glut.util.Config;
import com.jacknic.glut.util.Func;
import com.jacknic.glut.util.PageTool;
import com.jacknic.glut.util.PreferManager;
import com.jacknic.glut.view.widget.CourseTableView;
import com.jacknic.glut.view.widget.Dialogs;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * 课表
 */
public class CourseFragment extends Fragment {

    View fragment;
    @BindView(R.id.table_view)
    CourseTableView timeTableView;
    @BindView(R.id.tab_select_week)
    TabLayout tab_select_week;
    @BindView(R.id.tv_week)
    TextView tv_week;
    @BindView(R.id.iv_toggle)
    ImageView iv_toggle;
    @BindView(R.id.ll_select_time)
    LinearLayout ll_select_time;
    @BindView(R.id.tv_school_year)
    TextView tv_school_year;
    @BindView(R.id.tv_semester)
    TextView tv_semester;
    @BindView(R.id.gv_weekName)
    GridView gv_weekName;
    @BindView(R.id.tv_month)
    TextView tv_month;
    private int week_now;
    private SharedPreferences prefer;

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        fragment = inflater.inflate(R.layout.frag_course, container, false);
        prefer = PreferManager.getPrefer();
        ButterKnife.bind(this, fragment);
        EventBus.getDefault().register(this);
        setTab();
        showSelect();
        return fragment;
    }

    @OnClick(R.id.tv_week)
    void showDialog() {
        AlertDialog.Builder builder = Dialogs.getChangeWeekDialog(getActivity());
        builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                refresh();
            }
        });
        builder.show();
    }

    /**
     * 打开切换学期页
     */
    @OnClick({R.id.tv_school_year, R.id.tv_semester})
    void openChangeTerm() {
        PageTool.open(getContext(), new ChangeTermPage());
    }

    /**
     * 显示当前课程周数
     */
    private void showSelect() {
        week_now = Func.getWeekNow();
        Log.d("kc", "实际周数: " + week_now);
        selectWeek(week_now);
        int semester = prefer.getInt(Config.JW_SEMESTER, 1);
        int school_year = prefer.getInt(Config.JW_SCHOOL_YEAR, Calendar.getInstance().get(Calendar.YEAR));
        tv_week.setText("第" + week_now + "周 ");
        tv_school_year.setText(school_year + "年");
        tv_semester.setText((semester == 1 ? "春" : "秋") + "季学期");
    }


    final Animation.AnimationListener toggleListener = new Animation.AnimationListener() {
        @Override
        public void onAnimationStart(Animation animation) {
        }

        @Override
        public void onAnimationEnd(Animation animation) {
            ll_select_time.setVisibility(ll_select_time.getVisibility() != View.VISIBLE ? View.VISIBLE : View.GONE);
        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    };

    /**
     * tab栏开关
     */
    @OnClick(R.id.iv_toggle)
    void toggle(View v) {
        //当前是否可见，可见执行关闭操作，不可见执行展开
        boolean visible = ll_select_time.getVisibility() == View.VISIBLE;
        if (visible) {
            int tabPosition = tab_select_week.getSelectedTabPosition();
            if (tabPosition + 1 != week_now) {
                showSelect();
            }
        } else {
            ll_select_time.setVisibility(View.INVISIBLE);
            TabLayout.Tab tab = tab_select_week.getTabAt(week_now - 1);
            if (tab != null) {
                tab.select();
            }
        }
        Animation animToggle = AnimationUtils.loadAnimation(getContext(), visible ? R.anim.toggle_out : R.anim.toggle_in);
        v.setRotation(visible ? 0 : 45);
        animToggle.setAnimationListener(toggleListener);
        ll_select_time.startAnimation(animToggle);
        RotateAnimation rota = new RotateAnimation(visible ? -135 : 135, 0, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rota.setFillAfter(true);
        rota.setDuration(300L);
        v.startAnimation(rota);
    }

    /**
     * 设置tab滚动条
     */
    private void setTab() {
        int end_week = prefer.getInt(Config.JW_WEEK_END, 30);
        for (int i = 1; i <= end_week; i++) {
            String text = "第" + i + "周";
            SpannableString spanned = new SpannableString(text);
            spanned.setSpan(new RelativeSizeSpan(1.4f), 1, text.length() - 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            TextView tv_tab = new TextView(getContext());
            ColorStateList colorStateList = getContext().getResources().getColorStateList(R.color.tab_selector);
            tv_tab.setTextColor(colorStateList);
            tv_tab.setText(spanned);
            tv_tab.setGravity(Gravity.CENTER);
            TabLayout.Tab tab = tab_select_week.newTab();
            tab.setCustomView(tv_tab);
            tab_select_week.addTab(tab);
        }
        tab_select_week.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                selectWeek(position + 1);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }

    /**
     * 选择显示周课表
     *
     * @param week 周数
     */
    private void selectWeek(int week) {
        List<CourseEntity> courses = new CourseDao().getCourse(week);
        setTime(week - week_now);
        timeTableView.setTimeTable(courses);
    }

    /**
     * 时间提示设置
     *
     * @param addWeek 周数增量
     */
    private void setTime(final int addWeek) {
        final Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.WEEK_OF_YEAR, addWeek);
        System.out.println("初始时间：" + calendar.getTime().toLocaleString());
        while (calendar.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY) {
            calendar.add(Calendar.DATE, -1);
        }
        System.out.println("调整后时间：" + calendar.getTime().toLocaleString());
        tv_month.setText((calendar.get(Calendar.MONTH) + 1) + "月");
        final ArrayList<Integer> weekdays = new ArrayList<>(7);
        for (int i = 0; i < 7; i++) {
            weekdays.add(calendar.get(Calendar.DAY_OF_MONTH));
            calendar.add(Calendar.DATE, 1);
        }
        WeekNameAdapter weekNameAdapter = new WeekNameAdapter(weekdays);
        gv_weekName.setAdapter(weekNameAdapter);
    }

    /**
     * 课表变动事件刷新
     */
    @Keep
    @Subscribe
    public void updateCourse(UpdateCourseEvent event) {
        refresh();
    }

    /**
     * 刷新视图
     */
    private void refresh() {
        if (ll_select_time.getVisibility() == View.VISIBLE) iv_toggle.callOnClick();
        tab_select_week.removeAllTabs();
        Func.updateWidget(getContext());
        setTab();
        showSelect();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
    }
}
