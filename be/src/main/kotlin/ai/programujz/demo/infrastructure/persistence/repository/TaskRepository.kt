package ai.programujz.demo.infrastructure.persistence.repository

import ai.programujz.demo.infrastructure.persistence.entity.TaskAggregate
import ai.programujz.demo.infrastructure.persistence.entity.TaskStatus
import org.springframework.data.jdbc.repository.query.Modifying
import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.time.LocalDate
import java.util.*

@Repository
interface TaskAggregateRepository : CrudRepository<TaskAggregate, UUID> {

    fun findByDeletedAtIsNullOrderByDisplayOrder(): List<TaskAggregate>

    fun findByCategoryAndDeletedAtIsNullOrderByDisplayOrder(category: String): List<TaskAggregate>

    fun findByStatusAndDeletedAtIsNullOrderByDisplayOrder(status: TaskStatus): List<TaskAggregate>

    @Modifying
    @Query(
        """
        UPDATE tasks 
        SET deleted_at = CURRENT_TIMESTAMP, updated_at = CURRENT_TIMESTAMP 
        WHERE id = :id
    """
    )
    fun softDelete(@Param("id") id: UUID)

    @Query(
        """
        SELECT MAX(display_order) FROM tasks 
        WHERE deleted_at IS NULL
    """
    )
    fun findMaxDisplayOrder(): Int?

    @Query(
        """
        SELECT COUNT(*) FROM task_completions 
        WHERE task_id = :taskId 
        AND completed_date = :date
    """
    )
    fun hasCompletionForDate(
        @Param("taskId") taskId: UUID,
        @Param("date") date: LocalDate
    ): Boolean
}