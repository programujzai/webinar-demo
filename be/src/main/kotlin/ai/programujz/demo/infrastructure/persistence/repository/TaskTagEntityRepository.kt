package ai.programujz.demo.infrastructure.persistence.repository

import ai.programujz.demo.infrastructure.persistence.entity.TaskTagEntity
import org.springframework.data.jdbc.repository.query.Modifying
import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface TaskTagEntityRepository : CrudRepository<TaskTagEntity, Pair<UUID, UUID>> {
    
    fun findByTaskId(taskId: UUID): List<TaskTagEntity>
    
    fun findByTagId(tagId: UUID): List<TaskTagEntity>
    
    @Modifying
    @Query("DELETE FROM task_tags WHERE task_id = :taskId")
    fun deleteByTaskId(@Param("taskId") taskId: UUID)
    
    @Modifying
    @Query("DELETE FROM task_tags WHERE tag_id = :tagId")
    fun deleteByTagId(@Param("tagId") tagId: UUID)
    
    @Modifying
    @Query("DELETE FROM task_tags WHERE task_id = :taskId AND tag_id = :tagId")
    fun deleteByTaskIdAndTagId(@Param("taskId") taskId: UUID, @Param("tagId") tagId: UUID)
    
    @Query("SELECT EXISTS(SELECT 1 FROM task_tags WHERE task_id = :taskId AND tag_id = :tagId)")
    fun existsByTaskIdAndTagId(@Param("taskId") taskId: UUID, @Param("tagId") tagId: UUID): Boolean
    
    @Modifying
    @Query("INSERT INTO task_tags (task_id, tag_id, created_at) VALUES (:taskId, :tagId, CURRENT_TIMESTAMP)")
    fun insertTaskTag(@Param("taskId") taskId: UUID, @Param("tagId") tagId: UUID)
}