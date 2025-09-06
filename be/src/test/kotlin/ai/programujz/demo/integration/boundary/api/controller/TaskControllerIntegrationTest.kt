package ai.programujz.demo.integration.boundary.api.controller

import ai.programujz.demo.infrastructure.persistence.repository.TaskAggregateRepository
import ai.programujz.demo.integration.IntegrationTestBase
import ai.programujz.demo.integration.extractField
import ai.programujz.demo.integration.performPost
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.time.LocalDate
import java.util.UUID

@DisplayName("TaskController Integration Tests")
class TaskControllerIntegrationTest : IntegrationTestBase() {

    @Autowired
    private lateinit var taskAggregateRepository: TaskAggregateRepository

    @Test
    @DisplayName("Should create one-time task and persist to database")
    fun createOneTimeTask() {
        // Given - prepare the request JSON with correct structure
        val dueDate = LocalDate.now().plusDays(7)
        val request = """
            {
                "type": "ONE_TIME",
                "name": "Integration Test Task",
                "category": "Test Category",
                "dueDate": "$dueDate"
            }
        """.trimIndent()

        // When - perform the POST request
        val result = mockMvc.performPost("/api/v1/tasks", request)

        // Then - verify the response
        result.andExpect(status().isCreated)
            .andExpect(jsonPath("$.id").exists())
            .andExpect(jsonPath("$.name").value("Integration Test Task"))
            .andExpect(jsonPath("$.category").value("Test Category"))
            .andExpect(jsonPath("$.status").value("PENDING"))
            .andExpect(jsonPath("$.type").value("ONE_TIME"))
            .andExpect(jsonPath("$.dueDate").exists())
            .andExpect(jsonPath("$.createdAt").exists())

        // Extract ID from response using the extension function
        val taskId = UUID.fromString(result.extractField("id")!!)

        // Then - verify database state using the extracted ID
        val savedTask = taskAggregateRepository.findById(taskId)
        assertTrue(savedTask.isPresent, "Task should be found in database")
        
        val task = savedTask.get()
        assertEquals("Integration Test Task", task.name)
        assertEquals("Test Category", task.category)
        assertEquals("PENDING", task.status.name)
        assertEquals("ONE_TIME", task.taskType.name)
        assertNotNull(task.createdAt)
        assertNotNull(task.updatedAt)
        assertNull(task.deletedAt)
        
        // Verify one-time task specific details using raw JDBC
        val oneTimeDetails = jdbcTemplate.queryForMap(
            "SELECT * FROM one_time_tasks WHERE task_id = ?",
            taskId
        )
        assertEquals(dueDate.toString(), oneTimeDetails["due_date"].toString())
    }

    @Test
    @DisplayName("Should create recurring task with weekly pattern")
    fun createRecurringTask() {
        // Given - prepare the request JSON for a weekly recurring task
        val startDate = LocalDate.now()
        val endDate = LocalDate.now().plusMonths(3)
        val request = """
            {
                "type": "RECURRING",
                "name": "Weekly Team Meeting",
                "category": "Meetings",
                "recurrencePattern": "WEEKLY",
                "dayOfWeek": "MONDAY",
                "startDate": "$startDate",
                "endDate": "$endDate"
            }
        """.trimIndent()

        // When - perform the POST request
        val result = mockMvc.performPost("/api/v1/tasks", request)

        // Then - verify the response
        result.andExpect(status().isCreated)
            .andExpect(jsonPath("$.id").exists())
            .andExpect(jsonPath("$.name").value("Weekly Team Meeting"))
            .andExpect(jsonPath("$.category").value("Meetings"))
            .andExpect(jsonPath("$.status").value("PENDING"))
            .andExpect(jsonPath("$.type").value("RECURRING"))
            .andExpect(jsonPath("$.recurrencePattern").value("WEEKLY"))
            .andExpect(jsonPath("$.dayOfWeek").value("MONDAY"))
            .andExpect(jsonPath("$.startDate").exists())
            .andExpect(jsonPath("$.endDate").exists())

        // Extract ID from response using the extension function
        val taskId = UUID.fromString(result.extractField("id")!!)

        // Then - verify database state using the extracted ID
        val savedTask = taskAggregateRepository.findById(taskId)
        assertTrue(savedTask.isPresent, "Task should be found in database")
        
        val task = savedTask.get()
        assertEquals("Weekly Team Meeting", task.name)
        assertEquals("Meetings", task.category)
        assertEquals("PENDING", task.status.name)
        assertEquals("RECURRING", task.taskType.name)
        assertNotNull(task.createdAt)
        assertNotNull(task.updatedAt)
        assertNull(task.deletedAt)
        
        // Verify recurring task specific details using raw JDBC
        val recurringDetails = jdbcTemplate.queryForMap(
            "SELECT * FROM recurring_tasks WHERE task_id = ?",
            taskId
        )
        assertEquals("WEEKLY", recurringDetails["recurrence_pattern"])
        // day_of_week might be stored as integer (1 for MONDAY) in the database
        val dayOfWeek = recurringDetails["day_of_week"]
        assertTrue(dayOfWeek == "MONDAY" || dayOfWeek == 1 || dayOfWeek.toString() == "1", 
                   "Expected day_of_week to be MONDAY or 1, but was: $dayOfWeek")
        assertEquals(startDate.toString(), recurringDetails["start_date"].toString())
        assertEquals(endDate.toString(), recurringDetails["end_date"].toString())
    }
}