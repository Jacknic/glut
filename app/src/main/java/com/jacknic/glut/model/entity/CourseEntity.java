package com.jacknic.glut.model.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

/**
 * 课程实体类
 */

@Entity(nameInDb = "course")
public class CourseEntity {
    @Id(autoincrement = true)
    private Long id;//主鍵，自增
    private Integer schoolStartYear;//学年开始年
    private Integer semester;//学期
    private Integer dayOfWeek;//星期几
    private Integer startSection;//第几节课开始
    private Integer endSection;//第几节课结束
    private String courseName;//课程名
    private String courseNum;//课程编号
    private String classRoom;//教室
    private String week;//课程周
    private Integer weekType;//标记是否是单双周，0为每周,1单周，2双周
    private String smartPeriod;//开课周

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getSchoolStartYear() {
        return schoolStartYear;
    }

    public void setSchoolStartYear(Integer schoolStartYear) {
        this.schoolStartYear = schoolStartYear;
    }

    public Integer getSemester() {
        return semester;
    }

    public void setSemester(Integer semester) {
        this.semester = semester;
    }

    public Integer getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(Integer dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public Integer getStartSection() {
        return startSection;
    }

    public void setStartSection(Integer startSection) {
        this.startSection = startSection;
    }

    public Integer getEndSection() {
        return endSection;
    }

    public void setEndSection(Integer endSection) {
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


    public Integer getWeekType() {
        return weekType;
    }

    public void setWeekType(Integer weekType) {
        this.weekType = weekType;
    }

    public String getSmartPeriod() {
        return smartPeriod;
    }

    public void setSmartPeriod(String smartPeriod) {
        this.smartPeriod = smartPeriod;
    }


    public CourseEntity() {
    }


    @Generated(hash = 490863634)
    public CourseEntity(Long id, Integer schoolStartYear, Integer semester, Integer dayOfWeek, Integer startSection, Integer endSection, String courseName, String courseNum, String classRoom, String week, Integer weekType, String smartPeriod) {
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

    @Override
    public String toString() {
        return "CourseEntity{" +
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
}
