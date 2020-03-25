package com.jacknic.glut.base

import android.widget.AbsListView
import com.jacknic.glut.R
import com.jacknic.glut.databinding.PageBaseListBinding
import com.jacknic.glut.viewmodel.BaseRequestViewModel

/**
 * 可刷新页面基类
 *
 * @author Jacknic
 */
abstract class BaseRefreshListPage<ItemType, VM : BaseRequestViewModel>
    : AbsRefreshListPage<ItemType, VM, PageBaseListBinding>() {

    override val layoutResId = R.layout.page_base_list
    override fun getListView(): AbsListView = bind.lvItemList
    override fun getEmptyLayout() = bind.emptyView.root
    override fun getSwipeRefresh() = bind.swipeRefresh

}