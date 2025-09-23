package ai.programujz.demo.infrastructure.persistence.repository

import ai.programujz.demo.infrastructure.persistence.entity.TagEntity
import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface TagEntityRepository : CrudRepository<TagEntity, UUID> {
    
    fun findAllByOrderByName(): List<TagEntity>
    
    @Query("SELECT * FROM tags WHERE LOWER(name) = LOWER(:name)")
    fun findByNameIgnoreCase(@Param("name") name: String): TagEntity?
    
    @Query("SELECT EXISTS(SELECT 1 FROM tags WHERE LOWER(name) = LOWER(:name))")
    fun existsByNameIgnoreCase(@Param("name") name: String): Boolean
    
    fun findByIdIn(ids: List<UUID>): List<TagEntity>
    
    @Query("""
        SELECT COUNT(*) FROM task_tags tt 
        WHERE tt.tag_id = :tagId
    """)
    fun getUsageCount(@Param("tagId") tagId: UUID): Int
    
    @Query("""
        SELECT t.* FROM tags t
        INNER JOIN task_tags tt ON t.id = tt.tag_id
        WHERE tt.task_id = :taskId
        ORDER BY t.name
    """)
    fun findTagsByTaskId(@Param("taskId") taskId: UUID): List<TagEntity>
}