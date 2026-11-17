package com.example.helloworld.exception;

/**
 * Thrown when a request contains input that does not satisfy the API contract.
 * The message is intended for logging only and is never exposed to clients.
 */
public class InvalidInputException extends RuntimeException {

    public InvalidInputException(String message) {
        super(message);
    }
}
