package com.jacknic.glut

import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.text.HtmlCompat
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.jacknic.glut.data.model.Version
import com.jacknic.glut.data.util.URL_RELEASE_LOG
import com.jacknic.glut.service.UpdateService
import com.jacknic.glut.util.*
import com.jacknic.glut.viewmodel.AppViewModel
import kotlinx.android.synthetic.main.activity_main.*


/**
 * 主界面
 */
class MainActivity : AppCompatActivity() {

    private val topIds = setOf(R.id.loginPage, R.id.homePage)
    private val appBarConfiguration = AppBarConfiguration.Builder(topIds).build()
    private val keyTitle = "key_title"
    private val prefer = Preferences.getInstance()
    // private val permissions = arrayOf(WRITE_EXTERNAL_STORAGE, READ_PHONE_STATE, ACCESS_COARSE_LOCATION)
    private val appVm by viewModels<AppViewModel>()
    private var updateDialog: AlertDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        setupTheme()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupToolbar()
        checkAgree()
        if (prefer.autoCheck && appVm.version.value == null) {
            appVm.checkUpdate()
        }
        appVm.version.observe(this, Observer { showUpdateDialog(it) })
    }

    private fun checkAgree() {
        if (!prefer.agreed) {
            val html = assets.open("user_privacy.html").bufferedReader().readText()
            AlertDialog.Builder(this)
                .setMessage(HtmlCompat.fromHtml(html, HtmlCompat.FROM_HTML_MODE_COMPACT))
                .setPositiveButton(R.string.agree) { dialog, _ ->
                    prefer.agreed = true
                    dialog.dismiss()
                }
                .setNegativeButton(R.string.reject) { dialog, _ ->
                    finish()
                    dialog.dismiss()
                }.show()
        }
    }

    private fun showUpdateDialog(version: Version) {
        if (updateDialog?.isShowing == true) {
            return
        }
        val description = version.let { getString(R.string.new_version_description, it.versionName, it.date, it.info) }
        val builder = AlertDialog.Builder(this)
            .setTitle(R.string.new_version)
            .setMessage(description)
            .setNegativeButton(android.R.string.cancel) { dialog, _ ->
                dialog.dismiss()
            }
            .setNeutralButton(R.string.download_with_browser) { dialog, _ ->
                openLink(URL_RELEASE_LOG, getString(R.string.action_title_download))
                dialog.dismiss()
            }
        builder.setPositiveButton(R.string.update_now) { dialog, _ ->
            dialog.dismiss()
            // val url = "https://github.com/Jacknic/glut/releases/download/v5.4/app-github-release.apk"
            UpdateService.download(this, version.downloadUrl)
        }
        updateDialog = builder.show()
    }

    private fun setupTheme() {
        prefer.apply {
            if (nightTheme) {
                setTheme(THEME_LIST_NIGHT[themeIndex])
            } else {
                setTheme(THEME_LIST[themeIndex])
            }
        }
    }

    private fun setupToolbar() {
        val isTintToolbar = prefer.tintToolbar
        val toolbar: Toolbar = if (isTintToolbar) toolbarTint else toolbarNormal
        toolbar.fitsSystemWindows = true
        toolbar.visibility = View.VISIBLE
        setSupportActionBar(toolbar)
        val navCtrl = findNavController(R.id.pager)
        val colorAttr = if (isTintToolbar) R.attr.colorPrimary else R.attr.colorBackgroundFloating
        val themeColor = resolveColor(colorAttr)
        window.statusBarColor = themeColor
        /// window.navigationBarColor = themeColor
        if (isTintToolbar) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
        } else if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) {
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        }
        toolbar.setupWithNavController(navCtrl, appBarConfiguration)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        val title = supportActionBar?.title
        outState.putCharSequence(keyTitle, title)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        val title = savedInstanceState.getCharSequence(keyTitle, "")
        setTitle(title)
    }

}
