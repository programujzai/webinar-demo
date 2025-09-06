package ai.programujz.demo.boundary.api.controller.request

import jakarta.validation.constraints.Future
import jakarta.validation.constraints.NotBlank
import java.time.LocalDate

data class CreateOneTimeTaskRequest(
    @field:NotBlank
    override val name: String,
    override val category: String? = null,
    @field:Future(message = "Due date must be in the future")
    val dueDate: LocalDate
) : CreateTaskRequest()