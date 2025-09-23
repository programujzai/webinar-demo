package ai.programujz.demo.domain.repository

import ai.programujz.demo.domain.model.Tag
import ai.programujz.demo.domain.model.TagId
import ai.programujz.demo.domain.model.TaskId

interface TagRepository {
    fun save(tag: Tag): Tag
    fun findById(id: TagId): Tag?
    fun findAll(): List<Tag>
    fun findByName(name: String): Tag?
    fun findByNameIgnoreCase(name: String): Tag?
    fun findByIds(ids: List<TagId>): List<Tag>
    fun findTagsByTaskId(taskId: TaskId): List<Tag>
    fun delete(id: TagId)
    fun existsById(id: TagId): Boolean
    fun existsByNameIgnoreCase(name: String): Boolean
    fun getUsageCount(tagId: TagId): Int
    fun addTagToTask(taskId: TaskId, tagId: TagId)
    fun removeTagFromTask(taskId: TaskId, tagId: TagId)
    fun removeAllTagsFromTask(taskId: TaskId)
    fun setTaskTags(taskId: TaskId, tagIds: List<TagId>)
}