package io.github.freewulkanowy.services.sync.works

import io.github.freewulkanowy.data.db.entities.Semester
import io.github.freewulkanowy.data.db.entities.Student
import io.github.freewulkanowy.data.repositories.ConferenceRepository
import io.github.freewulkanowy.data.waitForResult
import io.github.freewulkanowy.services.sync.notifications.NewConferenceNotification
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class ConferenceWork @Inject constructor(
    private val conferenceRepository: ConferenceRepository,
    private val newConferenceNotification: NewConferenceNotification,
) : Work {

    override suspend fun doWork(student: Student, semester: Semester, notify: Boolean) {
        conferenceRepository.getConferences(
            student = student,
            semester = semester,
            forceRefresh = true,
            notify = notify
        ).waitForResult()

        conferenceRepository.getConferenceFromDatabase(semester).first()
            .filter { !it.isNotified }.let {
                if (it.isNotEmpty()) newConferenceNotification.notify(it, student)

                conferenceRepository.updateConference(it.onEach { conference ->
                    conference.isNotified = true
                })
            }
    }
}
