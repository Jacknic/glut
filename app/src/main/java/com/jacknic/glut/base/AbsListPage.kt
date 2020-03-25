package com.jacknic.glut.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.ArrayAdapter
import androidx.databinding.ViewDataBinding

/**
 * 列表页抽象类
 *
 * @author Jacknic
 */
abstract class AbsListPage<ItemType, Binding : ViewDataBinding> : BasePage<Binding>() {

    protected val mItemList = mutableListOf<ItemType>()
    protected lateinit var mAdapter: ArrayAdapter<ItemType>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedState: Bundle?): View? {
        val view = super.onCreateView(inflater, container, savedState)
        mAdapter = object : ArrayAdapter<ItemType>(requireContext(), 0, mItemList) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup) =
                onBindItemView(position, convertView, parent)
        }
        getListView().apply {
            adapter = mAdapter
            emptyView = getEmptyLayout()
        }
        return view
    }

    fun setDataList(dataList: List<ItemType>?) {
        mItemList.clear()
        if (dataList != null) {
            mItemList.addAll(dataList)
        }
        mAdapter.notifyDataSetChanged()
    }

    abstract fun getListView(): AbsListView
    abstract fun getEmptyLayout(): View?
    abstract fun onBindItemView(position: Int, convertView: View?, parent: ViewGroup): View

}