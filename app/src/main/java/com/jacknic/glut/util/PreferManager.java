package com.jacknic.glut.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * prefer管理器
 */

public class PreferManager {
    private static SharedPreferences prefer;

    private PreferManager() {
    }

    public static void init(Context context) {
        prefer = context.getSharedPreferences(Config.PREFER, Context.MODE_PRIVATE);
    }

    public static SharedPreferences getPrefer() {
        return prefer;
    }
}
