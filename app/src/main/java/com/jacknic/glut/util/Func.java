package com.jacknic.glut.util;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;

import com.jacknic.glut.R;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.AbsCallback;
import com.lzy.okgo.callback.StringCallback;

import java.io.File;

import okhttp3.Call;
import okhttp3.Response;

import static android.content.ContentValues.TAG;

/**
 * 常用函数
 */

public final class Func {
    /**
     * 获取整型数
     *
     * @param str_value
     * @param defaultValue
     * @return
     */
    public static int getInt(String str_value, int defaultValue) {
        int value = defaultValue;
        try {
            value = Integer.parseInt(str_value);
        } catch (Exception e) {
            Log.d(TAG, "getInt: " + e.getMessage());
        }
        return value;
    }

    /**
     * 课程节数转序号
     *
     * @param index
     * @return
     */
    public static String courseIndexToStr(int index) {
        StringBuilder stringBuilder = new StringBuilder();
        if (index > 4) {
            switch (index) {
                case 5:
                    stringBuilder.append("中午 1");
                    break;
                case 6:
                    stringBuilder.append("中午 2");
                    break;
                default:
                    index -= 2;
                    stringBuilder.append(index);
                    break;
            }
        } else {
            stringBuilder.append(index);
        }
        return stringBuilder.toString();
    }

    /**
     * 递归删除文件
     *
     * @param file 文件
     */
    public static void deleteFile(File file) {
        if (file.isDirectory()) {
            for (File fileItem : file.listFiles()) {
                deleteFile(fileItem);
            }
            file.delete();
        } else {
            file.delete();
        }
    }

    /**
     * 检验教务用户登录状态
     *
     * @param callback 回调
     */
    public static void checkLoginStatus(@NonNull final AbsCallback callback) {
        OkGo.get("http://202.193.80.58:81/academic/showHeader.do").execute(new StringCallback() {
            @Override
            public void onSuccess(String s, Call call, Response response) {
                callback.onSuccess(s, call, response);
            }

            @Override
            public void onError(Call call, Response response, Exception e) {
                callback.onError(call, response, e);
            }

            @Override
            public void onAfter(String s, Exception e) {
                callback.onAfter(s, e);
            }
        });
    }

    /**
     * 显示刷新按钮
     */
    public static void showRefreshView(Activity activity, @NonNull final View.OnClickListener onClickListener) {
        ImageView iv_setting = (ImageView) activity.findViewById(R.id.iv_setting);
        iv_setting.setVisibility(View.VISIBLE);
        iv_setting.setImageResource(R.drawable.ic_autorenew);

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickListener.onClick(v);
                RotateAnimation rotateAnimation = new RotateAnimation(0, 360 * 2, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                rotateAnimation.setDuration(800L);
                v.startAnimation(rotateAnimation);
            }
        };
        iv_setting.setOnClickListener(listener);
    }
}
