package com.jacknic.glut.util

import android.os.Bundle
import androidx.annotation.IdRes
import androidx.navigation.NavController
import androidx.navigation.navOptions
import com.jacknic.glut.MainNavDirections
import com.jacknic.glut.R

val defaultNavOptions = navOptions {
    anim {
        enter = R.anim.slide_in_right
        exit = R.anim.slide_out_left
        popEnter = R.anim.slide_in_left
        popExit = R.anim.slide_out_right
    }
}

/**
 * 页面跳转
 **/
fun NavController.toBrowser(url: String, title: String) {
    val directions = MainNavDirections.navToBrowserPage(url, title)
    navigate(directions)
}

/**
 * 页面跳转
 **/
fun NavController.toPage(@IdRes pageId: Int, args: Bundle? = null) {
    navigate(pageId, args, defaultNavOptions)
}

/**
 * 顶级页面
 *
 * <i>清空栈</i>
 */
fun NavController.topPage(@IdRes topPageId: Int) {
    navigate(topPageId, null, navOptions {
        popUpTo(R.id.main_nav) { inclusive = false }
    })
}