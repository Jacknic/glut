package com.jacknic.glut.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.lzy.okgo.OkGo;

/**
 * prefer管理器
 */

public class PreferManager {
    private static SharedPreferences prefer;

    private PreferManager() {
    }

    public static SharedPreferences getPrefer() {
        if (prefer == null) {
            prefer = OkGo.getContext().getSharedPreferences(Config.PREFER, Context.MODE_PRIVATE);
        }
        return prefer;
    }
}
