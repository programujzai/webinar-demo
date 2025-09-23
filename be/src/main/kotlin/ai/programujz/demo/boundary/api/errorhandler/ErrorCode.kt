package ai.programujz.demo.boundary.api.errorhandler

enum class ErrorCode {
    // Generic error codes
    UNSPECIFIED_CLIENT_ERROR_CODE,
    UNSPECIFIED_SERVER_ERROR_CODE,
    VALIDATION_ERROR,
    ACCESS_FORBIDDEN,
    RESOURCE_NOT_FOUND,
    RESOURCE_ALREADY_EXISTS,
    
    // Task-specific error codes
    TASK_NOT_FOUND,
    TASK_ALREADY_COMPLETED,
    TASK_ARCHIVED,
    INVALID_TASK_STATE,
    INVALID_RECURRENCE_CONFIGURATION,
    
    // Category-specific error codes
    CATEGORY_NOT_FOUND,
    
    // Tag-specific error codes
    TAG_NOT_FOUND,
    TAG_ALREADY_EXISTS,
    TAGS_NOT_FOUND,
    TOO_MANY_TAGS,
    
    // Business logic error codes
    INVALID_DATE_RANGE,
    INVALID_DISPLAY_ORDER,
    CONCURRENT_MODIFICATION
}