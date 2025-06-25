package com.fryconvert.airfryerconversion.web

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

data class ApiInfo(
    val name: String,
    val version: String,
    val description: String,
    val endpoints: Map<String, String>,
    val documentation: Map<String, String>
)

interface InfoController {
    fun info(): ResponseEntity<ApiInfo>
}

@RestController
@RequestMapping("/api/v1")
@Tag(name = "API Information", description = "API metadata and documentation links")
class InfoControllerImpl : InfoController {

    @GetMapping("/info")
    @Operation(
        summary = "Get API information",
        description = "Returns API metadata, available endpoints, and documentation links"
    )
    @ApiResponse(responseCode = "200", description = "API information retrieved successfully")
    override fun info(): ResponseEntity<ApiInfo> {
        val apiInfo = ApiInfo(
            name = "Air Fryer Conversion API",
            version = "0.0.1-SNAPSHOT",
            description = "Convert cooking times and temperatures from conventional/fan/gas ovens to air fryer settings",
            endpoints = mapOf(
                "GET /api/v1/convert" to "Convert oven settings to air fryer settings",
                "GET /api/v1/health" to "Check application health status",
                "GET /api/v1/info" to "Get API information and documentation"
            ),
            documentation = mapOf(
                "OpenAPI Specification" to "/api/docs",
                "GitHub Repository" to "https://github.com/your-username/air-fryer-conversion-api"
            )
        )
        
        return ResponseEntity.ok(apiInfo)
    }
}