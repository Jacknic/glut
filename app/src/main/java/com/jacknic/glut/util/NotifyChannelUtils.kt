package com.jacknic.glut.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationManagerCompat

/**
 * 通知工具
 *
 * @author Jacknic
 */
object NotifyChannelUtils {

    const val CHANNEL_DOWNLOAD = "CHANNEL_DOWNLOAD"

    fun registerChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= 26) {
            val managerCompat = NotificationManagerCompat.from(context)
            val notificationChannel = NotificationChannel(CHANNEL_DOWNLOAD, "文件下载", NotificationManager.IMPORTANCE_DEFAULT)
            managerCompat.createNotificationChannel(notificationChannel)
        }
    }
}