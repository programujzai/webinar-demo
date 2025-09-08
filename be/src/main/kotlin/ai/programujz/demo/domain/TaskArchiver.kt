package ai.programujz.demo.domain

import ai.programujz.demo.domain.exception.InvalidTaskStateException
import ai.programujz.demo.domain.exception.TaskNotFoundException
import ai.programujz.demo.domain.model.OneTimeTask
import ai.programujz.demo.domain.model.RecurringTask
import ai.programujz.demo.domain.model.Task
import ai.programujz.demo.domain.model.TaskId
import ai.programujz.demo.domain.model.TaskStatus
import ai.programujz.demo.domain.repository.TaskRepository
import org.springframework.stereotype.Component
import org.springframework.stereotype.Service
import java.time.Instant
import java.util.*

@Component
class TaskArchiver(
    private val taskRepository: TaskRepository
) {
    
    fun archiveTask(id: TaskId): Task {
        val task = taskRepository.findById(id)
            ?: throw TaskNotFoundException(id.value)
        
        if (task.status == TaskStatus.ARCHIVED) {
            throw InvalidTaskStateException("Task is already archived")
        }

        val archivedTask = when (task) {
            is OneTimeTask -> task.copy(
                status = TaskStatus.ARCHIVED
            )

            is RecurringTask -> task.copy(
                status = TaskStatus.ARCHIVED
            )
        }

        return taskRepository.save(archivedTask)
    }

    fun deleteTask(id: TaskId) {
        if (!taskRepository.existsById(id)) {
            throw TaskNotFoundException(id.value)
        }
        taskRepository.softDelete(id)
    }
}