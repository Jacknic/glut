package com.jacknic.glut.activity.educational;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.ImageView;
import android.widget.TextView;

import com.jacknic.glut.R;
import com.jacknic.glut.activity.BaseActivity;
import com.jacknic.glut.model.bean.StudentInfoBean;
import com.jacknic.glut.model.StudentInfoModel;

import java.io.File;

/**
 * 学籍信息
 */

public class StudentInfoActivity extends BaseActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_info);
        StudentInfoModel infoModel = new StudentInfoModel();
        StudentInfoBean infoBean = infoModel.getFromPrefer();
        setTextView(R.id.tv_toolbar_title, "学籍信息");
        setTextView(R.id.si_tv_id, infoBean.getId());
        setTextView(R.id.si_tv_birthday, infoBean.getBirthday());
        setTextView(R.id.si_tv_className, infoBean.getClassName());
        setTextView(R.id.si_tv_level, infoBean.getLevel());
        setTextView(R.id.si_tv_name, infoBean.getName());
        setTextView(R.id.si_tv_nation, infoBean.getNation());
        setTextView(R.id.si_tv_origin, infoBean.getOrigin());
        setTextView(R.id.si_tv_place, infoBean.getPlace());
        setTextView(R.id.si_tv_sid, infoBean.getSid());
        setTextView(R.id.si_tv_score, infoBean.getScore());
        setTextView(R.id.si_tv_role, infoBean.getRole());
        ImageView iv_header = (ImageView) findViewById(R.id.si_iv_header);
        File header_img = new File(getFilesDir(), infoBean.getSid() + ".jpg");
        if (header_img.exists()) {
            Bitmap bitmap = BitmapFactory.decodeFile(header_img.getAbsolutePath());
            iv_header.setImageBitmap(bitmap);
        }
        setStatusView();
    }

    /**
     * 文字赋值
     *
     * @param view_id
     * @param text
     */
    private void setTextView(int view_id, String text) {
        TextView textView = (TextView) findViewById(view_id);
        textView.setText(text);
    }
}
