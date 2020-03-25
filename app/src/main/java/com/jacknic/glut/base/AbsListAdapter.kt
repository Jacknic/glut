package com.jacknic.glut.base

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import java.util.*

/**
 * 普通列表抽象类
 *
 * @author Jacknic
 */
abstract class AbsListAdapter<T : Any, Binding : ViewDataBinding>(
    @LayoutRes private val itemRes: Int
) : ListAdapter<T, RecyclerView.ViewHolder>(
    object : DiffUtil.ItemCallback<T>() {
        override fun areItemsTheSame(oldItem: T, newItem: T) = oldItem == newItem
        override fun areContentsTheSame(oldItem: T, newItem: T) = Objects.equals(oldItem, newItem)
    }
) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = DataBindingUtil.inflate<Binding>(inflater, itemRes, parent, false)
        return object : RecyclerView.ViewHolder(binding.root) {}
    }
}