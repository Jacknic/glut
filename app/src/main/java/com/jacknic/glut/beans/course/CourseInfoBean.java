package com.jacknic.glut.beans.course;

/**
 * 课程信息
 */

public class CourseInfoBean {
    private int semester, schoolYearStart, id;
    private String
            courseName,
            courseNum,
            teacher,
            grade;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSemester() {
        return semester;
    }

    public void setSemester(int semester) {
        this.semester = semester;
    }

    public int getSchoolYearStart() {
        return schoolYearStart;
    }

    public void setSchoolYearStart(int schoolYearStart) {
        this.schoolYearStart = schoolYearStart;
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

    public String getTeacher() {
        return teacher;
    }

    public void setTeacher(String teacher) {
        this.teacher = teacher;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    @Override
    public String toString() {
        return "CourseInfoBean{" +
                "semester=" + semester +
                ", schoolYearStart=" + schoolYearStart +
                ", id=" + id +
                ", courseName='" + courseName + '\'' +
                ", courseNum='" + courseNum + '\'' +
                ", teacher='" + teacher + '\'' +
                ", grade='" + grade + '\'' +
                '}';
    }
}
