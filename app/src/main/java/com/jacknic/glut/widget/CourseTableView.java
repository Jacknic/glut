package com.jacknic.glut.widget;

import android.app.Activity;
import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jacknic.glut.R;
import com.jacknic.glut.beans.course.CourseBean;
import com.jacknic.glut.beans.course.CourseInfoBean;
import com.jacknic.glut.model.CourseInfoDbModel;
import com.jacknic.glut.utils.Config;
import com.jacknic.glut.utils.Func;
import com.lzy.okgo.OkGo;

import java.util.ArrayList;
import java.util.List;


/**
 * 课表显示View
 *
 * @author shallcheek
 */
public class CourseTableView extends LinearLayout {
    private final int START = 0;
    //最大节数
    public final int MAXNUM = 14;
    //显示到星期几
    public final int WEEKNUM = 7;
    //单个View高度
    private final int TimeTableHeight = 50;
    //线的高度
    private final int TimeTableLineHeight = 2;
    private final int TimeTableNumWidth = 20;
    private final int TimeTableWeekNameHeight = 30;
    private LinearLayout mHorizontalWeekLayout;//第一行的星期显示
    private LinearLayout mVerticalWeekLayout;//课程格子
    private String[] weekName = {"周一", "周二", "周三", "周四", "周五", "周六", "周日"};
    public String[] colorStr = new String[20];
    int colorNum = 0;
    //数据源
    private List<CourseBean> mListTimeTable = new ArrayList<>();
    private boolean hasNoonCourse = false;

    public CourseTableView(Context context) {
        super(context);
    }

