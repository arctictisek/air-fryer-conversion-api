package com.fryconvert.airfryerconversion.service

import com.fryconvert.airfryerconversion.domain.ConversionRequest
import com.fryconvert.airfryerconversion.domain.ConversionResult
import com.fryconvert.airfryerconversion.domain.OvenType
import org.springframework.stereotype.Service

interface AirFryerConversionService {
    fun convert(request: ConversionRequest): ConversionResult
}

@Service
class AirFryerConversionServiceImpl(
    private val temperatureConversionService: TemperatureConversionService
) : AirFryerConversionService {
    
    override fun convert(request: ConversionRequest): ConversionResult {
        val originalTempCelsius = temperatureConversionService.toCelsius(request.temperature, request.temperatureUnit)
        
        val (tempReduction, timeMultiplier) = when (request.ovenType) {
            OvenType.CONVENTIONAL, OvenType.GAS -> 20.0 to 0.75
            OvenType.FAN -> 10.0 to 0.85
        }
        
        val rawAirFryerTempC = originalTempCelsius - tempReduction
        val rawAirFryerTempF = temperatureConversionService.celsiusToFahrenheit(rawAirFryerTempC)
        val rawAirFryerTime = request.duration * timeMultiplier
        
        return ConversionResult(
            airFryerTime = roundToNearestMinute(rawAirFryerTime),
            airFryerTempC = roundToNearestFiveDegrees(rawAirFryerTempC),
            airFryerTempF = roundToNearestFiveDegrees(rawAirFryerTempF)
        )
    }
    
    private fun roundToNearestMinute(time: Double): Int = kotlin.math.max(1, kotlin.math.round(time).toInt())
    
    private fun roundToNearestFiveDegrees(temperature: Double): Int = 
        (kotlin.math.round(temperature / 5.0) * 5.0).toInt()
}
