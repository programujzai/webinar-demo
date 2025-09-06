package ai.programujz.demo.boundary.api.controller.request

import ai.programujz.demo.domain.model.RecurrencePattern
import jakarta.validation.constraints.FutureOrPresent
import jakarta.validation.constraints.NotBlank
import java.time.DayOfWeek
import java.time.LocalDate

data class CreateRecurringTaskRequest(
    @field:NotBlank
    override val name: String,
    override val category: String? = null,
    val recurrencePattern: RecurrencePattern,
    val dayOfWeek: DayOfWeek? = null,
    @field:FutureOrPresent(message = "Start date cannot be in the past")
    val startDate: LocalDate,
    val endDate: LocalDate? = null
) : CreateTaskRequest() {
    init {
        if (recurrencePattern == RecurrencePattern.WEEKLY) {
            requireNotNull(dayOfWeek) { "Day of week is required for weekly recurrence" }
        }
    }
}