package io.github.freewulkanowy.data.db.dao

import androidx.room.Dao
import androidx.room.Query
import io.github.freewulkanowy.data.db.entities.Exam
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import javax.inject.Singleton

@Singleton
@Dao
interface ExamDao : BaseDao<Exam> {

    @Query("SELECT * FROM Exams WHERE diary_id = :diaryId AND student_id = :studentId AND date >= :from AND date <= :end")
    fun loadAll(diaryId: Int, studentId: Int, from: LocalDate, end: LocalDate): Flow<List<Exam>>
}
