# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Development Commands

This repository uses Kotlin + Spring Boot + Gradle:

- Build: `./gradlew build`
- Test: `./gradlew test`
- Test Coverage: `./gradlew koverHtmlReport` (generates HTML coverage report)
- Run: `./gradlew bootRun`
- Clean: `./gradlew clean`
- Check (lint + test): `./gradlew check`
- Health Check: `curl http://localhost:8080/api/v1/health`
- API Info: `curl http://localhost:8080/api/v1/info`
- OpenAPI Spec: `curl http://localhost:8080/api/docs`
- Example Usage: `curl "http://localhost:8080/api/v1/convert?temperature=180&temperature_unit=C&duration=30&oven_type=FAN"`

## Code Practices and Standards

### File Format Requirements
- All files must be POSIX compliant
- All files must end with a newline character
- Use 4 spaces for indentation (no tabs)

### Kotlin Code Standards
- Use "yoda-style" conditions in `if` statements (constant on the left side of boolean operator)
  - Example: `if (0 == varValue)` instead of `if (varValue == 0)`
  - Example: `if (null == object)` instead of `if (object == null)`
  - Example: `if ("expected" == actualString)` instead of `if (actualString == "expected")`
- Use idiomatic Kotlin features and syntax wherever possible
  - Prefer `data classes` for simple data containers
  - Use `sealed classes` for restricted class hierarchies
  - Utilize extension functions when appropriate
  - Use `when` expressions instead of long `if-else` chains
  - Leverage scope functions (`let`, `run`, `apply`, `also`, `with`)
  - Use string templates instead of concatenation
  - Prefer nullable types and safe calls (`?.`) over null checks
- Follow functional programming principles
  - Favor immutability - use `val` over `var` whenever possible
  - Prefer immutable collections (`listOf`, `setOf`, `mapOf`)
  - Use higher-order functions and lambda expressions
  - Avoid side effects in functions when possible
  - Use pure functions that return values based only on input parameters
  - Prefer function composition and transformation over imperative loops
- Keep code concise by inlining statements
  - Avoid creating temporary variables when the value can be used directly
  - Example: `return calculateValue(input)` instead of `val result = calculateValue(input); return result`
  - Example: `items.filter { it.isValid }.map { it.transform() }` instead of separate val declarations
  - Only create intermediate variables when they improve readability or are used multiple times
- Use named parameters in lambdas for clarity
  - Prefer named parameters over implicit `it` unless the lambda is very short and straightforward
  - Example: `users.filter { user -> user.isActive }` instead of `users.filter { it.isActive }`
  - Example: `temperatures.map { temp -> temp.toCelsius() }` instead of `temperatures.map { it.toCelsius() }`
  - Acceptable use of `it`: simple cases like `list.filter { it > 0 }` or `items.forEach { println(it) }`
- Use Kotlin coroutines for asynchronous operations
  - Prefer coroutines (`suspend` functions, `async`/`await`) over reactive types like `Mono`, `Future`, `CompletableFuture`
  - Example: `suspend fun fetchData(): Data` instead of `fun fetchData(): Mono<Data>`
  - Use `async`/`await` for concurrent operations instead of combining reactive streams
  - Leverage `Flow` for reactive streams when working with sequences of data

### Testing Standards
- Use JUnit 5 for test execution and test lifecycle
- Use Kotest for assertions - leverage readable infix functions
  - Example: `result shouldBe expected` instead of `assertEquals(expected, result)`
  - Example: `value shouldNotBe null` instead of `assertNotNull(value)`
  - Example: `list shouldContain item` instead of `assertTrue(list.contains(item))`
  - Example: `temperature shouldBeInRange 160.0..200.0`
- Use MockK for mocking dependencies
- Use REST Assured for integration testing of REST endpoints
- Structure tests with clear Given/When/Then sections
- Use Kover for test coverage measurement and reporting

## Architecture

This is an Air Fryer Conversion API that converts cooking times and temperatures from conventional/fan/gas ovens to air fryer settings. The project is in early development stages.

### SOLID Principles
Strictly enforce SOLID principles throughout the codebase:
- **Single Responsibility Principle**: Each class should have one reason to change
- **Open/Closed Principle**: Classes should be open for extension, closed for modification
- **Liskov Substitution Principle**: Objects should be replaceable with instances of their subtypes
- **Interface Segregation Principle**: Clients should not depend on interfaces they don't use
- **Dependency Inversion Principle**: Depend on abstractions, not concretions

