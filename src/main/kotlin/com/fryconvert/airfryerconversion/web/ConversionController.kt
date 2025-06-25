package com.fryconvert.airfryerconversion.web

import com.fryconvert.airfryerconversion.domain.ConversionRequest
import com.fryconvert.airfryerconversion.domain.OvenType
import com.fryconvert.airfryerconversion.domain.TemperatureUnit
import com.fryconvert.airfryerconversion.service.AirFryerConversionService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

interface ConversionController {
    fun convert(
        @Parameter(description = "Temperature value or gas mark number", example = "180")
        @RequestParam
        temperature: Double,
        
        @Parameter(description = "Temperature unit (when GAS, oven_type must be 'gas')", example = "C")
        @RequestParam("temperature_unit")
        temperatureUnit: TemperatureUnit,
        
        @Parameter(description = "Cooking duration in minutes", example = "30")
        @RequestParam
        @Min(value = 1, message = "Duration must be at least 1 minute")
        duration: Int,
        
        @Parameter(description = "Type of oven (must be 'GAS' when temperature_unit is GAS)", example = "FAN")
        @RequestParam("oven_type")
        ovenType: OvenType
    ): ResponseEntity<ConversionResponse>
}

@RestController
@RequestMapping("/api/v1")
@Validated
@Tag(name = "Air Fryer Conversion", description = "Convert oven cooking times and temperatures to air fryer settings")
class ConversionControllerImpl(
    private val airFryerConversionService: AirFryerConversionService
) : ConversionController {

    @GetMapping("/convert")
    @Operation(
        summary = "Convert oven settings to air fryer settings",
        description = """
            Converts cooking time and temperature from conventional/fan/gas oven to air fryer settings.
            
            Temperature reductions:
            - Conventional/Gas: -20°C
            - Fan: -10°C
            
            Time multipliers:
            - Conventional/Gas: ×0.75
            - Fan: ×0.85
            
            Results are rounded to practical air fryer increments (5°C, whole minutes).
        """
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Conversion successful"),
            ApiResponse(responseCode = "400", description = "Invalid input parameters")
        ]
    )
    override fun convert(
        temperature: Double,
        temperatureUnit: TemperatureUnit,
        duration: Int,
        ovenType: OvenType
    ): ResponseEntity<ConversionResponse> {
        
        // Validate temperature_unit and oven_type combination
        if (TemperatureUnit.GAS == temperatureUnit && OvenType.GAS != ovenType) {
            throw IllegalArgumentException("When temperature_unit is GAS, oven_type must be 'GAS'. Gas marks are specific to gas ovens only.")
        }
        
        val request = ConversionRequest(
            temperature = temperature,
            temperatureUnit = temperatureUnit,
            duration = duration,
            ovenType = ovenType
        )
        
        val result = airFryerConversionService.convert(request)
        
        val response = ConversionResponse(
            success = true,
            data = result.toResponseData(),
            input = ConversionInputEcho(
                temperature = temperature,
                temperatureUnit = temperatureUnit,
                duration = duration,
                ovenType = ovenType
            )
        )
        
        return ResponseEntity.ok(response)
    }
}
