package com.jacknic.glut.beans.educational;


public class ExamInfoBean {
    /**
     * 考试时间
     */
    private String time;
    /**
     * 课程名称
     */
    private String name;

    /**
     * 考试地点
     */
    private String location;


    public ExamInfoBean() {
    }

    public ExamInfoBean(String name, String time, String location) {
        this.name = name;
        this.time = time;
        this.location = location;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

}
