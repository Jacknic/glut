package com.jacknic.glut.model;

import android.content.Context;
import android.content.SharedPreferences;

import com.jacknic.glut.beans.educational.StudentInfoBean;
import com.jacknic.glut.utils.Config;
import com.lzy.okgo.OkGo;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

/**
 * 学籍信息处理类
 */

public class StudentInfoModel {
    /**
     * 从文档中获取学生信息
     *
     * @param dom
     * @return
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
     *
     * @param infoBean
     */
    public void saveToPrefer(StudentInfoBean infoBean) {
        SharedPreferences preferences = OkGo.getContext().getSharedPreferences(Config.PREFER_JW, Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = preferences.edit();
        edit.putString("name", infoBean.getName());
        edit.putString("className", infoBean.getClassName());
        edit.putString("birthday", infoBean.getBirthday());
        edit.putString("place", infoBean.getPlace());
        edit.putString("id", infoBean.getId());
        edit.putString("nation", infoBean.getNation());
        edit.putString("role", infoBean.getRole());
        edit.putString("level", infoBean.getLevel());
        edit.putString("origin", infoBean.getOrigin());
        edit.putString("score", infoBean.getScore());
        edit.apply();
    }

    /**
     * 获取学生信息
     *
     * @return
     */
    public StudentInfoBean getFromPrefer() {
        StudentInfoBean infoBean = new StudentInfoBean();
        SharedPreferences preferences = OkGo.getContext().getSharedPreferences(Config.PREFER_JW, Context.MODE_PRIVATE);
        infoBean.setSid(preferences.getString("sid", ""));
        infoBean.setName(preferences.getString("name", ""));
        infoBean.setClassName(preferences.getString("className", ""));
        infoBean.setBirthday(preferences.getString("birthday", ""));
        infoBean.setPlace(preferences.getString("place", ""));
        infoBean.setId(preferences.getString("id", ""));
        infoBean.setNation(preferences.getString("nation", ""));
        infoBean.setRole(preferences.getString("role", ""));
        infoBean.setLevel(preferences.getString("level", ""));
        infoBean.setOrigin(preferences.getString("origin", ""));
        infoBean.setScore(preferences.getString("score", ""));
        return infoBean;
    }
}
