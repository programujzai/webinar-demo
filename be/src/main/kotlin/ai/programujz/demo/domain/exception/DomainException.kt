package ai.programujz.demo.domain.exception

import ai.programujz.demo.boundary.api.errorhandler.ErrorCode

abstract class DomainException(
    message: String,
    val errorCode: ErrorCode,
    cause: Throwable? = null
) : RuntimeException(message, cause)