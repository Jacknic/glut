package com.jacknic.glut.util

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import android.util.Log
import androidx.core.content.edit
import com.jacknic.glut.data.model.Student
import com.jacknic.glut.data.util.fromJSON
import com.jacknic.glut.data.util.toJSON
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * 偏好设置工具类
 *
 * @author Jacknic
 */

const val KEY_THEME_MODE = "key_theme_mode"
const val MODE_NIGHT_NO = 0
const val MODE_NIGHT_YES = 1
const val MODE_NIGHT_AUTO_TIME = 2
const val MODE_NIGHT_FOLLOW_SYSTEM = 3
const val KEY_THEME_INDEX = "key_theme_index"
const val DEFAULT_THEME_INDEX = 4
const val KEY_TINT_TOOLBAR = "key_tint_toolbar"
const val KEY_MARK_WEEK_TIME = "key_mark_week_time"
const val KEY_MARK_WEEK = "key_mark_week"
const val KEY_END_WEEK = "key_end_week"
const val KEY_SCHOOL_YEAR = "key_school_year"
const val KEY_TERM = "key_term"
const val KEY_LOGGED = "key_logged"
const val KEY_AUTO_CHECK = "key_auto_check"
const val KEY_SID = "key_sid"
const val KEY_PWD = "key_pwd"
const val KEY_CW_SID = "key_cw_sid"
const val KEY_CW_PWD = "key_cw_pwd"
const val KEY_STUDENT = "key_student"
const val KEY_DOWNLOAD_VERSION = "key_download_version"
const val KEY_DOWNLOAD_URL = "key_download_url"
const val KEY_DOWNLOAD_SIZE = "key_download_size"

class Preferences(val app: Application) {

    private val preferName = "prefer_glut"

    private val sharedPrefer: SharedPreferences = app.getSharedPreferences(preferName, Context.MODE_PRIVATE)

    /**
     * 状态栏着色
     */
    var tintToolbar: Boolean
        get() = sharedPrefer.getBoolean(KEY_TINT_TOOLBAR, false)
        set(value) = sharedPrefer.edit { putBoolean(KEY_TINT_TOOLBAR, value) }

    /**
     * 登录状态
     */
    var logged: Boolean
        get() = sharedPrefer.getBoolean(KEY_LOGGED, false)
        set(value) = sharedPrefer.edit { putBoolean(KEY_LOGGED, value) }

    /**
     * 夜间主题
     */
    val nightTheme: Boolean
        get() {
            return when (themeMode) {
                MODE_NIGHT_YES -> true
                MODE_NIGHT_AUTO_TIME -> {
                    val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
                    (hour < 6) || (hour >= 18)
                }
                MODE_NIGHT_FOLLOW_SYSTEM -> {
                    val currentNightMode = app.resources.configuration.uiMode and
                            Configuration.UI_MODE_NIGHT_MASK
                    currentNightMode == Configuration.UI_MODE_NIGHT_YES
                }
                else -> false
            }

        }

    var themeMode: Int
        get() = sharedPrefer.getInt(KEY_THEME_MODE, MODE_NIGHT_NO)
        set(value) {
            sharedPrefer.edit { putInt(KEY_THEME_MODE, value) }
        }

    /**
     * 主题索引
     */
    var themeIndex: Int
        get() = sharedPrefer.getInt(KEY_THEME_INDEX, DEFAULT_THEME_INDEX)
        set(value) = sharedPrefer.edit { putInt(KEY_THEME_INDEX, value) }

    /**
     * 自动检查更新
     */
    var autoCheck: Boolean
        get() = sharedPrefer.getBoolean(KEY_AUTO_CHECK, true)
        set(value) = sharedPrefer.edit { putBoolean(KEY_AUTO_CHECK, value) }

    /**
     * 周记录时间
     */
    private var markWeekTime: Long
        get() = sharedPrefer.getLong(KEY_MARK_WEEK_TIME, System.currentTimeMillis())
        set(value) {
            val markCalendar = Calendar.getInstance()
            markCalendar.timeInMillis = value
            // 记录周一 00:00:00 的时间
            markCalendar.firstDayOfWeek = Calendar.MONDAY
            markCalendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
            markCalendar.clear(Calendar.HOUR)
            markCalendar.clear(Calendar.MINUTE)
            markCalendar.clear(Calendar.SECOND)
            sharedPrefer.edit { putLong(KEY_MARK_WEEK_TIME, markCalendar.timeInMillis) }
        }

