package com.jacknic.glut.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.webkit.*
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.webkit.WebSettingsCompat
import androidx.webkit.WebViewFeature
import com.jacknic.glut.R
import com.jacknic.glut.base.BasePage
import com.jacknic.glut.databinding.PageBrowserBinding
import com.jacknic.glut.util.openLink
import com.jacknic.glut.util.toast


/**
 * 内置浏览器页面
 *
 * @author Jacknic
 */
class BrowserPage : BasePage<PageBrowserBinding>() {

    override val layoutResId = R.layout.page_browser
    override val menuResId = R.menu.browser_actions
    private lateinit var backPressedCallback: OnBackPressedCallback
    private var mFilePathCallback: ValueCallback<Array<Uri>>? = null
    private val codeChoiceFile = 10000

    @SuppressLint("RestrictedApi", "RequiresFeature")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val args: BrowserPageArgs = try {
            BrowserPageArgs.fromBundle(arguments!!)
        } catch (e: Exception) {
            requireContext().toast(R.string.browser_argument_error)
            null
        } ?: return
        setupWebView()
        backPressedCallback = object : OnBackPressedCallback(false) {
            override fun handleOnBackPressed() = bind.webView.goBack()
        }
        if (prefer.nightTheme) {
            val supported = WebViewFeature.isFeatureSupported(WebViewFeature.FORCE_DARK)
            if (supported) {
                WebSettingsCompat.setForceDark(bind.webView.settings, WebSettingsCompat.FORCE_DARK_ON)
            } else {
                bind.webView.alpha = 0.8f
            }
        }
        bind.webView.loadUrl(args.url)
        activity?.apply {
            this.title = args.title
            onBackPressedDispatcher.addCallback(this@BrowserPage, backPressedCallback)
        }
    }

    /**
     * 设置webView控件
     */
    @SuppressLint("SetJavaScriptEnabled")
    private fun setupWebView() {
        bind.webView.settings.apply {
            javaScriptEnabled = true
            displayZoomControls = false
            javaScriptCanOpenWindowsAutomatically = true
            domStorageEnabled = true
            useWideViewPort = true
            allowFileAccess = true
            mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
            blockNetworkImage = false
            loadsImagesAutomatically = true
            builtInZoomControls = true
            setSupportZoom(true)
        }

        // 内置浏览控件设置
        bind.webView.apply {
            setBackgroundColor(Color.TRANSPARENT)
            webViewClient = WebViewClient()
            webChromeClient = object : WebChromeClient() {
                override fun onReceivedTitle(view: WebView, title: String) {
                    if (title != view.url && isResumed) {
                        activity?.title = title
                    }
                }

                override fun onProgressChanged(view: WebView, newProgress: Int) {
                    val visible = newProgress < 100
                    if (visible) {
                        bind.progressBar.visibility = View.VISIBLE
                        bind.progressBar.progress = newProgress
                    } else {
                        backPressedCallback.isEnabled = canGoBack()
                        bind.progressBar.visibility = View.GONE
                    }
                }

                override fun onShowFileChooser(webView: WebView?, filePathCallback: ValueCallback<Array<Uri>>?, fileChooserParams: FileChooserParams?): Boolean {
                    openFileChooser()
                    mFilePathCallback = filePathCallback
                    return true
                }
            }
            setDownloadListener { url, _, _, _, _ ->
                AlertDialog.Builder(requireContext())
                    .setTitle(R.string.tips_file_download)
                    .setNegativeButton(android.R.string.cancel, null)
                    .setPositiveButton(android.R.string.ok) { dialog, _ ->
                        dialog.dismiss()
                        requireContext().openLink(url, getString(R.string.action_title_file_download))
                    }.show()
            }
        }
    }

    /**
     * 选择本地文件
     */
    private fun openFileChooser() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "*/*"
        val actionTitle = getString(R.string.action_title_file_upload)
        startActivityForResult(Intent.createChooser(intent, actionTitle), codeChoiceFile)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == codeChoiceFile) {
            if (data != null && resultCode == Activity.RESULT_OK) {
                data.apply {
                    val uri = getData()
                    val resultData: Array<Uri>? = if (uri == null) null else arrayOf(uri)
                    mFilePathCallback?.onReceiveValue(resultData)
                }
            } else {
                mFilePathCallback?.onReceiveValue(null)
                mFilePathCallback = null
            }
        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_open_with -> {
                val actionTitle = getString(R.string.action_title_choice_browser)
                requireContext().openLink(bind.webView.url, actionTitle)
                return true
            }
            R.id.action_refresh -> {
                bind.webView.reload()
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onDestroyView() {
        bind.webView.stopLoading()
        super.onDestroyView()
    }
}