package com.jacknic.glut.data.model

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

/**
 * 个人财务信息
 */
@Keep
data class Financial(
    @SerializedName("AcademyName")
    val academyName: String = "",
    @SerializedName("BankID")
    val bankID: String = "",
    @SerializedName("ClassName")
    val className: String = "",
    @SerializedName("DeferralDate")
    val deferralDate: String = "",
    @SerializedName("DeferralMoney")
    val deferralMoney: String = "",
    @SerializedName("Dorm")
    val dorm: String = "",
    @SerializedName("email")
    val email: String = "",
    @SerializedName("EntranceDate")
    val entranceDate: String = "",
    @SerializedName("ExamineeID")
    val examineID: String = "",
    @SerializedName("GradeName")
    val gradeName: String = "",
    @SerializedName("GreenExpress")
    val greenExpress: String = "",
    @SerializedName("IDcode")
    val id: String = "",
    @SerializedName("LimitCost")
    val limitCost: String = "",
    @SerializedName("newUser")
    val newUser: String = "",
    @SerializedName("outDate")
    val outDate: String = "",
    @SerializedName("PayMoney")
    val payMoney: String = "",
    @SerializedName("ReceivableMoney")
    val receivableMoney: String = "",
    @SerializedName("Remark")
    val remark: String = "",
    @SerializedName("SID")
    val sid: String = "",
    @SerializedName("SlurMoney")
    val slurMoney: String = "",
    @SerializedName("SpecialityName")
    val specialityName: String = "",
    @SerializedName("State")
    val state: String = "",
    @SerializedName("StateDate")
    val stateDate: String = "",
    @SerializedName("StudentID")
    val studentID: String = "",
    @SerializedName("StudentName")
    val studentName: String = "",
    @SerializedName("UnLimitCost")
    val unLimitCost: String = "",
    @SerializedName("valiDataCode")
    val validDataCode: String = ""
)