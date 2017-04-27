package com.jacknic.glut.beans.course;

/**
 * 课程实体类
 */
public class CourseBean {
    private int id;//主鍵，自增
    private int schoolStartYear;//学年开始年
    private int semester;//学期
    private int dayOfWeek;//星期几
    private int startSection;//第几节课开始
    private int endSection;//第几节课结束
    private String courseName;//课程名
    private String courseNum;//课程编号
    private String classRoom;//教室
    private String week;//课程周
    private int weekType;//标记是否是单双周，0为每周,1单周，2双周
    private String smartPeriod;//开课周

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSchoolStartYear() {
        return schoolStartYear;
    }

    public void setSchoolStartYear(int schoolStartYear) {
        this.schoolStartYear = schoolStartYear;
    }

    public int getSemester() {
        return semester;
    }

    public void setSemester(int semester) {
        this.semester = semester;
    }

    public int getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(int dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public int getStartSection() {
        return startSection;
    }

    public void setStartSection(int startSection) {
        this.startSection = startSection;
    }

    public int getEndSection() {
        return endSection;
    }

    public void setEndSection(int endSection) {
        this.endSection = endSection;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getCourseNum() {
        return courseNum;
    }

    public void setCourseNum(String courseNum) {
        this.courseNum = courseNum;
    }


    public String getClassRoom() {
        return classRoom;
    }

    public void setClassRoom(String classRoom) {
        this.classRoom = classRoom;
    }

    public String getWeek() {
        return week;
    }

    public void setWeek(String week) {
        this.week = week;
    }


    public int getWeekType() {
        return weekType;
    }

    public void setWeekType(int weekType) {
        this.weekType = weekType;
    }

    public String getSmartPeriod() {
        return smartPeriod;
    }

    public void setSmartPeriod(String smartPeriod) {
        this.smartPeriod = smartPeriod;
    }

    @Override
    public String toString() {
        return "CourseBean{" +
                "id=" + id +
                ", schoolStartYear=" + schoolStartYear +
                ", semester=" + semester +
                ", dayOfWeek=" + dayOfWeek +
                ", startSection=" + startSection +
                ", endSection=" + endSection +
                ", courseName='" + courseName + '\'' +
                ", courseNum='" + courseNum + '\'' +
                ", classRoom='" + classRoom + '\'' +
                ", week='" + week + '\'' +
                ", weekType=" + weekType +
                ", smartPeriod='" + smartPeriod + '\'' +
                '}';
    }

    public CourseBean() {
    }

    public CourseBean(int id, int schoolStartYear, int semester, int dayOfWeek, int startSection, int endSection, String courseName, String courseNum, String classRoom, String week, String startTime, String endTime, String grade, int weekType, String smartPeriod) {
        this.id = id;
        this.schoolStartYear = schoolStartYear;
        this.semester = semester;
        this.dayOfWeek = dayOfWeek;
        this.startSection = startSection;
        this.endSection = endSection;
        this.courseName = courseName;
        this.courseNum = courseNum;
        this.classRoom = classRoom;
        this.week = week;
        this.weekType = weekType;
        this.smartPeriod = smartPeriod;
    }
}
