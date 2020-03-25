package com.jacknic.glut.widget

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.jacknic.glut.R

/**
 * 样式化下拉刷新
 *
 * @author Jacknic
 */
class ThemeSwipeRefreshLayout(context: Context, attrs: AttributeSet?) : SwipeRefreshLayout(context, attrs) {

    init {
        val a = context.obtainStyledAttributes(attrs, R.styleable.ThemeSwipeRefreshLayout)
        val backgroundColor = a.getColor(R.styleable.ThemeSwipeRefreshLayout_backgroundColor, Color.WHITE)
        setProgressBackgroundColorSchemeColor(backgroundColor)
        val schemeColor = a.getColor(R.styleable.ThemeSwipeRefreshLayout_schemeColor, Color.BLACK)
        setColorSchemeColors(schemeColor)
        a.recycle()
        setProgressViewEndTarget(true, progressViewEndOffset)
    }
}