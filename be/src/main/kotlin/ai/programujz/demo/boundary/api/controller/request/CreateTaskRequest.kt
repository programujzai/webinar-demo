package ai.programujz.demo.boundary.api.controller.request

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes(
    JsonSubTypes.Type(value = CreateOneTimeTaskRequest::class, name = "ONE_TIME"),
    JsonSubTypes.Type(value = CreateRecurringTaskRequest::class, name = "RECURRING")
)
sealed class CreateTaskRequest {
    abstract val name: String
    abstract val category: String?
}