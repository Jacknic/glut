package com.jacknic.glut.ui

import android.app.Dialog
import android.os.Bundle
import android.view.*
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.jacknic.glut.R
import com.jacknic.glut.base.BaseListPage
import com.jacknic.glut.data.db.entity.CourseInfo
import com.jacknic.glut.databinding.ItemCourseInfoBinding
import com.jacknic.glut.util.getBinding
import com.jacknic.glut.util.toPage
import com.jacknic.glut.viewmodel.CourseInfoViewModel
import com.jacknic.glut.widget.CourseDetailsDialog

/**
 * 学期课表
 *
 * @author Jacknic
 */
class CourseListPage : BaseListPage<CourseInfo>() {

    override val menuResId = R.menu.add
    private val vm: CourseInfoViewModel by viewModels()
    private var isSelectMode = false
    private val selectedSet = mutableSetOf<CourseInfo>()

    private val backPressedCallback by lazy {
        object : OnBackPressedCallback(false) {
            override fun handleOnBackPressed() {
                if (isSelectMode) {
                    endSelectMode()
                }
            }
        }
    }

    private fun refreshSelectState() {
        val count = mItemList.size
        val checkedItemCount = selectedSet.size
        if (checkedItemCount >= count - 1 || checkedItemCount <= 1) {
            activity?.invalidateOptionsMenu()
        }
        mAdapter.notifyDataSetChanged()
        (activity as? AppCompatActivity)?.apply {
            val selectTips = if (isSelectMode) "已选中 ${checkedItemCount}/${count}" else ""
            supportActionBar?.subtitle = selectTips
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.onBackPressedDispatcher?.addCallback(this, backPressedCallback)
        vm.getTermCourses()
        vm.allCourseInfoList.observe(viewLifecycleOwner, Observer { setDataList(it) })
        bind.lvItemList.apply {
            setOnItemLongClickListener { _, _, position, _ ->
                val courseInfo = mItemList[position]
                if (!isSelectMode) {
                    selectOrRemove(courseInfo)
                    startSelectMode()
                    return@setOnItemLongClickListener true
                }
                false
            }
            setOnItemClickListener { _, _, position, _ ->
                val courseInfo = mItemList[position]
                if (isSelectMode) {
                    selectOrRemove(courseInfo)
                    refreshSelectState()
                } else {
                    if (detailsDialog == null || detailsDialog?.isShowing != true) {
                        detailsDialog = CourseDetailsDialog.build(courseInfo.courseNum, requireContext(), navCtrl)
                        detailsDialog?.show()
                    }
                }
            }
        }
    }

    private var detailsDialog: Dialog? = null

    private fun startSelectMode() {
        isSelectMode = true
        backPressedCallback.isEnabled = true
        refreshSelectState()
    }

    private fun endSelectMode() {
        selectedSet.clear()
        isSelectMode = false
        backPressedCallback.isEnabled = false
        refreshSelectState()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        val menuRes = if (isSelectMode) R.menu.select_actions else R.menu.add
        inflater.inflate(menuRes, menu)
    }

    private fun selectOrRemove(courseInfo: CourseInfo) {
        val remove = selectedSet.remove(courseInfo)
        if (!remove) {
            selectedSet.add(courseInfo)
        }
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        if (isSelectMode) {
            val selectAllItem = menu.findItem(R.id.action_select_all)
            selectAllItem.isVisible = selectedSet.size != mItemList.size
            val notEmpty = selectedSet.isNotEmpty()
            val selectNoneItem = menu.findItem(R.id.action_select_none)
            selectNoneItem.isVisible = notEmpty
            val deleteItem = menu.findItem(R.id.action_delete)
            deleteItem.isVisible = notEmpty
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_select_all -> {
                selectedSet.addAll(mItemList)
                refreshSelectState()
                true
            }
            R.id.action_select_none -> {
                selectedSet.clear()
                refreshSelectState()
                true
            }
            R.id.action_delete -> {
                confirmDialog.show()
                true
            }
            R.id.action_add -> {
                navCtrl.toPage(R.id.modifyCoursePage)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private val confirmDialog by lazy {
        AlertDialog.Builder(requireContext())
            .setMessage(R.string.tips_confirm_delete)
            .setNegativeButton(android.R.string.cancel, null)
            .setPositiveButton(android.R.string.ok) { dialog, _ ->
                dialog.dismiss()
                vm.deleteCourses(selectedSet.toList())
                mItemList.removeAll(selectedSet)
                endSelectMode()
            }
    }

    override fun onBindItemView(position: Int, convertView: View?, parent: ViewGroup): View {
        val itemBind = convertView.getBinding<ItemCourseInfoBinding>(parent, R.layout.item_course_info)
        val courseInfo = mItemList[position]
        itemBind.apply {
            course = courseInfo
            if (isSelectMode) {
                cbCheck.visibility = View.VISIBLE
                val selected = courseInfo in selectedSet
                cbCheck.isChecked = selected
            } else {
                cbCheck.visibility = View.GONE
            }
        }
        return itemBind.root
    }
}