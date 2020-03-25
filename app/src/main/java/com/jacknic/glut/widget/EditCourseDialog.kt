package com.jacknic.glut.widget

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.jacknic.glut.R
import com.jacknic.glut.data.db.entity.Course
import com.jacknic.glut.databinding.DialogEditCourseBinding
import com.jacknic.glut.databinding.ItemWeekArrangeBinding
import com.jacknic.glut.util.TimeUtils
import com.jacknic.glut.util.getBinding
import com.jacknic.glut.util.resolveColor

/**
 * 课程安排编辑
 *
 * @author Jacknic
 */
class EditCourseDialog(private val courseItem: Course, context: Context) : BottomSheetDialog(context) {

    private val selectedWeekSet = mutableSetOf<String>()
    private lateinit var allWeekListAdapter: ArrayAdapter<String>
    private var themeColor = 0
    private var normalColor = 0
    private lateinit var bind: DialogEditCourseBinding

    var modifyListener: OnModifyListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        themeColor = context.resolveColor(R.attr.colorPrimary)
        bind = DialogEditCourseBinding.inflate(layoutInflater)
        bind.course = courseItem
        val weekList = courseItem.smartPeriod.trim().split(" ")
        selectedWeekSet.addAll(weekList)
        setListeners()
        setContentView(bind.root)
    }

    private fun setListeners() {
        setupArrange()
        setupWeeks()
        bind.btnSave.setOnClickListener {
            val weeksSb = StringBuilder()
            selectedWeekSet.joinTo(weeksSb, separator = " ", prefix = " ", postfix = " ")
            val weeks = weeksSb.toString()
            courseItem.smartPeriod = weeks
            courseItem.week = weeks
            courseItem.dayOfWeek = bind.spWeekday.selectedItemPosition
            courseItem.startSection = bind.spCourseStart.selectedItemPosition
            courseItem.endSection = bind.spCourseEnd.selectedItemPosition
            modifyListener?.hold(courseItem)
        }
        bind.btnDelete.setOnClickListener { modifyListener?.remove(courseItem) }
    }

    private fun setupArrange() {
        val weekdayList = (0..7).map { "周" + TimeUtils.getWeekDay(it) }
        val arrangeList = (0..14).map { TimeUtils.courseIndex2Text(it) + "节" }
        val arrangeListAdapter = ArrayAdapter(context, android.R.layout.simple_list_item_1, arrangeList)
        bind.spWeekday.apply {
            adapter = ArrayAdapter(context, android.R.layout.simple_list_item_1, weekdayList)
            setSelection(courseItem.dayOfWeek)
        }
        val itemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>) = Unit
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                if (position == 0) {
                    bind.spCourseStart.setSelection(0)
                    bind.spCourseEnd.setSelection(0)
                }
                val start = bind.spCourseStart.selectedItemPosition
                val end = bind.spCourseEnd.selectedItemPosition
                if (start > end) {
                    val pos = if (parent.id == R.id.spCourseStart) start else end
                    bind.spCourseStart.setSelection(pos)
                    bind.spCourseEnd.setSelection(pos)
                }
            }
        }
        bind.spCourseStart.apply {
            adapter = arrangeListAdapter
            setSelection(courseItem.startSection)
            onItemSelectedListener = itemSelectedListener
        }
        bind.spCourseEnd.apply {
            adapter = arrangeListAdapter
            setSelection(courseItem.endSection)
            onItemSelectedListener = itemSelectedListener
        }
    }

    private fun setupWeeks() {
        val allWeeks = (1..30).map { it.toString() }
        allWeekListAdapter = object : ArrayAdapter<String>(context, 0, allWeeks) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val item = getItem(position)
                val itemBind = convertView.getBinding<ItemWeekArrangeBinding>(parent, R.layout.item_week_arrange)
                if (position == 0 && normalColor == 0) {
                    normalColor = itemBind.text1.textColors.defaultColor
                }
                itemBind.text1.text = item
                val selected = item in selectedWeekSet
                val color = if (selected) Color.WHITE else normalColor
                val bgColor = if (selected) themeColor else Color.TRANSPARENT
                itemBind.text1.apply {
                    setTextColor(color)
                    backgroundTintList = ColorStateList.valueOf(bgColor)
                }
                return itemBind.root
            }
        }
        bind.gvWeeks.apply {
            adapter = allWeekListAdapter
            setOnItemClickListener { _, _, position, _ ->
                val item = allWeeks[position]
                val selected = item in selectedWeekSet
                if (selected) {
                    selectedWeekSet.remove(item)
                } else {
                    selectedWeekSet.add(item)
                }
                allWeekListAdapter.notifyDataSetChanged()
            }
        }
    }

    interface OnModifyListener {
        /**
         * 暂存操作
         */
        fun hold(course: Course)

        /**
         * 移除操作
         */
        fun remove(course: Course)
    }
}