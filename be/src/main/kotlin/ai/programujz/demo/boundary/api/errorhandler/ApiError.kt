package ai.programujz.demo.boundary.api.errorhandler

import com.fasterxml.jackson.annotation.JsonInclude
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode
import java.time.Instant

@JsonInclude(JsonInclude.Include.NON_NULL)
data class ApiError(
    val status: Int,
    val error: String,
    val message: String,
    val code: ErrorCode,
    val timestamp: Instant = Instant.now(),
    val errors: List<ErrorItem>? = null
) {
    companion object {
        fun apiError(
            status: HttpStatusCode,
            message: String,
            code: ErrorCode,
            errors: List<ErrorItem>? = null
        ): ApiError {
            return ApiError(
                status = status.value(),
                error = when (status) {
                    HttpStatus.BAD_REQUEST -> "Bad Request"
                    HttpStatus.NOT_FOUND -> "Not Found"
                    HttpStatus.CONFLICT -> "Conflict"
                    HttpStatus.FORBIDDEN -> "Forbidden"
                    HttpStatus.UNAUTHORIZED -> "Unauthorized"
                    HttpStatus.INTERNAL_SERVER_ERROR -> "Internal Server Error"
                    else -> "Error"
                },
                message = message,
                code = code,
                errors = errors
            )
        }
    }
}