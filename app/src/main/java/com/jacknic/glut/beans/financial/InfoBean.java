package com.jacknic.glut.beans.financial;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * 个人财务信息
 */


public class InfoBean {

    @JSONField(name = "SlurMoney")
    private String slurMoney;//欠费金额

    @JSONField(name = "ReceivableMoney")
    private String receivableMoney;//实收金额

    @JSONField(name = "SID")
    private String sid;//学号

    @JSONField(name = "StudentName")
    private String studentName;//学生名

    @JSONField(name = "AcademyName")
    private String academy;//所属学院名称

    @JSONField(name = "SpecialityName")
    private String speciality_name;//专业名称

    @JSONField(name = "GradeName")
    private String grade;//所属年级


    @JSONField(name = "ClassName")
    private String className;//班级全称

    @JSONField(name = "ExamineeID")
    private String examineEid;//eid

    @JSONField(name = "PayMoney")
    private String payMoney;//已支付费用

    @JSONField(name = "LimitCost")
    private String limit_cost;//最低消费

    @JSONField(name = "State")
    private String state;//在校状态

    @JSONField(name = "StateDate")
    private String stateDate;//在校时间

    @JSONField(name = "EntranceDate")
    private String entranceDate;//入学时间

    @JSONField(name = "DeferralMoney")
    private String deferralMoney;//贷款金额

    @JSONField(name = "DeferralDate")
    private String deferralDate;//贷款到账时间

    @JSONField(name = "GreenExpress")
    private String greenExpress;//是否是绿色通道

    @JSONField(name = "Dorm")
    private String dorm;//宿舍号

    @JSONField(name = "UnLimitCost")
    private String unLimitCost;

    @JSONField(name = "BankID")
    private String bankId;//银行卡号

    @JSONField(name = "Remark")
    private String remark;//标注

    public void setSlurMoney(String slurMoney) {
        this.slurMoney = slurMoney;
    }

    public String getSlurMoney() {
        return slurMoney;
    }

    public void setReceivableMoney(String receivableMoney) {
        this.receivableMoney = receivableMoney;
    }

    public String getReceivableMoney() {
        return receivableMoney;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public String getSid() {
        return sid;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setAcademy(String academy) {
        this.academy = academy;
    }

    public String getAcademy() {
        return academy;
    }

    public void setSpeciality_name(String speciality_name) {
        this.speciality_name = speciality_name;
    }

    public String getSpeciality_name() {
        return speciality_name;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public String getGrade() {
        return grade;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getClassName() {
        return className;
    }

    public void setExamineEid(String examineEid) {
        this.examineEid = examineEid;
    }

    public String getExamineEid() {
        return examineEid;
    }

    public void setPayMoney(String payMoney) {
        this.payMoney = payMoney;
    }

    public String getPayMoney() {
        return payMoney;
    }

    public void setLimit_cost(String limit_cost) {
        this.limit_cost = limit_cost;
    }

    public String getLimit_cost() {
        return limit_cost;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getState() {
        return state;
    }

    public void setStateDate(String stateDate) {
        this.stateDate = stateDate;
    }

    public String getStateDate() {
        return stateDate;
    }

    public void setEntranceDate(String entranceDate) {
        this.entranceDate = entranceDate;
    }

    public String getEntranceDate() {
        return entranceDate;
    }

    public void setDeferralMoney(String deferralMoney) {
        this.deferralMoney = deferralMoney;
    }

    public String getDeferralMoney() {
        return deferralMoney;
    }

    public void setDeferralDate(String deferralDate) {
        this.deferralDate = deferralDate;
    }

    public String getDeferralDate() {
        return deferralDate;
    }

    public void setGreenExpress(String greenExpress) {
        this.greenExpress = greenExpress;
    }

    public String getGreenExpress() {
        return greenExpress;
    }

    public void setDorm(String dorm) {
        this.dorm = dorm;
    }

    public String getDorm() {
        return dorm;
    }

    public void setUnLimitCost(String unLimitCost) {
        this.unLimitCost = unLimitCost;
    }

    public String getUnLimitCost() {
        return unLimitCost;
    }

    public void setBankId(String bankId) {
        this.bankId = bankId;
    }

    public String getBankId() {
        return bankId;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getRemark() {
        return remark;
    }

}