package com.jacknic.glut.page;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.jacknic.glut.R;
import com.jacknic.glut.stacklibrary.RootFragment;
import com.jacknic.glut.util.ViewUtil;
import com.tencent.stat.StatService;

import java.util.Properties;

/**
 * 用户反馈
 */

public class FeedbackPage extends RootFragment {

    private EditText et_feedback;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View page = inflater.inflate(R.layout.page_feedback, container, false);
        ViewUtil.setTitle(getRoot(), "用户反馈");
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
                    close(FeedbackPage.this);
                } else {

                    Toast.makeText(getContext(), "反馈内容不能为空！", Toast.LENGTH_SHORT).show();
                }
            }
        });
        return page;
    }


}
