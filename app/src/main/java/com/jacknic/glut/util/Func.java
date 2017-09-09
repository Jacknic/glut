package com.jacknic.glut.util;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.jacknic.glut.R;
import com.jacknic.glut.model.bean.VersionBean;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.AbsCallback;
import com.lzy.okgo.callback.FileCallback;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.request.BaseRequest;

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

    /**
     * 检测更新
     */
    public static void checkUpdate(final FragmentActivity activity, final boolean showTips) {

        final PackageInfo packageInfo = getPackageInfo(activity);
        if (packageInfo == null) return;
        OkGo.get("https://raw.githubusercontent.com/Jacknic/glut/master/version.json").readTimeOut(15000L).execute(new StringCallback() {
            Toast tips = Toast.makeText(activity, "检查更新中...", Toast.LENGTH_LONG);

            @Override
            public void onBefore(BaseRequest request) {
                if (showTips) tips.show();
            }

            @Override
            public void onSuccess(String s, Call call, Response response) {
                int appVersionCode = packageInfo.versionCode;
                System.out.println("应用的版本号：" + appVersionCode);
                VersionBean versionBean;
                try {
                    versionBean = JSONObject.parseObject(s, VersionBean.class);
                    if (appVersionCode < versionBean.getVersionCode()) {
                        final String downloadUrl = versionBean.getDownloadUrl();
                        AlertDialog alertDialog = new AlertDialog.Builder(activity)
                                .setTitle("发现新版本！")
                                .setMessage("新版本：" + versionBean.getVersionName()
                                        + "\n发布时间：" + versionBean.getDate()
                                        + "\n更新描述：" + versionBean.getInfo()
                                ).setNegativeButton(android.R.string.cancel, null)
                                .setPositiveButton("立即更新", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        downloadApk(downloadUrl, activity);
                                    }
                                }).create();
                        tips.cancel();
                        alertDialog.show();
                    } else {
                        tips.setText("已是最新版本 ");
                        if (showTips) tips.show();
                    }
                } catch (Exception e) {
                    tips.setText("检查更新失败！");
                    if (showTips) tips.show();
                    System.err.println(e.getMessage());
                }

            }

            @Override
            public void onError(Call call, Response response, Exception e) {
                super.onError(call, response, e);
                tips.setText("检查更新失败！");
                if (showTips) tips.show();
            }
        });


    }

    /**
     * 获取包版本信息
     *
     * @param activity
     * @return
     */
    public static PackageInfo getPackageInfo(FragmentActivity activity) {
        PackageManager pm = activity.getPackageManager();
        try {
            return pm.getPackageInfo(activity.getPackageName(), PackageManager.GET_CONFIGURATIONS);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 下载安装包
     *
     * @param url 更新包下载地址
     */
    public static void downloadApk(String url, final Activity activity) {
        final AlertDialog dialog = new AlertDialog.Builder(activity)
                .setTitle("更新包下载中")
                .setMessage("下载进度... 0%")
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        OkGo.getInstance().cancelAll();
                    }
                })
                .setPositiveButton("后台下载", null).create();


        OkGo.get(url).execute(new FileCallback("glut_update.apk") {
            @Override
            public void onBefore(BaseRequest request) {
                super.onBefore(request);
                dialog.show();
            }

            @Override
            public void onSuccess(File file, Call call, Response response) {
                //下载完成启动安装
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
                activity.startActivity(intent);
            }

            @Override
            public void downloadProgress(long currentSize, long totalSize, float progress, long networkSpeed) {
                dialog.setMessage(String.format("下载进度... %.1f", progress * 100) + "%");
            }

            @Override
            public void onAfter(File file, Exception e) {
                super.onAfter(file, e);
                dialog.dismiss();
            }
        });
    }
}
