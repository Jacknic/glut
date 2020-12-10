package com.jacknic.glut.ui.home

import android.os.Bundle
import android.view.View
import androidx.core.net.toUri
import com.jacknic.glut.R
import com.jacknic.glut.base.BaseFragment
import com.jacknic.glut.data.util.FILE_NAME_HEAD_IMAGE
import com.jacknic.glut.databinding.FragMineBinding
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
        bind.student = prefer.student
        bind.rlAvatar.setOnClickListener { navCtrl.toPage(R.id.infoPage) }
        bind.btnEduInfo.setOnClickListener { navCtrl.toPage(R.id.infoPage) }
        bind.btnEduSchedule.setOnClickListener { navCtrl.toPage(R.id.processPage) }
        bind.btnExam.setOnClickListener { navCtrl.toPage(R.id.examListPage) }
        bind.btnGrade.setOnClickListener { navCtrl.toPage(R.id.gradePage) }
        bind.btnSettings.setOnClickListener { navCtrl.toPage(R.id.settingPage) }
        bind.ivAvatar.apply {
            setOnClickListener {
                prefer.showHead = !prefer.showHead
                if (prefer.showHead) showHeadImg() else setImageDrawable(null)
            }
        }
        if (prefer.showHead) {
            showHeadImg()
        }
    }

    private fun showHeadImg() {
        val imgFile = File(requireContext().filesDir, FILE_NAME_HEAD_IMAGE)
        if (imgFile.isFile) {
            bind.ivAvatar.setImageURI(imgFile.toUri())
        }
    }
}

