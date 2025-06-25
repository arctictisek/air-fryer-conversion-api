package com.fryconvert.airfryerconversion.web

data class ErrorDetail(
    val code: String,
    val message: String,
    val field: String? = null
)

data class ErrorResponse(
    val success: Boolean = false,
    val error: ErrorDetail
)
