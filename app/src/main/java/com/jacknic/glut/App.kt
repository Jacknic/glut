package com.jacknic.glut

import android.app.Application
import com.jacknic.glut.util.NotifyChannelUtils
import com.jacknic.glut.util.Preferences
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.FormatStrategy
import com.orhanobut.logger.Logger
import com.orhanobut.logger.PrettyFormatStrategy
import com.tencent.bugly.crashreport.CrashReport

class App : Application() {

    private val tag = "glut"

    override fun onCreate() {
        super.onCreate()
        Preferences.init(this)
        setLogger()
        CrashReport.initCrashReport(this)
        NotifyChannelUtils.registerChannel(this)
    }

    private fun setLogger() {
        val formatStrategy: FormatStrategy = PrettyFormatStrategy.newBuilder()
            .showThreadInfo(false)
            .tag(tag)
            .build()
        Logger.addLogAdapter(object : AndroidLogAdapter(formatStrategy) {
            override fun isLoggable(priority: Int, tag: String?): Boolean {
                return BuildConfig.DEBUG || priority >= Logger.INFO
            }
        })
    }
}