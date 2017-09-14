package com.jacknic.glut.util;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;

import com.jacknic.glut.R;
import com.lzy.okgo.OkGo;

/**
 * 视图工具类
 */

public class ViewUtil {
    /**
     * 显示刷新按钮
     */
    public static void showRefreshView(Activity activity, @NonNull final View.OnClickListener onClickListener) {
        ImageView iv_setting = (ImageView) activity.findViewById(R.id.iv_setting);
        iv_setting.setVisibility(View.VISIBLE);
        iv_setting.setImageResource(R.drawable.ic_autorenew);

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickListener.onClick(v);
                RotateAnimation rotateAnimation = new RotateAnimation(0, 360 * 2, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                rotateAnimation.setDuration(800L);
                v.startAnimation(rotateAnimation);
            }
        };
        iv_setting.setOnClickListener(listener);
    }

    /**
     * dp转换px
     */
    public static int dip2px(float dpValue) {
        float scale = OkGo.getContext().getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}
