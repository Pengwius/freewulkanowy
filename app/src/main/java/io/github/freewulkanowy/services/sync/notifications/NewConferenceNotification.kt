package io.github.freewulkanowy.services.sync.notifications

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import io.github.freewulkanowy.R
import io.github.freewulkanowy.data.db.entities.Conference
import io.github.freewulkanowy.data.db.entities.Student
import io.github.freewulkanowy.data.pojos.GroupNotificationData
import io.github.freewulkanowy.data.pojos.NotificationData
import io.github.freewulkanowy.ui.modules.Destination
import io.github.freewulkanowy.ui.modules.splash.SplashActivity
import io.github.freewulkanowy.utils.getPlural
import io.github.freewulkanowy.utils.toFormattedString
import java.time.Instant
import javax.inject.Inject

class NewConferenceNotification @Inject constructor(
    private val appNotificationManager: AppNotificationManager,
    @ApplicationContext private val context: Context
) {

    suspend fun notify(items: List<Conference>, student: Student) {
        val today = Instant.now()
        val lines = items.filter { !it.date.isBefore(today) }
            .map {
                "${it.date.toFormattedString("dd.MM")} - ${it.title}: ${it.subject}"
            }
            .ifEmpty { return }

        val notificationDataList = lines.map {
            NotificationData(
                title = context.getPlural(R.plurals.conference_notify_new_item_title, 1),
                content = it,
                destination = Destination.Conference
            )
        }

        val groupNotificationData = GroupNotificationData(
            notificationDataList = notificationDataList,
            title = context.getPlural(R.plurals.conference_notify_new_item_title, lines.size),
            content = context.getPlural(
                R.plurals.conference_notify_new_items,
                lines.size,
                lines.size
            ),
            destination = Destination.Conference,
            type = NotificationType.NEW_CONFERENCE
        )

        appNotificationManager.sendMultipleNotifications(groupNotificationData, student)
    }
}
