package ai.programujz.demo.infrastructure.persistence.repository

import ai.programujz.demo.domain.model.Task
import ai.programujz.demo.domain.model.TaskCompletion
import ai.programujz.demo.domain.model.TaskId
import ai.programujz.demo.domain.model.TaskStatus
import ai.programujz.demo.domain.repository.TaskRepository as DomainTaskRepository
import ai.programujz.demo.infrastructure.persistence.mapper.TaskPersistenceMapper
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

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
        val aggregate = mapper.toTaskAggregate(task)
        val savedAggregate = taskAggregateRepository.save(aggregate)
        return mapper.toDomainTask(savedAggregate)
    }
    
    override fun findById(id: TaskId): Task? {
        return taskAggregateRepository.findActiveById(id.value)?.let { aggregate ->
            mapper.toDomainTask(aggregate)
        }
    }
    
    override fun findAll(): List<Task> {
        return taskAggregateRepository.findAll()
            .filter { it.deletedAt == null }
            .map { mapper.toDomainTask(it) }
    }
    
    override fun findAllActive(): List<Task> {
        return taskAggregateRepository.findAllActive()
            .map { mapper.toDomainTask(it) }
    }
    
    override fun findByCategory(category: String): List<Task> {
        return taskAggregateRepository.findByCategory(category)
            .map { mapper.toDomainTask(it) }
    }
    
    override fun findByStatus(status: TaskStatus): List<Task> {
        return taskAggregateRepository.findByStatus(status.name)
            .map { mapper.toDomainTask(it) }
    }
    
    override fun findDueToday(): List<Task> {
        val today = LocalDate.now()
        val tasks = mutableListOf<Task>()
        
        // Get one-time tasks due today
        tasks.addAll(
            taskAggregateRepository.findOneTimeTasksByDueDate(today)
                .map { mapper.toDomainTask(it) }
        )
        
        // Get recurring tasks due today
        tasks.addAll(
            taskAggregateRepository.findRecurringTasksByNextDueDate(today)
                .map { mapper.toDomainTask(it) }
        )
        
        return tasks.sortedBy { it.displayOrder }
    }
    
    override fun findDueBetween(startDate: LocalDate, endDate: LocalDate): List<Task> {
        val tasks = mutableListOf<Task>()
        
        // Get one-time tasks in date range
        tasks.addAll(
            taskAggregateRepository.findOneTimeTasksByDueDateBetween(startDate, endDate)
                .map { mapper.toDomainTask(it) }
        )
        
        // Get recurring tasks due in date range
        // For recurring tasks, we get all that have nextDueDate <= endDate
        // This is a simplified approach - for full calendar view you might need more logic
        tasks.addAll(
            taskAggregateRepository.findRecurringTasksDue(endDate)
                .filter { aggregate ->
                    aggregate.recurringDetails?.nextDueDate?.let { nextDue ->
                        !nextDue.isBefore(startDate)
                    } ?: false
                }
                .map { mapper.toDomainTask(it) }
        )
        
        return tasks.distinctBy { it.id }.sortedBy { it.displayOrder }
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
        
        // Add completion to the aggregate
        val completionRecord = mapper.toTaskCompletionRecord(completion)
        val updatedAggregate = aggregate.copy(
            completions = aggregate.completions + completionRecord
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