package com.jacknic.glut.page;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.jacknic.glut.R;
import com.jacknic.glut.stacklibrary.RootFragment;
import com.jacknic.glut.util.ViewUtil;

/**
 * 内置浏览器
 */

public class BrowserPage extends RootFragment {

    private ProgressBar progressbar;
    private WebView webView;
    private View page;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        page = inflater.inflate(R.layout.page_browser, container, false);
        initViews();
        setWebView();
        Bundle bundle = getArguments();
        String url = bundle.getString("url");
        if (TextUtils.isEmpty(url)) {
            Toast.makeText(getContext(), "网址为空", Toast.LENGTH_SHORT).show();
            close();
        }
        webView.loadUrl(url);
        return page;
    }


    /**
     * 设置webView控件
     */
    @SuppressLint("SetJavaScriptEnabled")
    private void setWebView() {
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDisplayZoomControls(false);
        webSettings.setSupportZoom(true);
        //内置浏览控件设置
        webView.setWebViewClient(new WebViewClient());
        webView.setWebChromeClient(new WebChromeClient() {

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress < 100) {
                    progressbar.setVisibility(View.VISIBLE);
                } else if (newProgress == 100) {
                    progressbar.setVisibility(View.GONE);
                }
                progressbar.setProgress(newProgress);

                super.onProgressChanged(view, newProgress);
            }

        });
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
    }

    /**
     * 初始化视图控件
     */
    private void initViews() {
        ViewUtil.setTitle(getRoot(), "关闭");
        webView = (WebView) page.findViewById(R.id.web_view);
        progressbar = (ProgressBar) page.findViewById(R.id.progressbar);
    }


    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
//            super.onBackPressed();
        }
    }
}
