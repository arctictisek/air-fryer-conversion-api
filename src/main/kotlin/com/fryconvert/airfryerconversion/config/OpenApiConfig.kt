package com.fryconvert.airfryerconversion.config

import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.info.License
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class OpenApiConfig {

    @Bean
    fun openAPI(): OpenAPI = OpenAPI()
        .info(
            Info()
                .title("Air Fryer Conversion API")
                .description("""
                    Convert cooking times and temperatures from conventional/fan/gas ovens to air fryer settings.
                    
                    This API provides accurate conversions with proper rounding for practical air fryer usage:
                    - Temperatures rounded to nearest 5°C increments
                    - Time rounded to whole minutes with 1-minute minimum
                    - Supports Celsius, Fahrenheit, and Gas Mark inputs
                    - Handles conventional, fan, and gas oven types
                """.trimIndent())
                .version("0.0.1-SNAPSHOT")
                .license(
                    License()
                        .name("MIT License")
                        .url("https://opensource.org/licenses/MIT")
                )
        )
}
