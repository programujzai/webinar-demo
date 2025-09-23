package ai.programujz.demo.domain

import ai.programujz.demo.boundary.api.controller.request.TaskOrderUpdate
import ai.programujz.demo.boundary.api.controller.request.UpdateTaskRequest
import ai.programujz.demo.domain.exception.TaskArchivedException
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
class TaskUpdater(
    private val taskRepository: TaskRepository,
    private val tagService: TagService
) {
    
    fun updateTask(id: TaskId, request: UpdateTaskRequest): Task {
        val existingTask = taskRepository.findById(id)
            ?: throw TaskNotFoundException(id.value)
        
        if (existingTask.status == TaskStatus.ARCHIVED) {
            throw TaskArchivedException(id.value)
        }

        // Handle tags update if provided
        val updatedTags = request.tags?.let { tagIds ->
            tagService.validateTagLimit(tagIds)
            tagService.validateTagsExist(tagIds)
            tagService.getTagsByIds(tagIds)
        } ?: existingTask.tags

        return when (existingTask) {
            is OneTimeTask -> {
                val updated = existingTask.copy(
                    name = request.name ?: existingTask.name,
                    category = request.category ?: existingTask.category,
                    tags = updatedTags,
                    dueDate = request.dueDate ?: existingTask.dueDate
                )
                taskRepository.save(updated)
            }

            is RecurringTask -> {
                val updated = existingTask.copy(
                    name = request.name ?: existingTask.name,
                    category = request.category ?: existingTask.category,
                    tags = updatedTags,
                    recurrencePattern = request.recurrencePattern ?: existingTask.recurrencePattern,
                    dayOfWeek = request.dayOfWeek ?: existingTask.dayOfWeek,
                    endDate = request.endDate ?: existingTask.endDate
                )
                taskRepository.save(updated)
            }
        }
    }

    fun reorderTasks(taskOrders: List<TaskOrderUpdate>) {
        taskOrders.forEach { order ->
            val task = taskRepository.findById(TaskId.from(order.id))
                ?: throw TaskNotFoundException(order.id)

            val reorderedTask = when (task) {
                is OneTimeTask -> task.copy(
                    displayOrder = order.displayOrder
                )

                is RecurringTask -> task.copy(
                    displayOrder = order.displayOrder
                )
            }

            taskRepository.save(reorderedTask)
        }
    }
}