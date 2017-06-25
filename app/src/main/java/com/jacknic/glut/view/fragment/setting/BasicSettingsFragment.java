package com.jacknic.glut.view.fragment.setting;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.jacknic.glut.R;
import com.jacknic.glut.activity.BrowserActivity;
import com.jacknic.glut.activity.FeedbackActivity;
import com.jacknic.glut.bean.VersionBean;
import com.jacknic.glut.util.ActivityUtil;
import com.jacknic.glut.util.Config;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.FileCallback;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.request.BaseRequest;

import java.io.File;

import okhttp3.Call;
import okhttp3.Response;


/**
 * 基本设置
 */
public class BasicSettingsFragment extends Fragment implements View.OnClickListener {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View fragment = inflater.inflate(R.layout.fragment_setting_basic, container, false);
        TextView tv_select_theme_color = (TextView) fragment.findViewById(R.id.setting_tv_select_theme_color);
        ImageView iv_select_btn_color = (ImageView) fragment.findViewById(R.id.setting_iv_select_btn_color);
        TextView tv_select_btn_color = (TextView) fragment.findViewById(R.id.setting_tv_select_btn_color);
        SharedPreferences prefer_setting = getContext().getSharedPreferences(Config.PREFER_SETTING, Context.MODE_PRIVATE);
        int index = prefer_setting.getInt(Config.SETTING_COLOR_INDEX, 4);
        iv_select_btn_color.setImageResource(Config.colors[index]);
        tv_select_theme_color.setOnClickListener(this);
        tv_select_btn_color.setOnClickListener(this);
        TextView tv_feedback = (TextView) fragment.findViewById(R.id.setting_tv_feedback);
        tv_feedback.setOnClickListener(this);
        TextView tv_feedbackDeal = (TextView) fragment.findViewById(R.id.setting_tv_feedbackDeal);
        tv_feedbackDeal.setOnClickListener(this);
        TextView tv_checkUpdate = (TextView) fragment.findViewById(R.id.setting_tv_checkUpdate);
        tv_checkUpdate.setOnClickListener(this);
        return fragment;
    }

    /**
     * 检测更新
     */
    private void checkUpdate() {
        try {
            PackageManager pm = getActivity().getPackageManager();
            final PackageInfo packageInfo = pm.getPackageInfo(getActivity().getPackageName(), PackageManager.GET_CONFIGURATIONS);
            OkGo.get("https://raw.githubusercontent.com/Jacknic/glut/master/version.json").readTimeOut(15000L).execute(new StringCallback() {
                @Override
                public void onSuccess(String s, Call call, Response response) {
                    int appVersionCode = packageInfo.versionCode;
                    System.out.println("应用的版本号：" + appVersionCode);
                    VersionBean versionBean;
                    try {
                        versionBean = JSONObject.parseObject(s, VersionBean.class);
                        if (appVersionCode < versionBean.getVersionCode()) {
                            final String downloadUrl = versionBean.getDownloadUrl();
                            Toast.makeText(getContext(), "应用有新版本" + versionBean.getVersionName(), Toast.LENGTH_SHORT).show();
                            AlertDialog alertDialog = new AlertDialog.Builder(getContext())
                                    .setTitle("发现新版本！")
                                    .setMessage("新版本：" + versionBean.getVersionName()
                                            + "\n发布时间：" + versionBean.getDate()
                                            + "\n更新描述：" + versionBean.getInfo()
                                    ).setNegativeButton(android.R.string.cancel, null)
                                    .setPositiveButton("立即更新", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            downloadApk(downloadUrl);
                                        }
                                    }).create();
                            alertDialog.show();
                        } else {
                            Toast.makeText(getContext(), "已是最新版本 " + packageInfo.versionName, Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Toast.makeText(getContext(), "检查更新失败！", Toast.LENGTH_SHORT).show();
                        System.err.println(e.getMessage());
                    }

                }

                @Override
                public void onError(Call call, Response response, Exception e) {
                    super.onError(call, response, e);
                    Toast.makeText(getContext(), "检查更新失败！", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

    }

    /**
     * 下载安装包
     *
     * @param url 更新包下载地址
     */
    private void downloadApk(String url) {
        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setTitle("更新软件包下载中");
        progressDialog.setCancelable(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "取消更新", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                OkGo.getInstance().cancelAll();
            }
        });

        OkGo.get(url).execute(new FileCallback("glut_update.apk") {
            @Override
            public void onBefore(BaseRequest request) {
                super.onBefore(request);
                progressDialog.show();
            }

            @Override
            public void onSuccess(File file, Call call, Response response) {
                //下载完成启动安装
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
                getActivity().startActivity(intent);
            }

            @Override
            public void downloadProgress(long currentSize, long totalSize, float progress, long networkSpeed) {
                super.downloadProgress(currentSize, totalSize, progress, networkSpeed);
                progressDialog.setMax((int) (totalSize / 1024 * 1024));
                progressDialog.setProgress((int) (currentSize / 1024 * 1024));
                progressDialog.setMessage("当前下载速度：" + networkSpeed / 1024 + "K");
            }

            @Override
            public void onAfter(File file, Exception e) {
                super.onAfter(file, e);
                progressDialog.dismiss();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.setting_tv_select_theme_color:
                ColorsDialogFragment.launch(getActivity(), Config.SETTING_THEME_INDEX);
                break;
            case R.id.setting_tv_select_btn_color:
                ColorsDialogFragment.launch(getActivity(), Config.SETTING_COLOR_INDEX);
                break;
            case R.id.setting_tv_feedback:
                ActivityUtil.lunchActivity(getContext(), FeedbackActivity.class);
                break;
            case R.id.setting_tv_feedbackDeal:
                Intent intent = new Intent(getContext(), BrowserActivity.class);
                intent.setAction("https://github.com/Jacknic/glut/blob/master/feedback.md");
                getActivity().startActivity(intent);
                break;
            case R.id.setting_tv_checkUpdate:
                Toast.makeText(getContext(), "检查更新中...", Toast.LENGTH_LONG).show();
                checkUpdate();
                break;

        }
    }
}
