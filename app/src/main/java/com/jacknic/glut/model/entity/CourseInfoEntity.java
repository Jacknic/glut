package com.jacknic.glut.model.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

/**
 * 课程信息
 */

@Entity(nameInDb = "course_info")
public class CourseInfoEntity {
    @Id(autoincrement = true)
    private Long id;
    private Integer semester, schoolYearStart;
    private String
            courseName,
            courseNum,
            teacher,
            grade;

    @Generated(hash = 2062509544)
    public CourseInfoEntity(Long id, Integer semester, Integer schoolYearStart, String courseName,
                            String courseNum, String teacher, String grade) {
        this.id = id;
        this.semester = semester;
        this.schoolYearStart = schoolYearStart;
        this.courseName = courseName;
        this.courseNum = courseNum;
        this.teacher = teacher;
        this.grade = grade;
    }

    public CourseInfoEntity() {
    }

    @Override
    public String toString() {
        return "CourseInfoEntity{" +
                "semester=" + semester +
                ", schoolYearStart=" + schoolYearStart +
                ", id=" + id +
                ", courseName='" + courseName + '\'' +
                ", courseNum='" + courseNum + '\'' +
                ", teacher='" + teacher + '\'' +
                ", grade='" + grade + '\'' +
                '}';
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getSemester() {
        return this.semester;
    }

    public void setSemester(Integer semester) {
        this.semester = semester;
    }

    public Integer getSchoolYearStart() {
        return this.schoolYearStart;
    }

    public void setSchoolYearStart(Integer schoolYearStart) {
        this.schoolYearStart = schoolYearStart;
    }

    public String getCourseName() {
        return this.courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getCourseNum() {
        return this.courseNum;
    }

    public void setCourseNum(String courseNum) {
        this.courseNum = courseNum;
    }

    public String getTeacher() {
        return this.teacher;
    }

    public void setTeacher(String teacher) {
        this.teacher = teacher;
    }

    public String getGrade() {
        return this.grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

}
