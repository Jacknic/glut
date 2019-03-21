package com.jacknic.glut.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationManagerCompat;


import com.alibaba.fastjson.JSONObject;
import com.jacknic.glut.MainActivity;
import com.jacknic.glut.R;
import com.jacknic.glut.util.Config;
import com.jacknic.glut.util.PreferManager;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;

import androidx.work.Worker;
import androidx.work.WorkerParameters;
import okhttp3.Call;
import okhttp3.Response;

import static com.jacknic.glut.util.Config.URL_CW_API;

public class YueWarningWorker extends Worker {


    public YueWarningWorker(@NonNull Context context, @NonNull WorkerParameters parameters) {
        super(context, parameters);
    }

    @NonNull
    @Override
    public Result doWork() {
        OkGo.post(URL_CW_API).tag(this)
                .params("method", "getecardinfo")
                .params("stuid", "0")
                .params("carno", PreferManager.getPrefer().getString(Config.SID, ""))
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String res_json, Call call, Response response) {
                        JSONObject json;
                        try {
                            json = JSONObject.parseObject(res_json);
                        } catch (Exception e) {
                            return;
                        }
                        boolean success = json.getBoolean("success");
                        if (success) {
                            JSONObject data = json.getJSONObject("data");

                            Notification.Builder builder;
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                builder = new Notification.Builder(
                                        getApplicationContext(), MainActivity.CHANNEL_ID);

                            } else {
                                builder = new Notification.Builder(getApplicationContext());
                            }

                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                                    | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                            PendingIntent pendingIntent =
                                    PendingIntent.getActivity(
                                            getApplicationContext(), 0, intent, 0);

                            if (Double.valueOf(data.getString(Config.URL_CW_API_BALANCE)).intValue()
                                    < PreferManager.getPrefer().getInt(
                                            Config.KEY_MONEY_LIMIT, Config.DEFAULT_MONEY_LIMIT)) {

                                builder.setSmallIcon(R.drawable.ic_warn)
                                        .setContentTitle("一卡通余额过低")
                                        .setContentText("一卡通余额:"
                                                + data.getString(Config.URL_CW_API_BALANCE) + "元")
                                        .setPriority(Notification.PRIORITY_DEFAULT)
                                        .setContentIntent(pendingIntent)
                                        .setAutoCancel(true);

                                NotificationManagerCompat manager =
                                        NotificationManagerCompat.from(getApplicationContext());
                                manager.notify(0, builder.build());
                            }

                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                    }

                    @Override
                    public void onAfter(String s, Exception e) {
                    }
                });


        return Result.success();
    }
}