### Interface-First Design
- Every class must implement an interface (contract-first approach)
- Controllers, services, repositories, utilities - all should have corresponding interfaces
- `@RestController`, `@Service`, `@Component`, `@Repository` are just Spring annotations - the classes still need interfaces
- Exception: Classes that extend/implement Spring framework interfaces (e.g., `WebMvcConfigurer`, `Filter`, `HandlerInterceptor`)
- Exception: `@Configuration` classes that are purely framework configuration
- Interfaces and implementations can be in the same file when there's only one implementation
- Example structure:
  ```kotlin
  interface ConversionController {
      fun convert(temperature: Double, temperatureUnit: String, duration: Int, ovenType: String): ResponseEntity<ConversionResponse>
  }
  
  @RestController
  class ConversionControllerImpl : ConversionController {
      override fun convert(...): ResponseEntity<ConversionResponse> { ... }
  }
  
  // Same file is fine for single implementations
  interface ConversionService {
      fun convert(request: ConversionRequest): ConversionResult
  }
  
  @Service
  class ConversionServiceImpl : ConversionService {
      override fun convert(request: ConversionRequest): ConversionResult { ... }
  }
  ```

### API Documentation
- Thoroughly document all API endpoints using OpenAPI annotations
- Include detailed descriptions, parameter documentation, and response examples
- Use `@Operation`, `@Parameter`, `@ApiResponse` annotations for comprehensive documentation
- Generate OpenAPI specification without exposing Swagger UI (for external import only)

Key components (to be implemented):
- Temperature conversion utilities (Celsius, Fahrenheit, Gas Mark)
- Time conversion logic based on oven type
- API endpoints for conversion requests

## API Design

### API Endpoints

#### Conversion Endpoint
```
GET /api/v1/convert?temperature={number}&temperature_unit={C|F|GAS}&duration={minutes}&oven_type={CONVENTIONAL|FAN|GAS}
```

#### Health Check Endpoint  
```
GET /api/v1/health
```

#### API Information Endpoint
```
GET /api/v1/info
```

#### OpenAPI Documentation
```
GET /api/docs
```

**Query Parameters:**
- `temperature` (number) - Temperature value or gas mark number
- `temperature_unit` (string) - One of: "C", "F", "GAS"
- `duration` (number) - Time in minutes
- `oven_type` (string) - One of: "CONVENTIONAL", "FAN", "GAS"

**Success Response (200):**
```json
{
  "success": true,
  "data": {
    "air_fryer_time": 26,
    "air_fryer_temp_c": 170,
    "air_fryer_temp_f": 340
  },
  "input": {
    "temperature": 180,
    "temperature_unit": "C",
    "duration": 30,
    "oven_type": "FAN"
  }
}
```

**Output Rounding:**
- Temperatures are rounded to nearest 5-degree increment (matching typical air fryer controls)
- Time is rounded to nearest whole minute

**Error Response (400):**
```json
{
  "success": false,
  "error": {
    "code": "INVALID_PARAMETER",
    "message": "Invalid temperature_unit. Must be one of: C, F, GAS",
    "field": "temperature_unit"
  }
}
```

## Air Fryer Conversion API Specification

## Input Parameters
- `temperature`: Number (temperature value or gas mark number)
- `temperature_unit`: String ("C", "F", or "GAS")
- `duration`: Number (time in minutes)
- `oven_type`: String ("CONVENTIONAL", "FAN", "GAS")

## Output Parameters
- `air_fryer_time`: Number (time in minutes)
- `air_fryer_temp_c`: Number (temperature in Celsius)
- `air_fryer_temp_f`: Number (temperature in Fahrenheit)

## Gas Mark Temperature Reference
| Gas Mark | °C  | °F  |
|----------|-----|-----|
| ¼        | 110 | 230 |
| ½        | 120 | 248 |
| 1        | 140 | 284 |
| 2        | 150 | 302 |
| 3        | 160 | 320 |
| 4        | 180 | 356 |
| 5        | 190 | 374 |
| 6        | 200 | 392 |
| 7        | 220 | 428 |
| 8        | 230 | 446 |
| 9        | 240 | 464 |

## Conversion Logic

### Step 1: Normalise Input Temperature to Celsius
```
if temperature_unit == "GAS":
    temp_c = gas_mark_to_celsius(temperature)  // Use table above
elif temperature_unit == "F":
    temp_c = (temperature - 32) * 5/9
else:  // temperature_unit == "C"
    temp_c = temperature
```

### Step 2: Apply Air Fryer Conversion
```
if oven_type == "CONVENTIONAL" or oven_type == "GAS":
    air_fryer_temp_c = temp_c - 20
    air_fryer_time = duration * 0.75
elif oven_type == "FAN":
    air_fryer_temp_c = temp_c - 10
    air_fryer_time = duration * 0.85
```

### Step 3: Convert Temperature to Fahrenheit
```
air_fryer_temp_f = (air_fryer_temp_c * 9/5) + 32
```

## Summary Formulae

### Temperature Conversions (to air fryer)
- **Conventional/Gas**: Target °C - 20°C
- **Fan**: Target °C - 10°C

### Time Conversions (to air fryer)
- **Conventional/Gas**: Original time × 0.75
- **Fan**: Original time × 0.85

### Temperature Unit Conversions
- **°C to °F**: (°C × 9/5) + 32
- **°F to °C**: (°F - 32) × 5/9
