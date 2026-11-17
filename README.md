# Hello World API

A small Spring Boot web service that exposes a single endpoint:

```
GET /hello-world?name=alice
```

| Input                                         | Status            | Body                             |
|-----------------------------------------------|-------------------|----------------------------------|
| Name starts with `A`-`M` (any case)           | `200 OK`          | `{ "message": "Hello Alice" }`   |
| Name starts with `N`-`Z` (any case)           | `400 Bad Request` | `{ "error": "Invalid Input" }`   |
| `name` missing, empty, or whitespace only     | `400 Bad Request` | `{ "error": "Invalid Input" }`   |
| Name starts with a non-English-letter character | `400 Bad Request` | `{ "error": "Invalid Input" }` |

## Tech stack

- Java 17
- Spring Boot 3.3 (Spring Web)
- Maven (the Maven Wrapper is included, so a local Maven install is not needed)
- JUnit 5, AssertJ, MockMvc

## Prerequisites

- JDK 17 or later, available on `PATH` or through `JAVA_HOME`

## How to run the application

```bash
# macOS / Linux
./mvnw spring-boot:run

# Windows
mvnw.cmd spring-boot:run
```

The service starts on port `8080`. Set the `PORT` environment variable to use a different port.

You can also build and run the executable jar:

```bash
./mvnw clean package
java -jar target/hello-world-api-1.0.0.jar
```

Try it out:

```bash
curl -i "http://localhost:8080/hello-world?name=alice"   # 200 {"message":"Hello Alice"}
curl -i "http://localhost:8080/hello-world?name=zoe"     # 400 {"error":"Invalid Input"}
curl -i "http://localhost:8080/hello-world"              # 400 {"error":"Invalid Input"}
```

## How to run the tests

```bash
# macOS / Linux
./mvnw test

# Windows
mvnw.cmd test
```

The test suite has three layers:

| Test class                               | Scope                                                              |
|------------------------------------------|--------------------------------------------------------------------|
| `GreetingServiceTest`                    | Unit tests for the validation and greeting rules (no Spring context) |
| `HelloWorldControllerTest`               | Web layer tests with `@WebMvcTest` and MockMvc: status codes, JSON bodies, content type |
| `HelloWorldApplicationIntegrationTest`   | Starts the full application on a random port and calls it over real HTTP |

## Project structure

```
src/main/java/com/example/helloworld
├── HelloWorldApplication.java        # Spring Boot entry point
├── controller/HelloWorldController   # HTTP mapping only, no business logic
├── service/GreetingService           # Validation and greeting rules
├── dto/GreetingResponse, ErrorResponse  # Response bodies (Java records)
└── exception/
    ├── InvalidInputException         # Raised by the service for bad input
    └── GlobalExceptionHandler        # Maps InvalidInputException to 400 + error body
```

## Assumptions

- **Case-insensitive check.** `a`-`m` and `A`-`M` are treated the same, as are `n`-`z` and `N`-`Z`.
- **Whitespace is trimmed.** Leading and trailing whitespace (including Unicode whitespace) is ignored, so `name=%20bob%20` returns `Hello Bob`. A name that is only whitespace is treated as empty.
- **Only the first letter is capitalised.** The rest of the name is returned as given: `alice` becomes `Alice`, `ALICE` stays `ALICE`, `mary-jane` becomes `Mary-jane`.
- **Only the first character is validated.** Characters after the first one (spaces, hyphens, apostrophes and so on) are allowed, so names like `Jean Luc` or `Mary-Jane` work.
- **Non-English first characters are rejected.** If the first character is not one of the 26 English letters (a digit, a symbol, or an accented or non-Latin letter such as `É` or `а`), the request gets `400 Invalid Input`. The specification only defines the two halves of the English alphabet, so anything outside that is treated as invalid input.
- **Repeated parameters.** If `name` is sent more than once (`?name=alice&name=bob`), Spring joins the values with a comma (`alice,bob`), and that combined value is validated like any other name.
- **Other HTTP behaviour uses Spring defaults.** Unsupported methods on `/hello-world` return `405 Method Not Allowed`, and unknown paths return `404 Not Found`.
