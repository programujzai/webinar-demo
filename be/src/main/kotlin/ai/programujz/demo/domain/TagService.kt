package ai.programujz.demo.domain

import ai.programujz.demo.domain.exception.TagAlreadyExistsException
import ai.programujz.demo.domain.exception.TagNotFoundException
import ai.programujz.demo.domain.exception.TagsNotFoundException
import ai.programujz.demo.domain.exception.TooManyTagsException
import ai.programujz.demo.domain.model.Tag
import ai.programujz.demo.domain.model.TagId
import ai.programujz.demo.domain.repository.TagRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
@Transactional
class TagService(
    private val tagRepository: TagRepository
) {

    fun createTag(name: String, color: String? = null): Tag {
        // Check if tag with same name already exists (case-insensitive)
        val existingTag = tagRepository.findByNameIgnoreCase(name)
        if (existingTag != null) {
            throw TagAlreadyExistsException(name)
        }

        val finalColor = color ?: Tag.generateRandomColor()
        val tag = Tag(
            name = name.trim(),
            color = finalColor
        )

        return tagRepository.save(tag)
    }

    fun updateTag(id: UUID, name: String, color: String): Tag {
        val tagId = TagId.from(id)
        val existingTag = tagRepository.findById(tagId)
            ?: throw TagNotFoundException(id.toString())

        // Check if another tag with same name already exists (case-insensitive)
        val tagWithSameName = tagRepository.findByNameIgnoreCase(name)
        if (tagWithSameName != null && tagWithSameName.id != tagId) {
            throw TagAlreadyExistsException(name)
        }

        val updatedTag = existingTag.copy(
            name = name.trim(),
            color = color
        )

        return tagRepository.save(updatedTag)
    }

    fun getTag(id: UUID): Tag {
        val tagId = TagId.from(id)
        return tagRepository.findById(tagId)
            ?: throw TagNotFoundException(id.toString())
    }

    fun getAllTags(): List<Tag> {
        val tags = tagRepository.findAll()
        
        // Add usage count to each tag
        return tags.map { tag ->
            tag.copy() // Usage count will be calculated on demand via API response
        }.sortedBy { it.name }
    }

    fun getTagUsageCount(id: UUID): Int {
        val tagId = TagId.from(id)
        if (!tagRepository.existsById(tagId)) {
            throw TagNotFoundException(id.toString())
        }
        return tagRepository.getUsageCount(tagId)
    }

    fun deleteTag(id: UUID) {
        val tagId = TagId.from(id)
        if (!tagRepository.existsById(tagId)) {
            throw TagNotFoundException(id.toString())
        }

        // Repository will handle cascade delete of task-tag associations
        tagRepository.delete(tagId)
    }

    fun getTagsByIds(ids: List<UUID>): List<Tag> {
        val tagIds = ids.map { TagId.from(it) }
        val foundTags = tagRepository.findByIds(tagIds)
        
        // Validate all tags exist
        val foundTagIds = foundTags.mapNotNull { it.id }
        val missingTagIds = tagIds.filter { it !in foundTagIds }
        
        if (missingTagIds.isNotEmpty()) {
            throw TagsNotFoundException(missingTagIds.map { it.toString() })
        }
        
        return foundTags
    }

    fun validateTagsExist(tagIds: List<UUID>) {
        if (tagIds.isEmpty()) return
        
        val tags = getTagsByIds(tagIds)
        if (tags.size != tagIds.size) {
            val foundIds = tags.mapNotNull { it.id?.value }
            val missingIds = tagIds.filter { it !in foundIds }
            throw TagsNotFoundException(missingIds.map { it.toString() })
        }
    }

    fun validateTagLimit(tagIds: List<UUID>) {
        if (tagIds.size > MAX_TAGS_PER_TASK) {
            throw TooManyTagsException(tagIds.size, MAX_TAGS_PER_TASK)
        }
    }

    companion object {
        const val MAX_TAGS_PER_TASK = 10
    }
}