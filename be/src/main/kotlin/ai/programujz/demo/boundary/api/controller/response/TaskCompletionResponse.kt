package ai.programujz.demo.boundary.api.controller.response

import java.time.Instant
import java.time.LocalDate
import java.util.UUID

data class TaskCompletionResponse(
    val id: UUID,
    val completedDate: LocalDate,
    val completedAt: Instant,
    val notes: String?
)