package ai.programujz.demo.domain

import ai.programujz.demo.boundary.api.controller.request.CreateOneTimeTaskRequest
import ai.programujz.demo.boundary.api.controller.request.CreateRecurringTaskRequest
import ai.programujz.demo.boundary.api.controller.request.CreateTaskRequest
import ai.programujz.demo.domain.exception.InvalidRecurrenceConfigurationException
import ai.programujz.demo.domain.model.*
import ai.programujz.demo.domain.repository.TaskRepository
import org.springframework.stereotype.Component
import java.time.DayOfWeek
import java.time.LocalDate

@Component
class TaskCreator(
    private val taskRepository: TaskRepository
) {
    
    fun createTask(request: CreateTaskRequest): Task {
        val displayOrder = taskRepository.getMaxDisplayOrder() + 1

        return when (request) {
            is CreateOneTimeTaskRequest -> createOneTimeTask(request, displayOrder)
            is CreateRecurringTaskRequest -> createRecurringTask(request, displayOrder)
        }
    }

    private fun createOneTimeTask(request: CreateOneTimeTaskRequest, displayOrder: Int): OneTimeTask {
        val task = OneTimeTask(
            name = request.name,
            displayOrder = displayOrder,
            category = request.category,
            dueDate = request.dueDate,
            status = TaskStatus.PENDING
        )

        return taskRepository.save(task) as OneTimeTask
    }

    private fun createRecurringTask(request: CreateRecurringTaskRequest, displayOrder: Int): RecurringTask {
        val nextDueDate = calculateNextDueDate(
            request.startDate,
            request.recurrencePattern,
            request.dayOfWeek
        )

        val task = RecurringTask(
            name = request.name,
            displayOrder = displayOrder,
            category = request.category,
            recurrencePattern = request.recurrencePattern,
            dayOfWeek = request.dayOfWeek,
            startDate = request.startDate,
            endDate = request.endDate,
            nextDueDate = nextDueDate,
            status = TaskStatus.PENDING
        )

        return taskRepository.save(task) as RecurringTask
    }

    fun calculateNextDueDate(
        fromDate: LocalDate,
        pattern: RecurrencePattern,
        dayOfWeek: DayOfWeek?
    ): LocalDate {
        return when (pattern) {
            RecurrencePattern.DAILY -> fromDate
            RecurrencePattern.WEEKLY -> {
                if (dayOfWeek == null) {
                    throw InvalidRecurrenceConfigurationException("Day of week is required for weekly recurrence")
                }
                var date = fromDate
                while (date.dayOfWeek != dayOfWeek) {
                    date = date.plusDays(1)
                }
                date
            }
        }
    }
}