    /**
     * 记录周数
     */
    var markWeek: Int
        get() = sharedPrefer.getInt(KEY_MARK_WEEK, 1)
        set(value) {
            sharedPrefer.edit { putInt(KEY_MARK_WEEK, value) }
            markWeekTime = System.currentTimeMillis()
        }

    /**
     * 结束周数
     */
    var endWeek: Int
        get() = sharedPrefer.getInt(KEY_END_WEEK, 30)
        set(value) {
            sharedPrefer.edit { putInt(KEY_END_WEEK, value) }
        }

    /**
     * 学年
     */
    var schoolYear: Int
        get() = sharedPrefer.getInt(KEY_SCHOOL_YEAR, Calendar.getInstance().get(Calendar.YEAR))
        set(value) {
            sharedPrefer.edit { putInt(KEY_SCHOOL_YEAR, value) }
        }
    /**
     * 学期
     * 1 春季，2 秋季
     */
    var term: Int
        get() = sharedPrefer.getInt(KEY_TERM, 1)
        set(value) {
            sharedPrefer.edit { putInt(KEY_TERM, value) }
        }

    /**
     * 当前周数
     */
    val currWeek: Int
        get() {
            val weekTotalMs = TimeUnit.DAYS.toMillis(7)
            val currCalendar = Calendar.getInstance()
            // 计算到周日时间
            currCalendar.firstDayOfWeek = Calendar.MONDAY
            currCalendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY)
            val timeOffset = currCalendar.timeInMillis - markWeekTime
            return markWeek + (timeOffset / weekTotalMs).toInt()
        }

    var sid: String
        get() = sharedPrefer.getString(KEY_SID, "") ?: ""
        set(value) = sharedPrefer.edit { putString(KEY_SID, value) }

    var pwd: String
        get() = sharedPrefer.getString(KEY_PWD, "") ?: ""
        set(value) = sharedPrefer.edit { putString(KEY_PWD, value) }

    var cwSid: String
        get() = sharedPrefer.getString(KEY_CW_SID, null) ?: sid
        set(value) = sharedPrefer.edit { putString(KEY_CW_SID, value) }

    var cwPwd: String
        get() = sharedPrefer.getString(KEY_CW_PWD, "") ?: ""
        set(value) = sharedPrefer.edit { putString(KEY_CW_PWD, value) }

    /**
     * 版本连接
     */
    var downloadUrl: String?
        get() = sharedPrefer.getString(KEY_DOWNLOAD_URL, null)
        set(value) = sharedPrefer.edit { putString(KEY_DOWNLOAD_URL, value) }

    /**
     * 正在下载的版本
     */
    var downloadVersion: String?
        get() = sharedPrefer.getString(KEY_DOWNLOAD_VERSION, null)
        set(value) = sharedPrefer.edit { putString(KEY_DOWNLOAD_VERSION, value) }

    /**
     * 已下载大小
     */
    var downloadSize: Long
        get() = sharedPrefer.getLong(KEY_DOWNLOAD_SIZE, 0)
        set(value) {
            sharedPrefer.edit { putLong(KEY_DOWNLOAD_SIZE, value) }
        }

    var student: Student?
        get() {
            val str = sharedPrefer.getString(KEY_STUDENT, null)
            str?.let { return fromJSON(str) }
            return null
        }
        set(value) = sharedPrefer.edit { putString(KEY_STUDENT, value?.let { toJSON(it) }) }

    fun registerListener(listener: SharedPreferences.OnSharedPreferenceChangeListener) {
        sharedPrefer.registerOnSharedPreferenceChangeListener(listener)
    }

    fun unregisterListener(listener: SharedPreferences.OnSharedPreferenceChangeListener) {
        sharedPrefer.unregisterOnSharedPreferenceChangeListener(listener)
    }

    companion object {

        private var INSTANCE: Preferences? = null

        fun init(app: Application) {
            if (INSTANCE == null) {
                INSTANCE = Preferences(app)
            }
        }

        fun getInstance(): Preferences {
            if (INSTANCE == null) {
                Log.e(Preferences::javaClass.name, "工具未初始化")
            }
            return INSTANCE!!
        }
    }
}

