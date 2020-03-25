package com.jacknic.glut.ui

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.google.android.material.snackbar.Snackbar
import com.jacknic.glut.R
import com.jacknic.glut.base.AbsRefreshListPage
import com.jacknic.glut.databinding.ItemInfoBarBinding
import com.jacknic.glut.databinding.PageGradeBinding
import com.jacknic.glut.util.getBinding
import com.jacknic.glut.viewmodel.GradeListViewModel
import org.jsoup.nodes.Element
import org.jsoup.select.Elements
import java.util.*

/**
 * 成绩查询
 *
 * @author Jacknic
 */
class GradeListPage : AbsRefreshListPage<Triple<String, String, Boolean>, GradeListViewModel, PageGradeBinding>() {

    override val layoutResId = R.layout.page_grade
    override val vm by viewModels<GradeListViewModel>()
    override fun getSwipeRefresh() = bind.swipeRefresh
    override fun getListView() = bind.lvItemList
    override fun getEmptyLayout() = bind.emptyView.root
    private var gradeList = Elements()
    private var filterGradeList: List<Element> = emptyList()
    private val failedColor = 0xFFF44336.toInt()
    private val yearList = mutableListOf<String>()
    private lateinit var terms: Array<String>
    private var detailsDialog: Dialog? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedState: Bundle?): View? {
        val view = super.onCreateView(inflater, container, savedState)
        val calendar = Calendar.getInstance()
        val spring = calendar[Calendar.MONTH] < Calendar.JULY
        val yearNow = calendar.get(Calendar.YEAR)
        terms = resources.getStringArray(R.array.terms)
        yearList.add("全部")
        for (i in 0..5) {
            yearList.add("${yearNow - i}")
        }
        bind.spYear.apply {
            adapter = ArrayAdapter<String>(requireContext(), android.R.layout.simple_list_item_1, yearList)
            setSelection(if (spring) 2 else 1, false)
            onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {}
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    showSelect()
                }
            }
        }
        bind.spTerm.apply {
            setSelection(if (spring) 2 else 1, false)
            onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {}
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    showSelect()
                }
            }
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        vm.getGradeList().observe(viewLifecycleOwner, Observer {
            gradeList = it
            showSelect()
        })
        bind.lvItemList.apply {
            setOnItemClickListener { _, _, position, _ ->
                if (detailsDialog?.isShowing == true) {
                    return@setOnItemClickListener
                }
                if (position != 0) {
                    val header = filterGradeList[0]
                    val item = filterGradeList[position]
                    val sb = StringBuilder()
                    header.children().forEachIndexed { index, head ->
                        val value = item.child(index)
                        sb.append("${head.text()}：${value.text()}\n")
                    }
                    detailsDialog = AlertDialog.Builder(requireContext())
                        .setTitle(R.string.grade_details)
                        .setMessage(sb)
                        .setPositiveButton(android.R.string.ok, null)
                        .create()
                    detailsDialog?.show()
                }
            }
        }
    }

    override fun onBindItemView(position: Int, convertView: View?, parent: ViewGroup): View {
        val binding = convertView.getBinding<ItemInfoBarBinding>(parent, R.layout.item_info_bar)
        val item = mItemList[position]
        binding.apply {
            val grade = item.second
            val color = if (item.third) failedColor else tvKey.textColors.defaultColor
            tvValue.setTextColor(color)
            key = item.first
            value = grade
        }
        return binding.root
    }

    override fun refresh() {
        vm.fetchGrade()
    }

    private fun showSelect() {
        val yearIndex = bind.spYear.selectedItemPosition
        val termIndex = bind.spTerm.selectedItemPosition
        val year = if (yearIndex == 0) "" else yearList[yearIndex]
        val term = if (termIndex == 0) "" else terms[termIndex]
        val filterList = gradeList.filterIndexed(fun(index: Int, element: Element): Boolean {
            // 取表头
            if (index == 0) return true
            var selectThisYear = true
            if (year.isNotBlank()) {
                val text = element.child(0).text().trim()
                selectThisYear = year == text
            }
            if (!selectThisYear) return false

            var selectThisTerm = true
            if (term.isNotBlank()) {
                selectThisTerm = element.child(1).text().contains(term)
            }
            return selectThisYear && selectThisTerm
        })
        mItemList.clear()
        filterList.forEach {
            val courseName = it.child(4).text()
            val grade = it.child(7).text()
            val failedState = it.child(20).text()
            mItemList.add(Triple(courseName, grade, "不及格" == failedState))
        }
        if (mItemList.size <= 1) {
            mItemList.clear()
        }
        filterGradeList = filterList
        val countMsg = getString(R.string.message_grade_count, mItemList.size)
        Snackbar.make(bind.swipeRefresh, countMsg, Snackbar.LENGTH_SHORT).show()
        mAdapter.notifyDataSetChanged()
    }
}