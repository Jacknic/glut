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
import kotlinx.android.synthetic.main.page_home.*

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
                    bnvHome.apply {
                        val item = menu.getItem(position)
                        selectedItemId = item.itemId
                        activity?.title = item.title
                    }
                }
            })
        }
        bind.bnvHome.apply {
            setOnNavigationItemSelectedListener {
                menu.forEachIndexed { index, item ->
                    if (it == item) {
                        vpHome.setCurrentItem(index, true)
                    }
                }
                return@setOnNavigationItemSelectedListener true
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