package com.jacknic.glut.view.widget;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AlertDialog;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jacknic.glut.R;
import com.jacknic.glut.activity.educational.AddCourseActivity;
import com.jacknic.glut.model.dao.CourseInfoDao;
import com.jacknic.glut.model.entity.CourseEntity;
import com.jacknic.glut.model.entity.CourseInfoEntity;
import com.jacknic.glut.util.Config;
import com.jacknic.glut.util.Func;
import com.jacknic.glut.util.ViewUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * 课表显示View
 *
 * @author shallcheek
 */
public class CourseTableView extends LinearLayout {
    //最大节数
    public final int MAXNUM = 14;
    //显示到星期几
    public final int WEEKNUM = 7;
    //单个View高度
    private final int TimeTableHeight = 50;
    //线的高度
    private final int TimeTableLineHeight = 2;
    private final int TimeTableNumWidth = 20;
    private LinearLayout mCourseNumLayout;//课程格子
    private HashMap<String, Integer> colorMap = new HashMap<>();
    private int colorNum = getContext().getSharedPreferences(Config.PREFER_SETTING, Context.MODE_PRIVATE).getInt(Config.SETTING_COLOR_INDEX, 1) + 1;
    //数据源
    private List<CourseEntity> mListTimeTable = new ArrayList<>();
    private boolean hasNoonCourse = false;//中午是否有课

    public CourseTableView(Context context) {
        super(context);
    }

    public CourseTableView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    /**
     * 横的分界线
     */
    private View getWeekTransverseLine() {
        TextView mWeekline = new TextView(getContext());
        mWeekline.setBackgroundColor(getResources().getColor(R.color.cary));
        mWeekline.setHeight(TimeTableLineHeight);
        mWeekline.setWidth(LayoutParams.MATCH_PARENT);
        return mWeekline;
    }

    /**
     * 绘制控件
     */
    private void initView() {
        mCourseNumLayout = new LinearLayout(getContext());
        mCourseNumLayout.setOrientation(HORIZONTAL);
        hasNoonCourse = false;
        for (int i = 0; i <= WEEKNUM; i++) {
            List<CourseEntity> mListMon = new ArrayList<>();
            //逐个获取一周每天的课程安排
            for (CourseEntity courseEntity : mListTimeTable) {
                boolean startIn = courseEntity.getStartSection() == 5 || courseEntity.getStartSection() == 6;
                boolean centerIn = courseEntity.getStartSection() <= 5 && courseEntity.getEndSection() >= 5;
                if (startIn || centerIn) {
                    hasNoonCourse = true;
                }
                //放入星期列表
                if (courseEntity.getDayOfWeek() == i) {
                    //同一上课区域内有课程冲突的，只显示第一个课程数据
                    if (mListMon.size() > 0 && mListMon.get(mListMon.size() - 1).getEndSection() >= courseEntity.getStartSection()) {
                        continue;
                    }
                    mListMon.add(courseEntity);
                }
            }
            switch (i) {
                case 0:
                    //绘制1~MAXNUM，课程左部节数序号
                    LinearLayout mMonday = new LinearLayout(getContext());
                    mMonday.setBackgroundColor(getResources().getColor(R.color.white));
                    ViewGroup.LayoutParams mm = new ViewGroup.LayoutParams(ViewUtil.dip2px(TimeTableNumWidth), ViewGroup.LayoutParams.WRAP_CONTENT);
                    mMonday.setLayoutParams(mm);
                    mMonday.setOrientation(VERTICAL);
                    for (int j = 1; j <= MAXNUM; j++) {
                        //中午没课，不显示空格
                        if (!hasNoonCourse && (j == 5 || j == 6)) {
                            continue;
                        }
                        TextView mNum = new TextView(getContext());
                        mNum.setGravity(Gravity.CENTER);
                        mNum.setTextColor(getResources().getColor(R.color.primaryDark));
                        mNum.setHeight(ViewUtil.dip2px(TimeTableHeight));
                        mNum.setWidth(ViewUtil.dip2px(TimeTableNumWidth));
                        mNum.setTextSize(14);
                        mNum.setText(Func.courseIndexToStr(j));
                        mMonday.addView(mNum);
                        View weekTransverseLine = getWeekTransverseLine();
                        mMonday.addView(weekTransverseLine);
                    }
                    mCourseNumLayout.addView(mMonday);
                    break;
                case 1:
                case 2:
                case 3:
                case 4:
                case 5:
                case 6:
                case 7:

                    //绘制课表
                    LinearLayout mLayout = getTimeTableView(mListMon, i);
                    mLayout.setOrientation(VERTICAL);
                    ViewGroup.LayoutParams linearParams = new ViewGroup.LayoutParams((getViewWidth() - ViewUtil.dip2px(TimeTableNumWidth) - 2 * 6) / WEEKNUM, LayoutParams.WRAP_CONTENT);
                    mLayout.setLayoutParams(linearParams);
                    mLayout.setWeightSum(1);
                    mCourseNumLayout.addView(mLayout);
                    break;

                default:
                    break;
            }
            TextView viewItem = new TextView(getContext());
            viewItem.setLayoutParams(new ViewGroup.LayoutParams(2, ViewGroup.LayoutParams.MATCH_PARENT));
//            viewItem.setHeight(dip2px(TimeTableHeight * MAXNUM) + MAXNUM * 2);
//            viewItem.setWidth(2);
            viewItem.setBackgroundColor(getResources().getColor(R.color.primaryBackgroundTran));
            mCourseNumLayout.addView(viewItem);
        }
        addView(getWeekTransverseLine());
        addView(mCourseNumLayout);
        addView(getWeekTransverseLine());
    }

