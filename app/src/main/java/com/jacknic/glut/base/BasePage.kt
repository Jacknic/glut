package com.jacknic.glut.base

import android.os.Bundle
import androidx.databinding.ViewDataBinding

/**
 * 页面基类
 *
 * @author Jacknic
 */
abstract class BasePage<T : ViewDataBinding> : BaseFragment<T>() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}