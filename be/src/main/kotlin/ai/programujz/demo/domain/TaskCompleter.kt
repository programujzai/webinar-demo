package ai.programujz.demo.domain

import ai.programujz.demo.domain.exception.TaskAlreadyCompletedException
import ai.programujz.demo.domain.exception.TaskArchivedException
import ai.programujz.demo.domain.model.*
import ai.programujz.demo.domain.repository.TaskRepository
import org.springframework.stereotype.Component
import org.springframework.stereotype.Service
import java.time.Instant
import java.time.LocalDate
import java.util.*

@Component
class TaskCompleter(
    private val taskRepository: TaskRepository,
    private val taskFetcher: TaskFetcher,
    private val taskCreator: TaskCreator
) {
    
    fun completeTask(id: TaskId, notes: String?): Task {
        val task = taskFetcher.getTask(id)

        return when (task) {
            is OneTimeTask -> completeOneTimeTask(task, notes)
            is RecurringTask -> completeRecurringTask(task, notes)
        }
    }

    private fun completeOneTimeTask(task: OneTimeTask, notes: String?): OneTimeTask {
        if (task.status == TaskStatus.COMPLETED) {
            throw TaskAlreadyCompletedException(task.id!!.value)
        }
        
        if (task.status == TaskStatus.ARCHIVED) {
            throw TaskArchivedException(task.id!!.value)
        }
        
        val completedTask = task.copy(
            status = TaskStatus.COMPLETED,
            completedAt = Instant.now()
        )

        val completion = TaskCompletion(
            taskId = task.id!!,
            completedDate = LocalDate.now(),
            completedAt = Instant.now(),
            notes = notes
        )
        taskRepository.saveCompletion(completion)

        return taskRepository.save(completedTask) as OneTimeTask
    }

    private fun completeRecurringTask(task: RecurringTask, notes: String?): RecurringTask {
        val today = LocalDate.now()
        val existingCompletion = taskRepository.findCompletionByTaskIdAndDate(task.id!!, today)

        if (existingCompletion != null) {
            throw TaskAlreadyCompletedException(task.id.value)
        }
        
        if (task.status == TaskStatus.ARCHIVED) {
            throw TaskArchivedException(task.id.value)
        }

        val completion = TaskCompletion(
            taskId = task.id,
            completedDate = today,
            completedAt = Instant.now(),
            notes = notes
        )
        taskRepository.saveCompletion(completion)

        val nextDueDate = taskCreator.calculateNextDueDate(
            task.nextDueDate.plusDays(1),
            task.recurrencePattern,
            task.dayOfWeek
        )

        val updatedTask = task.copy(
            nextDueDate = nextDueDate
        )

        return taskRepository.save(updatedTask) as RecurringTask
    }
}