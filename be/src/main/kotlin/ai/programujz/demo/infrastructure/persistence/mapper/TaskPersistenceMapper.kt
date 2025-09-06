package ai.programujz.demo.infrastructure.persistence.mapper

import ai.programujz.demo.domain.model.OneTimeTask
import ai.programujz.demo.domain.model.RecurringTask
import ai.programujz.demo.domain.model.Task
import ai.programujz.demo.domain.model.TaskCompletion
import ai.programujz.demo.domain.model.TaskCompletionId
import ai.programujz.demo.domain.model.TaskId
import ai.programujz.demo.domain.model.toTaskCompletionId
import ai.programujz.demo.domain.model.toTaskId
import ai.programujz.demo.domain.model.toUUID as taskIdToUUID
import ai.programujz.demo.domain.model.TaskStatus as DomainTaskStatus
import ai.programujz.demo.domain.model.RecurrencePattern as DomainRecurrencePattern
import ai.programujz.demo.infrastructure.persistence.entity.OneTimeTaskDetails
import ai.programujz.demo.infrastructure.persistence.entity.RecurringTaskDetails
import ai.programujz.demo.infrastructure.persistence.entity.TaskAggregate
import ai.programujz.demo.infrastructure.persistence.entity.TaskCompletionRecord
import ai.programujz.demo.infrastructure.persistence.entity.TaskType
import ai.programujz.demo.infrastructure.persistence.entity.TaskStatus as EntityTaskStatus
import ai.programujz.demo.infrastructure.persistence.entity.RecurrencePattern as EntityRecurrencePattern
import org.springframework.stereotype.Component
import java.time.DayOfWeek
import java.time.Instant

/**
 * Mapper responsible for converting between domain models and persistence aggregates.
 * This keeps the domain layer completely isolated from persistence concerns.
 */
@Component
class TaskPersistenceMapper {
    
    /**
     * Convert domain Task to persistence TaskAggregate for new entities
     */
    fun toTaskAggregate(task: Task): TaskAggregate {
        return when (task) {
            is OneTimeTask -> TaskAggregate(
                id = task.id.taskIdToUUID(),
                name = task.name,
                displayOrder = task.displayOrder,
                category = task.category,
                taskType = TaskType.ONE_TIME,
                status = task.status.toEntityStatus(),
                createdAt = null,  // Will be set by Spring Data JDBC auditing
                updatedAt = null,  // Will be set by Spring Data JDBC auditing
                deletedAt = null,
                oneTimeDetails = OneTimeTaskDetails(
                    taskId = task.id.taskIdToUUID(),
                    dueDate = task.dueDate,
                    completedAt = task.completedAt
                ),
                recurringDetails = null,
                completions = emptySet()
            )
            
            is RecurringTask -> TaskAggregate(
                id = task.id.taskIdToUUID(),
                name = task.name,
                displayOrder = task.displayOrder,
                category = task.category,
                taskType = TaskType.RECURRING,
                status = task.status.toEntityStatus(),
                createdAt = null,  // Will be set by Spring Data JDBC auditing
                updatedAt = null,  // Will be set by Spring Data JDBC auditing
                deletedAt = null,
                oneTimeDetails = null,
                recurringDetails = RecurringTaskDetails(
                    taskId = task.id.taskIdToUUID(),
                    recurrencePattern = task.recurrencePattern.toEntityPattern(),
                    dayOfWeek = task.dayOfWeek?.value,
                    startDate = task.startDate,
                    endDate = task.endDate,
                    nextDueDate = task.nextDueDate
                ),
                completions = emptySet()
            )
        }
    }
    
    /**
     * Convert persistence TaskAggregate to domain Task
     */
    fun toDomainTask(aggregate: TaskAggregate): Task {
        return when (aggregate.taskType) {
            TaskType.ONE_TIME -> {
                val details = aggregate.oneTimeDetails
                    ?: throw IllegalStateException("One-time task missing oneTimeDetails")
                
                OneTimeTask(
                    id = aggregate.id.toTaskId(),
                    name = aggregate.name,
                    displayOrder = aggregate.displayOrder,
                    category = aggregate.category,
                    status = aggregate.status.toDomainStatus(),
                    dueDate = details.dueDate,
                    completedAt = details.completedAt
                )
            }
            
            TaskType.RECURRING -> {
                val details = aggregate.recurringDetails
                    ?: throw IllegalStateException("Recurring task missing recurringDetails")
                
                RecurringTask(
                    id = aggregate.id.toTaskId(),
                    name = aggregate.name,
                    displayOrder = aggregate.displayOrder,
                    category = aggregate.category,
                    status = aggregate.status.toDomainStatus(),
                    recurrencePattern = details.recurrencePattern.toDomainPattern(),
                    dayOfWeek = details.dayOfWeek?.let { DayOfWeek.of(it) },
                    startDate = details.startDate,
                    endDate = details.endDate,
                    nextDueDate = details.nextDueDate
                )
            }
        }
    }
    
    /**
     * Convert TaskAggregate with completions to domain Task and completions
     */
    fun toDomainTaskWithCompletions(aggregate: TaskAggregate): Pair<Task, List<TaskCompletion>> {
        val task = toDomainTask(aggregate)
        val completions = aggregate.completions.map { toTaskCompletion(it) }
        return task to completions
    }
    
    /**
     * Convert entity TaskCompletionRecord to domain TaskCompletion
     */
    fun toTaskCompletion(record: TaskCompletionRecord): TaskCompletion {
        return TaskCompletion(
            id = record.id.toTaskCompletionId(),
            taskId = TaskId.from(record.taskId),
            completedDate = record.completedDate,
            completedAt = record.completedAt,
            notes = record.notes
        )
    }
    
    /**
     * Convert domain TaskCompletion to entity TaskCompletionRecord
     */
    fun toTaskCompletionRecord(completion: TaskCompletion): TaskCompletionRecord {
        return TaskCompletionRecord(
            id = completion.id?.value,
            taskId = completion.taskId.value,
            completedDate = completion.completedDate,
            completedAt = completion.completedAt,
            notes = completion.notes
        )
    }
    
    // Extension functions for enum conversions
    private fun DomainTaskStatus.toEntityStatus(): EntityTaskStatus {
        return when (this) {
            DomainTaskStatus.PENDING -> EntityTaskStatus.PENDING
            DomainTaskStatus.COMPLETED -> EntityTaskStatus.COMPLETED
            DomainTaskStatus.ARCHIVED -> EntityTaskStatus.ARCHIVED
        }
    }
    
    private fun EntityTaskStatus.toDomainStatus(): DomainTaskStatus {
        return when (this) {
            EntityTaskStatus.PENDING -> DomainTaskStatus.PENDING
            EntityTaskStatus.COMPLETED -> DomainTaskStatus.COMPLETED
            EntityTaskStatus.ARCHIVED -> DomainTaskStatus.ARCHIVED
        }
    }
    
    private fun DomainRecurrencePattern.toEntityPattern(): EntityRecurrencePattern {
        return when (this) {
            DomainRecurrencePattern.DAILY -> EntityRecurrencePattern.DAILY
            DomainRecurrencePattern.WEEKLY -> EntityRecurrencePattern.WEEKLY
        }
    }
    
    private fun EntityRecurrencePattern.toDomainPattern(): DomainRecurrencePattern {
        return when (this) {
            EntityRecurrencePattern.DAILY -> DomainRecurrencePattern.DAILY
            EntityRecurrencePattern.WEEKLY -> DomainRecurrencePattern.WEEKLY
        }
    }
}