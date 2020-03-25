package com.jacknic.glut.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.util.TypedValue
import android.view.Gravity
import android.widget.Toast
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.annotation.StringRes
import androidx.core.content.FileProvider
import com.jacknic.glut.BuildConfig
import com.jacknic.glut.R
import com.jacknic.glut.service.CourseWidgetProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

private var toast: Toast? = null

/**
 * 提示toast
 **/
fun Context.toast(message: String?, duration: Int = Toast.LENGTH_SHORT) {
    toast?.cancel()
    toast = Toast.makeText(this, message, duration).apply {
        setGravity(Gravity.CENTER_HORIZONTAL, 0, 0)
        show()
    }
}

fun Context.toast(
    @StringRes stringRes: Int,
    duration: Int = Toast.LENGTH_SHORT
) = toast(getString(stringRes), duration)

suspend fun Context.toastOnMain(message: String?, duration: Int = Toast.LENGTH_SHORT) {
    withContext(Dispatchers.Main) {
        toast(message, duration)
    }
}

fun cancelToast() = toast?.cancel()

suspend fun cancelToastOnMain() = withContext(Dispatchers.Main) { toast?.cancel() }

@ColorInt
fun Context.resolveColor(@AttrRes colorAttrRes: Int): Int {
    val colorValue = TypedValue()
    theme.resolveAttribute(colorAttrRes, colorValue, true)
    return colorValue.data
}

/**
 * 主题颜色
 */
@ColorInt
fun Context.themeColor() = resolveColor(R.attr.colorPrimary)

/**
 * 更新桌面控件
 **/
fun Context.updateWidget() {
    val updateIntent = Intent(this, CourseWidgetProvider::class.java)
    updateIntent.action = CourseWidgetProvider.ACTION_UPDATE_COURSE_WIDGET
    sendBroadcast(updateIntent)
}

/**
 * 调用外部工具打开链接
 **/
fun Context.openLink(url: String, actionTitle: String = "选择") {
    val uri = Uri.parse(url)
    val intent = viewIntent()
    intent.data = uri
    openWith(intent, actionTitle)
}

/**
 * 打开意图
 */
fun Context.openWith(intent: Intent, actionTitle: String) {
    val resolveActivity = intent.resolveActivity(packageManager)
    if (resolveActivity != null) {
        val createChooser = Intent.createChooser(intent, actionTitle)
        createChooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(createChooser)
    } else {
        Toast.makeText(this, "未找到应用执行此操作！", Toast.LENGTH_SHORT).show()
    }
}

/**
 * 打开文件
 **/
fun Context.openFile(file: File, actionTitle: String = "打开文件") =
    openWith(fileIntent(file), actionTitle)

/**
 * 浏览意图
 */
fun viewIntent(): Intent {
    val intent = Intent(Intent.ACTION_VIEW)
    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
    return intent
}

/**
 * 文件意图
 */
fun Context.fileIntent(file: File, type: String? = null): Intent {
    // 判断是否是Android N以及更高的版本
    val intent = viewIntent()
    val uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".fileProvider", file)
    } else {
        Uri.fromFile(file)
    }
    intent.setDataAndType(uri, type)
    return intent
}

/**
 * apk 安装意图
 */
fun Context.installIntent(file: File): Intent {
    val type = "application/vnd.android.package-archive"
    return fileIntent(file, type)
}