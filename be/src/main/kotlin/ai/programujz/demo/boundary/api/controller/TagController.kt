package ai.programujz.demo.boundary.api.controller

import ai.programujz.demo.boundary.api.controller.request.CreateTagRequest
import ai.programujz.demo.boundary.api.controller.request.UpdateTagRequest
import ai.programujz.demo.boundary.api.controller.response.TagResponse
import ai.programujz.demo.boundary.api.controller.response.TagsResponse
import ai.programujz.demo.domain.TagService
import ai.programujz.demo.domain.model.Tag
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/api/v1/tags")
@CrossOrigin(origins = ["http://localhost:3000"])
class TagController(
    private val tagService: TagService
) {

    @GetMapping
    fun getAllTags(): ResponseEntity<TagsResponse> {
        val tags = tagService.getAllTags()
        val tagResponses = tags.map { tag ->
            TagResponse(
                id = tag.id!!.value,
                name = tag.name,
                color = tag.color,
                usageCount = tagService.getTagUsageCount(tag.id.value)
            )
        }
        return ResponseEntity.ok(TagsResponse(tagResponses))
    }

    @GetMapping("/{id}")
    fun getTag(@PathVariable id: UUID): ResponseEntity<TagResponse> {
        val tag = tagService.getTag(id)
        val tagResponse = TagResponse(
            id = tag.id!!.value,
            name = tag.name,
            color = tag.color,
            usageCount = tagService.getTagUsageCount(id)
        )
        return ResponseEntity.ok(tagResponse)
    }

    @PostMapping
    fun createTag(@Valid @RequestBody request: CreateTagRequest): ResponseEntity<TagResponse> {
        val tag = tagService.createTag(request.name, request.color)
        val tagResponse = TagResponse(
            id = tag.id!!.value,
            name = tag.name,
            color = tag.color,
            usageCount = 0 // New tag has no usage
        )
        return ResponseEntity.status(HttpStatus.CREATED).body(tagResponse)
    }

    @PutMapping("/{id}")
    fun updateTag(
        @PathVariable id: UUID,
        @Valid @RequestBody request: UpdateTagRequest
    ): ResponseEntity<TagResponse> {
        val tag = tagService.updateTag(id, request.name, request.color)
        val tagResponse = TagResponse(
            id = tag.id!!.value,
            name = tag.name,
            color = tag.color,
            usageCount = tagService.getTagUsageCount(id)
        )
        return ResponseEntity.ok(tagResponse)
    }

    @DeleteMapping("/{id}")
    fun deleteTag(@PathVariable id: UUID): ResponseEntity<Void> {
        tagService.deleteTag(id)
        return ResponseEntity.noContent().build()
    }
}