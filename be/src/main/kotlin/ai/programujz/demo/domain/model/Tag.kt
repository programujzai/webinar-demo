package ai.programujz.demo.domain.model

import java.time.Instant

/**
 * Domain model representing a Tag entity.
 * Tags can be associated with tasks to provide better categorization and filtering.
 */
data class Tag(
    val id: TagId? = null,
    val name: String,
    val color: String,
    val createdAt: Instant = Instant.now(),
    val updatedAt: Instant = Instant.now()
) {
    init {
        require(name.isNotBlank()) { "Tag name cannot be blank" }
        require(name.length <= 50) { "Tag name cannot exceed 50 characters" }
        require(color.matches(HEX_COLOR_REGEX)) { "Tag color must be a valid hex color code (e.g., #FF5733)" }
    }

    companion object {
        private val HEX_COLOR_REGEX = Regex("^#[0-9A-Fa-f]{6}$")
        
        /**
         * Predefined color palette for auto-generating tag colors
         */
        private val DEFAULT_COLORS = listOf(
            "#FF5733", "#33FF57", "#3357FF", "#FF33F5", "#F5FF33",
            "#33FFF5", "#F533FF", "#5733FF", "#FF5733", "#57FF33",
            "#3357FF", "#FF5733", "#5733FF", "#33F5FF", "#F53357",
            "#57F533", "#3357F5", "#F5FF57", "#FF3357", "#5733F5"
        )
        
        /**
         * Generates a random color from the predefined palette
         */
        fun generateRandomColor(): String = DEFAULT_COLORS.random()
    }
}