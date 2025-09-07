# Integration Testing Guide

## Table of Contents
1. [Overview](#overview)
2. [Phase 1: Analyze the Changes](#phase-1-analyze-the-changes)
3. [Phase 2: Define Test Cases](#phase-2-define-test-cases)
4. [Phase 3: Check Existing Tests](#phase-3-check-existing-tests)
5. [Phase 4: Implement Tests](#phase-4-implement-tests)
6. [Phase 5: Verify and Refactor](#phase-5-verify-and-refactor)
7. [Complete Example](#complete-example)
8. [Best Practices](#best-practices)

## Overview

Integration tests verify that different components of your application work together correctly. Unlike unit tests, they test the full request-response cycle, including:
- HTTP request/response handling
- Data persistence
- Transaction management
- Validation
- Error handling

## Phase 1: Analyze the Changes

### Goal
Understand what functionality needs to be tested.

### Steps
1. **Identify the endpoint(s)**
   - HTTP method (GET, POST, PUT, DELETE)
   - URL path and parameters
   - Request/response structure

2. **Understand the business logic**
   - What should happen on success?
   - What validations are in place?
   - What error cases exist?

3. **Identify dependencies**
   - Database operations
   - External services
   - Security requirements

### Example Analysis
```kotlin
// Analyzing a new endpoint: POST /api/v1/tasks
/*
 * Endpoint: Create Task
 * Method: POST
 * Path: /api/v1/tasks
 * Request Body: CreateTaskRequest (polymorphic - OneTime or Recurring)
 * Response: TaskDto
 * 
 * Business Logic:
 * - Validates request data
 * - Creates task in database
 * - Returns created task with generated ID
 * 
 * Dependencies:
 * - TaskRepository (database)
 * - TaskService (business logic)
 */
```

## Phase 2: Define Test Cases

### Goal
Create a comprehensive list of test scenarios.

### Test Case Categories

#### 1. Happy Path Tests
- Valid input with all required fields
- Valid input with optional fields
- Different variations of valid data

#### 2. Validation Tests
- Missing required fields
- Invalid field formats
- Business rule violations

#### 3. Edge Cases
- Boundary values
- Special characters
- Maximum/minimum values

#### 4. Error Scenarios
- Database failures
- Concurrent operations
- Authorization failures

### Example Test Cases
```markdown
## Test Cases for POST /api/v1/tasks

### Happy Path
1. ✅ Should create one-time task with all fields
2. ✅ Should create recurring task with weekly pattern
3. ✅ Should create task with minimal required fields

### Validation
4. ❌ Should reject task with past due date
5. ❌ Should reject task without name
6. ❌ Should reject recurring task without recurrence pattern

### Edge Cases
7. 🔄 Should handle task name with special characters
8. 🔄 Should handle maximum length task name
9. 🔄 Should create task with due date far in future

### Error Scenarios
10. 💥 Should return 400 for malformed JSON
11. 💥 Should return 401 for unauthorized request
12. 💥 Should handle database constraint violations
```

## Phase 3: Check Existing Tests

### Goal
Avoid duplication and understand current test coverage.

### Steps

1. **Search for existing test files**
```bash
# Find test files for the controller
find src/test -name "*TaskController*Test.kt"

# Search for tests of specific endpoints
grep -r "POST.*tasks" src/test/
```

2. **Review existing test coverage**
```kotlin
// Check what's already tested
class ExistingTaskControllerTest {
    // ✅ Already tested: Basic creation
    // ❌ Not tested: Validation errors
    // ❌ Not tested: Edge cases
}
```

3. **Decide on approach**
- **Extend**: Add new test methods to existing class
- **Create**: New test class for new functionality
- **Refactor**: Reorganize tests for better structure

### Example Discovery
```kotlin
// Found: TaskControllerIntegrationTest.kt
// Status: Has basic happy path tests
// Action: Extend with validation and error tests
```

## Phase 4: Implement Tests

### Goal
Write clean, maintainable integration tests.

### Test Structure

#### 1. Test Class Setup
```kotlin
@DisplayName("TaskController Integration Tests")
class TaskControllerIntegrationTest : IntegrationTestBase() {

    @Autowired
    private lateinit var taskRepository: TaskAggregateRepository
    
    @BeforeEach
    fun setUp() {
        // Clean state before each test
        cleanDatabase()
    }
}
```

#### 2. Test Method Structure (Given-When-Then)
```kotlin
@Test
@DisplayName("Should create one-time task and persist to database")
fun createOneTimeTask() {
    // Given - Prepare test data
    val dueDate = LocalDate.now().plusDays(7)
    val request = """
        {
            "type": "ONE_TIME",
            "name": "Integration Test Task",
            "category": "Test Category",
            "dueDate": "$dueDate"
        }
    """.trimIndent()

    // When - Execute the action
    val result = mockMvc.performPost("/api/v1/tasks", request)

    // Then - Verify the outcome
    result.andExpect(status().isCreated)
        .andExpect(jsonPath("$.id").exists())
        .andExpect(jsonPath("$.name").value("Integration Test Task"))
    
    // Then - Verify database state
    val taskId = UUID.fromString(result.extractField("id")!!)
    val savedTask = taskRepository.findById(taskId)
    assertTrue(savedTask.isPresent)
    assertEquals("Integration Test Task", savedTask.get().name)
}
```

#### 3. Validation Test Example
```kotlin
@Test
@DisplayName("Should reject task with past due date")
fun createTaskWithPastDueDate() {
    // Given - Invalid data with past date
    val pastDate = LocalDate.now().minusDays(1)
    val request = """
        {
            "type": "ONE_TIME",
            "name": "Invalid Task",
            "dueDate": "$pastDate"
        }
    """.trimIndent()

    // When - Attempt to create
    val result = mockMvc.performPost("/api/v1/tasks", request)

    // Then - Verify rejection
    result.andExpect(status().isBadRequest)
        .andExpect(jsonPath("$.error").exists())
        .andExpect(jsonPath("$.message").value(containsString("past")))
    
    // Verify nothing was saved
    assertEquals(0, taskRepository.count())
}
```

#### 4. Edge Case Test Example
```kotlin
@Test
@DisplayName("Should handle task with maximum length name")
fun createTaskWithMaxLengthName() {
    // Given - Edge case data
    val maxLengthName = "A".repeat(255) // Assuming 255 is max
    val request = """
        {
            "type": "ONE_TIME",
            "name": "$maxLengthName",
            "dueDate": "${LocalDate.now().plusDays(1)}"
        }
    """.trimIndent()

    // When
    val result = mockMvc.performPost("/api/v1/tasks", request)

    // Then
    result.andExpect(status().isCreated)
    val taskId = UUID.fromString(result.extractField("id")!!)
    val saved = taskRepository.findById(taskId).get()
    assertEquals(255, saved.name.length)
}
```

## Phase 5: Verify and Refactor

### Goal
Ensure tests are reliable and maintainable.

### Verification Steps

1. **Run tests multiple times**
```bash
# Run specific test multiple times to check stability
./gradlew test --tests TaskControllerIntegrationTest --rerun-tasks
```

2. **Check test independence**
```bash
# Run tests in different order
./gradlew test --tests TaskControllerIntegrationTest --random-order
```

3. **Verify cleanup**
```kotlin
@Test
fun verifyTestIsolation() {
    // Each test should start with clean database
    assertEquals(0, taskRepository.count(), "Database not clean")
}
```

### Refactoring Checklist

- [ ] Remove duplication using helper methods
- [ ] Extract common test data to builders/factories
- [ ] Group related tests using nested classes
- [ ] Add descriptive display names
- [ ] Ensure consistent assertion style

### Example Refactoring
```kotlin
// Before - Duplication
val request1 = """{"type": "ONE_TIME", "name": "Task 1", ...}"""
val request2 = """{"type": "ONE_TIME", "name": "Task 2", ...}"""

// After - Helper method
private fun createOneTimeTaskRequest(
    name: String = "Test Task",
    dueDate: LocalDate = LocalDate.now().plusDays(7)
) = """
    {
        "type": "ONE_TIME",
        "name": "$name",
        "dueDate": "$dueDate"
    }
""".trimIndent()
```

## Complete Example

Here's a complete integration test class following all phases:

```kotlin
package ai.programujz.demo.integration.boundary.api.controller

import ai.programujz.demo.infrastructure.persistence.repository.TaskAggregateRepository
import ai.programujz.demo.integration.IntegrationTestBase
import ai.programujz.demo.integration.extractField
import ai.programujz.demo.integration.performPost
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import java.time.LocalDate
import java.util.UUID

@DisplayName("TaskController Integration Tests")
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class TaskControllerIntegrationTest : IntegrationTestBase() {

    @Autowired
    private lateinit var taskRepository: TaskAggregateRepository

    // ========== Happy Path Tests ==========
    
    @Nested
    @DisplayName("Task Creation - Happy Path")
    inner class TaskCreationHappyPath {
        
        @Test
        @Order(1)
        @DisplayName("Should create one-time task with all fields")
        fun createCompleteOneTimeTask() {
            // Given
            val request = createOneTimeTaskRequest(
                name = "Complete Integration Test",
                category = "Testing",
                dueDate = LocalDate.now().plusDays(7)
            )

            // When
            val result = mockMvc.performPost("/api/v1/tasks", request)

            // Then - Response validation
            result.andExpect(status().isCreated)
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("Complete Integration Test"))
                .andExpect(jsonPath("$.category").value("Testing"))

            // Then - Database validation
            val taskId = UUID.fromString(result.extractField("id")!!)
            verifyTaskInDatabase(taskId, "Complete Integration Test", "Testing")
        }

        @Test
        @Order(2)
        @DisplayName("Should create recurring task with weekly pattern")
        fun createWeeklyRecurringTask() {
            // Given
            val request = createRecurringTaskRequest(
                name = "Weekly Meeting",
                recurrencePattern = "WEEKLY",
                dayOfWeek = "MONDAY"
            )

            // When
            val result = mockMvc.performPost("/api/v1/tasks", request)

            // Then
            result.andExpect(status().isCreated)
                .andExpect(jsonPath("$.recurrencePattern").value("WEEKLY"))
            
            val taskId = UUID.fromString(result.extractField("id")!!)
            verifyRecurringTaskInDatabase(taskId, "WEEKLY", "MONDAY")
        }
    }

    // ========== Validation Tests ==========
    
    @Nested
    @DisplayName("Task Creation - Validation")
    inner class TaskCreationValidation {
        
        @Test
        @DisplayName("Should reject task without name")
        fun rejectTaskWithoutName() {
            // Given
            val request = """
                {
                    "type": "ONE_TIME",
                    "dueDate": "${LocalDate.now().plusDays(1)}"
                }
            """.trimIndent()

            // When
            val result = mockMvc.performPost("/api/v1/tasks", request)

            // Then
            result.andExpect(status().isBadRequest)
                .andExpect(jsonPath("$.error").exists())
            
            assertEquals(0, taskRepository.count())
        }

        @Test
        @DisplayName("Should reject task with past due date")
        fun rejectTaskWithPastDueDate() {
            // Given
            val request = createOneTimeTaskRequest(
                dueDate = LocalDate.now().minusDays(1)
            )

            // When
            val result = mockMvc.performPost("/api/v1/tasks", request)

            // Then
            result.andExpect(status().isBadRequest)
                .andExpect(jsonPath("$.message").value(containsString("future")))
            
            assertEquals(0, taskRepository.count())
        }
    }

    // ========== Edge Cases ==========
    
    @Nested
    @DisplayName("Task Creation - Edge Cases")
    inner class TaskCreationEdgeCases {
        
        @Test
        @DisplayName("Should handle task with special characters in name")
        fun createTaskWithSpecialCharacters() {
            // Given
            val specialName = "Task with émojis 🎯 & symbols!@#$%"
            val request = createOneTimeTaskRequest(name = specialName)

            // When
            val result = mockMvc.performPost("/api/v1/tasks", request)

            // Then
            result.andExpect(status().isCreated)
            val taskId = UUID.fromString(result.extractField("id")!!)
            val saved = taskRepository.findById(taskId).get()
            assertEquals(specialName, saved.name)
        }

        @Test
        @DisplayName("Should handle maximum length task name")
        fun createTaskWithMaxLengthName() {
            // Given
            val maxName = "A".repeat(255)
            val request = createOneTimeTaskRequest(name = maxName)

            // When
            val result = mockMvc.performPost("/api/v1/tasks", request)

            // Then
            result.andExpect(status().isCreated)
            val saved = taskRepository.findById(
                UUID.fromString(result.extractField("id")!!)
            ).get()
            assertEquals(255, saved.name.length)
        }
    }

    // ========== Helper Methods ==========
    
    private fun createOneTimeTaskRequest(
        name: String = "Test Task",
        category: String? = null,
        dueDate: LocalDate = LocalDate.now().plusDays(7)
    ): String {
        val categoryField = category?.let { """"category": "$it",""" } ?: ""
        return """
            {
                "type": "ONE_TIME",
                "name": "$name",
                $categoryField
                "dueDate": "$dueDate"
            }
        """.trimIndent()
    }

    private fun createRecurringTaskRequest(
        name: String = "Recurring Task",
        recurrencePattern: String = "DAILY",
        dayOfWeek: String? = null,
        startDate: LocalDate = LocalDate.now(),
        endDate: LocalDate = LocalDate.now().plusMonths(3)
    ): String {
        val dayOfWeekField = dayOfWeek?.let { """"dayOfWeek": "$it",""" } ?: ""
        return """
            {
                "type": "RECURRING",
                "name": "$name",
                "recurrencePattern": "$recurrencePattern",
                $dayOfWeekField
                "startDate": "$startDate",
                "endDate": "$endDate"
            }
        """.trimIndent()
    }

    private fun verifyTaskInDatabase(
        taskId: UUID,
        expectedName: String,
        expectedCategory: String?
    ) {
        val task = taskRepository.findById(taskId)
        assertTrue(task.isPresent, "Task should exist in database")
        task.get().apply {
            assertEquals(expectedName, name)
            assertEquals(expectedCategory, category)
            assertNotNull(createdAt)
            assertNull(deletedAt)
        }
    }

    private fun verifyRecurringTaskInDatabase(
        taskId: UUID,
        expectedPattern: String,
        expectedDayOfWeek: String?
    ) {
        val recurringDetails = jdbcTemplate.queryForMap(
            "SELECT * FROM recurring_tasks WHERE task_id = ?",
            taskId
        )
        assertEquals(expectedPattern, recurringDetails["recurrence_pattern"])
        expectedDayOfWeek?.let {
            assertTrue(
                recurringDetails["day_of_week"].toString() == it || 
                recurringDetails["day_of_week"] == it.toDayOfWeekNumber(),
                "Day of week mismatch"
            )
        }
    }

    private fun String.toDayOfWeekNumber(): Int = when(this) {
        "MONDAY" -> 1
        "TUESDAY" -> 2
        "WEDNESDAY" -> 3
        "THURSDAY" -> 4
        "FRIDAY" -> 5
        "SATURDAY" -> 6
        "SUNDAY" -> 7
        else -> 0
    }
}
```

## Best Practices

### 1. Test Naming
- Use descriptive names that explain what is being tested
- Include the expected outcome in the name
- Use @DisplayName for better test reports

### 2. Test Data
- Use realistic but deterministic data
- Avoid random values that make tests flaky
- Create helper methods for common test data

### 3. Assertions
- Verify both response and database state
- Use specific assertions rather than generic ones
- Include meaningful assertion messages

### 4. Test Organization
- Group related tests using @Nested classes
- Order tests if there are dependencies
- Keep tests focused on single scenarios

### 5. Performance
- Use @DirtiesContext sparingly
- Reuse test containers when possible
- Clean up data in @BeforeEach, not @AfterEach

### 6. Maintenance
- Extract common setup to helper methods
- Use constants for repeated values
- Keep tests DRY but readable

## Troubleshooting Common Issues

### Issue 1: Flaky Tests
**Symptom**: Tests pass sometimes, fail others
**Solution**: 
- Remove time-dependent assertions
- Ensure proper database cleanup
- Check for test order dependencies

### Issue 2: Slow Tests
**Symptom**: Integration tests take too long
**Solution**:
- Use testcontainers with reuse
- Minimize database operations
- Run in parallel when possible

### Issue 3: Database State Issues
**Symptom**: Tests fail due to dirty database
**Solution**:
- Add cleanup in @BeforeEach
- Use @Transactional with @Rollback
- Verify isolation between tests

## Checklist for New Integration Tests

- [ ] Analyzed the endpoint/functionality to test
- [ ] Defined comprehensive test cases
- [ ] Checked for existing test coverage
- [ ] Implemented happy path tests
- [ ] Added validation tests
- [ ] Included edge cases
- [ ] Verified error handling
- [ ] Extracted common code to helpers
- [ ] Added meaningful test names
- [ ] Verified test independence
- [ ] Ran tests multiple times successfully
- [ ] Reviewed with team

## Resources

- [Spring Boot Testing Documentation](https://docs.spring.io/spring-boot/docs/current/reference/html/spring-boot-features.html#boot-features-testing)
- [MockMvc Documentation](https://docs.spring.io/spring-framework/docs/current/reference/html/testing.html#spring-mvc-test-framework)
- [JUnit 5 User Guide](https://junit.org/junit5/docs/current/user-guide/)
- [Testcontainers Documentation](https://www.testcontainers.org/)