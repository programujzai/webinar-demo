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

class TagNotFoundException(id: String) : DomainException(
    message = "Tag with id $id not found",
    errorCode = ErrorCode.TAG_NOT_FOUND
)

class TagAlreadyExistsException(name: String) : DomainException(
    message = "Tag with name '$name' already exists",
    errorCode = ErrorCode.TAG_ALREADY_EXISTS
)

class TagsNotFoundException(ids: List<String>) : DomainException(
    message = "Tags with ids ${ids.joinToString(", ")} not found",
    errorCode = ErrorCode.TAGS_NOT_FOUND
)

class TooManyTagsException(current: Int, max: Int) : DomainException(
    message = "Too many tags: $current. Maximum allowed: $max",
    errorCode = ErrorCode.TOO_MANY_TAGS
)