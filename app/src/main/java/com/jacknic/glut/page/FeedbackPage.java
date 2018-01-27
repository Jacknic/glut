package com.jacknic.glut.page;

import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.jacknic.glut.R;
import com.jacknic.glut.stacklibrary.PageTool;
import com.tencent.stat.StatService;

import java.util.Properties;

import butterknife.BindView;

/**
 * 用户反馈
 */

public class FeedbackPage extends BasePage {

    @BindView(R.id.et_feedback)
    EditText et_feedback;
    @BindView(R.id.fb_btn_submit)
    Button btnSubmit;

    @Override
    protected int getLayoutId() {
        mTitle = "用户反馈";
        return R.layout.page_feedback;
    }

    @Override
    void initPage() {
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submit();
            }
        });
    }

    /**
     * 提交反馈
     */
    private void submit() {
        String txt_content = et_feedback.getText().toString().trim();
        if (!TextUtils.isEmpty(txt_content)) {
            Properties content = new Properties();
            content.put("反馈", txt_content);
            StatService.trackCustomKVEvent(getContext(), "feedback", content);
            Toast.makeText(getContext(), "感谢你的反馈！在反馈进度可查看最新进展", Toast.LENGTH_SHORT).show();
            PageTool.close(FeedbackPage.this);
        } else {

            Toast.makeText(getContext(), "反馈内容不能为空！", Toast.LENGTH_SHORT).show();
        }
    }
}
