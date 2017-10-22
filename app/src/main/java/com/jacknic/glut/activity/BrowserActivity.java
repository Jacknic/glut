package com.jacknic.glut.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.jacknic.glut.R;
import com.jacknic.glut.util.ViewUtil;

/**
 * 内置浏览器
 */

public class BrowserActivity extends BaseActivity {

    private ProgressBar progressbar;
    private WebView webView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browser);
        initViews();
        setWebView();
        Intent intent = getIntent();
        String url = intent.getAction();
        webView.loadUrl(url);
    }

    /**
     * 设置webView控件
     */
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
                    invalidateOptionsMenu();
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
        TextView toolbar_title = (TextView) findViewById(R.id.tv_toolbar_title);
        ViewUtil.showBackIcon(this);
        ViewUtil.showRightImageView(this, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                webView.reload();
            }
        });
        toolbar_title.setText("关闭");
        toolbar_title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        webView = (WebView) findViewById(R.id.web_view);
        progressbar = (ProgressBar) findViewById(R.id.progressbar);
    }

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }
}
