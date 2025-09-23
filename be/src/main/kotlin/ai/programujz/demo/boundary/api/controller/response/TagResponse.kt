package ai.programujz.demo.boundary.api.controller.response

import java.util.*

data class TagResponse(
    val id: UUID,
    val name: String,
    val color: String,
    val usageCount: Int
)

data class TagsResponse(
    val tags: List<TagResponse>
)