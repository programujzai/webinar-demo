package ai.programujz.demo.infrastructure.persistence.repository

import ai.programujz.demo.domain.model.Tag
import ai.programujz.demo.domain.model.TagId
import ai.programujz.demo.domain.model.TaskId
import ai.programujz.demo.infrastructure.persistence.mapper.TagPersistenceMapper
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import ai.programujz.demo.domain.repository.TagRepository as DomainTagRepository

/**
 * Infrastructure implementation of domain TagRepository.
 * Handles all persistence operations and mapping between domain models and entities.
 */
@Repository
@Transactional
class TagRepositoryImpl(
    private val tagEntityRepository: TagEntityRepository,
    private val taskTagEntityRepository: TaskTagEntityRepository,
    private val mapper: TagPersistenceMapper
) : DomainTagRepository {

    override fun save(tag: Tag): Tag {
        val entity = mapper.toTagEntity(tag)
        val savedEntity = tagEntityRepository.save(entity)
        return mapper.toDomainTag(savedEntity)
    }

    override fun findById(id: TagId): Tag? {
        return tagEntityRepository.findById(id.value)
            .orElse(null)
            ?.let { mapper.toDomainTag(it) }
    }

    override fun findAll(): List<Tag> {
        return tagEntityRepository.findAllByOrderByName()
            .map { mapper.toDomainTag(it) }
    }

    override fun findByName(name: String): Tag? {
        return tagEntityRepository.findByNameIgnoreCase(name)
            ?.let { mapper.toDomainTag(it) }
    }

    override fun findByNameIgnoreCase(name: String): Tag? {
        return tagEntityRepository.findByNameIgnoreCase(name)
            ?.let { mapper.toDomainTag(it) }
    }

    override fun findByIds(ids: List<TagId>): List<Tag> {
        val uuids = ids.map { it.value }
        return tagEntityRepository.findByIdIn(uuids)
            .map { mapper.toDomainTag(it) }
    }

    override fun findTagsByTaskId(taskId: TaskId): List<Tag> {
        return tagEntityRepository.findTagsByTaskId(taskId.value)
            .map { mapper.toDomainTag(it) }
    }

    override fun delete(id: TagId) {
        // First remove all task-tag associations
        taskTagEntityRepository.deleteByTagId(id.value)
        // Then delete the tag itself
        tagEntityRepository.deleteById(id.value)
    }

    override fun existsById(id: TagId): Boolean {
        return tagEntityRepository.existsById(id.value)
    }

    override fun existsByNameIgnoreCase(name: String): Boolean {
        return tagEntityRepository.existsByNameIgnoreCase(name)
    }

    override fun getUsageCount(tagId: TagId): Int {
        return tagEntityRepository.getUsageCount(tagId.value)
    }

    override fun addTagToTask(taskId: TaskId, tagId: TagId) {
        if (!taskTagEntityRepository.existsByTaskIdAndTagId(taskId.value, tagId.value)) {
            taskTagEntityRepository.insertTaskTag(taskId.value, tagId.value)
        }
    }

    override fun removeTagFromTask(taskId: TaskId, tagId: TagId) {
        taskTagEntityRepository.deleteByTaskIdAndTagId(taskId.value, tagId.value)
    }

    override fun removeAllTagsFromTask(taskId: TaskId) {
        taskTagEntityRepository.deleteByTaskId(taskId.value)
    }

    override fun setTaskTags(taskId: TaskId, tagIds: List<TagId>) {
        // Remove all existing tags for the task
        taskTagEntityRepository.deleteByTaskId(taskId.value)
        
        // Add new tags
        tagIds.forEach { tagId ->
            taskTagEntityRepository.insertTaskTag(taskId.value, tagId.value)
        }
    }
}