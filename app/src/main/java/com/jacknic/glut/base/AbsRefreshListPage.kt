package com.jacknic.glut.base

import android.os.Bundle
import android.view.View
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Observer
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.jacknic.glut.viewmodel.BaseRequestViewModel
import com.jacknic.glut.widget.JwcLoginDialogFragment

/**
 * 可刷新页面抽象类
 *
 * @author Jacknic
 */
abstract class AbsRefreshListPage<ItemType, VM : BaseRequestViewModel, Binding : ViewDataBinding>
    : AbsListPage<ItemType, Binding>() {

    protected abstract val vm: VM
    protected abstract fun getSwipeRefresh(): SwipeRefreshLayout

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getSwipeRefresh().apply {
            isEnabled = true
            setOnRefreshListener { refresh() }
        }
        vm.apply {
            needLogin.observe(viewLifecycleOwner, Observer {
                if (it) {
                    showLogin()
                    needLogin.postValue(false)
                }
                getSwipeRefresh().isRefreshing = false
            })
            loadState.observe(viewLifecycleOwner, Observer { getSwipeRefresh().isRefreshing = it.first })
        }
    }

    open fun showLogin() {
        JwcLoginDialogFragment.show(childFragmentManager, Runnable { refresh() })
    }

    abstract fun refresh()
}