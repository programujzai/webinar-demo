# Task Management API Documentation

## Base URL
```
http://localhost:8080/api/v1
```

## Authentication
Currently no authentication is configured (test configuration in place).

## Common Data Types

### TaskStatus
Enum representing the status of a task:
- `PENDING` - Task is active and not completed
- `COMPLETED` - Task has been completed
- `ARCHIVED` - Task has been archived

### RecurrencePattern
Enum representing how often a task recurs:
- `DAILY` - Task recurs every day
- `WEEKLY` - Task recurs weekly on a specific day

### TaskType
Used in JSON `type` field to distinguish between task types:
- `ONE_TIME` - Single occurrence task with a specific due date
- `RECURRING` - Task that repeats based on a pattern

## Endpoints

### 1. Create Task
Creates a new task (either one-time or recurring).

**Endpoint:** `POST /tasks`

**Request Body:**

#### One-Time Task
```json
{
  "type": "ONE_TIME",
  "name": "Complete project report",
  "category": "Work",
  "dueDate": "2024-12-31"
}
```

#### Recurring Task
```json
{
  "type": "RECURRING",
  "name": "Weekly team meeting",
  "category": "Meetings",
  "recurrencePattern": "WEEKLY",
  "dayOfWeek": "MONDAY",
  "startDate": "2024-01-01",
  "endDate": "2024-12-31"
}
```

**Response:** `201 Created`
```json
{
  "type": "ONE_TIME",
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "name": "Complete project report",
  "displayOrder": 1,
  "category": "Work",
  "status": "PENDING",
  "dueDate": "2024-12-31",
  "completedAt": null,
  "createdAt": "2024-01-01T10:00:00Z",
  "updatedAt": "2024-01-01T10:00:00Z"
}
```

**Validation Rules:**
- `name` is required and cannot be blank
- For ONE_TIME tasks: `dueDate` must be in the future
- For RECURRING tasks: 
  - `startDate` cannot be in the past
  - `dayOfWeek` is required when `recurrencePattern` is WEEKLY

### 2. Update Task
Updates an existing task.

**Endpoint:** `PUT /tasks/{id}`

**Path Parameters:**
- `id` (UUID) - Task identifier

**Request Body:**
```json
{
  "name": "Updated task name",
  "category": "Personal",
  "dueDate": "2024-12-25",
  "recurrencePattern": "DAILY",
  "dayOfWeek": "FRIDAY",
  "endDate": "2024-12-31"
}
```

**Note:** All fields are optional. Only provided fields will be updated.

**Response:** `200 OK`
Returns the updated task in the same format as Create Task response.

### 3. Get Task by ID
Retrieves a specific task by its ID.

**Endpoint:** `GET /tasks/{id}`

**Path Parameters:**
- `id` (UUID) - Task identifier

**Response:** `200 OK`
Returns the task in the same format as Create Task response.

### 4. Get Tasks (List/Filter)
Retrieves a list of tasks with optional filtering.

**Endpoint:** `GET /tasks`

**Query Parameters (all optional):**
- `category` (string) - Filter by category
- `status` (TaskStatus) - Filter by status (PENDING, COMPLETED, ARCHIVED)
- `dueDate` (date) - Get tasks due on a specific date
- `startDate` (date) - Used with `endDate` to get tasks due in a date range
- `endDate` (date) - Used with `startDate` to get tasks due in a date range

**Examples:**
- Get all tasks: `GET /tasks`
- Get tasks by category: `GET /tasks?category=Work`
- Get tasks due today: `GET /tasks?dueDate=2024-01-01`
- Get tasks due between dates: `GET /tasks?startDate=2024-01-01&endDate=2024-01-31`

**Response:** `200 OK`
```json
[
  {
    "type": "ONE_TIME",
    "id": "550e8400-e29b-41d4-a716-446655440000",
    "name": "Task 1",
    "displayOrder": 1,
    "category": "Work",
    "status": "PENDING",
    "dueDate": "2024-12-31",
    "completedAt": null,
    "createdAt": "2024-01-01T10:00:00Z",
    "updatedAt": "2024-01-01T10:00:00Z"
  },
  {
    "type": "RECURRING",
    "id": "550e8400-e29b-41d4-a716-446655440001",
    "name": "Task 2",
    "displayOrder": 2,
    "category": "Personal",
    "status": "PENDING",
    "recurrencePattern": "WEEKLY",
    "dayOfWeek": "MONDAY",
    "startDate": "2024-01-01",
    "endDate": "2024-12-31",
    "nextDueDate": "2024-01-08",
    "lastCompletedDate": null,
    "createdAt": "2024-01-01T10:00:00Z",
    "updatedAt": "2024-01-01T10:00:00Z"
  }
]
```

