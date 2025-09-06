package ai.programujz.demo.infrastructure.persistence.repository

import ai.programujz.demo.infrastructure.persistence.entity.TaskAggregate
import org.springframework.data.jdbc.repository.query.Modifying
import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.time.LocalDate
import java.util.UUID

/**
 * Spring Data JDBC repository for TaskAggregate.
 * This single repository handles the entire aggregate including one-time/recurring details and completions.
 */
@Repository
interface TaskAggregateRepository : CrudRepository<TaskAggregate, UUID> {
    
    @Query("""
        SELECT * FROM tasks 
        WHERE deleted_at IS NULL 
        ORDER BY display_order
    """)
    fun findAllActive(): List<TaskAggregate>
    
    @Query("""
        SELECT * FROM tasks 
        WHERE category = :category 
        AND deleted_at IS NULL 
        ORDER BY display_order
    """)
    fun findByCategory(@Param("category") category: String): List<TaskAggregate>
    
    @Query("""
        SELECT * FROM tasks 
        WHERE status = :status 
        AND deleted_at IS NULL 
        ORDER BY display_order
    """)
    fun findByStatus(@Param("status") status: String): List<TaskAggregate>
    
    @Query("""
        SELECT t.* FROM tasks t
        INNER JOIN one_time_tasks ott ON t.id = ott.task_id
        WHERE ott.due_date = :date 
        AND t.deleted_at IS NULL
        ORDER BY t.display_order
    """)
    fun findOneTimeTasksByDueDate(@Param("date") date: LocalDate): List<TaskAggregate>
    
    @Query("""
        SELECT t.* FROM tasks t
        INNER JOIN one_time_tasks ott ON t.id = ott.task_id
        WHERE ott.due_date BETWEEN :startDate AND :endDate 
        AND t.deleted_at IS NULL
        ORDER BY t.display_order
    """)
    fun findOneTimeTasksByDueDateBetween(
        @Param("startDate") startDate: LocalDate, 
        @Param("endDate") endDate: LocalDate
    ): List<TaskAggregate>
    
    @Query("""
        SELECT t.* FROM tasks t
        INNER JOIN recurring_tasks rt ON t.id = rt.task_id
        WHERE rt.next_due_date = :date 
        AND t.deleted_at IS NULL
        ORDER BY t.display_order
    """)
    fun findRecurringTasksByNextDueDate(@Param("date") date: LocalDate): List<TaskAggregate>
    
    @Query("""
        SELECT t.* FROM tasks t
        INNER JOIN recurring_tasks rt ON t.id = rt.task_id
        WHERE rt.next_due_date <= :date 
        AND t.deleted_at IS NULL
        ORDER BY t.display_order
    """)
    fun findRecurringTasksDue(@Param("date") date: LocalDate): List<TaskAggregate>
    
    @Modifying
    @Query("""
        UPDATE tasks 
        SET deleted_at = CURRENT_TIMESTAMP, updated_at = CURRENT_TIMESTAMP 
        WHERE id = :id
    """)
    fun softDelete(@Param("id") id: UUID)
    
    @Query("""
        SELECT MAX(display_order) FROM tasks 
        WHERE deleted_at IS NULL
    """)
    fun findMaxDisplayOrder(): Int?
    
    @Query("""
        SELECT * FROM tasks 
        WHERE id = :id 
        AND deleted_at IS NULL
    """)
    fun findActiveById(@Param("id") id: UUID): TaskAggregate?
    
    @Query("""
        SELECT COUNT(*) FROM task_completions 
        WHERE task_id = :taskId 
        AND completed_date = :date
    """)
    fun hasCompletionForDate(
        @Param("taskId") taskId: UUID, 
        @Param("date") date: LocalDate
    ): Boolean
}