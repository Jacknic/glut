package com.jacknic.glut.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.jacknic.glut.R;

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
        setStatusView();
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
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, final String url) {
                if (!url.startsWith("http://") && !url.startsWith("https://"))
                    view.loadUrl("http://" + url);
                else
                    view.loadUrl(url);
                return true;
            }

        });
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
        ImageView iv_setting = (ImageView) findViewById(R.id.iv_setting);
        iv_setting.setVisibility(View.VISIBLE);
        iv_setting.setImageResource(R.drawable.ic_autorenew);
        iv_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RotateAnimation rotateAnimation = new RotateAnimation(0, 360 * 2, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                rotateAnimation.setDuration(1000L);
                v.startAnimation(rotateAnimation);
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
