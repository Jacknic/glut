package com.jacknic.glut.widget

import android.graphics.*
import android.graphics.drawable.Drawable
import androidx.annotation.ColorInt

/**
 * 网格背景
 *
 * @author Jacknic
 */
class GridDrawable(
    private val startOffset: Float,
    private val divHeight: Float,
    @ColorInt lineColor: Int,
    private val density: Float
) : Drawable() {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = lineColor
        alpha = 100
        strokeCap = Paint.Cap.ROUND
        style = Paint.Style.STROKE
        strokeWidth = density
        pathEffect = DashPathEffect(floatArrayOf(4 * density, 4 * density), 1F)
    }
    private val mPath = Path()

    override fun draw(canvas: Canvas) {
        val height = bounds.height()
        val width = bounds.width().toFloat()
        var offsetY = divHeight * 2
        while (offsetY < height) {
            mPath.reset()
            mPath.moveTo(startOffset, offsetY)
            mPath.lineTo(width, offsetY)
            //canvas.drawLine(startOffset, offsetY - density / 2, width, offsetY, paint)
            canvas.drawPath(mPath, paint)
            offsetY += divHeight * 2
        }

    }

    override fun setAlpha(alpha: Int) {
        paint.alpha = alpha
    }

    override fun getOpacity() = PixelFormat.TRANSPARENT

    override fun setColorFilter(colorFilter: ColorFilter?) {
        paint.colorFilter = colorFilter
    }
}