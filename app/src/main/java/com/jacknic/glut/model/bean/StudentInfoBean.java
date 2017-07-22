package com.jacknic.glut.model.bean;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * 学生信息
 */

public class StudentInfoBean {
    @JSONField
    private String sid = "";//学号
    @JSONField
    private String name = "";//姓名
    @JSONField
    private String className = "";//班级
    @JSONField
    private String birthday = "";//出生日期
    @JSONField
    private String place = "";//籍贯
    @JSONField
    private String id = "";//证件号
    @JSONField
    private String nation = "";//民族
    @JSONField
    private String role = "";//政治面貌
    @JSONField
    private String level = "";//文化程度
    @JSONField
    private String origin = "";//来源地
    @JSONField
    private String score = "";//高考分数

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNation() {
        return nation;
    }

    public void setNation(String nation) {
        this.nation = nation;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    @Override
    public String toString() {
        return "StudentInfoBean{" +
                "sid='" + sid + '\'' +
                ", name='" + name + '\'' +
                ", className='" + className + '\'' +
                ", birthday='" + birthday + '\'' +
                ", place='" + place + '\'' +
                ", id='" + id + '\'' +
                ", nation='" + nation + '\'' +
                ", role='" + role + '\'' +
                ", level='" + level + '\'' +
                ", origin='" + origin + '\'' +
                ", score='" + score + '\'' +
                '}';
    }
}
