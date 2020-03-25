package com.jacknic.glut.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import com.jacknic.glut.R
import com.jacknic.glut.data.db.entity.Course
import com.jacknic.glut.databinding.ItemFlagBinding
import com.jacknic.glut.databinding.ItemWeekCourseBinding
import com.jacknic.glut.util.THEME_COLOR_RES
import com.jacknic.glut.util.TimeUtils
import com.jacknic.glut.util.resolveColor
import kotlin.math.roundToInt

/**
 * 课表控件
 *
 * @author Jacknic
 */
class TimeTableView(context: Context, attrs: AttributeSet?) : FrameLayout(context, attrs) {

    private val density = context.resources.displayMetrics.density
    private val defaultCellHeight = (50 * density).roundToInt()
    private val defaultHeaderWidth = (20 * density).roundToInt()
    private var headerWidth = defaultHeaderWidth
    private var cellHeight = defaultCellHeight
    private val cellSpace = (2 * density).roundToInt()
    private var flagAttached = false
    private var hasMoonCourse = false
    private val inflater = LayoutInflater.from(context)
    private var rowCount = 10
    private val moonCourse = intArrayOf(5, 6)
    private var colorNum = 1
    private val flagView by lazy {
        ItemFlagBinding.inflate(inflater, this, false).root
    }
    private var pointerId = -1

    private val viewCachedMap = mutableMapOf<Int, MutableMap<Course, View>>()
    private val conflictCourseMap = mutableMapOf<View, MutableList<Course>>()

    private val colorMap = mutableMapOf<String, Int>()

    var courseClickListener: OnCourseClickListener? = null

    var courseList = mutableListOf<Course>()
        set(values) {
            courseList.clear()
            courseList.addAll(values)
            post { relayout() }
        }

    interface OnCourseClickListener {

        /**
         * 课程点击事件
         */
        fun onItemClick(view: View, courses: List<Course>)

        /**
         * 空白格子点击事件
         */
        fun onAddClick(dayOfWeek: Int, courseStart: Int)
    }

