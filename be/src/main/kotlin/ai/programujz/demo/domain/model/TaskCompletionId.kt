package ai.programujz.demo.domain.model

import java.util.UUID

/**
 * Value Object representing a TaskCompletion identifier.
 * Ensures type safety and encapsulates ID generation logic.
 */
@JvmInline
value class TaskCompletionId private constructor(val value: UUID) {
    
    companion object {
        /**
         * Creates a new random TaskCompletionId
         */
        fun generate(): TaskCompletionId = TaskCompletionId(UUID.randomUUID())
        
        /**
         * Creates a TaskCompletionId from an existing UUID
         */
        fun from(uuid: UUID): TaskCompletionId = TaskCompletionId(uuid)
        
        /**
         * Creates a TaskCompletionId from a UUID string
         * @throws IllegalArgumentException if the string is not a valid UUID
         */
        fun from(uuidString: String): TaskCompletionId = TaskCompletionId(UUID.fromString(uuidString))
        
        /**
         * Creates a TaskCompletionId from a nullable UUID
         * Returns null if the input is null
         */
        fun fromNullable(uuid: UUID?): TaskCompletionId? = uuid?.let { TaskCompletionId(it) }
    }
    
    override fun toString(): String = value.toString()
}

/**
 * Extension function to convert nullable UUID to nullable TaskCompletionId
 */
fun UUID?.toTaskCompletionId(): TaskCompletionId? = this?.let { TaskCompletionId.from(it) }

/**
 * Extension function to convert TaskCompletionId to UUID
 */
fun TaskCompletionId?.toUUID(): UUID? = this?.value