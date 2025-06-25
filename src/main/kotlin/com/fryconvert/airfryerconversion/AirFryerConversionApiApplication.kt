package com.fryconvert.airfryerconversion

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class AirFryerConversionApiApplication

fun main(args: Array<String>) {
    runApplication<AirFryerConversionApiApplication>(*args)
}