    public CourseTableView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    /**
     * 横的分界线
     *
     * @return
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
        mHorizontalWeekLayout = new LinearLayout(getContext());
        mHorizontalWeekLayout.setOrientation(HORIZONTAL);
        mHorizontalWeekLayout.setBackgroundColor(getResources().getColor(R.color.white));
        mVerticalWeekLayout = new LinearLayout(getContext());
        mVerticalWeekLayout.setOrientation(HORIZONTAL);
        for (int i = 0; i <= WEEKNUM; i++) {
            switch (i) {
                //表头周数、星期一到星期天
                case 0:
                    //课表出的0,0格子 空白的
                    TextView mTime = new TextView(getContext());
                    mTime.setTextSize(10);
                    mTime.setGravity(Gravity.CENTER);
                    mTime.setTextColor(getResources().getColor(R.color.pink));
                    mTime.setHeight(dip2px(TimeTableWeekNameHeight));
                    mTime.setWidth((dip2px(TimeTableNumWidth)));
                    mHorizontalWeekLayout.addView(mTime);

                    //绘制1~MAXNUM
                    LinearLayout mMonday = new LinearLayout(getContext());
                    mMonday.setBackgroundColor(getResources().getColor(R.color.white));
                    ViewGroup.LayoutParams mm = new ViewGroup.LayoutParams(dip2px(TimeTableNumWidth), dip2px(MAXNUM * TimeTableHeight) + MAXNUM * 2);
                    mMonday.setLayoutParams(mm);
                    mMonday.setOrientation(VERTICAL);
                    for (int j = 1; j <= MAXNUM; j++) {
                        TextView mNum = new TextView(getContext());
                        mNum.setGravity(Gravity.CENTER);
                        mNum.setTextColor(getResources().getColor(R.color.primaryDark));
                        mNum.setHeight(dip2px(TimeTableHeight));
                        mNum.setWidth(dip2px(TimeTableNumWidth));
                        mNum.setTextSize(14);
                        mNum.setText(Func.courseIndexToStr(j));
                        mMonday.addView(mNum);
                        mMonday.addView(getWeekTransverseLine());
                    }
                    mVerticalWeekLayout.addView(mMonday);
                    break;
                case 1:
                case 2:
                case 3:
                case 4:
                case 5:
                case 6:
                case 7:
                    // 设置显示星期一 到星期天
                    LinearLayout mHoriView = new LinearLayout(getContext());
                    mHoriView.setGravity(Gravity.CENTER);
                    mHoriView.setOrientation(VERTICAL);
                    TextView l = new TextView(getContext());
                    l.setHeight(dip2px(TimeTableWeekNameHeight + 2));
                    l.setWidth(2);
                    l.setBackgroundColor(getResources().getColor(R.color.cary));
                    //星期分割线
                    mHorizontalWeekLayout.addView(l);
                    TextView mWeekName = new TextView(getContext());
                    mWeekName.setTextColor(getResources().getColor(R.color.primaryDark));
                    mWeekName.setWidth(((getViewWidth() - dip2px(TimeTableNumWidth) - 2 * 6)) / WEEKNUM);
                    mWeekName.setHeight(dip2px(TimeTableWeekNameHeight));
                    mWeekName.setGravity(Gravity.CENTER);
                    mWeekName.setTextSize(14);
                    mWeekName.setText(weekName[i - 1]);
                    mHoriView.addView(mWeekName);
                    mHorizontalWeekLayout.addView(mHoriView);

                    List<CourseBean> mListMon = new ArrayList<>();
                    //遍历出星期1~7的课表
                    for (CourseBean courseBean : mListTimeTable) {
                        if (courseBean.getStartSection() == 5 || courseBean.getStartSection() == 6) {
                            hasNoonCourse = true;
                        }
                        if (courseBean.getDayOfWeek() == i) {
                            mListMon.add(courseBean);
                        }
                    }
                    //添加
                    LinearLayout mLayout = getTimeTableView(mListMon, i);
                    mLayout.setOrientation(VERTICAL);
                    ViewGroup.LayoutParams linearParams = new ViewGroup.LayoutParams((getViewWidth() - dip2px(TimeTableNumWidth) - 2 * 6) / WEEKNUM, LayoutParams.MATCH_PARENT);
                    mLayout.setLayoutParams(linearParams);
                    mLayout.setWeightSum(1);
                    mVerticalWeekLayout.addView(mLayout);
                    break;

                default:
                    break;
            }
            TextView l = new TextView(getContext());
            l.setHeight(dip2px(TimeTableHeight * MAXNUM) + MAXNUM * 2);
            l.setWidth(2);
            l.setBackgroundColor(getResources().getColor(R.color.primaryBackgroundTran));
            mVerticalWeekLayout.addView(l);
        }
        addView(mHorizontalWeekLayout);
        addView(getWeekTransverseLine());
        addView(mVerticalWeekLayout);
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

    private View addStartView(int startNum, final int week, final int start) {
        final LinearLayout mStartView = new LinearLayout(getContext());
        mStartView.setOrientation(VERTICAL);
        for (int i = 1; i < startNum; i++) {
            TextView mTime = new TextView(getContext());
            mTime.setGravity(Gravity.CENTER);
            mTime.setHeight(dip2px(TimeTableHeight));
            mTime.setWidth(dip2px(TimeTableHeight));
            mStartView.addView(mTime);
            mStartView.addView(getWeekTransverseLine());
            //这里可以处理空白处点击添加课表
//            mTime.setOnClickListener(new OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Toast.makeText(getContext(), "星期" + week + "第" + (start + num) + "节", Toast.LENGTH_LONG).show();
//                }
//            });

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
    private LinearLayout getTimeTableView(List<CourseBean> courses, int DayOfWeek) {
        LinearLayout mTimeTableView = new LinearLayout(getContext());
        mTimeTableView.setOrientation(VERTICAL);
        int modelSize = courses.size();
        if (modelSize <= 0) {
            mTimeTableView.addView(addStartView(MAXNUM + 1, DayOfWeek, 0));
        } else {
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
                    mTimeTableView.addView(addStartView(MAXNUM - courses.get(i).getEndSection(), DayOfWeek, courses.get(i).getEndSection()));
                }
            }
        }
        return mTimeTableView;
    }

    /**
     * 获取单个课表View 也可以自定义我这个
     *
     * @param courseBean 数据类型
     * @return
     */
    @SuppressWarnings("deprecation")
    private View getMode(final CourseBean courseBean) {
        final LayoutInflater inflater = LayoutInflater.from(getContext());
        final LinearLayout course = (LinearLayout) inflater.inflate(R.layout.course_item, this, false);
        TextView mTimeTableNameView = (TextView) course.findViewById(R.id.course_item);
        mTimeTableNameView.setPadding(dip2px(2), 0, dip2px(2), 0);
        int num = courseBean.getEndSection() - courseBean.getStartSection();
        mTimeTableNameView.setHeight(dip2px((num + 1) * TimeTableHeight) + num * 2);
        mTimeTableNameView.setTextColor(getContext().getResources().getColor(
                android.R.color.white));
        mTimeTableNameView.setWidth(dip2px(50));
        mTimeTableNameView.setTextSize(12);
        mTimeTableNameView.setGravity(Gravity.CENTER);
        mTimeTableNameView.setText(courseBean.getCourseName() + "@" + courseBean.getClassRoom());
        course.addView(getWeekTransverseLine());
        Drawable text_bg = getContext().getResources().getDrawable(R.drawable.round_ts_bg_white_5dp);
        int color = Config.colors[getColorNum(courseBean.getCourseName())];
        text_bg.setColorFilter(getContext().getResources().getColor(color), PorterDuff.Mode.SRC_IN);
        text_bg.setAlpha(150);
        course.setBackgroundDrawable(text_bg);
//        mTimeTableNameView.getBackground().setColorFilter(Config.colors[getColorNum(courseBean.getCourseName())], PorterDuff.Mode.SRC_ATOP);
        course.setAlpha(0.9f);
        course.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                CourseInfoDbModel infoDbModel = new CourseInfoDbModel(OkGo.getContext());
                CourseInfoBean courseInfo = infoDbModel.getCourseInfo(courseBean.getCourseNum());
                CourseDialog.getDialog((Activity) getContext(), courseBean, courseInfo).show();
            }
        });
        return course;
    }

    /**
     * 转换dp
     *
     * @param dpValue
     * @return
     */
    public int dip2px(float dpValue) {
        float scale = getContext().getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public void setTimeTable(List<CourseBean> courses) {
        this.mListTimeTable = courses;
        removeAllViews();
        for (CourseBean courseBean : courses) {
            addTimeName(courseBean.getCourseName());
        }
        initView();
    }

    /**
     * 输入课表名循环判断是否数组存在该课表 如果存在输出true并退出循环 如果不存在则存入colorSt[20]数组
     *
     * @param name
     */
    private void addTimeName(String name) {
        boolean isRepeat = true;
        for (int i = 1; i < Config.colors.length; i++) {
            if (name.equals(colorStr[i])) {
                isRepeat = true;
                break;
            } else {
                isRepeat = false;
            }
        }
        if (!isRepeat) {
            colorStr[colorNum] = name;
            colorNum++;
        }
    }

    /**
     * 获取数组中的课程名
     *
     * @param name
     * @return
     */
    public int getColorNum(String name) {
        int num = 1;
        for (int i = 1; i < Config.colors.length; i++) {
            if (name.equals(colorStr[i])) {
                num = i;
            }
        }
        return num;
    }
}