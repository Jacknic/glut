package com.jacknic.glut.util

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager

/**
 * 获取绑定对象
 */
fun <T : ViewDataBinding> View?.getBinding(parent: ViewGroup, @LayoutRes layoutRes: Int): T {
    val binding = this.findBinding<T>()
    return binding
        ?: DataBindingUtil.inflate(LayoutInflater.from(parent.context), layoutRes, parent, false)
}

/**
 * 查找绑定对象
 */
fun <T : ViewDataBinding> View?.findBinding() = this?.let { DataBindingUtil.findBinding<T>(it) }

/**
 * 显示为对话框
 */
fun DialogFragment.showDialog(fragmentManager: FragmentManager, tag: String? = null) {
    if (!isAdded) {
        val realTag = tag ?: this.javaClass.name
        showNow(fragmentManager, realTag)
    }
}