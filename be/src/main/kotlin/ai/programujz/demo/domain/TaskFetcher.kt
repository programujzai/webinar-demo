package ai.programujz.demo.domain

import ai.programujz.demo.domain.exception.TaskNotFoundException
import ai.programujz.demo.domain.model.Task
import ai.programujz.demo.domain.model.TaskCompletion
import ai.programujz.demo.domain.model.TaskId
import ai.programujz.demo.domain.repository.TaskRepository
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.util.*

@Service
class TaskFetcher(
    private val taskRepository: TaskRepository
) {
    
    fun getTask(id: TaskId): Task {
        return taskRepository.findById(id)
            ?: throw TaskNotFoundException(id.value)
    }

    fun getAllTasks(): List<Task> {
        return taskRepository.findAllActive()
    }

    fun getTasksByCategory(category: String): List<Task> {
        return taskRepository.findByCategory(category)
    }

    fun getTasksDueToday(): List<Task> {
        return taskRepository.findDueToday()
    }

    fun getTasksDueBetween(startDate: LocalDate, endDate: LocalDate): List<Task> {
        return taskRepository.findDueBetween(startDate, endDate)
    }

    fun getTaskCompletions(id: TaskId): List<TaskCompletion> {
        if (!taskRepository.existsById(id)) {
            throw TaskNotFoundException(id.value)
        }
        return taskRepository.findCompletionsByTaskId(id)
    }
}