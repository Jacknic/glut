package com.jacknic.glut.page;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.jacknic.glut.R;
import com.jacknic.glut.util.ViewUtil;

/**
 * 内置浏览器
 */

public class BrowserPage extends Fragment {

    private ProgressBar progressbar;
    private WebView webView;
    private View page;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        page = inflater.inflate(R.layout.page_browser, container, false);
        ViewUtil.setTitle(getContext(), "");
        initViews();
        setWebView();
        Bundle bundle = getArguments();
        String url = bundle.getString("url");
        if (TextUtils.isEmpty(url)) {
            Toast.makeText(getContext(), "网址为空", Toast.LENGTH_SHORT).show();
            getActivity().onBackPressed();
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
        webView = (WebView) page.findViewById(R.id.web_view);
        progressbar = (ProgressBar) page.findViewById(R.id.progressbar);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_page_browser, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_refresh:
                webView.reload();
                return true;
            case R.id.menu_back:
                if (webView.canGoBack()) {
                    webView.goBack();
                }
                return true;
            case R.id.menu_open_with:
                String url = webView.getUrl();
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                getContext().startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }
}
