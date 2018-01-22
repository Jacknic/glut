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

/**
 * 用户反馈
 */

public class FeedbackPage extends BasePage {

    private EditText et_feedback;

    @Override
    protected int getLayoutId() {
        mTitle = "用户反馈";
        return R.layout.page_feedback;
    }

    @Override
    void initPage() {
        et_feedback = (EditText) page.findViewById(R.id.fb_et_feedback);
        Button btn_submit = (Button) page.findViewById(R.id.fb_btn_submit);
        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
        });
    }
}
