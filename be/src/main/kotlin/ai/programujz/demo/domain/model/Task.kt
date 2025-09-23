package ai.programujz.demo.domain.model

import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDate

sealed class Task {
    abstract val id: TaskId?
    abstract val name: String
    abstract val displayOrder: Int
    abstract val category: String?
    abstract val status: TaskStatus
    abstract val tags: List<Tag>
}

data class OneTimeTask(
    override val id: TaskId? = null,
    override val name: String,
    override val displayOrder: Int,
    override val category: String? = null,
    override val status: TaskStatus = TaskStatus.PENDING,
    override val tags: List<Tag> = emptyList(),
    val dueDate: LocalDate,
    val completedAt: Instant? = null
) : Task()

data class RecurringTask(
    override val id: TaskId? = null,
    override val name: String,
    override val displayOrder: Int,
    override val category: String? = null,
    override val status: TaskStatus = TaskStatus.PENDING,
    override val tags: List<Tag> = emptyList(),
    val recurrencePattern: RecurrencePattern,
    val dayOfWeek: DayOfWeek? = null,
    val startDate: LocalDate,
    val endDate: LocalDate? = null,
    val nextDueDate: LocalDate
) : Task()

enum class TaskStatus {
    PENDING,
    COMPLETED,
    ARCHIVED
}

enum class RecurrencePattern {
    DAILY,
    WEEKLY
}

data class TaskCompletion(
    val id: TaskCompletionId? = null,
    val taskId: TaskId,
    val completedDate: LocalDate,
    val completedAt: Instant = Instant.now(),
    val notes: String? = null
)