package com.fryconvert.airfryerconversion.service

import com.fryconvert.airfryerconversion.domain.TemperatureUnit
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import org.junit.jupiter.api.Test

class TemperatureConversionServiceTest {

    private val temperatureConversionService = TemperatureConversionServiceImpl()

    @Test
    fun `should convert Celsius to Celsius unchanged`() {
        // Given
        val temperature = 180.0
        val unit = TemperatureUnit.C

        // When
        val result = temperatureConversionService.toCelsius(temperature, unit)

        // Then
        result shouldBe 180.0
    }

    @Test
    fun `should convert Fahrenheit to Celsius`() {
        // Given
        val temperature = 356.0 // 180°C
        val unit = TemperatureUnit.F

        // When
        val result = temperatureConversionService.toCelsius(temperature, unit)

        // Then
        result shouldBe 180.0
    }

    @Test
    fun `should convert gas mark 4 to Celsius`() {
        // Given
        val temperature = 4.0
        val unit = TemperatureUnit.GAS

        // When
        val result = temperatureConversionService.toCelsius(temperature, unit)

        // Then
        result shouldBe 180.0
    }

    @Test
    fun `should convert all valid gas marks to Celsius`() {
        // Given/When/Then
        temperatureConversionService.toCelsius(0.25, TemperatureUnit.GAS) shouldBe 110.0
        temperatureConversionService.toCelsius(0.5, TemperatureUnit.GAS) shouldBe 120.0
        temperatureConversionService.toCelsius(1.0, TemperatureUnit.GAS) shouldBe 140.0
        temperatureConversionService.toCelsius(2.0, TemperatureUnit.GAS) shouldBe 150.0
        temperatureConversionService.toCelsius(3.0, TemperatureUnit.GAS) shouldBe 160.0
        temperatureConversionService.toCelsius(4.0, TemperatureUnit.GAS) shouldBe 180.0
        temperatureConversionService.toCelsius(5.0, TemperatureUnit.GAS) shouldBe 190.0
        temperatureConversionService.toCelsius(6.0, TemperatureUnit.GAS) shouldBe 200.0
        temperatureConversionService.toCelsius(7.0, TemperatureUnit.GAS) shouldBe 220.0
        temperatureConversionService.toCelsius(8.0, TemperatureUnit.GAS) shouldBe 230.0
        temperatureConversionService.toCelsius(9.0, TemperatureUnit.GAS) shouldBe 240.0
    }

    @Test
    fun `should throw exception for invalid gas mark`() {
        // Given
        val invalidGasMark = 10.0
        val unit = TemperatureUnit.GAS

        // When/Then
        val exception = shouldThrow<IllegalArgumentException> {
            temperatureConversionService.toCelsius(invalidGasMark, unit)
        }
        exception.message shouldContain "Invalid gas mark: 10.0"
        exception.message shouldContain "Valid gas marks:"
    }

    @Test
    fun `should convert Celsius to Fahrenheit`() {
        // Given
        val celsius = 180.0

        // When
        val result = temperatureConversionService.celsiusToFahrenheit(celsius)

        // Then
        result shouldBe 356.0
    }

    @Test
    fun `should convert freezing point correctly`() {
        // Given
        val celsius = 0.0

        // When
        val result = temperatureConversionService.celsiusToFahrenheit(celsius)

        // Then
        result shouldBe 32.0
    }

    @Test
    fun `should convert boiling point correctly`() {
        // Given
        val celsius = 100.0

        // When
        val result = temperatureConversionService.celsiusToFahrenheit(celsius)

        // Then
        result shouldBe 212.0
    }
}
