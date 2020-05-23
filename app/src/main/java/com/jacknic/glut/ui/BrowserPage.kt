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
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.webkit.WebSettingsCompat
import androidx.webkit.WebViewFeature
import com.jacknic.glut.R
import com.jacknic.glut.base.BasePage
import com.jacknic.glut.data.util.URL_GLUT_CW
import com.jacknic.glut.data.util.URL_GLUT_JW
import com.jacknic.glut.databinding.PageBrowserBinding
import com.jacknic.glut.util.openLink
import com.jacknic.glut.util.toast
import com.jacknic.glut.viewmodel.WebBrowserViewModel
import com.jacknic.glut.widget.CwcLoginDialogFragment
import com.jacknic.glut.widget.JwcLoginDialogFragment


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
    private val vm by viewModels<WebBrowserViewModel>()
    private var isJwSite = false
    private var isCwSite = false
    private val supportSchemes = listOf("http", "https", "file")

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
                bind.webView.setBackgroundColor(Color.TRANSPARENT)
                WebSettingsCompat.setForceDark(bind.webView.settings, WebSettingsCompat.FORCE_DARK_ON)
            } else {
                bind.webView.alpha = 0.8f
            }
        }
        val url = args.url
        loadFirstUrl(url)
        activity?.apply {
            this.title = args.title
            onBackPressedDispatcher.addCallback(this@BrowserPage, backPressedCallback)
        }
    }

    private fun loadFirstUrl(url: String?) {
        val loadUrlAction = Runnable { bind.webView.loadUrl(url) }
        if (url?.startsWith(URL_GLUT_JW) == true) {
            isJwSite = true
            vm.checkJw()
        }
        if (url?.startsWith(URL_GLUT_CW) == true) {
            isCwSite = true
            vm.checkCw()
        }
        vm.apply {
            needLogin.observe(viewLifecycleOwner, Observer {
                if (it) {
                    bind.webView.stopLoading()
                    if (isJwSite) JwcLoginDialogFragment.show(childFragmentManager, loadUrlAction)
                    if (isCwSite) CwcLoginDialogFragment.show(childFragmentManager, loadUrlAction)
                    needLogin.postValue(false)
                }
            })
        }
        loadUrlAction.run()
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
            webViewClient = object : WebViewClient() {

                override fun shouldOverrideUrlLoading(webView: WebView, request: WebResourceRequest): Boolean {
                    val uri = request.url
                    if (uri.scheme !in supportSchemes) {
                        requireContext().openLink(uri.toString())
                        return true
                    }
                    return super.shouldOverrideUrlLoading(webView, request)
                }
            }
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
                val url = bind.webView.url
                if (url.startsWith("file")) {
                    requireContext().toast(R.string.inner_page_tips)
                } else {
                    val actionTitle = getString(R.string.action_title_choice_browser)
                    requireContext().openLink(url, actionTitle)
                }
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