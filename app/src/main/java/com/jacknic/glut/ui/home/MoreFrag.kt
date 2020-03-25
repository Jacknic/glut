package com.jacknic.glut.ui.home

import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.jacknic.glut.R
import com.jacknic.glut.base.BaseFragment
import com.jacknic.glut.data.db.entity.Course
import com.jacknic.glut.data.util.URL_GLUT_JW
import com.jacknic.glut.data.util.URL_GLUT_LIB
import com.jacknic.glut.databinding.FragMoreBinding
import com.jacknic.glut.databinding.ItemCourseTodayBinding
import com.jacknic.glut.util.getBinding
import com.jacknic.glut.util.toBrowser
import com.jacknic.glut.util.toPage
import com.jacknic.glut.viewmodel.CourseViewModel
import com.jacknic.glut.viewmodel.CwcInfoViewModel
import com.jacknic.glut.widget.CourseDetailsDialog

/**
 * 更多页
 *
 * @author Jacknic
 */
class MoreFrag : BaseFragment<FragMoreBinding>() {

    override val layoutResId = R.layout.frag_more
    private val courseList = mutableListOf<Course>()
    private val cwVm by activityViewModels<CwcInfoViewModel>()
    private val courseVm by activityViewModels<CourseViewModel>()
    private val todayCourses = mutableListOf<Course>()
    private lateinit var mAdapter: ArrayAdapter<Course>
    private var detailsDialog: Dialog? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setListeners()
        setObserves()
    }

    private fun setListeners() {
        bind.swipeRefresh.apply {
            setOnRefreshListener {
                postDelayed({ isRefreshing = false }, 2000L)
                courseVm.notifyChanged()
                bind.tvFinance.setText(R.string.data_loading)
                cwVm.fetchBalance()
            }
        }
        bind.tvFinance.setOnClickListener {
            navCtrl.toPage(R.id.financePage)
        }

        bind.tvJwc.setOnClickListener {
            navCtrl.toBrowser(URL_GLUT_JW + "index_new.jsp", "教务处")
        }
        bind.tvLib.setOnClickListener {
            navCtrl.toBrowser(URL_GLUT_LIB + "opac/index", "图书馆")
        }
        mAdapter = object : ArrayAdapter<Course>(requireContext(), 0, todayCourses) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val binding = convertView.getBinding<ItemCourseTodayBinding>(parent, R.layout.item_course_today)
                val item = getItem(position)
                binding.course = item
                return binding.root
            }
        }
        bind.lvTodayCourseList.apply {
            emptyView = bind.emptyView.root
            setOnItemClickListener { _, _, position, _ ->
                if (detailsDialog?.isShowing != true) {
                    val course = todayCourses[position]
                    detailsDialog = CourseDetailsDialog.build(course.courseNum, requireContext(), navCtrl, course)
                    detailsDialog?.show()
                }
            }
            adapter = mAdapter
        }
    }

    private fun setObserves() {
        courseVm.getTodayCourseList().observe(viewLifecycleOwner, Observer {
            todayCourses.clear()
            todayCourses.addAll(it)
            bind.tvTodayCourses.isVisible = todayCourses.isNotEmpty()
            mAdapter.notifyDataSetChanged()
        })
        cwVm.balance.observe(viewLifecycleOwner, Observer {
            bind.tvFinance.text = it
            bind.swipeRefresh.isRefreshing = false
        })
    }

}