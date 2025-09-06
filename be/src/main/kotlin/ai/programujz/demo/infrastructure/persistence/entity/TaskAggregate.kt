package ai.programujz.demo.infrastructure.persistence.entity

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.relational.core.mapping.MappedCollection
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant
import java.time.LocalDate
import java.util.*

@Table("tasks")
data class TaskAggregate(
    @Id
    val id: UUID? = null,
    val name: String,
    val displayOrder: Int,
    val category: String?,
    val taskType: TaskType,
    val status: TaskStatus,
    @CreatedDate
    val createdAt: Instant? = null,
    @LastModifiedDate
    val updatedAt: Instant? = null,
    val deletedAt: Instant? = null,

    // One-to-one relationships as part of the aggregate
    @MappedCollection(idColumn = "task_id")
    val oneTimeDetails: OneTimeTaskDetails? = null,

    @MappedCollection(idColumn = "task_id")
    val recurringDetails: RecurringTaskDetails? = null,

    // One-to-many relationship
    @MappedCollection(idColumn = "task_id")
    val completions: Set<TaskCompletionRecord> = emptySet()
) {
    init {
        // Validation to ensure consistency
        when (taskType) {
            TaskType.ONE_TIME -> require(oneTimeDetails != null && recurringDetails == null) {
                "One-time task must have oneTimeDetails and no recurringDetails"
            }

            TaskType.RECURRING -> require(recurringDetails != null && oneTimeDetails == null) {
                "Recurring task must have recurringDetails and no oneTimeDetails"
            }
        }
    }

    fun complete(notes: String? = null): TaskAggregate {
        return when (taskType) {
            TaskType.ONE_TIME -> this.copy(
                status = TaskStatus.COMPLETED,
                oneTimeDetails = oneTimeDetails?.copy(completedAt = Instant.now()),
                completions = completions + TaskCompletionRecord(
                    taskId = id!!,
                    completedDate = LocalDate.now(),
                    notes = notes
                )
            )

            TaskType.RECURRING -> {
                val nextDueDate = recurringDetails!!.calculateNextDueDate()
                this.copy(
                    recurringDetails = recurringDetails.copy(nextDueDate = nextDueDate),
                    completions = completions + TaskCompletionRecord(
                        taskId = id!!,
                        completedDate = LocalDate.now(),
                        notes = notes
                    )
                )
            }
        }
    }
}

@Table("one_time_tasks")
data class OneTimeTaskDetails(
    val taskId: UUID? = null,
    val dueDate: LocalDate,
    val completedAt: Instant? = null
)

@Table("recurring_tasks")
data class RecurringTaskDetails(
    val taskId: UUID? = null,
    val recurrencePattern: RecurrencePattern,
    val dayOfWeek: Int? = null,
    val startDate: LocalDate,
    val endDate: LocalDate? = null,
    val nextDueDate: LocalDate
) {
    fun calculateNextDueDate(): LocalDate {
        return when (recurrencePattern) {
            RecurrencePattern.DAILY -> nextDueDate.plusDays(1)
            RecurrencePattern.WEEKLY -> nextDueDate.plusWeeks(1)
        }
    }
}

@Table("task_completions")
data class TaskCompletionRecord(
    @Id
    val id: UUID? = null,
    val taskId: UUID,
    val completedDate: LocalDate,
    val completedAt: Instant = Instant.now(),
    val notes: String? = null
)

enum class TaskType {
    ONE_TIME,
    RECURRING
}

enum class TaskStatus {
    PENDING,
    COMPLETED,
    ARCHIVED
}

enum class RecurrencePattern {
    DAILY,
    WEEKLY
}