    private fun setupFlagView() {
        setOnTouchListener { v, event ->
            if (v.performClick()) {
                return@setOnTouchListener false
            }
            return@setOnTouchListener when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    pointerId = event.getPointerId(0)
                    event.x > headerWidth
                }
                MotionEvent.ACTION_UP -> {
                    // 多点触控错位问题
                    if (pointerId != event.getPointerId(0)) {
                        return@setOnTouchListener false
                    }
                    if (flagAttached) {
                        removeView(flagView)
                        flagAttached = false
                        return@setOnTouchListener false
                    }
                    val cellWidth = (width - headerWidth) / 7
                    val topOffset = event.y.roundToInt() / cellHeight
                    val leftOffset = (event.x - headerWidth).roundToInt() / cellWidth
                    val flagParams = LayoutParams(cellWidth, cellHeight).apply {
                        topMargin = topOffset * cellHeight
                        leftMargin = headerWidth + leftOffset * cellWidth
                    }
                    flagView.setOnClickListener {
                        val dayOfWeek = leftOffset + 1
                        val courseStart = topOffset + if (!hasMoonCourse && topOffset > 4) 3 else 1
                        courseClickListener?.onAddClick(dayOfWeek, courseStart)
                    }
                    addView(flagView, flagParams)
                    flagAttached = true
                    true
                }
                else -> false
            }
        }
    }

    private fun getColor(key: String): Int {
        val color = colorMap[key]
        return if (color != null) {
            color
        } else {
            val newColorRes = THEME_COLOR_RES[colorNum % THEME_COLOR_RES.size]
            val newColor = ContextCompat.getColor(context, newColorRes)
            colorMap[key] = newColor
            colorNum++
            newColor
        }
    }

    private fun relayout() {
        conflictCourseMap.clear()
        viewCachedMap.clear()
        removeAllViews()
        hasMoonCourse = false
        rowCount = 10
        courseList.forEach {
            if (moonCourse.contains(it.startSection) || moonCourse.contains(it.endSection)) {
                hasMoonCourse = true
            }
            if (it.startSection <= 5 && it.endSection >= 5) {
                hasMoonCourse = true
            }
            if (it.endSection > rowCount) {
                rowCount = it.endSection
            }
        }
        val viewGroup = parent as ViewGroup
        val realCount = if (hasMoonCourse) rowCount else rowCount - 2
        val fixCellHeight = (viewGroup.height) / realCount
        if (fixCellHeight > cellHeight) {
            cellHeight = fixCellHeight
        }
        setupFlagView()
        fetchHeaderItem()
        fetchCourseItem()
    }

    private fun fetchHeaderItem() {
        val bgColor = context.resolveColor(R.attr.colorBackgroundFloating)
        val divColor = context.resolveColor(R.attr.colorButtonNormal)
        background = GridDrawable(headerWidth.toFloat(), cellHeight.toFloat(), divColor, density)

        for (i in 1..rowCount) {
            if (!hasMoonCourse && moonCourse.contains(i)) {
                continue
            }
            val tableHeader = inflater.inflate(R.layout.item_table_side, this, false) as TextView
            var marginTop = (i - 1) * cellHeight
            if (!hasMoonCourse && i > 6) {
                marginTop -= 2 * cellHeight
            }
            val paddingBottom = if (i == rowCount) 0 else cellSpace / 2
            val layoutParams = LayoutParams(headerWidth, cellHeight - paddingBottom).apply {
                topMargin = marginTop
            }
            val textValue = TimeUtils.courseIndex2Text(i, "\n")
            tableHeader.text = textValue
            tableHeader.setBackgroundColor(bgColor)
            addView(tableHeader, layoutParams)
        }
    }

    private fun fetchCourseItem() {
        val cellItemWidth = (width - headerWidth) / 7
        for (course in courseList) {
            if (isConflict(course)) {
                continue
            }
            val binding = ItemWeekCourseBinding.inflate(inflater, this, false)
            binding.course = course
            val color = getColor(course.courseName)
            binding.cardCourse.setCardBackgroundColor(color)
            val cellCount = course.endSection - course.startSection + 1
            val itemHeight = cellHeight * cellCount
            val startOffset = course.startSection - 1
            var marginTop = startOffset * cellHeight
            if (!hasMoonCourse && course.startSection > 6) {
                marginTop -= 2 * cellHeight
            }
            val marginLeft = (course.dayOfWeek - 1) * cellItemWidth + headerWidth
            val layoutParams = LayoutParams(cellItemWidth, itemHeight).apply {
                topMargin = marginTop
                leftMargin = marginLeft
            }
            val view = binding.root
            courseClickListener?.apply {
                view.setOnClickListener {
                    val list = conflictCourseMap[it] ?: listOf(course)
                    onItemClick(it, list)
                }
            }
            cacheView(course, view)
            addView(view, layoutParams)
        }
    }

    private fun cacheView(course: Course, view: View) {
        val index = course.dayOfWeek
        var list = viewCachedMap[index]
        if (list == null) {
            list = mutableMapOf()
            viewCachedMap[index] = list
        }
        list[course] = view
    }

    private fun isConflict(course: Course): Boolean {
        val index = course.dayOfWeek
        viewCachedMap[index]?.forEach { item ->
            val itemCourse = item.key
            if (itemCourse.startSection >= course.startSection
                && itemCourse.endSection <= course.endSection
            ) {
                // Logger.d("显示冲突课程：${course.courseName} => ${itemCourse.courseName}")
                item.value.apply {
                    DataBindingUtil.findBinding<ItemWeekCourseBinding>(this)?.apply {
                        ivLayers.isVisible = true
                    }
                    var mutableList = conflictCourseMap[item.value]
                    if (mutableList == null) {
                        mutableList = mutableListOf(itemCourse)
                        conflictCourseMap[item.value] = mutableList
                    }
                    mutableList.add(course)
                }
                return true
            }
        }
        return false
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        if (w != 0 && h != 0) {
            post { relayout() }
        }
    }
}