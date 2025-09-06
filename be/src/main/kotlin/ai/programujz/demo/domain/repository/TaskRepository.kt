package ai.programujz.demo.domain.repository

import ai.programujz.demo.domain.model.Task
import ai.programujz.demo.domain.model.TaskCompletion
import ai.programujz.demo.domain.model.TaskId
import ai.programujz.demo.domain.model.TaskStatus
import java.time.LocalDate

interface TaskRepository {
    fun save(task: Task): Task
    fun findById(id: TaskId): Task?
    fun findAll(): List<Task>
    fun findAllActive(): List<Task>
    fun findByCategory(category: String): List<Task>
    fun findByStatus(status: TaskStatus): List<Task>
    fun findDueToday(): List<Task>
    fun findDueBetween(startDate: LocalDate, endDate: LocalDate): List<Task>
    fun delete(id: TaskId)
    fun softDelete(id: TaskId)
    fun getMaxDisplayOrder(): Int
    fun existsById(id: TaskId): Boolean
    fun saveCompletion(completion: TaskCompletion): TaskCompletion
    fun findCompletionsByTaskId(taskId: TaskId): List<TaskCompletion>
    fun findCompletionByTaskIdAndDate(taskId: TaskId, date: LocalDate): TaskCompletion?
}