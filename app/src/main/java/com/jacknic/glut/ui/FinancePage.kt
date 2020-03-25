package com.jacknic.glut.ui

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.jacknic.glut.R
import com.jacknic.glut.base.AbsRefreshListPage
import com.jacknic.glut.data.util.URL_GLUT_CW
import com.jacknic.glut.databinding.ItemInfoBarBinding
import com.jacknic.glut.databinding.PageFinanceBinding
import com.jacknic.glut.util.getBinding
import com.jacknic.glut.util.themeColor
import com.jacknic.glut.util.toBrowser
import com.jacknic.glut.viewmodel.CwcInfoViewModel
import com.jacknic.glut.widget.CwcLoginDialogFragment

/**
 * 财务页
 *
 * @author Jacknic
 */
class FinancePage : AbsRefreshListPage<Pair<String, String>, CwcInfoViewModel, PageFinanceBinding>() {

    override val vm by activityViewModels<CwcInfoViewModel>()
    override val layoutResId = R.layout.page_finance
    private val urlCwcMobile = URL_GLUT_CW + "mobile"
    private val urls = arrayOf(
        // 交易记录
        "$urlCwcMobile/jyjl",
        // 学费项目
        "$urlCwcMobile/xfxm",
        // 财务处主页
        "$urlCwcMobile/index",
        // 缴费明细
        "$urlCwcMobile/jfmx",
        // 一卡通充值
        "$urlCwcMobile/yktzxcz")

    override fun getListView() = bind.lvInfo
    override fun getEmptyLayout() = bind.emptyView.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (prefer.tintToolbar) {
            bind.tvBalance.apply {
                backgroundTintList = ColorStateList.valueOf(context.themeColor())
                setTextColor(Color.WHITE)
            }
        }
        val linkBtnList = listOf<TextView>(
            bind.tvRecord, bind.tvCostItems, bind.tvHomeIndex, bind.tvDetails, bind.tvCharge
        )
        linkBtnList.forEachIndexed { index, textView ->
            val url = urls[index]
            link(textView, url)
        }
        vm.balance.observe(viewLifecycleOwner, Observer {
            bind.tvBalance.text = it
        })
        vm.loadState.observe(viewLifecycleOwner, Observer {
            bind.swipeRefresh.isRefreshing = it.first
        })
        vm.info.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                val newList = mutableListOf<Pair<String, String>>()
                newList.add("姓名" to it.studentName)
                newList.add("学号" to it.sid)
                newList.add("班级" to it.className)
                newList.add("学院" to it.academyName)
                newList.add("身份证" to it.id)
                newList.add("银行卡" to it.bankID)
                newList.add("已交费用" to it.payMoney)
                newList.add("贷款金额" to it.deferralMoney)
                newList.add("贷款到账" to it.deferralDate)
                setDataList(newList)
            }
        })
    }

    private fun link(tv: TextView, url: String) {
        tv.setOnClickListener {
            navCtrl.toBrowser(url, tv.text.toString())
        }
    }

    override fun getSwipeRefresh() = bind.swipeRefresh

    override fun refresh() {
        vm.refreshInfo()
        bind.tvBalance.setText(R.string.data_loading)
    }

    override fun showLogin() {
        CwcLoginDialogFragment.show(childFragmentManager, Runnable { refresh() })
    }

    override fun onBindItemView(position: Int, convertView: View?, parent: ViewGroup): View {
        val item = mItemList[position]
        val binding = convertView.getBinding<ItemInfoBarBinding>(parent, R.layout.item_info_bar)
        if (convertView == null) {
            binding.tvValue.setTextIsSelectable(true)
        }
        binding.key = item.first
        binding.value = item.second
        return binding.root
    }
}