# Backend API Changes Documentation

## Overview
This document describes the new tag management feature and related task enhancements implemented in the backend. The changes enable tasks to be categorized with multiple tags for better organization and filtering.

## New API Endpoints

### Tag Management API

#### 1. Get All Tags
- **Endpoint:** `GET /api/v1/tags`
- **Response:**
```json
{
  "tags": [
    {
      "id": "uuid",
      "name": "string",
      "color": "#hexcode",
      "usageCount": 0
    }
  ]
}
```

#### 2. Get Single Tag
- **Endpoint:** `GET /api/v1/tags/{id}`
- **Response:**
```json
{
  "id": "uuid",
  "name": "string",
  "color": "#hexcode",
  "usageCount": 0
}
```

#### 3. Create Tag
- **Endpoint:** `POST /api/v1/tags`
- **Request:**
```json
{
  "name": "string (required, 1-50 chars)",
  "color": "#hexcode (optional, defaults to random)"
}
```
- **Response:** `201 Created` with tag object
- **Validation:**
  - Name must be 1-50 characters
  - Color must be valid hex format (#RRGGBB)
  - Tag names are unique (case-insensitive)

#### 4. Update Tag
- **Endpoint:** `PUT /api/v1/tags/{id}`
- **Request:**
```json
{
  "name": "string (required, 1-50 chars)",
  "color": "#hexcode (required)"
}
```
- **Response:** Updated tag object
- **Validation:** Same as create, plus color is required

#### 5. Delete Tag
- **Endpoint:** `DELETE /api/v1/tags/{id}`
- **Response:** `204 No Content`
- **Behavior:** Cascade deletes all task-tag associations

### Enhanced Task API

#### Task Creation with Tags
- **Endpoint:** `POST /api/v1/tasks`
- **Request Types:**

**One-Time Task:**
```json
{
  "type": "ONE_TIME",
  "name": "string (required)",
  "category": "string (optional)",
  "tags": ["uuid", "uuid"] // Optional, max 10 tags
  "dueDate": "YYYY-MM-DD (required, future date)"
}
```

**Recurring Task:**
```json
{
  "type": "RECURRING",
  "name": "string (required)",
  "category": "string (optional)",
  "tags": ["uuid", "uuid"], // Optional, max 10 tags
  "recurrencePattern": "DAILY|WEEKLY|MONTHLY",
  "dayOfWeek": "MONDAY|TUESDAY|..." // Required for WEEKLY pattern
  "startDate": "YYYY-MM-DD (required)",
  "endDate": "YYYY-MM-DD (optional)"
}
```

#### Task Update with Tags
- **Endpoint:** `PUT /api/v1/tasks/{id}`
- **Request:**
```json
{
  "name": "string (optional)",
  "category": "string (optional)",
  "tags": ["uuid", "uuid"], // Optional, replaces all tags
  "dueDate": "YYYY-MM-DD (optional)",
  "recurrencePattern": "DAILY|WEEKLY|MONTHLY (optional)",
  "dayOfWeek": "MONDAY|TUESDAY|... (optional)",
  "endDate": "YYYY-MM-DD (optional)"
}
```

#### Task Query with Tag Filtering
- **Endpoint:** `GET /api/v1/tasks`
- **Query Parameters:**
  - `tags`: Comma-separated UUIDs for tag filtering
  - `category`: Filter by category
  - `status`: Filter by task status
  - `dueDate`: Filter by exact due date
  - `startDate`: Filter tasks after this date
  - `endDate`: Filter tasks before this date

#### Task Response Format
All task responses now include tags:
```json
{
  "type": "ONE_TIME|RECURRING",
  "id": "uuid",
  "name": "string",
  "displayOrder": 0,
  "category": "string",
  "status": "PENDING|COMPLETED|ARCHIVED",
  "createdAt": "ISO-8601",
  "updatedAt": "ISO-8601",
  "tags": [
    {
      "id": "uuid",
      "name": "string",
      "color": "#hexcode"
    }
  ],
  // Additional fields based on task type
}
```

## Business Rules and Behaviors

### Tag Management
1. **Unique Names:** Tag names must be unique (case-insensitive)
2. **Color Assignment:** If no color provided during creation, a random color is generated
3. **Color Format:** Colors must be in hex format (#RRGGBB)
4. **Cascade Delete:** Deleting a tag removes all associations with tasks
5. **Usage Count:** Each tag tracks how many tasks use it

### Task-Tag Association
1. **Maximum Tags:** Tasks can have up to 10 tags
2. **Tag Validation:** All tag IDs must exist when creating/updating tasks
3. **Replace Behavior:** Updating task tags replaces all existing tags (not additive)
4. **Tag Preservation:** Tags remain associated with completed/archived tasks

### Error Responses
Common error codes:
- `400`: Invalid request (validation errors)
- `404`: Tag or task not found
- `409`: Tag name already exists

Error response format:
```json
{
  "error": "ERROR_CODE",
  "message": "Human-readable error message",
  "details": {} // Optional additional details
}
```

## Database Changes
- New `tags` table with id, name, color, timestamps
- New `task_tags` junction table for many-to-many relationship
- Unique index on tag names (case-insensitive)
- Foreign key constraints with cascade delete

## CORS Configuration
- Backend allows CORS from `http://localhost:3000` for frontend development

## Important Implementation Notes

1. **Tag IDs:** Use UUID format for all tag identifiers
2. **Timestamps:** All timestamps use ISO-8601 format
3. **Validation:** Backend validates all inputs; frontend should mirror these validations
4. **Sorting:** Tags are returned sorted alphabetically by name
5. **Transaction Safety:** All tag operations are transactional