package com.fryconvert.airfryerconversion.web

import jakarta.validation.ConstraintViolationException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MissingServletRequestParameterException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException

interface GlobalExceptionHandler {
    fun handleValidationException(ex: ConstraintViolationException): ResponseEntity<ErrorResponse>
    fun handleMissingParameter(ex: MissingServletRequestParameterException): ResponseEntity<ErrorResponse>
    fun handleTypeMismatch(ex: MethodArgumentTypeMismatchException): ResponseEntity<ErrorResponse>
    fun handleIllegalArgument(ex: IllegalArgumentException): ResponseEntity<ErrorResponse>
    fun handleGenericException(ex: Exception): ResponseEntity<ErrorResponse>
}

@ControllerAdvice
class GlobalExceptionHandlerImpl : GlobalExceptionHandler {

    @ExceptionHandler(ConstraintViolationException::class)
    override fun handleValidationException(ex: ConstraintViolationException): ResponseEntity<ErrorResponse> {
        val violation = ex.constraintViolations.firstOrNull()
        val fieldName = violation?.propertyPath?.toString()
        val message = violation?.message ?: "Validation failed"
        
        val errorResponse = ErrorResponse(
            error = ErrorDetail(
                code = "VALIDATION_ERROR",
                message = message,
                field = fieldName
            )
        )
        
        return ResponseEntity.badRequest().body(errorResponse)
    }

    @ExceptionHandler(MissingServletRequestParameterException::class)
    override fun handleMissingParameter(ex: MissingServletRequestParameterException): ResponseEntity<ErrorResponse> {
        val errorResponse = ErrorResponse(
            error = ErrorDetail(
                code = "MISSING_PARAMETER",
                message = "Required parameter '${ex.parameterName}' is missing",
                field = ex.parameterName
            )
        )
        
        return ResponseEntity.badRequest().body(errorResponse)
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException::class)
    override fun handleTypeMismatch(ex: MethodArgumentTypeMismatchException): ResponseEntity<ErrorResponse> {
        val parameterName = ex.name
        val expectedType = ex.requiredType?.simpleName ?: "unknown"
        val actualValue = ex.value
        
        val message = when {
            "TemperatureUnit" == expectedType -> 
                "Invalid temperature_unit: $actualValue. Must be one of: C, F, GAS"
            "OvenType" == expectedType -> 
                "Invalid oven_type: $actualValue. Must be one of: CONVENTIONAL, FAN, GAS"
            else -> "Invalid value '$actualValue' for parameter '$parameterName'. Expected type: $expectedType"
        }
        
        val errorResponse = ErrorResponse(
            error = ErrorDetail(
                code = "INVALID_PARAMETER",
                message = message,
                field = parameterName
            )
        )
        
        return ResponseEntity.badRequest().body(errorResponse)
    }

    @ExceptionHandler(IllegalArgumentException::class)
    override fun handleIllegalArgument(ex: IllegalArgumentException): ResponseEntity<ErrorResponse> {
        val errorResponse = ErrorResponse(
            error = ErrorDetail(
                code = "INVALID_PARAMETER",
                message = ex.message ?: "Invalid parameter value"
            )
        )
        
        return ResponseEntity.badRequest().body(errorResponse)
    }

    @ExceptionHandler(Exception::class)
    override fun handleGenericException(ex: Exception): ResponseEntity<ErrorResponse> {
        val errorResponse = ErrorResponse(
            error = ErrorDetail(
                code = "INTERNAL_ERROR",
                message = "An unexpected error occurred"
            )
        )
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse)
    }
}
