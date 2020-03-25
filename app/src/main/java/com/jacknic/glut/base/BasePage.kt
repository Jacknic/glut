package com.jacknic.glut.base

import android.os.Bundle
import androidx.databinding.ViewDataBinding
import com.tencent.stat.StatService

/**
 * 页面基类
 *
 * @author Jacknic
 */
abstract class BasePage<T : ViewDataBinding> : BaseFragment<T>() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        StatService.trackBeginPage(requireContext(), javaClass.simpleName)
    }

    override fun onDestroy() {
        super.onDestroy()
        StatService.trackEndPage(requireContext(), javaClass.simpleName)
    }
}