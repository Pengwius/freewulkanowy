package io.github.freewulkanowy.services.sync.notifications

import android.content.Context
import androidx.core.text.parseAsHtml
import dagger.hilt.android.qualifiers.ApplicationContext
import io.github.freewulkanowy.R
import io.github.freewulkanowy.data.db.entities.SchoolAnnouncement
import io.github.freewulkanowy.data.db.entities.Student
import io.github.freewulkanowy.data.pojos.GroupNotificationData
import io.github.freewulkanowy.data.pojos.NotificationData
import io.github.freewulkanowy.ui.modules.Destination
import io.github.freewulkanowy.ui.modules.splash.SplashActivity
import io.github.freewulkanowy.utils.getPlural
import javax.inject.Inject

class NewSchoolAnnouncementNotification @Inject constructor(
    private val appNotificationManager: AppNotificationManager,
    @ApplicationContext private val context: Context
) {

    suspend fun notify(items: List<SchoolAnnouncement>, student: Student) {
        val notificationDataList = items.map {
            NotificationData(
                destination = Destination.SchoolAnnouncement,
                title = context.getPlural(
                    R.plurals.school_announcement_notify_new_item_title,
                    1
                ),
                content = "${it.subject}: ${it.content.parseAsHtml()}"
            )
        }
        val groupNotificationData = GroupNotificationData(
            type = NotificationType.NEW_ANNOUNCEMENT,
            destination = Destination.SchoolAnnouncement,
            title = context.getPlural(
                R.plurals.school_announcement_notify_new_item_title,
                items.size
            ),
            content = context.getPlural(
                R.plurals.school_announcement_notify_new_items,
                items.size,
                items.size
            ),
            notificationDataList = notificationDataList
        )

        appNotificationManager.sendMultipleNotifications(groupNotificationData, student)
    }
}
