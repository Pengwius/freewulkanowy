package io.github.freewulkanowy.ui.modules.mobiledevice

import io.github.freewulkanowy.data.db.entities.MobileDevice
import io.github.freewulkanowy.ui.base.BaseView

interface MobileDeviceView : BaseView {

    val isViewEmpty: Boolean

    fun initView()

    fun updateData(data: List<MobileDevice>)

    fun deleteItem(device: MobileDevice, position: Int)

    fun restoreDeleteItem(device: MobileDevice, position: Int)

    fun showUndo(device: MobileDevice, position: Int)

    fun showRefresh(show: Boolean)

    fun showProgress(show: Boolean)

    fun enableSwipe(enable: Boolean)

    fun showContent(show: Boolean)

    fun showEmpty(show: Boolean)

    fun showErrorView(show: Boolean)

    fun setErrorDetails(message: String)

    fun showTokenDialog()

    fun resetView()
}
