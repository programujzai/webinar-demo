package ai.programujz.demo.infrastructure.persistence.entity

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant
import java.util.*

@Table("tags")
data class TagEntity(
    @Id
    val id: UUID? = null,
    val name: String,
    val color: String,
    @CreatedDate
    val createdAt: Instant? = null,
    @LastModifiedDate
    val updatedAt: Instant? = null
)

@Table("task_tags")
data class TaskTagEntity(
    val taskId: UUID,
    val tagId: UUID,
    @CreatedDate
    val createdAt: Instant? = null
)