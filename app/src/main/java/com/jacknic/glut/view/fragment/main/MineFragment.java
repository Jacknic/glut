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
import com.jacknic.glut.model.StudentInfoModel;
import com.jacknic.glut.model.bean.StudentInfoBean;
import com.jacknic.glut.page.ExamListPage;
import com.jacknic.glut.page.GradeListPage;
import com.jacknic.glut.page.ProcessPage;
import com.jacknic.glut.page.SettingPage;
import com.jacknic.glut.page.StudentInfoPage;
import com.jacknic.glut.stacklibrary.RootFragment;
import com.jacknic.glut.util.Config;
import com.jacknic.glut.util.Func;
import com.jacknic.glut.util.ImageUtil;

import java.io.File;


/**
 * 我的
 */
public class MineFragment extends Fragment implements View.OnClickListener {


    private ImageView ivHeader;
    private View fragment;
    private SharedPreferences preferJw;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        preferJw = getContext().getSharedPreferences(Config.PREFER, Context.MODE_PRIVATE);
        String sid = preferJw.getString("sid", "");
        StudentInfoBean studentInfo = new StudentInfoModel().getFromPrefer();
        boolean isShowHeadImg = preferJw.getBoolean("isShowHeadImg", true);
        fragment = inflater.inflate(R.layout.frag_mine, container, false);
        TextView tvSid = (TextView) fragment.findViewById(R.id.jw_tv_sid);
        tvSid.setText(sid);
        TextView tvName = (TextView) fragment.findViewById(R.id.jw_tv_name);
        tvName.setText(studentInfo.getName());
        TextView tv_className = (TextView) fragment.findViewById(R.id.jw_tv_className);
        tv_className.setText(studentInfo.getClassName());
        findAndSetOnclick(R.id.tv_kaoshi);
        findAndSetOnclick(R.id.tv_chengji);
        findAndSetOnclick(R.id.tv_xueye);
        findAndSetOnclick(R.id.tv_xueji);
        findAndSetOnclick(R.id.tv_setting);
        ivHeader = (ImageView) fragment.findViewById(R.id.jw_iv_header);
        ImageView iv_header_bg = (ImageView) fragment.findViewById(R.id.jw_iv_header_bg);
        if (!isShowHeadImg) {
            ivHeader.setVisibility(View.GONE);
        }
        iv_header_bg.setOnClickListener(this);
        ivHeader.setOnClickListener(this);
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
                ivHeader.setImageBitmap(ImageUtil.getCircleBitmap(bitmap));
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
        RootFragment rootFragment = Func.getTopFragment(getActivity());
        switch (v.getId()) {
            case R.id.tv_kaoshi:
                rootFragment.open(new ExamListPage());
                break;
            case R.id.tv_xueji:
                rootFragment.open(new StudentInfoPage());
                break;
            case R.id.tv_xueye:
                rootFragment.open(new ProcessPage());
                break;
            case R.id.tv_chengji:
                rootFragment.open(new GradeListPage());
                break;
            case R.id.tv_setting:
                rootFragment.open(new SettingPage());
                break;
            case R.id.jw_iv_header:
                v.setVisibility(View.GONE);
                preferJw.edit().putBoolean("isShowHeadImg", false).apply();
                break;
            case R.id.jw_iv_header_bg:
                ivHeader.setVisibility(View.VISIBLE);
                preferJw.edit().putBoolean("isShowHeadImg", true).apply();
                break;
        }

    }
}
