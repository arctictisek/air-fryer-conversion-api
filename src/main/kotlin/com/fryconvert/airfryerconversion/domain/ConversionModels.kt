package com.fryconvert.airfryerconversion.domain

enum class TemperatureUnit {
    C, F, GAS;
    
    companion object {
        fun fromString(value: String): TemperatureUnit = when (value.uppercase()) {
            "C" -> C
            "F" -> F
            "GAS" -> GAS
            else -> throw IllegalArgumentException("Invalid temperature_unit: $value. Must be one of: C, F, GAS")
        }
    }
}

enum class OvenType {
    CONVENTIONAL, FAN, GAS;
    
    companion object {
        fun fromString(value: String): OvenType = when (value.uppercase()) {
            "CONVENTIONAL" -> CONVENTIONAL
            "FAN" -> FAN
            "GAS" -> GAS
            else -> throw IllegalArgumentException("Invalid oven type: $value. Must be one of: conventional, fan, gas")
        }
    }
}

data class ConversionRequest(
    val temperature: Double,
    val temperatureUnit: TemperatureUnit,
    val duration: Int,
    val ovenType: OvenType
)

data class ConversionResult(
    val airFryerTime: Int,
    val airFryerTempC: Int,
    val airFryerTempF: Int
)
