package com.example.helloworld.service;

import com.example.helloworld.exception.InvalidInputException;
import org.springframework.stereotype.Service;

@Service
public class GreetingService {

    private static final String GREETING_PREFIX = "Hello ";

    /**
     * Builds a greeting for the given name.
     *
     * @param name the raw name; surrounding whitespace is ignored
     * @return a greeting such as {@code "Hello Alice"}
     * @throws InvalidInputException if the name is missing, blank, or does not
     *                               start with a letter in the range A-M (case-insensitive)
     */
    public String greet(String name) {
        if (name == null) {
            throw new InvalidInputException("Name parameter is missing");
        }

        String trimmed = name.strip();
        if (trimmed.isEmpty()) {
            throw new InvalidInputException("Name parameter is empty");
        }

        char firstLetter = trimmed.charAt(0);
        if (!isInFirstHalfOfAlphabet(firstLetter)) {
            throw new InvalidInputException("Name must start with a letter between A and M");
        }

        return GREETING_PREFIX + capitalize(trimmed);
    }

    private static boolean isInFirstHalfOfAlphabet(char c) {
        return (c >= 'a' && c <= 'm') || (c >= 'A' && c <= 'M');
    }

    private static String capitalize(String value) {
        return Character.toUpperCase(value.charAt(0)) + value.substring(1);
    }
}
