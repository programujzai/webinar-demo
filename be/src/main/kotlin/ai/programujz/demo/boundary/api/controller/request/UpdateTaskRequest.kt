package ai.programujz.demo.boundary.api.controller.request

import ai.programujz.demo.domain.model.RecurrencePattern
import java.time.DayOfWeek
import java.time.LocalDate

data class UpdateTaskRequest(
    val name: String? = null,
    val category: String? = null,
    val tags: List<java.util.UUID>? = null,
    val dueDate: LocalDate? = null,
    val recurrencePattern: RecurrencePattern? = null,
    val dayOfWeek: DayOfWeek? = null,
    val endDate: LocalDate? = null
)