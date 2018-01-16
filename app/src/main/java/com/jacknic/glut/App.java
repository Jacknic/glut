package com.jacknic.glut;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.cookie.store.PersistentCookieStore;
import com.tencent.stat.StatCrashReporter;
import com.tencent.stat.StatService;

import java.util.concurrent.TimeUnit;

/**
 * application
 */

public class App extends android.app.Application {
    @Override
    public void onCreate() {
        super.onCreate();
        OkGo.init(this);
        //配置不跳转,超时6秒
        OkGo.getInstance().getOkHttpClientBuilder()
                .followRedirects(false)
                .connectTimeout(6, TimeUnit.SECONDS);
        //默认cookie存储
        OkGo.getInstance().setCookieStore(new PersistentCookieStore());
        StatService.setContext(this);
        StatService.registerActivityLifecycleCallbacks(this);
        StatCrashReporter crashReporter = StatCrashReporter.getStatCrashReporter(this);
        // 开启异常时的实时上报
        crashReporter.setEnableInstantReporting(true);
        // 开启java异常捕获
        crashReporter.setJavaCrashHandlerStatus(true);
    }
}
