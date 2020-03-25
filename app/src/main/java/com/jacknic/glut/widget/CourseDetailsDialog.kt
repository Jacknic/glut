package com.jacknic.glut.widget

import android.content.Context
import android.os.Bundle
import androidx.core.view.isVisible
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.jacknic.glut.MainNavDirections
import com.jacknic.glut.R
import com.jacknic.glut.base.AbsListAdapter
import com.jacknic.glut.data.db.entity.Course
import com.jacknic.glut.data.db.entity.CourseInfo
import com.jacknic.glut.data.repository.local.CourseInfoRepository
import com.jacknic.glut.data.repository.local.CourseRepository
import com.jacknic.glut.databinding.DialogCourseDetailsBinding
import com.jacknic.glut.databinding.ItemCourseArrangeBinding
import com.jacknic.glut.util.findBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking

/**
 * 课程详情对话框
 *
 * @author Jacknic
 */
class CourseDetailsDialog(
    private val courseInfo: CourseInfo,
    private val courseList: List<Course>,
    context: Context,
    private val nav: NavController
) : BottomSheetDialog(context) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val bind = DialogCourseDetailsBinding.inflate(layoutInflater)
        bind.apply {
            info = courseInfo
            val empty = courseList.isEmpty()
            rvCourseArrange.isVisible = !empty
            tvEmpty.isVisible = empty
            if (!empty) {
                rvCourseArrange.adapter = object : AbsListAdapter<Course, ItemCourseArrangeBinding>(R.layout.item_course_arrange) {
                    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
                        val binding = holder.itemView.findBinding<ItemCourseArrangeBinding>()
                        binding?.apply {
                            val item = getItem(position)
                            this.course = item
                        }
                    }
                }.apply { submitList(courseList) }
            }
            ivEditCourse.setOnClickListener {
                val toAdd = MainNavDirections.navToAdd(courseInfo.courseNum)
                nav.navigate(toAdd)
                dismiss()
            }
        }
        setContentView(bind.root)
    }

    companion object {
        private val courseInfoRepo by lazy { CourseInfoRepository() }
        private val courseRepo by lazy { CourseRepository() }

        fun build(courseNum: String, context: Context, nav: NavController, selectCourse: Course? = null): BottomSheetDialog? {
            var courseInfo: CourseInfo? = null
            var courseList: List<Course> = emptyList()
            runBlocking(Dispatchers.IO) {
                courseInfo = courseInfoRepo.getByCourseNum(courseNum) ?: return@runBlocking null
                courseList = courseRepo.findByCourseNum(courseNum)
                //.filter { it.classRoom.isNotEmpty() && it.startSection != 0 && it.endSection != 0 }
                selectCourse?.apply {
                    val first = courseList.firstOrNull { it.id == id }
                    first?.apply {
                        val mutableList = courseList.toMutableList()
                        mutableList.remove(this)
                        mutableList.add(0, this)
                        courseList = mutableList
                    }
                }
            }
            return courseInfo?.let { CourseDetailsDialog(it, courseList, context, nav) }
        }
    }
}