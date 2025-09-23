package ai.programujz.demo.domain.model

import java.util.UUID

/**
 * Value Object representing a Tag identifier.
 * Ensures type safety and encapsulates ID generation logic.
 */
@JvmInline
value class TagId private constructor(val value: UUID) {
    
    companion object {
        /**
         * Creates a new random TagId
         */
        fun generate(): TagId = TagId(UUID.randomUUID())
        
        /**
         * Creates a TagId from an existing UUID
         */
        fun from(uuid: UUID): TagId = TagId(uuid)
        
        /**
         * Creates a TagId from a UUID string
         * @throws IllegalArgumentException if the string is not a valid UUID
         */
        fun from(uuidString: String): TagId = TagId(UUID.fromString(uuidString))
        
        /**
         * Creates a TagId from a nullable UUID
         * Returns null if the input is null
         */
        fun fromNullable(uuid: UUID?): TagId? = uuid?.let { TagId(it) }
    }
    
    override fun toString(): String = value.toString()
}

/**
 * Extension function to convert nullable UUID to nullable TagId
 */
fun UUID?.toTagId(): TagId? = this?.let { TagId.from(it) }

/**
 * Extension function to convert TagId to UUID
 */
fun TagId?.toUUID(): UUID? = this?.value