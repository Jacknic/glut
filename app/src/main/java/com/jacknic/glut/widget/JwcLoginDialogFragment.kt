package com.jacknic.glut.widget

import android.os.Bundle
import android.view.View
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.jacknic.glut.viewmodel.JwcLoginViewModel

/**
 * 教务登录对话框
 *
 * @author Jacknic
 */
class JwcLoginDialogFragment : BaseLoginDialogFragment<JwcLoginViewModel>() {

    override val vm by viewModels<JwcLoginViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        vm.captchaPass.observe(viewLifecycleOwner, Observer { bind.ivCaptchaPass.isChecked = it })
        vm.student.observe(viewLifecycleOwner, Observer {
            nextAction?.run()
            dismiss()
        })
    }

    companion object {

        /**
         * 显示教务登录对话框
         **/
        fun show(manager: FragmentManager, nextAction: Runnable?) {
            showDialog(JwcLoginDialogFragment(), nextAction, manager)
        }
    }
}