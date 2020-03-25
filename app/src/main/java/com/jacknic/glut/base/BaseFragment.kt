package com.jacknic.glut.base

import android.os.Bundle
import android.view.*
import androidx.annotation.LayoutRes
import androidx.annotation.MenuRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.jacknic.glut.util.Preferences

/**
 * Fragment 基类
 *
 * @author Jacknic
 */
abstract class BaseFragment<T : ViewDataBinding> : Fragment() {

    /**
     * 无效菜单资源
     **/
    private val invalidMenuRes = 0
    protected val navCtrl by lazy { findNavController() }
    protected val prefer = Preferences.getInstance()
    protected lateinit var bind: T
    /**
     * 布局资源
     */
    abstract val layoutResId: Int
        @LayoutRes get

    /**
     * 菜单资源
     */
    @MenuRes
    open val menuResId = invalidMenuRes

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedState: Bundle?): View? {
        if (menuResId != invalidMenuRes) {
            setHasOptionsMenu(true)
        }
        bind = DataBindingUtil.inflate(inflater, layoutResId, container, false)
        return bind.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        if (menuResId != invalidMenuRes) {
            inflater.inflate(menuResId, menu)
        }
    }

}