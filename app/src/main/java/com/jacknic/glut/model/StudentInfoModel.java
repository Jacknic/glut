package com.jacknic.glut.model;

import android.content.SharedPreferences;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jacknic.glut.model.bean.StudentInfoBean;
import com.jacknic.glut.util.PreferManager;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

/**
 * 学籍信息处理类
 */

public class StudentInfoModel {

    private String student_info = "student_info";

    /**
     * 从文档中获取学生信息
     */
    public StudentInfoBean getStudentInfo(String dom) {
        StudentInfoBean infoBean = new StudentInfoBean();
        try {
            Document document = Jsoup.parse(dom);
            String sid = document.getElementsByAttributeValue("name", "username").get(0).val();
            String name = document.getElementsByAttributeValue("name", "realname").get(0).val();
            String birthday = document.getElementsByAttributeValue("name", "birthday").get(0).val();
            String className = document.getElementById("classChange").text();
            String place = document.getElementsByAttributeValue("name", "nativePlace").get(0).val();
            String id = document.getElementsByAttributeValue("name", "idno").get(0).val();
            String nation = document.getElementsByAttributeValue("name", "folkid").get(0).parent().text();
            String role = document.getElementsByAttributeValue("name", "politicalStatusId").get(0).parent().text();
            String level = document.getElementsByAttributeValue("name", "literacyId").get(0).parent().text();
            String origin = document.getElementsByAttributeValue("name", "stusourceId").get(0).parent().text();
            String score = document.getElementsByAttributeValue("name", "entrExamScore").get(0).val();
            infoBean.setSid(sid);
            infoBean.setName(name);
            infoBean.setBirthday(birthday);
            infoBean.setClassName(className);
            infoBean.setPlace(place);
            infoBean.setId(id);
            infoBean.setNation(nation);
            infoBean.setRole(role);
            infoBean.setLevel(level);
            infoBean.setOrigin(origin);
            infoBean.setScore(score);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return infoBean;
    }

    /**
     * 写入学生信息
     */
    public void saveToPrefer(StudentInfoBean infoBean) {
        SharedPreferences.Editor edit = PreferManager.getPrefer().edit();
        edit.putString(student_info, JSON.toJSONString(infoBean));
        edit.apply();
    }

    /**
     * 获取学生信息
     */
    public StudentInfoBean getFromPrefer() {
        StudentInfoBean infoBean = JSONObject.parseObject(PreferManager.getPrefer().getString(student_info, ""), StudentInfoBean.class);
        return infoBean == null ? new StudentInfoBean() : infoBean;
    }
}
