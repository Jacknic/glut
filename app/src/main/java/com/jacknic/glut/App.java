package com.jacknic.glut;

import android.util.Log;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.cookie.store.PersistentCookieStore;
import com.tencent.bugly.crashreport.CrashReport;

/**
 * application
 */

public class App extends android.app.Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("OkGo", "--------------------------------------！");
        Log.d("OkGo", "OkGo执行初始化！");
        OkGo.init(this);
        OkGo.getInstance().getOkHttpClientBuilder().followRedirects(false);
        OkGo.getInstance().setCookieStore(new PersistentCookieStore());
        CrashReport.initCrashReport(getApplicationContext(), "245dec660f", true);
    }
}
