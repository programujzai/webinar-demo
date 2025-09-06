package ai.programujz.demo.domain.model

import java.util.UUID

/**
 * Value Object representing a Task identifier.
 * Ensures type safety and encapsulates ID generation logic.
 */
@JvmInline
value class TaskId private constructor(val value: UUID) {
    
    companion object {
        /**
         * Creates a new random TaskId
         */
        fun generate(): TaskId = TaskId(UUID.randomUUID())
        
        /**
         * Creates a TaskId from an existing UUID
         */
        fun from(uuid: UUID): TaskId = TaskId(uuid)
        
        /**
         * Creates a TaskId from a UUID string
         * @throws IllegalArgumentException if the string is not a valid UUID
         */
        fun from(uuidString: String): TaskId = TaskId(UUID.fromString(uuidString))
        
        /**
         * Creates a TaskId from a nullable UUID
         * Returns null if the input is null
         */
        fun fromNullable(uuid: UUID?): TaskId? = uuid?.let { TaskId(it) }
    }
    
    override fun toString(): String = value.toString()
}

/**
 * Extension function to convert nullable UUID to nullable TaskId
 */
fun UUID?.toTaskId(): TaskId? = this?.let { TaskId.from(it) }

/**
 * Extension function to convert TaskId to UUID
 */
fun TaskId?.toUUID(): UUID? = this?.value