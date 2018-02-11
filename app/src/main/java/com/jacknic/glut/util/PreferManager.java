package com.jacknic.glut.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * prefer管理器
 */

public class PreferManager {
    private static SharedPreferences prefer;
    private Context mContext;
    private static PreferManager preferManager;

    private PreferManager() {
    }

    public static void init(Context context) {
        preferManager = new PreferManager();
        preferManager.mContext = context;
    }

    public static SharedPreferences getPrefer() {
        if (prefer == null) {
            prefer = preferManager.mContext.getSharedPreferences(Config.PREFER, Context.MODE_PRIVATE);
        }
        return prefer;
    }
}
