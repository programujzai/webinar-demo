package ai.programujz.demo.boundary.api.controller.response

import ai.programujz.demo.domain.model.TaskStatus
import java.time.Instant
import java.time.LocalDate
import java.util.UUID

data class OneTimeTaskResponse(
    override val id: UUID,
    override val name: String,
    override val displayOrder: Int,
    override val category: String?,
    override val status: TaskStatus,
    val dueDate: LocalDate,
    val completedAt: Instant?,
    override val createdAt: Instant,
    override val updatedAt: Instant
) : TaskResponse()