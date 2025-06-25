package com.fryconvert.airfryerconversion.service

import com.fryconvert.airfryerconversion.domain.TemperatureUnit
import org.springframework.stereotype.Service

interface TemperatureConversionService {
    fun toCelsius(temperature: Double, unit: TemperatureUnit): Double
    fun celsiusToFahrenheit(celsius: Double): Double
}

@Service
class TemperatureConversionServiceImpl : TemperatureConversionService {
    
    private val gasMarkToCelsius = mapOf(
        0.25 to 110.0,
        0.5 to 120.0,
        1.0 to 140.0,
        2.0 to 150.0,
        3.0 to 160.0,
        4.0 to 180.0,
        5.0 to 190.0,
        6.0 to 200.0,
        7.0 to 220.0,
        8.0 to 230.0,
        9.0 to 240.0
    )
    
    override fun toCelsius(temperature: Double, unit: TemperatureUnit): Double = when (unit) {
        TemperatureUnit.C -> temperature
        TemperatureUnit.F -> (temperature - 32) * 5 / 9
        TemperatureUnit.GAS -> gasMarkToCelsius[temperature] 
            ?: throw IllegalArgumentException("Invalid gas mark: $temperature. Valid gas marks: ${gasMarkToCelsius.keys.sorted()}")
    }
    
    override fun celsiusToFahrenheit(celsius: Double): Double = (celsius * 9 / 5) + 32
}
