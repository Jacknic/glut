package com.jacknic.glut.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.jacknic.glut.R;
import com.tencent.stat.StatService;

import java.util.Properties;

/**
 * 用户反馈
 */

public class FeedbackActivity extends BaseActivity {

    private Button btn_submit;
    private EditText et_feedback;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        setStatusView();
        TextView title = (TextView) findViewById(R.id.tv_toolbar_title);
        title.setText("反馈");
        et_feedback = (EditText) findViewById(R.id.fb_et_feedback);
        btn_submit = (Button) findViewById(R.id.fb_btn_submit);
        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String txt_content = et_feedback.getText().toString().trim();
                if (!TextUtils.isEmpty(txt_content)) {
                    Properties content = new Properties();
                    content.put("反馈", txt_content);
                    StatService.trackCustomKVEvent(FeedbackActivity.this, "feedback", content);
                    Toast.makeText(FeedbackActivity.this, "感谢你的反馈！", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(FeedbackActivity.this, "反馈内容不能为空！", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
