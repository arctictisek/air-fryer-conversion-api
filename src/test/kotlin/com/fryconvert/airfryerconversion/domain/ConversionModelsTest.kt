package com.fryconvert.airfryerconversion.domain

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import org.junit.jupiter.api.Test

class ConversionModelsTest {

    @Test
    fun `TemperatureUnit fromString should handle valid uppercase values`() {
        // Given/When/Then
        TemperatureUnit.fromString("C") shouldBe TemperatureUnit.C
        TemperatureUnit.fromString("F") shouldBe TemperatureUnit.F
        TemperatureUnit.fromString("GAS") shouldBe TemperatureUnit.GAS
    }

    @Test
    fun `TemperatureUnit fromString should handle valid lowercase values`() {
        // Given/When/Then
        TemperatureUnit.fromString("c") shouldBe TemperatureUnit.C
        TemperatureUnit.fromString("f") shouldBe TemperatureUnit.F
        TemperatureUnit.fromString("gas") shouldBe TemperatureUnit.GAS
    }

    @Test
    fun `TemperatureUnit fromString should handle valid mixed case values`() {
        // Given/When/Then
        TemperatureUnit.fromString("Gas") shouldBe TemperatureUnit.GAS
        TemperatureUnit.fromString("gAs") shouldBe TemperatureUnit.GAS
    }

    @Test
    fun `TemperatureUnit fromString should throw exception for invalid values`() {
        // Given
        val invalidValue = "INVALID"

        // When/Then
        val exception = shouldThrow<IllegalArgumentException> {
            TemperatureUnit.fromString(invalidValue)
        }
        exception.message shouldContain "Invalid temperature_unit: INVALID"
        exception.message shouldContain "Must be one of: C, F, GAS"
    }

    @Test
    fun `OvenType fromString should handle valid uppercase values`() {
        // Given/When/Then
        OvenType.fromString("CONVENTIONAL") shouldBe OvenType.CONVENTIONAL
        OvenType.fromString("FAN") shouldBe OvenType.FAN
        OvenType.fromString("GAS") shouldBe OvenType.GAS
    }

    @Test
    fun `OvenType fromString should handle valid lowercase values`() {
        // Given/When/Then
        OvenType.fromString("conventional") shouldBe OvenType.CONVENTIONAL
        OvenType.fromString("fan") shouldBe OvenType.FAN
        OvenType.fromString("gas") shouldBe OvenType.GAS
    }

    @Test
    fun `OvenType fromString should handle valid mixed case values`() {
        // Given/When/Then
        OvenType.fromString("Conventional") shouldBe OvenType.CONVENTIONAL
        OvenType.fromString("Fan") shouldBe OvenType.FAN
        OvenType.fromString("Gas") shouldBe OvenType.GAS
    }

    @Test
    fun `OvenType fromString should throw exception for invalid values`() {
        // Given
        val invalidValue = "INVALID"

        // When/Then
        val exception = shouldThrow<IllegalArgumentException> {
            OvenType.fromString(invalidValue)
        }
        exception.message shouldContain "Invalid oven type: INVALID"
        exception.message shouldContain "Must be one of: conventional, fan, gas"
    }

    @Test
    fun `ConversionRequest should be created with all required fields`() {
        // Given
        val request = ConversionRequest(
            temperature = 180.0,
            temperatureUnit = TemperatureUnit.C,
            duration = 30,
            ovenType = OvenType.FAN
        )

        // When/Then
        request.temperature shouldBe 180.0
        request.temperatureUnit shouldBe TemperatureUnit.C
        request.duration shouldBe 30
        request.ovenType shouldBe OvenType.FAN
    }

    @Test
    fun `ConversionResult should be created with all required fields`() {
        // Given
        val result = ConversionResult(
            airFryerTime = 25,
            airFryerTempC = 170,
            airFryerTempF = 340
        )

        // When/Then
        result.airFryerTime shouldBe 25
        result.airFryerTempC shouldBe 170
        result.airFryerTempF shouldBe 340
    }
}
