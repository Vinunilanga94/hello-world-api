package com.example.helloworld.service;

import com.example.helloworld.exception.InvalidInputException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class GreetingServiceTest {

    private final GreetingService service = new GreetingService();

    @ParameterizedTest(name = "\"{0}\" -> \"{1}\"")
    @CsvSource(delimiter = '|', value = {
            "alice     | Hello Alice",
            "Alice     | Hello Alice",
            "ALICE     | Hello ALICE",
            "bob       | Hello Bob",
            "a         | Hello A",
            "m         | Hello M",
            "M         | Hello M",
            "mary-jane | Hello Mary-jane",
            "jean luc  | Hello Jean luc"
    })
    void greetsNamesStartingWithAThroughM(String name, String expected) {
        assertThat(service.greet(name)).isEqualTo(expected);
    }

    @Test
    void ignoresSurroundingWhitespace() {
        assertThat(service.greet("  alice \t")).isEqualTo("Hello Alice");
    }

    @ParameterizedTest
    @ValueSource(strings = {"n", "N", "nancy", "Nancy", "zoe", "Z", "oscar", "Yusuf"})
    void rejectsNamesStartingWithNThroughZ(String name) {
        assertThatThrownBy(() -> service.greet(name)).isInstanceOf(InvalidInputException.class);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "   ", "\t", "\n", " "})
    void rejectsMissingOrBlankNames(String name) {
        assertThatThrownBy(() -> service.greet(name)).isInstanceOf(InvalidInputException.class);
    }

    @ParameterizedTest
    @ValueSource(strings = {"1alice", "-alice", "_bob", "'carol", "@dave", "émile", "Ángel", "аnna", "ß"})
    void rejectsNamesNotStartingWithAnEnglishLetter(String name) {
        assertThatThrownBy(() -> service.greet(name)).isInstanceOf(InvalidInputException.class);
    }
}
