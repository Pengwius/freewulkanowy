package io.github.freewulkanowy.ui.modules.studentinfo

data class StudentInfoItem(
    val title: String,
    val subtitle: String,
    val showArrow: Boolean,
    val viewType: StudentInfoView.Type? = null,
)
