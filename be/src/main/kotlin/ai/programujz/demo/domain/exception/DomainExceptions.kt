package ai.programujz.demo.domain.exception

import ai.programujz.demo.boundary.api.errorhandler.ErrorCode
import java.util.UUID

class TaskNotFoundException(id: UUID) : DomainException(
    message = "Task with id $id not found",
    errorCode = ErrorCode.TASK_NOT_FOUND
)

class InvalidTaskStateException(message: String) : DomainException(
    message = message,
    errorCode = ErrorCode.INVALID_TASK_STATE
)

class InvalidRecurrenceConfigurationException(message: String) : DomainException(
    message = message,
    errorCode = ErrorCode.INVALID_RECURRENCE_CONFIGURATION
)

class TaskAlreadyCompletedException(id: UUID) : DomainException(
    message = "Task with id $id is already completed",
    errorCode = ErrorCode.TASK_ALREADY_COMPLETED
)

class TaskArchivedException(id: UUID) : DomainException(
    message = "Task with id $id is archived and cannot be modified",
    errorCode = ErrorCode.TASK_ARCHIVED
)

class CategoryNotFoundException(category: String) : DomainException(
    message = "Category '$category' not found",
    errorCode = ErrorCode.CATEGORY_NOT_FOUND
)