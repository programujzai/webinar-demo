package ai.programujz.demo.boundary.api.controller.response

import ai.programujz.demo.domain.model.RecurrencePattern
import ai.programujz.demo.domain.model.TaskStatus
import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDate
import java.util.UUID

data class RecurringTaskResponse(
    override val id: UUID,
    override val name: String,
    override val displayOrder: Int,
    override val category: String?,
    override val status: TaskStatus,
    override val tags: List<TagSummaryResponse>,
    val recurrencePattern: RecurrencePattern,
    val dayOfWeek: DayOfWeek?,
    val startDate: LocalDate,
    val endDate: LocalDate?,
    val nextDueDate: LocalDate,
    val lastCompletedDate: LocalDate?,
    override val createdAt: Instant,
    override val updatedAt: Instant
) : TaskResponse()