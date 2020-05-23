package com.jacknic.glut.util

import android.content.Context
import androidx.annotation.StyleRes
import com.jacknic.glut.R

/**
 * 配色数组
 */
val THEME_COLOR_RES = arrayOf(
    R.color.black,
    R.color.purple,
    R.color.purpleDeep,
    R.color.indigo,
    R.color.blue,
    R.color.blueLight,
    R.color.cyan,
    R.color.teal,
    R.color.green,
    R.color.greenLight,
    R.color.brown,
    R.color.amber,
    R.color.orangeDeep,
    R.color.red,
    R.color.pink
)

/**
 * 获取主题配色列表
 */
fun getPaletteColors(context: Context): MutableList<Int> {
    val ta = context.resources.obtainTypedArray(R.array.primary_palettes)
    val colors = mutableListOf<Int>()
    for (index in 0 until ta.length()) {
        val overlay = ta.peekValue(index)
        val overlayTa = context.obtainStyledAttributes(overlay.resourceId, intArrayOf(R.attr.colorPrimary))
        val color = overlayTa.getColor(0, 0)
        colors.add(color)
        overlayTa.recycle()
    }
    ta.recycle()
    return colors
}

/**
 * 获取主题配色样式表ID
 */
@StyleRes
fun getPaletteStyle(context: Context, themeIndex: Int): Int {
    val ta = context.resources.obtainTypedArray(R.array.primary_palettes)
    val paletteStyle = ta.getResourceId(themeIndex, 0)
    ta.recycle()
    return paletteStyle
}
