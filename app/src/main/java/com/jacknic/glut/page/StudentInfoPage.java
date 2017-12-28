package com.jacknic.glut.page;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.jacknic.glut.R;
import com.jacknic.glut.model.EduInfoModel;
import com.jacknic.glut.model.StudentInfoModel;
import com.jacknic.glut.model.bean.StudentInfoBean;
import com.jacknic.glut.stacklibrary.RootFragment;
import com.jacknic.glut.util.Func;
import com.jacknic.glut.util.ViewUtil;
import com.jacknic.glut.view.widget.Dialogs;
import com.lzy.okgo.callback.AbsCallbackWrapper;
import com.lzy.okgo.callback.StringCallback;

import java.io.File;

import okhttp3.Call;
import okhttp3.Response;

/**
 * 学籍信息
 */

public class StudentInfoPage extends RootFragment {

    private View page;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        page = inflater.inflate(R.layout.page_student_info, container, false);
        ViewUtil.setTitle(getRoot(), "学籍信息");
        setStudentInfo();
        return page;
    }


    /**
     * 文字赋值
     *
     * @param view_id
     * @param text
     */
    private void setTextView(int view_id, String text) {
        TextView textView = (TextView) page.findViewById(view_id);
        textView.setText(text);
    }

    /**
     * 显示学生信息
     */
    private void setStudentInfo() {
        StudentInfoModel infoModel = new StudentInfoModel();
        StudentInfoBean infoBean = infoModel.getFromPrefer();
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
        ImageView iv_header = (ImageView) page.findViewById(R.id.si_iv_header);
        File header_img = new File(getContext().getFilesDir(), infoBean.getSid() + ".jpg");
        if (header_img.exists()) {
            Bitmap bitmap = BitmapFactory.decodeFile(header_img.getAbsolutePath());
            iv_header.setImageBitmap(bitmap);
        }
    }

    /**
     * 刷新信息
     */
    private void refresh() {
        Snackbar.make(getActivity().getWindow().getDecorView(), "刷新中...", Snackbar.LENGTH_LONG).show();
        Func.checkLoginStatus(new StringCallback() {
            @Override
            public void onSuccess(String s, Call call, Response response) {
                getInfo();
            }

            @Override
            public void onError(Call call, Response response, Exception e) {
                Dialogs.getLoginJw(getActivity(), new AbsCallbackWrapper() {
                    @Override
                    public void onAfter(Object o, Exception e) {
                        refresh();
                    }
                }).show();
            }
        });
    }

    /**
     * 获取信息
     */
    private void getInfo() {
        EduInfoModel eduInfoModel = new EduInfoModel();
        eduInfoModel.getStudentInfo(new StringCallback() {
            @Override
            public void onSuccess(String s, Call call, Response response) {
                setStudentInfo();
                Snackbar.make(getActivity().getWindow().getDecorView(), "刷新成功", Snackbar.LENGTH_SHORT).show();
            }
        });
    }
}
