package io.github.freewulkanowy.data.db.dao

import androidx.room.Dao
import androidx.room.Query
import io.github.freewulkanowy.data.db.entities.MailboxType
import io.github.freewulkanowy.data.db.entities.Recipient
import javax.inject.Singleton

@Singleton
@Dao
interface RecipientDao : BaseDao<Recipient> {

    @Query("SELECT * FROM Recipients WHERE type = :type AND studentMailboxGlobalKey = :studentMailboxGlobalKey")
    suspend fun loadAll(type: MailboxType, studentMailboxGlobalKey: String): List<Recipient>
}
