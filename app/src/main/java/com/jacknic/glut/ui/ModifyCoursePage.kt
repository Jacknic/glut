package com.jacknic.glut.ui

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.jacknic.glut.R
import com.jacknic.glut.base.AbsListAdapter
import com.jacknic.glut.base.BasePage
import com.jacknic.glut.data.db.entity.Course
import com.jacknic.glut.databinding.ItemCourseArrangeBinding
import com.jacknic.glut.databinding.PageModifyCourseBinding
import com.jacknic.glut.util.findBinding
import com.jacknic.glut.viewmodel.CourseViewModel
import com.jacknic.glut.viewmodel.ModifyCourseViewModel
import com.jacknic.glut.widget.EditCourseDialog

/**
 * 添加课程安排
 *
 * @author Jacknic
 */
class ModifyCoursePage : BasePage<PageModifyCourseBinding>(), EditCourseDialog.OnModifyListener {

    override val menuResId = R.menu.edit_actions
    override val layoutResId = R.layout.page_modify_course
    private var editCourseDialog: EditCourseDialog? = null
    private val vm by viewModels<ModifyCourseViewModel>()
    private lateinit var courseListAdapter: AbsListAdapter<Course, ItemCourseArrangeBinding>
    private var startSection = 0
    private var dayOfWeek = 0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var courseNum: String? = null
        if (arguments != null) {
            val args = ModifyCoursePageArgs.fromBundle(arguments!!)
            startSection = args.courseStart
            dayOfWeek = args.dayOfWeek
            courseNum = args.courseNum
        }
        val isAdd = courseNum.isNullOrBlank()
        val titleRes = if (isAdd) R.string.add_course else R.string.modify_course
        activity?.setTitle(titleRes)
        if (isAdd) {
            showEditDialog(Course(
                startSection = startSection,
                endSection = startSection,
                dayOfWeek = dayOfWeek,
                smartPeriod = " ${prefer.currWeek} "
            ))
        }
        setListeners()
        setObserves(courseNum)
    }

    private fun setListeners() {
        courseListAdapter = object : AbsListAdapter<Course, ItemCourseArrangeBinding>(R.layout.item_course_arrange) {
            override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
                val binding = holder.itemView.findBinding<ItemCourseArrangeBinding>()
                binding?.apply {
                    val item = getItem(position)
                    this.course = item
                    root.setOnClickListener { showEditDialog(item) }
                }
            }
        }
        courseListAdapter.submitList(vm.courseList)
        bind.btnAdd.setOnClickListener { showEditDialog(Course(smartPeriod = " ${prefer.currWeek} ")) }
        bind.rvEditList.adapter = courseListAdapter
    }

    private fun setObserves(courseNum: String?) {
        vm.getCourseInfo(courseNum).observe(viewLifecycleOwner, Observer { bind.courseInfo = it })
        vm.changeEvent.observe(viewLifecycleOwner, Observer {
            bind.rvEditList.isVisible = vm.courseList.isNotEmpty()
            courseListAdapter.notifyDataSetChanged()
        })
    }

    private fun showEditDialog(item: Course) {
        if (editCourseDialog?.isShowing != true) {
            editCourseDialog = EditCourseDialog(item, requireContext()).also {
                it.modifyListener = this@ModifyCoursePage
                it.show()
            }
        }
    }

    override fun hold(course: Course) {
        if (course !in vm.courseList) {
            vm.courseList.add(course)
        }
        vm.editedCourseSet.add(course)
        vm.changeEvent.postValue(true)
        editCourseDialog?.dismiss()
    }

    override fun remove(course: Course) {
        vm.courseList.remove(course)
        vm.editedCourseSet.remove(course)
        vm.deletedCourseSet.add(course)
        vm.changeEvent.postValue(true)
        editCourseDialog?.dismiss()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_ok -> {
                vm.save()
                activityViewModels<CourseViewModel>().value.needRefresh.postValue(true)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}