package io.github.freewulkanowy.data.db.entities

import java.io.Serializable

data class StudentWithSemesters(
    val student: Student,
    val semesters: List<Semester>
) : Serializable
