package com.jacknic.glut.ui.home

import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.buildSpannedString
import androidx.core.text.scale
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.google.android.material.tabs.TabLayout
import com.jacknic.glut.MainNavDirections
import com.jacknic.glut.R
import com.jacknic.glut.base.BaseFragment
import com.jacknic.glut.data.db.entity.Course
import com.jacknic.glut.databinding.FragCourseBinding
import com.jacknic.glut.util.TimeUtils
import com.jacknic.glut.util.toPage
import com.jacknic.glut.viewmodel.CourseViewModel
import com.jacknic.glut.widget.CourseDetailsDialog
import com.jacknic.glut.widget.TimeTableView
import java.util.*

/**
 * 课表页
 *
 * @author Jacknic
 */
class CourseFrag : BaseFragment<FragCourseBinding>() {

    override val layoutResId = R.layout.frag_course
    override val menuResId = R.menu.course_actions
    private val vm by activityViewModels<CourseViewModel>()
    private val courseList = mutableListOf<Course>()
    private val weekDays = mutableListOf<String>()
    private lateinit var weekDayAdapter: ArrayAdapter<String>
    private var selectedIndex = -1
    private var expanded = false
    private val maxWeek = 50
    private var detailsDialog: Dialog? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setListeners()
        setObservers()
    }

    private fun setListeners() {
        bind.fabPinWeek.apply {
            setOnClickListener {
                vm.setCurrWeek(bind.weekTabs.selectedTabPosition + 1)
                setSubtitle(subtitle)
            }
            setOnLongClickListener {
                val itemList = mutableListOf<String>()
                for (i in 1..maxWeek) {
                    itemList.add(i.toString())
                }
                AlertDialog.Builder(requireContext())
                    .setTitle("修改结束周")
                    .setSingleChoiceItems(itemList.toTypedArray(), prefer.endWeek - 1) { dialog, which ->
                        val newValue = which + 1
                        if (prefer.endWeek != newValue) {
                            prefer.endWeek = newValue
                            vm.notifyChanged()
                        }
                        dialog.dismiss()
                    }.setNegativeButton(android.R.string.cancel, null)
                    .show()
                true
            }
        }
        weekDayAdapter = object : ArrayAdapter<String>(requireContext(), R.layout.item_week_day, weekDays) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val itemView = super.getView(position, convertView, parent)
                var color = Color.TRANSPARENT
                if (selectedIndex == position) {
                    val textView = itemView as TextView
                    color = textView.textColors.withAlpha(10).defaultColor
                }
                itemView.setBackgroundColor(color)
                return itemView
            }
        }
        bind.gvWeekDays.adapter = weekDayAdapter
        bind.timeTable.apply {
            courseClickListener = object : TimeTableView.OnCourseClickListener {
                override fun onItemClick(view: View, courses: List<Course>) {
                    if (courses.size == 1) {
                        showDetails(courses[0])
                    } else {
                        val optionList = courses.map { it.courseName + "@" + it.classRoom }
                        AlertDialog.Builder(requireContext())
                            .setIcon(R.drawable.ic_layers)
                            .setItems(optionList.toTypedArray()) { dialog, position ->
                                dialog.dismiss()
                                val course = courses[position]
                                showDetails(course)
                            }
                            .show()
                    }
                }

                override fun onAddClick(dayOfWeek: Int, courseStart: Int) {
                    val toAdd = MainNavDirections.navToAdd(null, courseStart, dayOfWeek)
                    navCtrl.toPage(R.id.modifyCoursePage, toAdd.arguments)
                }
            }
        }

    }

    private fun showDetails(course: Course) {
        if (detailsDialog?.isShowing != true) {
            detailsDialog = CourseDetailsDialog.build(course.courseNum, requireContext(), navCtrl, course)
            detailsDialog?.show()
        }
    }

    private fun setObservers() {
        vm.weekCourseList.observe(viewLifecycleOwner, Observer {
            courseList.clear()
            courseList.addAll(it)
            bind.timeTable.courseList = courseList
        })
        vm.weekDayList.observe(viewLifecycleOwner, Observer {
            val calendar = Calendar.getInstance()
            val curWeekDay = calendar.get(Calendar.DAY_OF_WEEK)
            weekDays.clear()
            weekDays.addAll(it)
            val currMonth = weekDays.removeAt(0)
            bind.tvMonth.text = currMonth
            selectedIndex = if (vm.addonsWeek.value == 0) (curWeekDay + 5) % 7 else -1
            weekDayAdapter.notifyDataSetChanged()
        })
        vm.endWeek.observe(viewLifecycleOwner, Observer {
            bind.weekTabs.apply {
                removeAllTabs()
                for (i in 1..it) {
                    val tab = newTab()
                    tab.text = buildSpannedString {
                        append("第")
                        scale(1.4F) { append("$i") }
                        append("周")
                    }
                    addTab(tab)
                }
                addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                    override fun onTabReselected(tab: TabLayout.Tab?) = Unit
                    override fun onTabUnselected(tab: TabLayout.Tab?) = Unit

                    override fun onTabSelected(tab: TabLayout.Tab?) {
                        tab?.apply {
                            vm.setAddonsWeek((position + 1) - vm.currWeek.value!!)
                        }
                    }
                })
            }
        })
        vm.currWeek.observe(viewLifecycleOwner, Observer {
            val position = it - 1
            bind.weekTabs.apply {
                post { setScrollPosition(position, 0F, true) }
            }
            bind.weekTabs.getTabAt(position)?.select()
        })

        vm.addonsWeek.observe(viewLifecycleOwner, Observer {
            bind.fabPinWeek.apply {
                if (it != 0) show() else hide()
            }
            val opened = it != 0
            if (opened != expanded) {
                expanded = opened
                activity?.invalidateOptionsMenu()
            }
        })
        vm.needRefresh.observe(viewLifecycleOwner, Observer {
            if (it) {
                vm.notifyChanged()
            }
        })
    }

    override fun onResume() {
        super.onResume()
        setSubtitle(subtitle)
    }

    private val subtitle: String
        get() {
            val term = TimeUtils.term2text(prefer.term)
            return "${vm.schoolYear.value}年$term 第${vm.currWeek.value}周"
        }

    override fun onPause() {
        super.onPause()
        setSubtitle("")
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        menu.findItem(R.id.action_expand_less).isVisible = expanded
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_courses -> {
                navCtrl.toPage(R.id.courseListPage)
                return true
            }
            R.id.action_all_term -> {
                navCtrl.toPage(R.id.termListPage)
                return true
            }
            R.id.action_expand_less -> {
                vm.setAddonsWeek(0)
                bind.appbar.setExpanded(false)
                resetWeekTab()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun resetWeekTab() {
        bind.weekTabs.apply {
            getTabAt(vm.currWeek.value!! - 1)?.select()
        }
    }

    private fun setSubtitle(title: String) {
        (activity as? AppCompatActivity)?.apply {
            supportActionBar?.subtitle = title
        }
    }

}