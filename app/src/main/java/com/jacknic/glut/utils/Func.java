package com.jacknic.glut.utils;

import android.util.Log;

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
}
