package io.github.freewulkanowy.data.repositories

import io.github.freewulkanowy.data.WulkanowySdkFactory
import io.github.freewulkanowy.data.db.dao.RecipientDao
import io.github.freewulkanowy.data.db.entities.Mailbox
import io.github.freewulkanowy.data.db.entities.MailboxType
import io.github.freewulkanowy.data.db.entities.Message
import io.github.freewulkanowy.data.db.entities.Recipient
import io.github.freewulkanowy.data.db.entities.Student
import io.github.freewulkanowy.data.mappers.mapToEntities
import io.github.freewulkanowy.utils.AutoRefreshHelper
import io.github.freewulkanowy.utils.getRefreshKey
import io.github.freewulkanowy.utils.uniqueSubtract
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RecipientRepository @Inject constructor(
    private val recipientDb: RecipientDao,
    private val wulkanowySdkFactory: WulkanowySdkFactory,
    private val refreshHelper: AutoRefreshHelper,
) {

    private val cacheKey = "recipient"

    suspend fun refreshRecipients(student: Student, mailbox: Mailbox, type: MailboxType) {
        val new = wulkanowySdkFactory.create(student)
            .getRecipients(mailbox.globalKey)
            .mapToEntities(mailbox.globalKey)
        val old = recipientDb.loadAll(type, mailbox.globalKey)

        recipientDb.removeOldAndSaveNew(
            oldItems = old uniqueSubtract new,
            newItems = new uniqueSubtract old,
        )

        refreshHelper.updateLastRefreshTimestamp(getRefreshKey(cacheKey, student))
    }

    suspend fun getRecipients(
        student: Student,
        mailbox: Mailbox?,
        type: MailboxType,
    ): List<Recipient> {
        mailbox ?: return emptyList()

        val cached = recipientDb.loadAll(type, mailbox.globalKey)

        val isExpired = refreshHelper.shouldBeRefreshed(getRefreshKey(cacheKey, student))
        return if (cached.isEmpty() || isExpired) {
            refreshRecipients(student, mailbox, type)
            recipientDb.loadAll(type, mailbox.globalKey)
        } else cached
    }

    suspend fun getMessageSender(
        student: Student,
        mailbox: Mailbox?,
        message: Message,
    ): List<Recipient> {
        mailbox ?: return emptyList()

        return wulkanowySdkFactory.create(student)
            .getMessageReplayDetails(message.messageGlobalKey)
            .sender
            .let(::listOf)
            .mapToEntities(mailbox.globalKey)
    }
}