    /**
     * 获取屏幕像素宽度
     *
     * @return 宽度
     */
    private int getViewWidth() {
        WindowManager wm = (WindowManager) getContext().getSystemService(
                Context.WINDOW_SERVICE);
        return wm.getDefaultDisplay().getWidth();
    }

    /**
     * 绘制空白格子
     *
     * @param count 格子个数
     * @param week  第几周
     * @param start 课程开头
     * @return
     */
    private View addStartView(int count, final int week, final int start) {
        final LinearLayout mStartView = new LinearLayout(getContext());
        mStartView.setOrientation(VERTICAL);
        for (int i = 1; i < count; i++) {
            final int course_now = start + i;
            if (!hasNoonCourse && (course_now == 5 || course_now == 6)) {
                continue;
            }
            TextView mTime = new TextView(getContext());
            mTime.setGravity(Gravity.CENTER);
            mTime.setHeight(ViewUtil.dip2px(TimeTableHeight));
            mTime.setWidth(ViewUtil.dip2px(TimeTableHeight));
            mStartView.addView(mTime);
            View transverseLine = getWeekTransverseLine();
            mStartView.addView(transverseLine);
            //这里可以处理空白处点击添加课表
            mTime.setOnLongClickListener(new OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    Intent intent = new Intent(getContext(), AddCourseActivity.class);
                    intent.putExtra("start", course_now);
                    intent.putExtra("weekDay", week);
                    getContext().startActivity(intent);
                    return false;
                }
            });

        }
        return mStartView;
    }

    /**
     * 星期一到星期天的课表
     *
     * @param courses
     * @param DayOfWeek
     * @return
     */
    private LinearLayout getTimeTableView(List<CourseEntity> courses, int DayOfWeek) {
        // 每日课表为一条竖线布局
        LinearLayout mTimeTableView = new LinearLayout(getContext());
        mTimeTableView.setOrientation(VERTICAL);
        int modelSize = courses.size();
        if (modelSize <= 0) {
            //如果没有数据，全部填充空白格子
            int add = 1;
            if (!hasNoonCourse) {
                add = 0;
            }
            mTimeTableView.addView(addStartView(MAXNUM + add, DayOfWeek, 0));
        } else {
            //有数据遍历数据
            for (int i = 0; i < modelSize; i++) {
                if (i == 0) {
                    //添加的0到开始节数的空格
                    mTimeTableView.addView(addStartView(courses.get(0).getStartSection(), DayOfWeek, 0));
                    mTimeTableView.addView(getMode(courses.get(0)));
                } else if (courses.get(i).getStartSection() - courses.get(i - 1).getStartSection() > 0) {
                    //填充
                    mTimeTableView.addView(addStartView(courses.get(i).getStartSection() - courses.get(i - 1).getEndSection(), DayOfWeek, courses.get(i - 1).getEndSection()));
                    mTimeTableView.addView(getMode(courses.get(i)));
                }
                if (i + 1 == modelSize) {
                    //剩下的补空白格子
                    mTimeTableView.addView(addStartView(MAXNUM - courses.get(i).getEndSection(), DayOfWeek, courses.get(i).getEndSection()));
                }
            }
        }
        return mTimeTableView;
    }

    /**
     * 获取单个课表View 也可以自定义我这个
     *
     * @param courseEntity 数据类型
     */
    @SuppressWarnings("deprecation")
    private View getMode(final CourseEntity courseEntity) {
        final LayoutInflater inflater = LayoutInflater.from(getContext());
        final LinearLayout course = (LinearLayout) inflater.inflate(R.layout.course_item, this, false);
        TextView courseView = (TextView) course.findViewById(R.id.course_item);
        courseView.setPadding(ViewUtil.dip2px(2), 0, ViewUtil.dip2px(2), 0);
        int num = courseEntity.getEndSection() - courseEntity.getStartSection();
        courseView.setHeight(ViewUtil.dip2px((num + 1) * TimeTableHeight) + num * 2);
        courseView.setTextColor(getContext().getResources().getColor(
                android.R.color.white));
        courseView.setWidth(ViewUtil.dip2px(50));
        courseView.setTextSize(12);
        courseView.setGravity(Gravity.CENTER);
        courseView.setText(courseEntity.getCourseName() + "@" + courseEntity.getClassRoom());
        course.addView(getWeekTransverseLine());
        Drawable text_bg = getContext().getResources().getDrawable(R.drawable.round_ts_bg_white_5dp);
        int color = Config.COLORS[getColorIndex(courseEntity.getCourseName())];
        text_bg.setColorFilter(getContext().getResources().getColor(color), PorterDuff.Mode.SRC_IN);
        text_bg.setAlpha(150);
        course.setBackgroundDrawable(text_bg);
        course.setAlpha(0.9f);
        course.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                CourseInfoEntity courseInfo = new CourseInfoDao().getCourseInfo(courseEntity.getCourseNum());
                AlertDialog.Builder dialogBuilder = Dialogs.getCourseInfoDialog((Activity) getContext(), courseEntity, courseInfo);
                AlertDialog dialog = dialogBuilder.create();
                Window window = dialog.getWindow();
                if (window != null) {
                    //透明对话框背景
                    window.setBackgroundDrawable(new ColorDrawable());
                }
                dialog.show();
            }
        });
        return course;
    }

    public void setTimeTable(List<CourseEntity> courses) {
        this.mListTimeTable = courses;
        removeAllViews();
        for (CourseEntity courseEntity : courses) {
            addTimeName(courseEntity.getCourseName());
        }
        initView();
    }

    /**
     * 输入课表名循环判断是否数组存在该课表 如果存在输出true并退出循环 如果不存在则存入colorSt[20]数组
     */
    private void addTimeName(String name) {
        Integer color_index = colorMap.get(name);
        if (color_index == null) {
            int new_index = (colorNum++) % Config.COLORS.length;
            //不要黑色背景
            if (new_index == 0) {
                new_index = Config.COLORS.length / 2;
            }
            colorMap.put(name, new_index);
        }

    }

    /**
     * 获取数组中的课程名
     */
    public int getColorIndex(String name) {
        Integer color_index = colorMap.get(name);
        if (color_index == null) {
            color_index = 1;
        }
        return color_index;
    }
}