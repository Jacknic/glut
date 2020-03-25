package com.jacknic.glut.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import com.jacknic.glut.R
import com.jacknic.glut.base.AbsListPage
import com.jacknic.glut.data.util.FILE_NAME_HEAD_IMAGE
import com.jacknic.glut.databinding.ItemInfoBarBinding
import com.jacknic.glut.databinding.PageInfoBinding
import com.jacknic.glut.util.getBinding
import com.jacknic.glut.util.openFile
import java.io.File

/**
 * 学籍信息
 *
 * @author Jacknic
 */
class InfoPage : AbsListPage<Pair<String, String>, PageInfoBinding>() {

    override val layoutResId = R.layout.page_info
    override fun getListView() = bind.lvInfo
    override fun getEmptyLayout() = bind.emptyView.root

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedState: Bundle?): View? {
        loadData()
        return super.onCreateView(inflater, container, savedState)
    }

    private fun loadData() {
        prefer.student?.apply {
            mItemList.add("姓名" to name)
            mItemList.add("学号" to sid)
            mItemList.add("班级" to className)
            mItemList.add("出生日期" to birthday)
            mItemList.add("籍贯" to place)
            mItemList.add("证件号" to id)
            mItemList.add("民族" to nation)
            mItemList.add("政治面貌" to role)
            mItemList.add("文化程度" to level)
            mItemList.add("学生来源" to origin)
            mItemList.add("高考分数" to score)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val imgFile = File(requireContext().filesDir, FILE_NAME_HEAD_IMAGE)
        bind.ivAvatar.apply {
            if (imgFile.isFile) {
                visibility = View.VISIBLE
                setOnClickListener { requireContext().openFile(imgFile, "查看头像") }
                setImageURI(imgFile.toUri())
            } else {
                visibility = View.INVISIBLE
            }
        }
    }

    override fun onBindItemView(position: Int, convertView: View?, parent: ViewGroup): View {
        val binding = convertView.getBinding<ItemInfoBarBinding>(parent, R.layout.item_info_bar)
        mItemList[position].apply {
            binding.key = first
            binding.value = second
        }
        return binding.root
    }

}