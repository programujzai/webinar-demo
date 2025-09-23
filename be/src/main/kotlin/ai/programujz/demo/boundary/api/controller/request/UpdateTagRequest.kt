package ai.programujz.demo.boundary.api.controller.request

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size

data class UpdateTagRequest(
    @field:NotBlank(message = "Tag name cannot be blank")
    @field:Size(min = 1, max = 50, message = "Tag name must be between 1 and 50 characters")
    val name: String,
    
    @field:NotBlank(message = "Color cannot be blank")
    @field:Pattern(
        regexp = "^#[0-9A-Fa-f]{6}$",
        message = "Color must be a valid hex color code (e.g., #FF5733)"
    )
    val color: String
)