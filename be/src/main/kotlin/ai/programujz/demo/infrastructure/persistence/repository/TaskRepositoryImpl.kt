package ai.programujz.demo.infrastructure.persistence.repository

import ai.programujz.demo.domain.model.Task
import ai.programujz.demo.domain.model.TaskCompletion
import ai.programujz.demo.domain.model.TaskId
import ai.programujz.demo.domain.model.TaskSearchParams
import ai.programujz.demo.domain.model.TaskStatus
import ai.programujz.demo.infrastructure.persistence.entity.TaskType
import ai.programujz.demo.infrastructure.persistence.mapper.TaskPersistenceMapper
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import ai.programujz.demo.domain.repository.TaskRepository as DomainTaskRepository

/**
 * Infrastructure implementation of domain TaskRepository using TaskAggregate.
 * Handles all persistence operations and mapping between domain models and aggregates.
 */
@Repository
@Transactional
class TaskRepositoryImpl(
    private val taskAggregateRepository: TaskAggregateRepository,
    private val mapper: TaskPersistenceMapper
) : DomainTaskRepository {

    override fun save(task: Task): Task {
        // Check if this is an update by looking for existing aggregate
        val existingAggregate = task.id?.let { id ->
            taskAggregateRepository.findById(id.value).orElse(null)
        }

        val aggregate = mapper.toTaskAggregate(task, existingAggregate)
        val savedAggregate = taskAggregateRepository.save(aggregate)
        return mapper.toDomainTask(savedAggregate)
    }

    override fun findById(id: TaskId): Task? {
        return taskAggregateRepository.findById(id.value)
            .filter { it.deletedAt == null }
            .orElse(null)
            ?.let { aggregate ->
                mapper.toDomainTask(aggregate)
            }
    }

    override fun findAll(): List<Task> {
        return taskAggregateRepository.findAll()
            .filter { it.deletedAt == null }
            .map { mapper.toDomainTask(it) }
    }

    override fun findAllActive(): List<Task> {
        return taskAggregateRepository.findByDeletedAtIsNullOrderByDisplayOrder()
            .map { mapper.toDomainTask(it) }
    }

    override fun findByCategory(category: String): List<Task> {
        return taskAggregateRepository.findByCategoryAndDeletedAtIsNullOrderByDisplayOrder(category)
            .map { mapper.toDomainTask(it) }
    }

    override fun findByStatus(status: TaskStatus): List<Task> {
        val entityStatus = ai.programujz.demo.infrastructure.persistence.entity.TaskStatus.valueOf(status.name)
        return taskAggregateRepository.findByStatusAndDeletedAtIsNullOrderByDisplayOrder(entityStatus)
            .map { mapper.toDomainTask(it) }
    }

    override fun findDueToday(): List<Task> {
        val today = LocalDate.now()

        // Get all active tasks with relationships loaded, then filter in memory
        return taskAggregateRepository.findByDeletedAtIsNullOrderByDisplayOrder()
            .filter { aggregate ->
                when (aggregate.taskType) {
                    TaskType.ONE_TIME ->
                        aggregate.oneTimeDetails?.dueDate == today

                    TaskType.RECURRING ->
                        aggregate.recurringDetails?.nextDueDate == today
                }
            }
            .map { mapper.toDomainTask(it) }
    }

    override fun findDueBetween(startDate: LocalDate, endDate: LocalDate): List<Task> {
        return taskAggregateRepository.findByDeletedAtIsNullOrderByDisplayOrder()
            .filter { aggregate ->
                when (aggregate.taskType) {
                    TaskType.ONE_TIME -> {
                        val dueDate = aggregate.oneTimeDetails?.dueDate
                        dueDate != null && !dueDate.isBefore(startDate) && !dueDate.isAfter(endDate)
                    }

                    TaskType.RECURRING -> {
                        val nextDueDate = aggregate.recurringDetails?.nextDueDate
                        nextDueDate != null && !nextDueDate.isAfter(endDate) && !nextDueDate.isBefore(startDate)
                    }
                }
            }
            .map { mapper.toDomainTask(it) }
    }

    override fun findBySearchParams(searchParams: TaskSearchParams): List<Task> {
        var aggregates = taskAggregateRepository.findByDeletedAtIsNullOrderByDisplayOrder()

        // Filter by category if provided
        searchParams.category?.let { category ->
            aggregates = aggregates.filter { it.category == category }
        }

        // Filter by status if provided
        searchParams.status?.let { status ->
            val entityStatus = ai.programujz.demo.infrastructure.persistence.entity.TaskStatus.valueOf(status.name)
            aggregates = aggregates.filter { it.status == entityStatus }
        }

        // Filter by date range if both dates are provided
        if (searchParams.startDate != null && searchParams.endDate != null) {
            aggregates = aggregates.filter { aggregate ->
                when (aggregate.taskType) {
                    TaskType.ONE_TIME -> {
                        val dueDate = aggregate.oneTimeDetails?.dueDate
                        dueDate != null && !dueDate.isBefore(searchParams.startDate) && !dueDate.isAfter(searchParams.endDate)
                    }

                    TaskType.RECURRING -> {
                        val nextDueDate = aggregate.recurringDetails?.nextDueDate
                        nextDueDate != null && !nextDueDate.isAfter(searchParams.endDate) && !nextDueDate.isBefore(searchParams.startDate)
                    }
                }
            }
        }
        // Filter by specific due date if provided (overrides date range)
        else if (searchParams.dueDate != null) {
            aggregates = aggregates.filter { aggregate ->
                when (aggregate.taskType) {
                    TaskType.ONE_TIME ->
                        aggregate.oneTimeDetails?.dueDate == searchParams.dueDate

                    TaskType.RECURRING ->
                        aggregate.recurringDetails?.nextDueDate == searchParams.dueDate
                }
            }
        }

        return aggregates.map { mapper.toDomainTask(it) }
    }

    override fun delete(id: TaskId) {
        taskAggregateRepository.deleteById(id.value)
    }

    override fun softDelete(id: TaskId) {
        taskAggregateRepository.softDelete(id.value)
    }

    override fun getMaxDisplayOrder(): Int {
        return taskAggregateRepository.findMaxDisplayOrder() ?: 0
    }

    override fun existsById(id: TaskId): Boolean {
        return taskAggregateRepository.existsById(id.value)
    }

    override fun saveCompletion(completion: TaskCompletion): TaskCompletion {
        val taskId = completion.taskId
        val aggregate = taskAggregateRepository.findById(taskId.value).orElseThrow {
            IllegalArgumentException("Task not found: $taskId")
        }

        // Add completion to the aggregate - preserve createdAt
        val completionRecord = mapper.toTaskCompletionRecord(completion)
        val updatedAggregate = aggregate.copy(
            completions = aggregate.completions + completionRecord,
            createdAt = aggregate.createdAt  // Explicitly preserve createdAt
        )

        val savedAggregate = taskAggregateRepository.save(updatedAggregate)

        // Return the saved completion (it will have generated ID if it was null)
        return savedAggregate.completions
            .find { it.completedDate == completion.completedDate && it.taskId == taskId.value }
            ?.let { mapper.toTaskCompletion(it) }
            ?: completion
    }

    override fun findCompletionsByTaskId(taskId: TaskId): List<TaskCompletion> {
        return taskAggregateRepository.findById(taskId.value).orElse(null)?.let { aggregate ->
            aggregate.completions.map { mapper.toTaskCompletion(it) }
        } ?: emptyList()
    }

    override fun findCompletionByTaskIdAndDate(taskId: TaskId, date: LocalDate): TaskCompletion? {
        return taskAggregateRepository.findById(taskId.value).orElse(null)?.let { aggregate ->
            aggregate.completions
                .find { it.completedDate == date }
                ?.let { mapper.toTaskCompletion(it) }
        }
    }
}