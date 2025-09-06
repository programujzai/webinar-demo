package ai.programujz.demo.boundary.api.controller.request

import jakarta.validation.constraints.NotEmpty
import java.util.UUID

data class TaskOrderUpdate(
    val id: UUID,
    val displayOrder: Int
)

data class ReorderTasksRequest(
    @field:NotEmpty
    val taskOrders: List<TaskOrderUpdate>
)