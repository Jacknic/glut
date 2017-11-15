package com.jacknic.glut;

import com.jacknic.glut.util.ActivityLifecycleListener;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.cookie.store.PersistentCookieStore;
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
        registerActivityLifecycleCallbacks(new ActivityLifecycleListener());
    }
}
