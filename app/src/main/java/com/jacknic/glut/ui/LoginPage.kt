package com.jacknic.glut.ui

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.jacknic.glut.R
import com.jacknic.glut.base.BasePage
import com.jacknic.glut.databinding.DialogStatusBinding
import com.jacknic.glut.databinding.PageLoginBinding
import com.jacknic.glut.util.topPage
import com.jacknic.glut.viewmodel.JwcLoginViewModel

/**
 * 登录页
 *
 * @author Jacknic
 */
class LoginPage : BasePage<PageLoginBinding>() {

    override val layoutResId = R.layout.page_login
    override val menuResId = R.menu.enter
    private val vm by viewModels<JwcLoginViewModel>()
    private val statusDialogBind by lazy { DialogStatusBinding.inflate(layoutInflater) }
    private val statusDialog by lazy {
        AlertDialog.Builder(requireContext(), R.style.Theme_MaterialComponents_BottomSheetDialog)
            .setView(statusDialogBind.root)
            .setCancelable(false)
            .create()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (prefer.logged) {
            navCtrl.topPage(R.id.homePage)
            return
        }
        activity?.setTitle(R.string.login)
        bind.vm = vm
        setObserves()
    }

    private fun setObserves() {
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
        vm.captchaPass.observe(viewLifecycleOwner, Observer { bind.ivCaptchaPass.isChecked = it })
        vm.student.observe(viewLifecycleOwner, Observer { goHome() })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_enter -> {
                goHome()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun goHome() {
        statusDialog.dismiss()
        prefer.logged = true
        navCtrl.topPage(R.id.homePage)
    }

}