package com.jacknic.glut.ui

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.jacknic.glut.R
import com.jacknic.glut.base.BaseRefreshListPage
import com.jacknic.glut.data.model.Exam
import com.jacknic.glut.databinding.ItemExamBinding
import com.jacknic.glut.util.getBinding
import com.jacknic.glut.util.resolveColor
import com.jacknic.glut.viewmodel.ExamListViewModel
import com.orhanobut.logger.Logger
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


/**
 * 考试安排
 *
 * @author Jacknic
 */
class ExamListPage : BaseRefreshListPage<Exam, ExamListViewModel>() {

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA)
    private val currTime = System.currentTimeMillis()
    private val activeColor by lazy { requireContext().resolveColor(R.attr.colorPrimary) }
    override val vm by viewModels<ExamListViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setObserve()
    }

    override fun refresh() {
        this.vm.fetchExam()
    }

    private fun setObserve() {
        this.vm.getExamList().observe(viewLifecycleOwner, Observer { setDataList(it) })
    }

    override fun onBindItemView(position: Int, convertView: View?, parent: ViewGroup): View {
        val itemBind = convertView.getBinding<ItemExamBinding>(parent, R.layout.item_exam)
        val exam = mItemList[position]
        val valid = validTime(exam)
        itemBind.apply {
            if (valid) {
                ivIcon.imageTintList = ColorStateList.valueOf(activeColor)
                ivIcon.setImageResource(R.drawable.ic_border_color)
                tvExamName.setTextColor(activeColor)
                root.alpha = 1F
            } else {
                val textColors = tvExamLocation.textColors
                ivIcon.imageTintList = textColors//ColorStateList.valueOf(invalidColor)
                ivIcon.setImageResource(R.drawable.ic_assignment_turned_in)
                tvExamName.setTextColor(textColors)
                root.alpha = 0.5F
            }
        }
        itemBind.exam = exam
        return itemBind.root
    }

    private fun validTime(exam: Exam): Boolean {
        try {
            val date = dateFormat.parse(exam.time)
            return date?.let { it.time >= currTime } ?: false
        } catch (e: ParseException) {
            Logger.e(e, "日期转换失败")
        }
        return true
    }
}