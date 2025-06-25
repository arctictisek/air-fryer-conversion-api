package com.fryconvert.airfryerconversion.web

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.Instant

data class HealthStatus(
    val status: String,
    val timestamp: String,
    val service: String,
    val version: String
)

interface HealthController {
    fun health(): ResponseEntity<HealthStatus>
}

@RestController
@RequestMapping("/api/v1")
@Tag(name = "Health Check", description = "Application health monitoring")
class HealthControllerImpl : HealthController {

    @GetMapping("/health")
    @Operation(
        summary = "Check application health",
        description = "Returns the current health status of the air fryer conversion API"
    )
    @ApiResponse(responseCode = "200", description = "Service is healthy")
    override fun health(): ResponseEntity<HealthStatus> {
        val healthStatus = HealthStatus(
            status = "UP",
            timestamp = Instant.now().toString(),
            service = "air-fryer-conversion-api",
            version = "0.0.1-SNAPSHOT"
        )
        
        return ResponseEntity.ok(healthStatus)
    }
}
