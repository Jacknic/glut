package com.jacknic.glut.ui.home

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.core.net.toUri
import com.jacknic.glut.R
import com.jacknic.glut.base.BaseFragment
import com.jacknic.glut.data.util.FILE_NAME_HEAD_IMAGE
import com.jacknic.glut.databinding.FragMineBinding
import com.jacknic.glut.util.themeColor
import com.jacknic.glut.util.toPage
import java.io.File

/**
 * 我的
 *
 * @author Jacknic
 */
class MineFrag : BaseFragment<FragMineBinding>() {

    override val layoutResId = R.layout.frag_mine

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (prefer.tintToolbar) {
            bind.rlAvatar.apply {
                backgroundTintList = ColorStateList.valueOf(context.themeColor())
            }
            bind.tvUsername.apply {
                setTextColor(Color.WHITE)
                bind.tvClassName.setTextColor(textColors.withAlpha(127))
            }
        }
        bind.student = prefer.student
        bind.rlAvatar.setOnClickListener { navCtrl.toPage(R.id.infoPage) }
        bind.btnEduInfo.setOnClickListener { navCtrl.toPage(R.id.infoPage) }
        bind.btnEduSchedule.setOnClickListener { navCtrl.toPage(R.id.processPage) }
        bind.btnExam.setOnClickListener { navCtrl.toPage(R.id.examListPage) }
        bind.btnGrade.setOnClickListener { navCtrl.toPage(R.id.gradePage) }
        bind.btnSettings.setOnClickListener { navCtrl.toPage(R.id.settingPage) }

        val imgFile = File(requireContext().filesDir, FILE_NAME_HEAD_IMAGE)
        if (imgFile.isFile) {
            bind.ivAvatar.setImageURI(imgFile.toUri())
        }
    }
}