### 5. Complete Task
Marks a task as completed with optional notes.

**Endpoint:** `POST /tasks/{id}/complete`

**Path Parameters:**
- `id` (UUID) - Task identifier

**Request Body (optional):**
```json
{
  "notes": "Completed ahead of schedule"
}
```

**Response:** `200 OK`
Returns the updated task with status changed to COMPLETED.

### 6. Archive Task
Archives a task.

**Endpoint:** `POST /tasks/{id}/archive`

**Path Parameters:**
- `id` (UUID) - Task identifier

**Response:** `200 OK`
Returns the updated task with status changed to ARCHIVED.

### 7. Delete Task
Permanently deletes a task.

**Endpoint:** `DELETE /tasks/{id}`

**Path Parameters:**
- `id` (UUID) - Task identifier

**Response:** `204 No Content`

### 8. Reorder Tasks
Updates the display order of multiple tasks.

**Endpoint:** `PUT /tasks/reorder`

**Request Body:**
```json
{
  "taskOrders": [
    {
      "id": "550e8400-e29b-41d4-a716-446655440000",
      "displayOrder": 1
    },
    {
      "id": "550e8400-e29b-41d4-a716-446655440001",
      "displayOrder": 2
    }
  ]
}
```

**Response:** `200 OK`
Returns the updated list of all tasks with new display orders.

### 9. Get Task Completions
Retrieves the completion history for a recurring task.

**Endpoint:** `GET /tasks/{id}/completions`

**Path Parameters:**
- `id` (UUID) - Task identifier

**Response:** `200 OK`
```json
[
  {
    "id": "650e8400-e29b-41d4-a716-446655440000",
    "completedDate": "2024-01-01",
    "completedAt": "2024-01-01T10:30:00Z",
    "notes": "Completed on time"
  },
  {
    "id": "650e8400-e29b-41d4-a716-446655440001",
    "completedDate": "2024-01-08",
    "completedAt": "2024-01-08T11:00:00Z",
    "notes": null
  }
]
```

## Error Responses

The API uses standard HTTP status codes and returns error details in the response body.

### Error Response Format
```json
{
  "error": "Validation Error",
  "message": "Invalid request data",
  "details": [
    {
      "field": "dueDate",
      "message": "Due date must be in the future"
    }
  ]
}
```

### Common Error Codes
- `400 Bad Request` - Invalid request data or validation errors
- `404 Not Found` - Resource not found
- `500 Internal Server Error` - Server error

## Data Models

### OneTimeTask Response
```typescript
{
  type: "ONE_TIME"
  id: string (UUID)
  name: string
  displayOrder: number
  category?: string
  status: TaskStatus
  dueDate: string (ISO date)
  completedAt?: string (ISO timestamp)
  createdAt: string (ISO timestamp)
  updatedAt: string (ISO timestamp)
}
```

### RecurringTask Response
```typescript
{
  type: "RECURRING"
  id: string (UUID)
  name: string
  displayOrder: number
  category?: string
  status: TaskStatus
  recurrencePattern: RecurrencePattern
  dayOfWeek?: DayOfWeek
  startDate: string (ISO date)
  endDate?: string (ISO date)
  nextDueDate: string (ISO date)
  lastCompletedDate?: string (ISO date)
  createdAt: string (ISO timestamp)
  updatedAt: string (ISO timestamp)
}
```

### TaskCompletion Response
```typescript
{
  id: string (UUID)
  completedDate: string (ISO date)
  completedAt: string (ISO timestamp)
  notes?: string
}
```

## Notes

- All timestamps are in ISO 8601 format with UTC timezone
- All dates are in ISO 8601 date format (YYYY-MM-DD)
- UUIDs follow the standard UUID v4 format
- The API currently runs on port 8080 by default
- CORS is configured to allow all origins in the current test configuration