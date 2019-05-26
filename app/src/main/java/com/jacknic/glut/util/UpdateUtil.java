package com.jacknic.glut.util;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.jacknic.glut.BuildConfig;
import com.jacknic.glut.model.bean.VersionBean;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.FileCallback;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.request.BaseRequest;

import java.io.File;

import okhttp3.Call;
import okhttp3.Response;

/**
 * 应用更新工具类
 */

public class UpdateUtil {
    /**
     * 检测更新
     */
    public static void checkUpdate(final FragmentActivity activity, final boolean showTips) {

        OkGo.get("https://raw.githubusercontent.com/Jacknic/glut/master/version.json").readTimeOut(15000L).execute(new StringCallback() {
            Toast tips = Toast.makeText(activity, "检查更新中...", Toast.LENGTH_LONG);

            @Override
            public void onBefore(BaseRequest request) {
                if (showTips) tips.show();
            }

            @Override
            public void onSuccess(String s, Call call, Response response) {
                int appVersionCode = BuildConfig.VERSION_CODE;
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
                                .setNeutralButton("浏览器下载", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(downloadUrl));
                                        activity.startActivity(intent);
                                    }
                                })
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
     * 下载安装包
     *
     * @param url 更新包下载地址
     */
    public static void downloadApk(final String url, final Activity activity) {
        final AlertDialog dialog = new AlertDialog.Builder(activity)
                .setTitle("更新包下载中")
                .setMessage("下载进度... 0%")
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        OkGo.getInstance().cancelAll();
                    }
                })
                .setPositiveButton("后台下载", null)
                .create();
        OkGo.get(url).execute(new FileCallback(activity.getCacheDir().getAbsolutePath(), "glut_update.apk") {
            @Override
            public void onBefore(BaseRequest request) {
                dialog.show();
            }

            @Override
            public void onError(Call call, Response response, Exception e) {
                super.onError(call, response, e);
                if (response != null && response.isRedirect()) {
                    String location = response.header("Location");
                    if (!TextUtils.isEmpty(location)) {
                        downloadApk(location, activity);
                    }
                }
            }

            @Override
            public void onSuccess(File file, Call call, Response response) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                String type = "application/vnd.android.package-archive";
                //判断是否是AndroidN以及更高的版本
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    Uri contentUri = FileProvider.getUriForFile(activity, BuildConfig.APPLICATION_ID + ".fileProvider", file);
                    intent.setDataAndType(contentUri, type);
                } else {
                    intent.setDataAndType(Uri.fromFile(file), type);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                }
                activity.startActivity(intent);
            }

            @Override
            public void downloadProgress(long currentSize, long totalSize, float progress, long networkSpeed) {
                dialog.setMessage(String.format("下载进度... %.1f%%", progress * 100));
            }

            @Override
            public void onAfter(File file, Exception e) {
                super.onAfter(file, e);
                dialog.dismiss();
            }
        });
    }
}
