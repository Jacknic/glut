package com.jacknic.glut.widget

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.jacknic.glut.R
import com.jacknic.glut.databinding.DialogStatusBinding
import com.jacknic.glut.databinding.PageLoginBinding
import com.jacknic.glut.util.showDialog
import com.jacknic.glut.viewmodel.BaseLoginViewModel

/**
 * 教务登录对话框
 *
 * @author Jacknic
 */
abstract class BaseLoginDialogFragment<VM : BaseLoginViewModel> : BottomSheetDialogFragment() {

    abstract val vm: VM
    var nextAction: Runnable? = null
    lateinit var bind: PageLoginBinding

    private val statusDialog by lazy {
        AlertDialog.Builder(requireContext(), R.style.Theme_MaterialComponents_BottomSheetDialog)
            .setView(statusDialogBind.root).create()
    }
    private val statusDialogBind by lazy { DialogStatusBinding.inflate(layoutInflater) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        bind = PageLoginBinding.inflate(inflater, container, false)
        bind.ivInputBox.isVisible = false
        bind.root.setBackgroundResource(R.drawable.bg_round_top)
        bind.vm = vm
        vm.captchaBitmap.observe(viewLifecycleOwner, Observer {
            bind.ivCaptcha.setImageBitmap(it)
            bind.inputCaptcha.setText("")
        })
        vm.loadState.observe(viewLifecycleOwner, Observer {
            if (it.first) {
                statusDialogBind.tvStatusMsg.text = it.second
                statusDialog.show()
            } else statusDialog.dismiss()
        })
        return bind.root
    }

    companion object {

        fun <VM : BaseLoginViewModel> showDialog(
            loginDialog: BaseLoginDialogFragment<VM>,
            nextAction: Runnable?,
            manager: FragmentManager
        ) {
            loginDialog.nextAction = nextAction
            loginDialog.showDialog(manager)
        }
    }
}