package com.fryconvert.airfryerconversion.web

import com.fryconvert.airfryerconversion.domain.ConversionResult
import com.fryconvert.airfryerconversion.domain.OvenType
import com.fryconvert.airfryerconversion.domain.TemperatureUnit

data class ConversionResponseData(
    val airFryerTime: Int,
    val airFryerTempC: Int,
    val airFryerTempF: Int
)

data class ConversionInputEcho(
    val temperature: Double,
    val temperatureUnit: TemperatureUnit,
    val duration: Int,
    val ovenType: OvenType
)

data class ConversionResponse(
    val success: Boolean,
    val data: ConversionResponseData,
    val input: ConversionInputEcho
)

fun ConversionResult.toResponseData(): ConversionResponseData = ConversionResponseData(
    airFryerTime = this.airFryerTime,
    airFryerTempC = this.airFryerTempC,
    airFryerTempF = this.airFryerTempF
)
