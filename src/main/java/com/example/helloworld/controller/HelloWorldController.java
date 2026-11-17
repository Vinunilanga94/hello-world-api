package com.example.helloworld.controller;

import com.example.helloworld.dto.GreetingResponse;
import com.example.helloworld.service.GreetingService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloWorldController {

    private final GreetingService greetingService;

    public HelloWorldController(GreetingService greetingService) {
        this.greetingService = greetingService;
    }

    /**
     * Greets the given name. Validation failures are translated into a
     * 400 response by {@link com.example.helloworld.exception.GlobalExceptionHandler}.
     */
    @GetMapping(path = "/hello-world", produces = MediaType.APPLICATION_JSON_VALUE)
    public GreetingResponse helloWorld(@RequestParam(name = "name", required = false) String name) {
        return new GreetingResponse(greetingService.greet(name));
    }
}
