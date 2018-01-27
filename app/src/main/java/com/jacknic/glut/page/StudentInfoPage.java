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

import butterknife.BindView;
import okhttp3.Call;
import okhttp3.Response;

/**
 * 学籍信息
 */

public class StudentInfoPage extends BasePage {

    @BindView(R.id.si_tv_id)
    TextView tv_id;
    @BindView(R.id.si_tv_birthday)
    TextView tv_birthday;
    @BindView(R.id.si_tv_className)
    TextView tv_className;
    @BindView(R.id.si_tv_level)
    TextView tv_level;
    @BindView(R.id.si_tv_name)
    TextView tv_name;
    @BindView(R.id.si_tv_nation)
    TextView tv_nation;
    @BindView(R.id.si_tv_origin)
    TextView tv_origin;
    @BindView(R.id.si_tv_place)
    TextView tv_place;
    @BindView(R.id.si_tv_sid)
    TextView tv_sid;
    @BindView(R.id.si_tv_score)
    TextView tv_score;
    @BindView(R.id.si_tv_role)
    TextView tv_role;
    @BindView(R.id.si_iv_header)
    ImageView iv_header;

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
     * 显示学生信息
     */
    private void setStudentInfo() {
        StudentInfoModel infoModel = new StudentInfoModel();
        StudentInfoBean infoBean = infoModel.getFromPrefer();
        tv_id.setText(infoBean.getId());
        tv_birthday.setText(infoBean.getBirthday());
        tv_className.setText(infoBean.getClassName());
        tv_level.setText(infoBean.getLevel());
        tv_name.setText(infoBean.getName());
        tv_nation.setText(infoBean.getNation());
        tv_origin.setText(infoBean.getOrigin());
        tv_place.setText(infoBean.getPlace());
        tv_sid.setText(infoBean.getSid());
        tv_score.setText(infoBean.getScore());
        tv_role.setText(infoBean.getRole());
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
