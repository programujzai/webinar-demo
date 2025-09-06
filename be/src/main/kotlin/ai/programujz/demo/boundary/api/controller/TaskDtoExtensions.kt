package ai.programujz.demo.boundary.api.controller

import ai.programujz.demo.boundary.api.controller.response.OneTimeTaskResponse
import ai.programujz.demo.boundary.api.controller.response.RecurringTaskResponse
import ai.programujz.demo.boundary.api.controller.response.TaskCompletionResponse
import ai.programujz.demo.boundary.api.controller.response.TaskResponse
import ai.programujz.demo.domain.model.OneTimeTask
import ai.programujz.demo.domain.model.RecurringTask
import ai.programujz.demo.domain.model.Task
import ai.programujz.demo.domain.model.TaskCompletion
import java.time.Instant

fun Task.toResponse(): TaskResponse = when (this) {
    is OneTimeTask -> OneTimeTaskResponse(
        id = id!!.value,
        name = name,
        displayOrder = displayOrder,
        category = category,
        status = status,
        dueDate = dueDate,
        completedAt = completedAt,
        createdAt = Instant.EPOCH, // TODO: Should come from persistence layer
        updatedAt = Instant.EPOCH  // TODO: Should come from persistence layer
    )
    is RecurringTask -> RecurringTaskResponse(
        id = id!!.value,
        name = name,
        displayOrder = displayOrder,
        category = category,
        status = status,
        recurrencePattern = recurrencePattern,
        dayOfWeek = dayOfWeek,
        startDate = startDate,
        endDate = endDate,
        nextDueDate = nextDueDate,
        lastCompletedDate = null, // To be populated from completions if needed
        createdAt = Instant.EPOCH, // TODO: Should come from persistence layer
        updatedAt = Instant.EPOCH  // TODO: Should come from persistence layer
    )
}

fun TaskCompletion.toResponse(): TaskCompletionResponse = TaskCompletionResponse(
    id = id!!.value,
    completedDate = completedDate,
    completedAt = completedAt,
    notes = notes
)