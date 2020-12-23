package com.jacknic.glut.ui

import android.os.Bundle
import android.view.View
import androidx.core.view.forEachIndexed
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.ViewPager
import com.jacknic.glut.R
import com.jacknic.glut.base.BasePage
import com.jacknic.glut.databinding.PageHomeBinding
import com.jacknic.glut.ui.home.CourseFrag
import com.jacknic.glut.ui.home.MineFrag
import com.jacknic.glut.ui.home.MoreFrag
import com.jacknic.glut.util.resolveColor

/**
 * 主界面
 *
 * @author Jacknic
 */
class HomePage : BasePage<PageHomeBinding>() {

    override val layoutResId = R.layout.page_home
    private val pages = arrayOf<Fragment>(CourseFrag(), MoreFrag(), MineFrag())

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setListeners()
        if (prefer.pinExplore) {
            bind.bnvHome.apply {
                selectedItemId = R.id.page_explore
                menu.findItem(R.id.page_explore).setIcon(R.drawable.ic_amp_stories)
            }
        }
    }

    private fun setListeners() {
        bind.vpHome.apply {
            offscreenPageLimit = 2
            adapter = object : FragmentStatePagerAdapter(childFragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
                override fun getItem(position: Int) = pages[position]
                override fun getCount() = pages.size
            }
            addOnPageChangeListener(object : ViewPager.SimpleOnPageChangeListener() {
                override fun onPageSelected(position: Int) {
                    bind.bnvHome.apply {
                        val item = menu.getItem(position)
                        selectedItemId = item.itemId
                        activity?.title = item.title
                    }
                }
            })
        }
        bind.bnvHome.apply {
            setBackgroundColor(context.resolveColor(R.attr.colorSurface))
            setOnNavigationItemSelectedListener {
                menu.forEachIndexed { index, item ->
                    if (it == item) {
                        bind.vpHome.setCurrentItem(index, true)
                    }
                }
                return@setOnNavigationItemSelectedListener true
            }
            setOnNavigationItemReselectedListener {
                when (it.itemId) {
                    R.id.page_explore -> {
                        val pin = !prefer.pinExplore
                        prefer.pinExplore = pin
                        val icoRes = if (pin) R.drawable.ic_amp_stories else R.drawable.ic_explore
                        menu.findItem(R.id.page_explore).setIcon(icoRes)
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        bind.bnvHome.apply {
            activity?.title = menu.findItem(selectedItemId).title
        }
    }

}