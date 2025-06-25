package com.fryconvert.airfryerconversion.integration

import io.kotest.matchers.shouldBe
import io.restassured.RestAssured
import io.restassured.http.ContentType
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.test.context.TestPropertySource

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = ["logging.level.org.springframework.web=DEBUG"])
class ConversionControllerIntegrationTest {

    @LocalServerPort
    private var port: Int = 0

    @BeforeEach
    fun setUp() {
        RestAssured.port = port
        RestAssured.basePath = "/api/v1"
    }

    @Test
    fun `should convert conventional oven settings successfully`() {
        RestAssured
            .given()
                .queryParam("temperature", 180.0)
                .queryParam("temperature_unit", "C")
                .queryParam("duration", 30)
                .queryParam("oven_type", "CONVENTIONAL")
            .`when`()
                .get("/convert")
            .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("success", `is`(true))
                .body("data.airFryerTime", `is`(22))
                .body("data.airFryerTempC", `is`(160))
                .body("data.airFryerTempF", `is`(320))
                .body("input.temperature", `is`(180.0f))
                .body("input.temperatureUnit", `is`("C"))
                .body("input.duration", `is`(30))
                .body("input.ovenType", `is`("CONVENTIONAL"))
    }

    @Test
    fun `should convert fan oven settings successfully`() {
        RestAssured
            .given()
                .queryParam("temperature", 180.0)
                .queryParam("temperature_unit", "C")
                .queryParam("duration", 30)
                .queryParam("oven_type", "FAN")
            .`when`()
                .get("/convert")
            .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("success", `is`(true))
                .body("data.airFryerTime", `is`(26))
                .body("data.airFryerTempC", `is`(170))
                .body("data.airFryerTempF", `is`(340))
                .body("input.temperatureUnit", `is`("C"))
                .body("input.ovenType", `is`("FAN"))
    }

    @Test
    fun `should convert gas mark settings successfully`() {
        RestAssured
            .given()
                .queryParam("temperature", 4.0)
                .queryParam("temperature_unit", "GAS")
                .queryParam("duration", 45)
                .queryParam("oven_type", "GAS")
            .`when`()
                .get("/convert")
            .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("success", `is`(true))
                .body("data.airFryerTime", `is`(34))
                .body("data.airFryerTempC", `is`(160))
                .body("data.airFryerTempF", `is`(320))
                .body("input.temperature", `is`(4.0f))
                .body("input.temperatureUnit", `is`("GAS"))
                .body("input.ovenType", `is`("GAS"))
    }

    @Test
    fun `should convert Fahrenheit temperature successfully`() {
        RestAssured
            .given()
                .queryParam("temperature", 350.0)
                .queryParam("temperature_unit", "F")
                .queryParam("duration", 25)
                .queryParam("oven_type", "FAN")
            .`when`()
                .get("/convert")
            .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("success", `is`(true))
                .body("data.airFryerTime", `is`(21))
                .body("input.temperatureUnit", `is`("F"))
    }

    @Test
    fun `should ensure minimum cooking time of 1 minute`() {
        RestAssured
            .given()
                .queryParam("temperature", 180.0)
                .queryParam("temperature_unit", "C")
                .queryParam("duration", 1)
                .queryParam("oven_type", "CONVENTIONAL")
            .`when`()
                .get("/convert")
            .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("success", `is`(true))
                .body("data.airFryerTime", `is`(1))
    }

    @Test
    fun `should return 400 when temperature parameter is missing`() {
        RestAssured
            .given()
                .queryParam("temperature_unit", "C")
                .queryParam("duration", 30)
                .queryParam("oven_type", "FAN")
            .`when`()
                .get("/convert")
            .then()
                .statusCode(400)
                .contentType(ContentType.JSON)
                .body("success", `is`(false))
                .body("error.code", `is`("MISSING_PARAMETER"))
                .body("error.message", `is`("Required parameter 'temperature' is missing"))
                .body("error.field", `is`("temperature"))
    }

    @Test
    fun `should return 400 when temperature_unit parameter is missing`() {
        RestAssured
            .given()
                .queryParam("temperature", 180.0)
                .queryParam("duration", 30)
                .queryParam("oven_type", "FAN")
            .`when`()
                .get("/convert")
            .then()
                .statusCode(400)
                .contentType(ContentType.JSON)
                .body("success", `is`(false))
                .body("error.code", `is`("MISSING_PARAMETER"))
                .body("error.message", `is`("Required parameter 'temperature_unit' is missing"))
                .body("error.field", `is`("temperature_unit"))
    }

    @Test
    fun `should return 400 when duration parameter is missing`() {
        RestAssured
            .given()
                .queryParam("temperature", 180.0)
                .queryParam("temperature_unit", "C")
                .queryParam("oven_type", "FAN")
            .`when`()
                .get("/convert")
            .then()
                .statusCode(400)
                .contentType(ContentType.JSON)
                .body("success", `is`(false))
                .body("error.code", `is`("MISSING_PARAMETER"))
                .body("error.message", `is`("Required parameter 'duration' is missing"))
                .body("error.field", `is`("duration"))
    }

