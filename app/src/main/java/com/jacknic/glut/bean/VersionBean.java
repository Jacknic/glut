package com.jacknic.glut.bean;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * app版本信息
 */

public class VersionBean {

    @JSONField(name = "date")
    private String date;
    @JSONField(name = "versionCode")
    private int versionCode;
    @JSONField(name = "versionName")
    private String versionName;
    @JSONField(name = "downloadUrl")
    private String downloadUrl;
    @JSONField(name = "info")
    private String info;
    @JSONField(name = "remark")
    private String remark;

    public VersionBean(String remark, String date, int versionCode, String versionName, String downloadUrl, String info) {
        this.remark = remark;
        this.date = date;
        this.versionCode = versionCode;
        this.versionName = versionName;
        this.downloadUrl = downloadUrl;
        this.info = info;
    }

    public VersionBean() {
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(int versionCode) {
        this.versionCode = versionCode;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
