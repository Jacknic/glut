package com.jacknic.glut.ui

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.appcompat.app.AlertDialog
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.activityViewModels
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.jacknic.glut.MainActivity
import com.jacknic.glut.R
import com.jacknic.glut.base.BasePage
import com.jacknic.glut.data.repository.remote.JwcRepository
import com.jacknic.glut.data.util.URL_FEEDBACK
import com.jacknic.glut.data.util.URL_RELEASE_LOG
import com.jacknic.glut.databinding.DialogColorChoiceBinding
import com.jacknic.glut.databinding.ItemColorChoiceBinding
import com.jacknic.glut.databinding.PageSettingBinding
import com.jacknic.glut.util.*
import com.jacknic.glut.viewmodel.AppViewModel

/**
 * 设置
 *
 * @author Jacknic
 */
class SettingPage : BasePage<PageSettingBinding>(), SharedPreferences.OnSharedPreferenceChangeListener {

    private val restartActions = arrayOf(KEY_THEME_INDEX, KEY_THEME_MODE, KEY_TINT_TOOLBAR)
    override val layoutResId = R.layout.page_setting
    private val appVm by activityViewModels<AppViewModel>()
    private val colorListDialog by lazy { buildColorChoiceDialog() }
    private val themeModeDialog by lazy { buildThemeModeDialog() }
    private val exitDialog by lazy { buildExitDialog() }
    private val aboutDialog by lazy {
        BottomSheetDialog(requireContext()).apply {
            setContentView(R.layout.dialog_about)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setListeners()
    }

    private fun setListeners() {
        val modeArray = resources.getStringArray(R.array.theme_mode_array)
        val mode = modeArray[prefer.themeMode]
        bind.let {
            it.prefer = prefer
            it.btnThemeMode.append("($mode)")
            if (prefer.nightTheme) {
                it.btnThemeMode.setIconResource(R.drawable.ic_moon)
            }
        }
        prefer.registerListener(this)
        bind.apply {
            btnThemeColor.setOnClickListener { colorListDialog.show() }
            btnThemeMode.setOnClickListener { themeModeDialog.show() }
            btnLogout.setOnClickListener { exitDialog.show() }
            btnUpdateCheck.setOnClickListener {
                requireContext().toast("正在检查版本更新...")
                appVm.checkUpdate(true)
            }
            btnReleaseLog.setOnClickListener { navCtrl.toBrowser(URL_RELEASE_LOG, getString(R.string.release_log)) }
            btnFeedback.setOnClickListener { navCtrl.toBrowser(URL_FEEDBACK, getString(R.string.feedback)) }
            btnAbout.setOnClickListener { aboutDialog.show() }
        }
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
        if (key in restartActions) {
            activity?.apply {
                window.setWindowAnimations(android.R.style.Animation_Toast)
                window.decorView.postDelayed({ recreate() }, 300L)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        prefer.unregisterListener(this)
    }

    private fun buildColorChoiceDialog(): BottomSheetDialog {
        val binding = DialogColorChoiceBinding.inflate(layoutInflater)
        val selectedIndex = prefer.themeIndex
        val dialog = BottomSheetDialog(requireContext())
        dialog.setContentView(binding.root)
        binding.gvColors.apply {
            adapter = object : ArrayAdapter<Int>(context, 0, THEME_COLOR_RES) {
                override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                    val itemBind: ItemColorChoiceBinding = convertView.getBinding(parent, R.layout.item_color_choice)
                    val selected = position == selectedIndex
                    val imgRes = if (selected) R.drawable.ic_check_circle else R.drawable.ic_lens
                    val colorRes = getItem(position) ?: R.color.black
                    val tintColor = ResourcesCompat.getColor(parent.resources, colorRes, null)
                    itemBind.ivColor.apply {
                        setImageResource(imgRes)
                        setColorFilter(tintColor)
                    }
                    return itemBind.root
                }
            }
            setOnItemClickListener { _, _, position, _ ->
                dialog.dismiss()
                prefer.themeIndex = position
            }
        }
        return dialog
    }

    private fun buildThemeModeDialog(): AlertDialog {
        val modeArray = resources.getStringArray(R.array.theme_mode_array)
        return AlertDialog.Builder(requireContext())
            .setSingleChoiceItems(modeArray, prefer.themeMode) { dialog, which ->
                dialog.dismiss()
                prefer.themeMode = which
            }
            .create()
    }

    private fun buildExitDialog(): AlertDialog {
        return AlertDialog.Builder(requireContext())
            .setMessage(R.string.tips_logout)
            .setNegativeButton(android.R.string.cancel, null)
            .setPositiveButton(android.R.string.ok) { dialog, _ ->
                logout()
                dialog.dismiss()
            }.create()
    }

    private fun logout() {
        val jwcRepo = JwcRepository()
        jwcRepo.logout()
        activity?.apply {
            finish()
            startActivity(Intent(this, MainActivity::class.java))
        }
    }
}