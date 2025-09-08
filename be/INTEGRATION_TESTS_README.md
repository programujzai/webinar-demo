# Integration Tests Setup

This project uses Spring Boot integration tests with Testcontainers for PostgreSQL database testing.

## Features

- **Testcontainers PostgreSQL**: Real PostgreSQL database running in Docker for tests
- **Database Migrations**: Flyway migrations run automatically during tests
- **Transaction Rollback**: Tests are transactional and rolled back after each test
- **Database Validation**: Direct database assertions to verify data persistence
- **API Testing**: Full HTTP request/response cycle testing with MockMvc

## Dependencies Added

```gradle
testImplementation 'org.testcontainers:testcontainers:1.20.4'
testImplementation 'org.testcontainers:postgresql:1.20.4'
testImplementation 'org.testcontainers:junit-jupiter:1.20.4'
testImplementation 'io.rest-assured:rest-assured:5.5.0'
testImplementation 'io.rest-assured:kotlin-extensions:5.5.0'
```

## Project Structure

```
src/test/kotlin/ai/programujz/demo/integration/
├── IntegrationTestBase.kt          # Base class with Testcontainers setup
├── TestDataBuilder.kt              # Test data factories and request helpers
├── SimpleIntegrationTest.kt        # Simple context loading test
└── TaskControllerIntegrationTest.kt # Comprehensive controller tests

src/test/resources/
└── application-integration-test.yml # Test configuration profile
```

## Key Components

### 1. IntegrationTestBase.kt
Base class that:
- Configures Testcontainers PostgreSQL container
- Sets up Spring Boot test context
- Provides database utility methods (cleanDatabase, countRows)
- Configures dynamic properties for database connection

### 2. TestDataBuilder.kt
Provides:
- Factory methods for creating test request payloads
- TestRequestHelper for simplified HTTP request execution
- Consistent test data generation

### 3. TaskControllerIntegrationTest.kt
Comprehensive integration tests covering:
- Task creation (one-time and recurring)
- Task retrieval and filtering
- Task updates
- Task completion with history tracking
- Task archiving and deletion
- Task reordering
- Database state validation

## Running Tests

### Run all tests:
```bash
./gradlew test
```

### Run only integration tests:
```bash
./gradlew test --tests "ai.programujz.demo.integration.*"
```

### Run specific test class:
```bash
./gradlew test --tests "ai.programujz.demo.integration.TaskControllerIntegrationTest"
```

### Run with detailed output:
```bash
./gradlew test --info
```

## Test Configuration

The tests use a dedicated profile `integration-test` with configuration in `application-integration-test.yml`:

```yaml
spring:
  datasource:
    driver-class-name: org.postgresql.Driver
  
  flyway:
    enabled: true
    locations: classpath:db/migration
    baseline-on-migrate: true
```

## Database Access During Tests

Tests can directly interact with the database using:

```kotlin
// Clean all tables
cleanDatabase()

// Count rows in a table
val count = countRows("tasks")

// Direct JDBC access
jdbcTemplate.execute("SELECT * FROM tasks")
```

## Test Patterns

### 1. Arrange-Act-Assert Pattern
```kotlin
@Test
fun testExample() {
    // Arrange
    val request = TestDataBuilder.createOneTimeTaskRequest(...)
    
    // Act
    val result = requestHelper.performPost("/api/v1/tasks", request)
    
    // Assert
    result.andExpect(status().isCreated)
    assert(countRows("tasks") == 1)
}
```

### 2. Nested Test Organization
Tests are organized using JUnit 5's @Nested classes for better structure:
- CreateTask
- GetTasks
- UpdateTask
- CompleteTask
- ArchiveAndDelete
- ReorderTasks

### 3. Database State Validation
Each test validates both:
- API response correctness
- Actual database state changes

## Docker Requirements

Testcontainers requires Docker to be running. The tests will automatically:
1. Pull the PostgreSQL 15 Alpine image if not present
2. Start a container with a test database
3. Run Flyway migrations
4. Execute tests
5. Stop and remove the container

## Troubleshooting

### Container Reuse
To enable container reuse between test runs, create `~/.testcontainers.properties`:
```properties
testcontainers.reuse.enable=true
```

### Debugging Database State
To inspect the database during test execution:
1. Add a breakpoint in your test
2. While paused, connect to the database using the logged JDBC URL
3. The URL is printed in test output: `Container is started (JDBC URL: jdbc:postgresql://localhost:XXXXX/testdb)`

### Test Failures
If tests fail:
1. Check test reports at `build/reports/tests/test/index.html`
2. Ensure Docker is running
3. Verify Flyway migrations are correct
4. Check for @Transactional rollback issues

## Best Practices

1. **Clean Database**: Always clean the database in @BeforeEach to ensure test isolation
2. **Use Test Builders**: Use TestDataBuilder for consistent test data
3. **Validate Database**: Assert both API responses and database state
4. **Transaction Management**: Be aware of @Transactional behavior in tests
5. **Container Lifecycle**: Let Testcontainers manage container lifecycle automatically

## Example Test

```kotlin
@Test
@DisplayName("Should create task and persist to database")
fun createTaskIntegrationTest() {
    // Given
    val request = TestDataBuilder.createOneTimeTaskRequest(
        title = "Integration Test Task",
        priority = "HIGH"
    )
    
    // When
    val result = requestHelper.performPost("/api/v1/tasks", request)
    
    // Then - API Response
    result.andExpect(status().isCreated)
        .andExpect(jsonPath("$.title").value("Integration Test Task"))
        .andExpect(jsonPath("$.priority").value("HIGH"))
    
    // Then - Database State
    assert(countRows("tasks") == 1) { "Expected 1 task in database" }
    
    val savedTask = jdbcTemplate.queryForMap(
        "SELECT * FROM tasks WHERE title = ?",
        "Integration Test Task"
    )
    assert(savedTask["priority"] == "HIGH")
}
```

## Security Considerations

The current tests may need adjustments for Spring Security. If you encounter 403 Forbidden errors:
1. Add proper authentication to test requests
2. Configure test security context
3. Use @WithMockUser or similar annotations
4. Disable security for integration tests if appropriate

## Future Enhancements

Consider adding:
1. Test fixtures for common test data
2. Custom assertions for domain objects
3. Performance testing with larger datasets
4. API documentation generation from tests
5. Test coverage reporting
6. Parallel test execution configuration