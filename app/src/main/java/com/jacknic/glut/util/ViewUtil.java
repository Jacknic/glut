package com.jacknic.glut.util;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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
     * 显示工具栏右部按钮
     */
    public static void showRightImageView(Activity activity, @NonNull final View.OnClickListener onClickListener) {
        ImageView bar_iv_right = (ImageView) activity.findViewById(R.id.iv_right);
        bar_iv_right.setVisibility(View.VISIBLE);
        bar_iv_right.setImageResource(R.drawable.ic_autorenew);

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickListener.onClick(v);
                RotateAnimation rotateAnimation = new RotateAnimation(0, 360 * 2, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                rotateAnimation.setDuration(800L);
                v.startAnimation(rotateAnimation);
            }
        };
        bar_iv_right.setOnClickListener(listener);
    }

    /**
     * 设置工具栏返回按钮
     */
    public static void showBackIcon(final AppCompatActivity activity) {
        Toolbar toolbar = (Toolbar) activity.findViewById(R.id.toolbar);
        ActionBar supportActionBar = activity.getSupportActionBar();
        if (supportActionBar == null && toolbar != null) {
            activity.setSupportActionBar(toolbar);
            supportActionBar = activity.getSupportActionBar();
        } else {
            return;
        }
        if (supportActionBar != null) {
            supportActionBar.setDisplayShowHomeEnabled(true);
            supportActionBar.setDisplayHomeAsUpEnabled(true);
            Drawable navigationIcon = toolbar.getNavigationIcon();
            if (navigationIcon != null) {
                //返回按钮填充白色
                navigationIcon.setColorFilter(Color.WHITE, PorterDuff.Mode.DST);
            }
            supportActionBar.setElevation(50f);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    activity.onBackPressed();
                }
            });
        }
    }

    /**
     * dp转换px
     */
    public static int dip2px(float dpValue) {
        float scale = OkGo.getContext().getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}
