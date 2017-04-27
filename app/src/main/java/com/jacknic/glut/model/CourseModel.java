package com.jacknic.glut.model;

import com.jacknic.glut.beans.course.CourseBean;
import com.jacknic.glut.beans.course.CourseInfoBean;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 课程处理模型
 */

public class CourseModel {
    private Document document;
    private ArrayList<CourseInfoBean> courseInfoList = new ArrayList<>();

    public CourseModel(String dom) {
        document = Jsoup.parse(dom);
    }

    /**
     * 获取课程表
     *
     * @return 课程条目列表
     */
    public ArrayList<CourseBean> getCourses() {
        ArrayList<CourseBean> courseList = new ArrayList<>();
        Elements courses = document.getElementsByClass("infolist_tab").first().select(".infolist_common");
        int year = getSchoolYear();
        int semester = getSemester();
        for (Element e : courses) {
            CourseBean course = null;
            String number = e.child(0).text();
            String order = e.child(1).text();
            String name = e.child(2).text();
            String teacher = e.child(3).text();
            String grade = e.child(4).text();
            String type = e.child(5).text();
            String method = e.child(6).text();
            String test = e.child(7).text();
            CourseInfoBean courseInfo = new CourseInfoBean();
            courseInfo.setCourseNum(number);
            courseInfo.setCourseName(name);
            courseInfo.setGrade(grade);
            courseInfo.setSemester(semester);
            courseInfo.setSchoolYearStart(year);
            courseInfo.setTeacher(teacher);
            courseInfo.setId(Integer.parseInt(order));
            courseInfoList.add(courseInfo);

            Elements info = e.child(9).getElementsByTag("tr");
            int dayOf = -1;


            /*无教学时间地点的情况*/
            if (info.size() == 0) {
                course = new CourseBean();
                course.setSchoolStartYear(year);
                course.setSemester(semester);
                course.setCourseNum(number);
                course.setCourseName(name);
                courseList.add(course);
            } else {
            /*有教学时间地点的情况*/
                for (Element tr : info) {
                    course = new CourseBean();
                    course.setSchoolStartYear(year);
                    course.setSemester(semester);
                    course.setCourseNum(number);
                    course.setCourseName(name);
                    String week = tr.child(0).text();
                    course.setWeek(week);
                    String dayOfWeek = tr.child(1).text();
                    switch (dayOfWeek.charAt(dayOfWeek.length() - 1)) {
                        case '一':
                            dayOf = 1;
                            break;
                        case '二':
                            dayOf = 2;
                            break;
                        case '三':
                            dayOf = 3;
                            break;
                        case '四':
                            dayOf = 4;
                            break;
                        case '五':
                            dayOf = 5;
                            break;
                        case '六':
                            dayOf = 6;
                            break;
                        case '日':
                            dayOf = 7;
                            break;
                    }
                    course.setDayOfWeek(dayOf);
                    Pattern p = Pattern.compile("(\\d+)(-)?(\\d+)?(\\D)?周");
                    Matcher m = p.matcher(week);
                    if (m.find()) {
                        String start = m.group(1);
                        System.out.println("开始周：" + start);
                        String end = m.group(3) == null ? start : m.group(3);
                        System.out.println("结束周：" + end);
                        String everyWeek = m.group(4);
                        int weekType = 0;
                        if ("单".equals(everyWeek)) {
                            weekType = 1;
                        } else if ("双".equals(everyWeek)) {
                            weekType = 2;
                        }
                        StringBuilder smp = new StringBuilder(" ");
                        if (start != null) {
                            if (weekType == 0) {
                                for (int begin = Integer.parseInt(start); begin <= Integer.parseInt(end); begin++) {
                                    smp.append(begin).append(" ");
                                }
                            } else {
                                for (int begin = Integer.parseInt(start); begin <= Integer.parseInt(end); begin += 2) {
                                    smp.append(begin).append(" ");
                                }
                            }
                        } else {
                            smp.append("0").append(" ");
                        }
                        System.out.println("周数排序是： " + smp.toString());
                        course.setSmartPeriod(smp.toString());
                        course.setWeekType(weekType);
                    }
                    String courseArrangement = tr.child(2).text();
                    Pattern ap = Pattern.compile("(\\d+)[-、](\\d+)");
                    Matcher am = ap.matcher(courseArrangement);
                    if (am.find()) {
                        course.setStartSection(Integer.valueOf(am.group(1)));
                        course.setEndSection(Integer.valueOf(am.group(2)));
                    }
                    String classRoom = tr.child(3).text();
                    course.setClassRoom(classRoom);
                    courseList.add(course);
                }
            }
        }
        return courseList;
    }

    /**
     * 获取现在的学年
     *
     * @return 学年
     */
    public int getSchoolYear() {
        int year = Calendar.getInstance().get(Calendar.YEAR);
        Element years = document.select("select[name='year'] option").get(0);
        String str_year = years.text();
        try {
            year = Integer.parseInt(str_year);
        } catch (Exception ignored) {
        }
        return year;
    }


    /**
     * 当前学期
     *
     * @return 学期（1春季、2秋季）
     */
    public int getSemester() {
        int semester = 1;
        Element years = document.select("select[name='term'] option").get(0);
        String str_semester = years.text();
        try {
            semester = Integer.parseInt(str_semester);
        } catch (Exception ignored) {
        }
        return semester;
    }

    /**
     * 获取课程信息
     * <p>
     * 先调用getCourses
     *
     * @return 信息列表
     */
    public ArrayList<CourseInfoBean> getCourseInfoList() {
        return courseInfoList;
    }
}
