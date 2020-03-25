package com.jacknic.glut.service

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.JobIntentService
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.net.toUri
import com.jacknic.glut.R
import com.jacknic.glut.data.net.api.AppApi
import com.jacknic.glut.data.util.FILE_NAME_UPDATE_APK
import com.jacknic.glut.data.util.URL_RELEASE_LOG
import com.jacknic.glut.data.util.getFmtBytes
import com.jacknic.glut.util.NotifyChannelUtils
import com.jacknic.glut.util.Preferences
import com.jacknic.glut.util.installIntent
import com.jacknic.glut.util.viewIntent
import retrofit2.Retrofit
import java.io.File
import java.io.IOException
import java.io.RandomAccessFile

/**
 * 应用更新服务
 *
 * @author Jacknic
 */
class UpdateService : JobIntentService() {

    private var lastTime = 0L
    private val updateFile by lazy { File(cacheDir, FILE_NAME_UPDATE_APK) }
    private val managerCompat by lazy { NotificationManagerCompat.from(this) }
    private val prefer = Preferences.getInstance()

    override fun onHandleWork(intent: Intent) {
        when (intent.action) {
            ACTION_DOWNLOAD -> {
                val url = intent.getStringExtra(KEY_URL) ?: return
                val before = getNotifyBuilder("等待下载文件", "")
                    .setProgress(0, 0, true)
                    .build()
                managerCompat.notify(DOWNLOAD_JOB_ID, before)
                try {
                    downloadFile(url)
                    sendDoneNotification()
                } catch (e: Exception) {
                    e.printStackTrace()
                    sendErrorNotification()
                }
            }
        }
    }

    @Throws(Exception::class)
    private fun downloadFile(url: String) {
        if (!updateFile.exists()) {
            updateFile.createNewFile()
            prefer.downloadSize = 0
        }
        val raFile = RandomAccessFile(updateFile, "rwd")
        if (url != prefer.downloadUrl) {
            prefer.downloadUrl = url
            prefer.downloadSize = 0
            raFile.setLength(0)
        }
        var totalSize = raFile.length()
        var savedSize = prefer.downloadSize
        val range = if (totalSize > 0) "bytes=${savedSize}-" else ""
        val response = appApi.download(url, range).execute()
        val body = response.body()
        if (body != null) {
            val available = body.contentLength()
            if (totalSize == 0L) {
                raFile.setLength(available)
                totalSize = available
            }
            val byteStream = body.byteStream()
            val buff = ByteArray(8 * 1024)
            raFile.seek(savedSize)
            while (savedSize < totalSize) {
                val len = byteStream.read(buff)
                if (len == -1) {
                    break
                }
                raFile.write(buff, 0, len)
                savedSize += len
                prefer.downloadSize = savedSize
                sendProgressNotification(totalSize, savedSize)
            }
            byteStream.close()
            raFile.close()
            if (savedSize != totalSize) {
                updateFile.delete()
                throw IOException("文件下载错误")
            }
        }
    }

    private fun getNotifyBuilder(title: String, content: String): NotificationCompat.Builder {
        return NotificationCompat.Builder(this, NotifyChannelUtils.CHANNEL_DOWNLOAD)
            .setContentTitle(title)
            .setContentText(content)
            .setSmallIcon(R.drawable.ic_pull)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setOnlyAlertOnce(true)
    }

    private fun sendProgressNotification(total: Long, progress: Long) {
        val currentTimeMillis = System.currentTimeMillis()
        // 间隔2秒更新
        val delayed = currentTimeMillis - lastTime < 2000
        if (delayed) {
            return
        }
        lastTime = currentTimeMillis
        val percent = (progress * 100 / total).toInt()
        val totalSize = getFmtBytes(total, false)
        val currentSize = getFmtBytes(progress, false)
        val title = "正在下载文件...${percent}%"
        val content = "${currentSize}/${totalSize}"
        val progressNotify = getNotifyBuilder(title, content)
            .setProgress(100, percent, false)
            .setAutoCancel(false)
            .build()
        managerCompat.notify(DOWNLOAD_JOB_ID, progressNotify)
    }

    private fun sendDoneNotification() {
        val intent = installIntent(updateFile)
        startActivity(intent)
        val installIntent = PendingIntent.getActivity(this, DOWNLOAD_JOB_ID, intent, PendingIntent.FLAG_CANCEL_CURRENT)
        val done = getNotifyBuilder("下载完成", "点击安装")
            .setSmallIcon(R.drawable.ic_check_circle)
            .setContentIntent(installIntent)
            .setProgress(0, 0, false)
            .build()
        managerCompat.notify(DOWNLOAD_JOB_ID, done)
    }

    private fun sendErrorNotification() {
        val viewIntent = viewIntent()
        viewIntent.data = URL_RELEASE_LOG.toUri()
        val openPending = PendingIntent.getActivity(this, DOWNLOAD_JOB_ID, viewIntent, PendingIntent.FLAG_CANCEL_CURRENT)
        val openBrowser = NotificationCompat.Action(R.drawable.ic_public, "浏览器下载", openPending)
        val errorNote = getNotifyBuilder("下载文件出错！", "")
            .setProgress(0, 0, false)
            .setSmallIcon(R.drawable.ic_delete)
            .addAction(openBrowser)
            .setAutoCancel(true)
            .build()
        managerCompat.notify(DOWNLOAD_JOB_ID, errorNote)
    }

    companion object {
        private const val KEY_URL = "KEY_URL"
        private const val ACTION_DOWNLOAD = "ACTION_DOWNLOAD"
        private const val DOWNLOAD_JOB_ID = 10000

        private val appApi by lazy {
            Retrofit.Builder()
                .baseUrl("http://localhost/")
                .build()
                .create(AppApi::class.java)
        }

        fun download(context: Context, fileUrl: String) {
            val intent = downloadIntent(context, fileUrl)
            enqueueWork(context, UpdateService::class.java, DOWNLOAD_JOB_ID, intent)
        }

        private fun downloadIntent(context: Context, fileUrl: String): Intent {
            return Intent(context, UpdateService::class.java).apply {
                action = ACTION_DOWNLOAD
                putExtra(KEY_URL, fileUrl)
            }
        }
    }
}