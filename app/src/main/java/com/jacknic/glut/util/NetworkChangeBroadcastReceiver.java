package com.jacknic.glut.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.lzy.okgo.OkGo;

/**
 * 网络不稳定时取消请求
 */
public class NetworkChangeBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        OkGo.getInstance().cancelAll();
    }
}
