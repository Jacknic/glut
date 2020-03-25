package com.jacknic.glut.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import com.jacknic.glut.R
import com.jacknic.glut.base.BaseFragment
import com.jacknic.glut.util.topPage

/**
 * 过渡页面
 *
 * @author Jacknic
 */
class SplashPage : BaseFragment<ViewDataBinding>() {

    override val layoutResId = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedState: Bundle?): View? {
        if (prefer.logged) {
            navCtrl.topPage(R.id.homePage)
        } else {
            navCtrl.topPage(R.id.loginPage)
        }
        return null
    }
}