package com.jacknic.glut.util;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

/**
 * Activity生命周期监听
 */

public class ActivityLifecycleListener implements Application.ActivityLifecycleCallbacks {
    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

    }

    @Override
    public void onActivityStarted(Activity activity) {
        ActivityManager.getManager().remove(activity);
    }

    @Override
    public void onActivityResumed(Activity activity) {
    }

    @Override
    public void onActivityPaused(Activity activity) {
    }

    @Override
    public void onActivityStopped(Activity activity) {
        ActivityManager.getManager().add(activity);
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        ActivityManager.getManager().remove(activity);
    }
}
