package com.fryconvert.airfryerconversion.service

import com.fryconvert.airfryerconversion.domain.ConversionRequest
import com.fryconvert.airfryerconversion.domain.OvenType
import com.fryconvert.airfryerconversion.domain.TemperatureUnit
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test

class AirFryerConversionServiceTest {

    private val temperatureConversionService = mockk<TemperatureConversionService>()
    private val airFryerConversionService = AirFryerConversionServiceImpl(temperatureConversionService)

    @Test
    fun `should convert conventional oven settings correctly`() {
        // Given
        val request = ConversionRequest(
            temperature = 180.0,
            temperatureUnit = TemperatureUnit.C,
            duration = 30,
            ovenType = OvenType.CONVENTIONAL
        )
        
        every { temperatureConversionService.toCelsius(180.0, TemperatureUnit.C) } returns 180.0
        every { temperatureConversionService.celsiusToFahrenheit(160.0) } returns 320.0

        // When
        val result = airFryerConversionService.convert(request)

        // Then
        result.airFryerTempC shouldBe 160  // 180 - 20
        result.airFryerTempF shouldBe 320
        result.airFryerTime shouldBe 22    // 30 * 0.75 = 22.5 → rounds to 22 (banker's rounding)
        
        verify { temperatureConversionService.toCelsius(180.0, TemperatureUnit.C) }
        verify { temperatureConversionService.celsiusToFahrenheit(160.0) }
    }

    @Test
    fun `should convert fan oven settings correctly`() {
        // Given
        val request = ConversionRequest(
            temperature = 180.0,
            temperatureUnit = TemperatureUnit.C,
            duration = 30,
            ovenType = OvenType.FAN
        )
        
        every { temperatureConversionService.toCelsius(180.0, TemperatureUnit.C) } returns 180.0
        every { temperatureConversionService.celsiusToFahrenheit(170.0) } returns 338.0

        // When
        val result = airFryerConversionService.convert(request)

        // Then
        result.airFryerTempC shouldBe 170  // 180 - 10
        result.airFryerTempF shouldBe 340  // 338 → rounds to nearest 5
        result.airFryerTime shouldBe 26    // 30 * 0.85 = 25.5 → rounds to 26 (banker's rounding)
        
        verify { temperatureConversionService.toCelsius(180.0, TemperatureUnit.C) }
        verify { temperatureConversionService.celsiusToFahrenheit(170.0) }
    }

    @Test
    fun `should convert gas oven settings correctly`() {
        // Given
        val request = ConversionRequest(
            temperature = 4.0,
            temperatureUnit = TemperatureUnit.GAS,
            duration = 45,
            ovenType = OvenType.GAS
        )
        
        every { temperatureConversionService.toCelsius(4.0, TemperatureUnit.GAS) } returns 180.0
        every { temperatureConversionService.celsiusToFahrenheit(160.0) } returns 320.0

        // When
        val result = airFryerConversionService.convert(request)

        // Then
        result.airFryerTempC shouldBe 160  // 180 - 20
        result.airFryerTempF shouldBe 320
        result.airFryerTime shouldBe 34    // 45 * 0.75 = 33.75 → rounds to 34
        
        verify { temperatureConversionService.toCelsius(4.0, TemperatureUnit.GAS) }
        verify { temperatureConversionService.celsiusToFahrenheit(160.0) }
    }

    @Test
    fun `should ensure minimum cooking time of 1 minute`() {
        // Given
        val request = ConversionRequest(
            temperature = 180.0,
            temperatureUnit = TemperatureUnit.C,
            duration = 1,
            ovenType = OvenType.CONVENTIONAL
        )
        
        every { temperatureConversionService.toCelsius(180.0, TemperatureUnit.C) } returns 180.0
        every { temperatureConversionService.celsiusToFahrenheit(160.0) } returns 320.0

        // When
        val result = airFryerConversionService.convert(request)

        // Then
        result.airFryerTime shouldBe 1  // 1 * 0.75 = 0.75 → min(1, round(0.75)) = 1
        
        verify { temperatureConversionService.toCelsius(180.0, TemperatureUnit.C) }
        verify { temperatureConversionService.celsiusToFahrenheit(160.0) }
    }

    @Test
    fun `should round temperatures to nearest 5 degrees`() {
        // Given
        val request = ConversionRequest(
            temperature = 183.0,  // Will become 163°C after -20°C reduction
            temperatureUnit = TemperatureUnit.C,
            duration = 30,
            ovenType = OvenType.CONVENTIONAL
        )
        
        every { temperatureConversionService.toCelsius(183.0, TemperatureUnit.C) } returns 183.0
        every { temperatureConversionService.celsiusToFahrenheit(163.0) } returns 325.4

        // When
        val result = airFryerConversionService.convert(request)

        // Then
        result.airFryerTempC shouldBe 165  // 163 → rounds to 165
        result.airFryerTempF shouldBe 325  // 325.4 → rounds to 325
        
        verify { temperatureConversionService.toCelsius(183.0, TemperatureUnit.C) }
        verify { temperatureConversionService.celsiusToFahrenheit(163.0) }
    }

    @Test
    fun `should handle Fahrenheit input correctly`() {
        // Given
        val request = ConversionRequest(
            temperature = 350.0,
            temperatureUnit = TemperatureUnit.F,
            duration = 25,
            ovenType = OvenType.FAN
        )
        
        every { temperatureConversionService.toCelsius(350.0, TemperatureUnit.F) } returns 176.67
        every { temperatureConversionService.celsiusToFahrenheit(166.67) } returns 332.0

        // When
        val result = airFryerConversionService.convert(request)

        // Then
        result.airFryerTempC shouldBe 165  // 176.67 - 10 = 166.67 → rounds to 165
        result.airFryerTempF shouldBe 330  // 332.0 → rounds to 330
        result.airFryerTime shouldBe 21    // 25 * 0.85 = 21.25 → rounds to 21
        
        verify { temperatureConversionService.toCelsius(350.0, TemperatureUnit.F) }
        verify { temperatureConversionService.celsiusToFahrenheit(166.67) }
    }
}
