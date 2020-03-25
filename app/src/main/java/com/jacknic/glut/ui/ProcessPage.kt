package com.jacknic.glut.ui

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.jacknic.glut.R
import com.jacknic.glut.base.AbsRefreshListPage
import com.jacknic.glut.databinding.ItemProgressBinding
import com.jacknic.glut.databinding.PageProcessBinding
import com.jacknic.glut.util.getBinding
import com.jacknic.glut.viewmodel.ProcessViewModel

/**
 * 学业进度
 *
 * @author Jacknic
 */
class ProcessPage : AbsRefreshListPage<String, ProcessViewModel, PageProcessBinding>() {

    override val layoutResId = R.layout.page_process
    private var normalColor = 0
    private var passColor = 0
    override val vm by viewModels<ProcessViewModel>()
    override fun getSwipeRefresh() = bind.swipeRefresh
    override fun getListView() = bind.gvProgress
    override fun getEmptyLayout() = bind.emptyView.root
    override fun refresh() = vm.fetchProcess()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        passColor = ContextCompat.getColor(requireContext(), R.color.green)
        vm.getElementList().observe(viewLifecycleOwner, Observer { setDataList(it) })
        vm.loadState.observe(viewLifecycleOwner, Observer { bind.swipeRefresh.isRefreshing = it.first })
        bind.swipeRefresh.setOnRefreshListener { vm.fetchProcess() }
    }

    override fun onBindItemView(position: Int, convertView: View?, parent: ViewGroup): View {
        val itemBind = convertView.getBinding<ItemProgressBinding>(parent, R.layout.item_progress)
        if (position == 0 && normalColor == 0) {
            normalColor = itemBind.text1.textColors.defaultColor
        }
        val textItem = mItemList[position]
        val textColor = if ("通过" == textItem) passColor else normalColor
        itemBind.text1.apply {
            text = textItem
            setTextColor(textColor)
        }
        return itemBind.root
    }
}