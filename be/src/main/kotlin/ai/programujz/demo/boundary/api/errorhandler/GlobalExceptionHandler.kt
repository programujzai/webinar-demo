package ai.programujz.demo.boundary.api.errorhandler

import ai.programujz.demo.domain.exception.CategoryNotFoundException
import ai.programujz.demo.domain.exception.DomainException
import ai.programujz.demo.domain.exception.InvalidRecurrenceConfigurationException
import ai.programujz.demo.domain.exception.InvalidTaskStateException
import ai.programujz.demo.domain.exception.TaskAlreadyCompletedException
import ai.programujz.demo.domain.exception.TaskArchivedException
import ai.programujz.demo.domain.exception.TaskNotFoundException
import org.slf4j.LoggerFactory
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode
import org.springframework.http.ResponseEntity
import org.springframework.security.access.AccessDeniedException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler
import java.util.NoSuchElementException

@ControllerAdvice
class GlobalExceptionHandler : ResponseEntityExceptionHandler() {

    private val log = LoggerFactory.getLogger(GlobalExceptionHandler::class.java)

    @ExceptionHandler(Exception::class)
    @ResponseBody
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    fun handleException(exception: Exception): ApiError {
        log.error(exception.message, exception)
        return ApiError.apiError(
            HttpStatus.INTERNAL_SERVER_ERROR,
            "An unexpected error has occurred on the server.",
            ErrorCode.UNSPECIFIED_SERVER_ERROR_CODE
        )
    }

    @ExceptionHandler(AccessDeniedException::class)
    @ResponseBody
    @ResponseStatus(HttpStatus.FORBIDDEN)
    fun handleException(exception: AccessDeniedException): ApiError {
        return ApiError.apiError(
            HttpStatus.FORBIDDEN,
            "Access denied",
            ErrorCode.ACCESS_FORBIDDEN
        )
    }

    @ExceptionHandler(NoSuchElementException::class)
    @ResponseBody
    @ResponseStatus(HttpStatus.NOT_FOUND)
    fun handleException(exception: NoSuchElementException): ApiError {
        return ApiError.apiError(
            HttpStatus.NOT_FOUND,
            "Resource with provided ID not found",
            ErrorCode.RESOURCE_NOT_FOUND
        )
    }

    // Handle all DomainException subclasses
    @ExceptionHandler(TaskNotFoundException::class)
    @ResponseBody
    @ResponseStatus(HttpStatus.NOT_FOUND)
    fun handleTaskNotFoundException(exception: TaskNotFoundException): ApiError {
        return ApiError.apiError(
            HttpStatus.NOT_FOUND,
            exception.message ?: "Task not found",
            exception.errorCode
        )
    }

    @ExceptionHandler(CategoryNotFoundException::class)
    @ResponseBody
    @ResponseStatus(HttpStatus.NOT_FOUND)
    fun handleCategoryNotFoundException(exception: CategoryNotFoundException): ApiError {
        return ApiError.apiError(
            HttpStatus.NOT_FOUND,
            exception.message ?: "Category not found",
            exception.errorCode
        )
    }

    @ExceptionHandler(
        InvalidTaskStateException::class,
        InvalidRecurrenceConfigurationException::class,
        TaskAlreadyCompletedException::class,
        TaskArchivedException::class
    )
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleBadRequestExceptions(exception: DomainException): ApiError {
        return ApiError.apiError(
            HttpStatus.BAD_REQUEST,
            exception.message ?: "Bad request",
            exception.errorCode
        )
    }

    // Generic handler for any DomainException not explicitly handled
    @ExceptionHandler(DomainException::class)
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleDomainException(exception: DomainException): ApiError {
        log.warn("Unhandled domain exception: ${exception::class.simpleName}", exception)
        return ApiError.apiError(
            HttpStatus.BAD_REQUEST,
            exception.message ?: "Domain error occurred",
            exception.errorCode
        )
    }

    override fun handleMethodArgumentNotValid(
        ex: MethodArgumentNotValidException,
        headers: HttpHeaders,
        statusCode: HttpStatusCode,
        request: WebRequest
    ): ResponseEntity<Any>? {
        logExceptionDetails(ex, statusCode, request)

        val errors = ex.bindingResult.fieldErrors
            .map { ErrorItem.of(it.field, it.defaultMessage) }

        val apiError = ApiError.apiError(
            HttpStatus.BAD_REQUEST,
            "Validation failed",
            ErrorCode.VALIDATION_ERROR,
            errors
        )

        return handleExceptionInternal(ex, apiError, headers, statusCode, request)
    }

    override fun handleExceptionInternal(
        ex: Exception,
        body: Any?,
        headers: HttpHeaders,
        statusCode: HttpStatusCode,
        request: WebRequest
    ): ResponseEntity<Any>? {
        logExceptionDetails(ex, statusCode, request)

        val errorCode = if (statusCode.is4xxClientError) {
            ErrorCode.UNSPECIFIED_CLIENT_ERROR_CODE
        } else {
            ErrorCode.UNSPECIFIED_SERVER_ERROR_CODE
        }

        val apiError = body ?: ApiError.apiError(
            statusCode,
            ex.message ?: "An error occurred",
            errorCode
        )

        return super.handleExceptionInternal(ex, apiError, headers, statusCode, request)
    }

    private fun logExceptionDetails(exception: Exception, statusCode: HttpStatusCode, request: WebRequest) {
        val headers = getHeaders(request)
        log.error(
            "Request = ${request.getDescription(true)}, headers = $headers, statusCode = $statusCode, exception message = ${exception.message}",
            exception
        )
    }

    private fun getHeaders(request: WebRequest): Map<String, List<String>> {
        val headers = mutableMapOf<String, List<String>>()

        request.headerNames.forEach { name ->
            if (!HttpHeaders.AUTHORIZATION.equals(name, ignoreCase = true)) {
                request.getHeaderValues(name)?.let { values ->
                    headers[name] = values.toList()
                }
            }
        }

        return headers
    }
}