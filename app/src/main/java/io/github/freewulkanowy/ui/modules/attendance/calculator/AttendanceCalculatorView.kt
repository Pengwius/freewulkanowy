package io.github.freewulkanowy.ui.modules.attendance.calculator

import io.github.freewulkanowy.data.pojos.AttendanceData
import io.github.freewulkanowy.ui.base.BaseView

interface AttendanceCalculatorView : BaseView {

    val isViewEmpty: Boolean

    fun initView()

    fun showRefresh(show: Boolean)

    fun showContent(show: Boolean)

    fun showProgress(show: Boolean)

    fun enableSwipe(enable: Boolean)

    fun showEmpty(show: Boolean)

    fun showErrorView(show: Boolean)

    fun setErrorDetails(message: String)

    fun updateData(data: List<AttendanceData>)

    fun clearView()

    fun openSettingsView()
}