    @Test
    fun `should return 400 when oven_type parameter is missing`() {
        RestAssured
            .given()
                .queryParam("temperature", 180.0)
                .queryParam("temperature_unit", "C")
                .queryParam("duration", 30)
            .`when`()
                .get("/convert")
            .then()
                .statusCode(400)
                .contentType(ContentType.JSON)
                .body("success", `is`(false))
                .body("error.code", `is`("MISSING_PARAMETER"))
                .body("error.message", `is`("Required parameter 'oven_type' is missing"))
                .body("error.field", `is`("oven_type"))
    }

    @Test
    fun `should return 400 when temperature_unit is invalid`() {
        RestAssured
            .given()
                .queryParam("temperature", 180.0)
                .queryParam("temperature_unit", "INVALID")
                .queryParam("duration", 30)
                .queryParam("oven_type", "FAN")
            .`when`()
                .get("/convert")
            .then()
                .statusCode(400)
                .contentType(ContentType.JSON)
                .body("success", `is`(false))
                .body("error.code", `is`("INVALID_PARAMETER"))
                .body("error.message", `is`("Invalid temperature_unit: INVALID. Must be one of: C, F, GAS"))
                .body("error.field", `is`("temperature_unit"))
    }

    @Test
    fun `should return 400 when oven_type is invalid`() {
        RestAssured
            .given()
                .queryParam("temperature", 180.0)
                .queryParam("temperature_unit", "C")
                .queryParam("duration", 30)
                .queryParam("oven_type", "INVALID")
            .`when`()
                .get("/convert")
            .then()
                .statusCode(400)
                .contentType(ContentType.JSON)
                .body("success", `is`(false))
                .body("error.code", `is`("INVALID_PARAMETER"))
                .body("error.message", `is`("Invalid oven_type: INVALID. Must be one of: CONVENTIONAL, FAN, GAS"))
                .body("error.field", `is`("oven_type"))
    }

    @Test
    fun `should return 400 when temperature is not a number`() {
        RestAssured
            .given()
                .queryParam("temperature", "abc")
                .queryParam("temperature_unit", "C")
                .queryParam("duration", 30)
                .queryParam("oven_type", "FAN")
            .`when`()
                .get("/convert")
            .then()
                .statusCode(400)
                .contentType(ContentType.JSON)
                .body("success", `is`(false))
                .body("error.code", `is`("INVALID_PARAMETER"))
                .body("error.message", containsString("Invalid value 'abc' for parameter 'temperature'"))
                .body("error.field", `is`("temperature"))
    }

    @Test
    fun `should return 400 when duration is less than 1`() {
        RestAssured
            .given()
                .queryParam("temperature", 180.0)
                .queryParam("temperature_unit", "C")
                .queryParam("duration", 0)
                .queryParam("oven_type", "FAN")
            .`when`()
                .get("/convert")
            .then()
                .statusCode(400)
                .contentType(ContentType.JSON)
                .body("success", `is`(false))
                .body("error.code", `is`("VALIDATION_ERROR"))
                .body("error.message", `is`("Duration must be at least 1 minute"))
    }

    @Test
    fun `should return 400 when GAS temperature_unit combined with non-GAS oven_type`() {
        RestAssured
            .given()
                .queryParam("temperature", 4.0)
                .queryParam("temperature_unit", "GAS")
                .queryParam("duration", 30)
                .queryParam("oven_type", "FAN")
            .`when`()
                .get("/convert")
            .then()
                .statusCode(400)
                .contentType(ContentType.JSON)
                .body("success", `is`(false))
                .body("error.code", `is`("INVALID_PARAMETER"))
                .body("error.message", `is`("When temperature_unit is GAS, oven_type must be 'GAS'. Gas marks are specific to gas ovens only."))
    }

    @Test
    fun `should return 400 when invalid gas mark is provided`() {
        RestAssured
            .given()
                .queryParam("temperature", 10.0)
                .queryParam("temperature_unit", "GAS")
                .queryParam("duration", 30)
                .queryParam("oven_type", "GAS")
            .`when`()
                .get("/convert")
            .then()
                .statusCode(400)
                .contentType(ContentType.JSON)
                .body("success", `is`(false))
                .body("error.code", `is`("INVALID_PARAMETER"))
                .body("error.message", containsString("Invalid gas mark: 10.0"))
    }

    @Test
    fun `should handle temperature rounding to nearest 5 degrees`() {
        RestAssured
            .given()
                .queryParam("temperature", 183.0)
                .queryParam("temperature_unit", "C")
                .queryParam("duration", 30)
                .queryParam("oven_type", "CONVENTIONAL")
            .`when`()
                .get("/convert")
            .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("success", `is`(true))
                .body("data.airFryerTempC", `is`(165)) // 183 - 20 = 163 → rounds to 165
    }
}
