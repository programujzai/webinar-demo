package ai.programujz.demo.domain.model

import java.time.LocalDate

data class TaskSearchParams(
    val category: String? = null,
    val status: TaskStatus? = null,
    val dueDate: LocalDate? = null,
    val startDate: LocalDate? = null,
    val endDate: LocalDate? = null
)