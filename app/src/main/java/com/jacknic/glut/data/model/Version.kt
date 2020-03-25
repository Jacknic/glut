package com.jacknic.glut.data.model

import com.google.gson.annotations.SerializedName

/**
 * app 版本信息
 */
data class Version(

    /**
     * 发布时间
     **/
    @SerializedName("date")
    val date: String = "",

    /**
     * 版本号
     **/
    @SerializedName("versionCode")
    val versionCode: Int = 0,

    /**
     * 版本名称
     **/
    @SerializedName("versionName")
    val versionName: String = "",

    /**
     * 下载链接
     **/
    @SerializedName("downloadUrl")
    val downloadUrl: String = "",

    /**
     * 更新描述
     **/
    @SerializedName("info")
    val info: String = "",

    /**
     * 备注信息
     **/
    @SerializedName("remark")
    val remark: String = ""
)
