package ai.programujz.demo.boundary.api.controller.response

import ai.programujz.demo.domain.model.TaskStatus
import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import java.time.Instant
import java.util.UUID

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes(
    JsonSubTypes.Type(value = OneTimeTaskResponse::class, name = "ONE_TIME"),
    JsonSubTypes.Type(value = RecurringTaskResponse::class, name = "RECURRING")
)
sealed class TaskResponse {
    abstract val id: UUID
    abstract val name: String
    abstract val displayOrder: Int
    abstract val category: String?
    abstract val status: TaskStatus
    abstract val createdAt: Instant
    abstract val updatedAt: Instant
}