package ai.programujz.demo.domain

import ai.programujz.demo.boundary.api.controller.request.CreateTaskRequest
import ai.programujz.demo.boundary.api.controller.request.TaskOrderUpdate
import ai.programujz.demo.boundary.api.controller.request.UpdateTaskRequest
import ai.programujz.demo.domain.model.Task
import ai.programujz.demo.domain.model.TaskCompletion
import ai.programujz.demo.domain.model.TaskId
import ai.programujz.demo.domain.model.TaskSearchParams
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.util.*

@Service
@Transactional
class TaskService(
    private val taskCreator: TaskCreator,
    private val taskFetcher: TaskFetcher,
    private val taskCompleter: TaskCompleter,
    private val taskUpdater: TaskUpdater,
    private val taskArchiver: TaskArchiver
) {

    fun createTask(request: CreateTaskRequest): Task {
        return taskCreator.createTask(request)
    }

    fun updateTask(id: UUID, request: UpdateTaskRequest): Task {
        return taskUpdater.updateTask(TaskId.from(id), request)
    }

    fun getTask(id: UUID): Task {
        return taskFetcher.getTask(TaskId.from(id))
    }

    fun getAllTasks(): List<Task> {
        return taskFetcher.getAllTasks()
    }

    fun getTasksByCategory(category: String): List<Task> {
        return taskFetcher.getTasksByCategory(category)
    }

    fun getTasksDueToday(): List<Task> {
        return taskFetcher.getTasksDueToday()
    }

    fun getTasksDueBetween(startDate: LocalDate, endDate: LocalDate): List<Task> {
        return taskFetcher.getTasksDueBetween(startDate, endDate)
    }

    fun getBySearchParams(searchParams: TaskSearchParams): List<Task> {
        return taskFetcher.getBySearchParams(searchParams)
    }

    fun completeTask(id: UUID, notes: String?): Task {
        return taskCompleter.completeTask(TaskId.from(id), notes)
    }

    fun deleteTask(id: UUID) {
        taskArchiver.deleteTask(TaskId.from(id))
    }

    fun reorderTasks(taskOrders: List<TaskOrderUpdate>) {
        taskUpdater.reorderTasks(taskOrders)
    }

    fun archiveTask(id: UUID): Task {
        return taskArchiver.archiveTask(TaskId.from(id))
    }

    fun getTaskCompletions(id: UUID): List<TaskCompletion> {
        return taskFetcher.getTaskCompletions(TaskId.from(id))
    }
}