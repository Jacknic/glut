package com.jacknic.glut.ui

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.jacknic.glut.R
import com.jacknic.glut.base.AbsListPage
import com.jacknic.glut.data.db.entity.CourseInfo
import com.jacknic.glut.databinding.DialogPullTermBinding
import com.jacknic.glut.databinding.ItemTermBinding
import com.jacknic.glut.databinding.PageTermListBinding
import com.jacknic.glut.util.getBinding
import com.jacknic.glut.util.toast
import com.jacknic.glut.viewmodel.CourseInfoViewModel
import com.jacknic.glut.viewmodel.CourseViewModel
import com.jacknic.glut.widget.JwcLoginDialogFragment
import java.util.*

/**
 * 所有课表
 *
 * @author Jacknic
 */
class TermListPage : AbsListPage<CourseInfo, PageTermListBinding>() {

    override val menuResId = R.menu.pull
    private var selectedItem: CourseInfo? = null
    private var nextAction: Runnable? = null
    private var selectedYear = 0
    private var selectedTerm = 1
    private val courseInfoVm by viewModels<CourseInfoViewModel>()
    private val courseVm by activityViewModels<CourseViewModel>()
    private val pullDialog by lazy { buildPullTermDialog() }
    override val layoutResId = R.layout.page_term_list
    override fun getListView() = bind.lvTermList
    override fun getEmptyLayout() = bind.emptyView.root
    private var switchDialog: AlertDialog? = null
    private var deleteDialog: AlertDialog? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setListeners()
        setObserves()
    }

    private fun setListeners() {
        bind.lvTermList.setOnItemLongClickListener { _, _, position, _ ->
            val courseInfo = mItemList[position]
            if (deleteDialog?.isShowing != true) {
                deleteDialog = AlertDialog.Builder(requireContext())
                    .setTitle(R.string.tips_delete_term)
                    .setMessage(R.string.delete_term_info)
                    .setNegativeButton(android.R.string.cancel, null)
                    .setPositiveButton(android.R.string.ok) { dialog, _ ->
                        courseInfoVm.deleteTerm(courseInfo).start()
                        dialog.dismiss()
                        if (courseInfo == selectedItem) {
                            courseVm.notifyChanged()
                        }
                    }
                    .show()
            }
            true
        }
        bind.lvTermList.setOnItemClickListener { _, _, position, _ ->
            val courseInfo = mItemList[position]
            val selected = courseInfo == selectedItem
            if (!selected && switchDialog?.isShowing != true) {
                switchDialog = AlertDialog.Builder(requireContext())
                    .setMessage(R.string.tips_switch_term)
                    .setNegativeButton(android.R.string.cancel, null)
                    .setPositiveButton(android.R.string.ok) { dialog, _ ->
                        selectedItem = courseInfo
                        mAdapter.notifyDataSetChanged()
                        prefer.term = courseInfo.term
                        prefer.schoolYear = courseInfo.schoolYear
                        courseVm.notifyChanged()
                        dialog.dismiss()
                    }
                    .show()
            }
        }
    }

    private fun setObserves() {
        courseInfoVm.loadTermList()
        courseInfoVm.termCourseInfoList.observe(viewLifecycleOwner, Observer {
            val schoolYear = prefer.schoolYear
            val term = prefer.term
            selectedItem = it.firstOrNull { courseIfo ->
                schoolYear == courseIfo.schoolYear && term == courseIfo.term
            }
            setDataList(it)
        })
        courseInfoVm.needLogin.observe(viewLifecycleOwner, Observer {
            if (it) {
                JwcLoginDialogFragment.show(childFragmentManager, nextAction)
                nextAction = null
                courseInfoVm.needLogin.postValue(false)
            }
        })
    }

    override fun onBindItemView(position: Int, convertView: View?, parent: ViewGroup): View {
        val itemBind = convertView.getBinding<ItemTermBinding>(parent, R.layout.item_term)
        val courseInfo = mItemList[position]
        val selected = courseInfo == selectedItem
        itemBind.courseInfo = courseInfo
        itemBind.ivSelect.isSelected = selected
        return itemBind.root
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId == R.id.action_pull) {
            pullDialog.show()
            true
        } else {
            super.onOptionsItemSelected(item)
        }
    }

    private fun buildPullTermDialog(): BottomSheetDialog {
        val dialogBind = DialogPullTermBinding.inflate(layoutInflater)
        val dialog = BottomSheetDialog(requireContext())
        dialog.setContentView(dialogBind.root)
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        selectedYear = year
        val yearList = mutableListOf<String>()
        for (i in 0..4) {
            yearList.add("${year - i}")
        }
        dialogBind.apply {
            spYear.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) = Unit
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    val itemValue = yearList[position]
                    selectedYear = itemValue.toIntOrNull() ?: year
                }
            }
            spYear.adapter = ArrayAdapter<String>(requireContext(), android.R.layout.simple_list_item_1, yearList)
            rgTerms.setOnCheckedChangeListener { _, checkedId ->
                selectedTerm = if (checkedId == R.id.rbAutumn) 2 else 1
            }
            btnPull.setOnClickListener {
                mItemList.forEach {
                    if (it.schoolYear == selectedYear && it.term == selectedTerm) {
                        requireContext().toast("该学期已存在！")
                        return@setOnClickListener
                    }
                }
                pullDialog.dismiss()
                nextAction = Runnable {
                    courseInfoVm.fetchTerm(selectedYear, selectedTerm)
                }
                nextAction?.run()
            }
        }
        return dialog
    }
}