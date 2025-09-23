package ai.programujz.demo.infrastructure.persistence.mapper

import ai.programujz.demo.domain.model.Tag
import ai.programujz.demo.domain.model.TagId
import ai.programujz.demo.domain.model.toTagId
import ai.programujz.demo.domain.model.toUUID as tagIdToUUID
import ai.programujz.demo.infrastructure.persistence.entity.TagEntity
import org.springframework.stereotype.Component

@Component
class TagPersistenceMapper {
    
    /**
     * Convert domain Tag to persistence TagEntity
     */
    fun toTagEntity(tag: Tag): TagEntity {
        return TagEntity(
            id = tag.id.tagIdToUUID(),
            name = tag.name,
            color = tag.color,
            createdAt = null,  // Will be set by Spring Data JDBC auditing if null
            updatedAt = null   // Will be set by Spring Data JDBC auditing
        )
    }
    
    /**
     * Convert persistence TagEntity to domain Tag
     */
    fun toDomainTag(entity: TagEntity): Tag {
        return Tag(
            id = entity.id.toTagId(),
            name = entity.name,
            color = entity.color,
            createdAt = entity.createdAt ?: throw IllegalStateException("TagEntity createdAt cannot be null"),
            updatedAt = entity.updatedAt ?: throw IllegalStateException("TagEntity updatedAt cannot be null")
        )
    }
    
    /**
     * Convert list of TagEntity to list of domain Tag
     */
    fun toDomainTags(entities: List<TagEntity>): List<Tag> {
        return entities.map { toDomainTag(it) }
    }
}