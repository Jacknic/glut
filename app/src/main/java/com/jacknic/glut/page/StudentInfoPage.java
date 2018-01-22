package com.jacknic.glut.page;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.ImageView;
import android.widget.TextView;

import com.jacknic.glut.R;
import com.jacknic.glut.model.EduInfoModel;
import com.jacknic.glut.model.StudentInfoModel;
import com.jacknic.glut.model.bean.StudentInfoBean;
import com.jacknic.glut.util.Func;
import com.jacknic.glut.util.SnackbarTool;
import com.jacknic.glut.view.widget.Dialogs;
import com.lzy.okgo.callback.AbsCallbackWrapper;
import com.lzy.okgo.callback.StringCallback;

import java.io.File;

import okhttp3.Call;
import okhttp3.Response;

/**
 * 学籍信息
 */

public class StudentInfoPage extends BasePage {

    @Override
    protected int getLayoutId() {
        mTitle = "学籍信息";
        return R.layout.page_student_info;
    }

    @Override
    void initPage() {
        setStudentInfo();
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

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_refresh, menu);
    }

    /**
     * 刷新信息
     */
    @Override
    void refresh() {
        SnackbarTool.showLong("刷新中...");
        Func.checkLoginStatus(new StringCallback() {
            @Override
            public void onSuccess(String s, Call call, Response response) {
                getInfo();
            }

            @Override
            public void onError(Call call, Response response, Exception e) {
                Dialogs.showLoginJw(getContext(), new AbsCallbackWrapper() {
                    @Override
                    public void onAfter(Object o, Exception e) {
                        refresh();
                    }
                });
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
                if (getContext() == null) return;
                setStudentInfo();
                SnackbarTool.showShort("刷新成功");
            }
        });
    }
}
