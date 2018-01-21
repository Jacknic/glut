package com.jacknic.glut.page;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.jacknic.glut.R;
import com.tencent.stat.StatService;

import java.util.Properties;

/**
 *
 */

public class BrowserPage extends BasePage {

    private ProgressBar progressbar;
    private WebView webView;

    @Override
    protected int getLayoutId() {
        return R.layout.page_browser;
    }

    @Override
    public void onStart() {
        super.onStart();
        initViews();
        setWebView();
        Bundle bundle = getArguments();
        String url = bundle.getString("url");
        if (TextUtils.isEmpty(url)) {
            Toast.makeText(getContext(), "网址为空", Toast.LENGTH_SHORT).show();
            getActivity().onBackPressed();
        }
        webView.loadUrl(url);
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
                    Properties properties = new Properties();
                    properties.put("url", view.getUrl());
                    properties.put("title", view.getTitle());
                    StatService.trackCustomKVEvent(getContext(), "浏览网页内容", properties);
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
        webView = (WebView) page.findViewById(R.id.web_view);
        progressbar = (ProgressBar) page.findViewById(R.id.progressbar);
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_page_browser, menu);
    }

    @Override
    void refresh() {
        webView.reload();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_back:
                if (webView.canGoBack()) {
                    webView.goBack();
                }
                return true;
            case R.id.menu_open_with:
                String url = webView.getUrl();
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                getContext().startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
