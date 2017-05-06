package com.jacknic.glut;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.cookie.store.PersistentCookieStore;
import com.tencent.stat.StatService;

/**
 * application
 */

public class App extends android.app.Application {
    @Override
    public void onCreate() {
        super.onCreate();
        OkGo.init(this);
        OkGo.getInstance().getOkHttpClientBuilder().followRedirects(false);
        OkGo.getInstance().setCookieStore(new PersistentCookieStore());
        StatService.setContext(this);
        StatService.registerActivityLifecycleCallbacks(this);
    }
}
