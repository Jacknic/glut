package com.jacknic.glut.base

import android.widget.AbsListView
import com.jacknic.glut.R
import com.jacknic.glut.databinding.PageBaseListBinding

/**
 * 列表页基类
 *
 * @author Jacknic
 */
abstract class BaseListPage<ItemType> : AbsListPage<ItemType, PageBaseListBinding>() {

    override val layoutResId = R.layout.page_base_list
    override fun getListView(): AbsListView = bind.lvItemList
    override fun getEmptyLayout() = bind.emptyView.root

}