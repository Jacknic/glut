package com.jacknic.glut.view.fragment.main;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.jacknic.glut.R;
import com.jacknic.glut.activity.SettingActivity;
import com.jacknic.glut.activity.educational.ExamListActivity;
import com.jacknic.glut.activity.educational.GradeListActivity;
import com.jacknic.glut.activity.educational.ProcessActivity;
import com.jacknic.glut.activity.educational.StudentInfoActivity;
import com.jacknic.glut.util.ActivityUtil;
import com.jacknic.glut.util.Config;
import com.jacknic.glut.util.ImageUtil;

import java.io.File;


/**
 * 我的
 */
public class Fragment_wd extends Fragment implements View.OnClickListener {

    private ImageView iv_header;
    private View fragment;
    private SharedPreferences prefer_jw;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        prefer_jw = getContext().getSharedPreferences(Config.PREFER_JW, Context.MODE_PRIVATE);
        String sid = prefer_jw.getString("sid", "");
        String name = prefer_jw.getString("name", "");
        String className = prefer_jw.getString("className", "");
        boolean isShowHeadImg = prefer_jw.getBoolean("isShowHeadImg", true);
        fragment = inflater.inflate(R.layout.fragment_wd, container, false);
        TextView tv_sid = (TextView) fragment.findViewById(R.id.jw_tv_sid);
        tv_sid.setText(sid);
        TextView tv_name = (TextView) fragment.findViewById(R.id.jw_tv_name);
        tv_name.setText(name);
        TextView tv_className = (TextView) fragment.findViewById(R.id.jw_tv_className);
        tv_className.setText(className);
        findAndSetOnclick(R.id.tv_kaoshi);
        findAndSetOnclick(R.id.tv_chengji);
        findAndSetOnclick(R.id.tv_xueye);
        findAndSetOnclick(R.id.tv_xueji);
        findAndSetOnclick(R.id.tv_setting);
        iv_header = (ImageView) fragment.findViewById(R.id.jw_iv_header);
        ImageView iv_header_bg = (ImageView) fragment.findViewById(R.id.jw_iv_header_bg);
        if (!isShowHeadImg) {
            iv_header.setVisibility(View.GONE);
        }
        iv_header_bg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iv_header.setVisibility(View.VISIBLE);
                prefer_jw.edit().putBoolean("isShowHeadImg", true).apply();
            }
        });
        iv_header.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setVisibility(View.GONE);
                prefer_jw.edit().putBoolean("isShowHeadImg", false).apply();
            }
        });
        setHeaderImage(sid);
        return fragment;
    }

    /**
     * 设置头像
     *
     * @param sid 学号
     */
    private void setHeaderImage(String sid) {
        if (!TextUtils.isEmpty(sid)) {
            File imgPath = new File(getContext().getFilesDir(), sid + ".jpg");
            if (imgPath.exists()) {
                Bitmap headerImg = BitmapFactory.decodeFile(imgPath.getAbsolutePath());
                if (headerImg == null) return;
                Bitmap bitmap = ImageUtil.centerSquareScaleBitmap(headerImg, headerImg.getWidth() - 2);
                iv_header.setImageBitmap(ImageUtil.getCircleBitmap(bitmap));
            }
        }
    }

    /**
     * 设置点击事件监听
     *
     * @param id view id
     */
    private void findAndSetOnclick(int id) {
        fragment.findViewById(id).setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_kaoshi:
                ActivityUtil.lunchActivity(getContext(), ExamListActivity.class);
                break;
            case R.id.tv_xueji:
                ActivityUtil.lunchActivity(getContext(), StudentInfoActivity.class);
                break;
            case R.id.tv_xueye:
                ActivityUtil.lunchActivity(getContext(), ProcessActivity.class);
                break;
            case R.id.tv_chengji:
                ActivityUtil.lunchActivity(getContext(), GradeListActivity.class);
                break;
            case R.id.tv_setting:
                ActivityUtil.lunchActivity(getContext(), SettingActivity.class);
                break;

        }

    }
}
