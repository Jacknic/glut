package com.jacknic.glut.widget

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.jacknic.glut.viewmodel.CwcLoginViewModel

/**
 * 财务登录对话框
 *
 * @author Jacknic
 */
class CwcLoginDialogFragment : BaseLoginDialogFragment<CwcLoginViewModel>() {

    override val vm by viewModels<CwcLoginViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bind.btnLogin.text = "登录财务"
        bind.ivCaptchaPass.isVisible = false
        vm.user.observe(viewLifecycleOwner, Observer {
            nextAction?.run()
            dismiss()
        })
    }

    companion object {

        /**
         * 显示财务登录对话框
         **/
        fun show(manager: FragmentManager, nextAction: Runnable?) {
            showDialog(CwcLoginDialogFragment(), nextAction, manager)
        }
    }
}