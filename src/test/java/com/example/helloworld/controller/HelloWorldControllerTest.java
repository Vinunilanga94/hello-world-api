package com.example.helloworld.controller;

import com.example.helloworld.service.GreetingService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(HelloWorldController.class)
@Import(GreetingService.class)
class HelloWorldControllerTest {

    private static final String ENDPOINT = "/hello-world";

    @Autowired
    private MockMvc mockMvc;

    @Test
    void returnsGreetingForValidName() throws Exception {
        mockMvc.perform(get(ENDPOINT).param("name", "alice"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().json("{\"message\":\"Hello Alice\"}", true));
    }

    @ParameterizedTest
    @ValueSource(strings = {"a", "Alice", "mike", "M"})
    void returnsOkForFirstHalfOfAlphabet(String name) throws Exception {
        mockMvc.perform(get(ENDPOINT).param("name", name))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").exists());
    }

    @ParameterizedTest
    @ValueSource(strings = {"nancy", "N", "zoe", "Zack"})
    void returnsBadRequestForSecondHalfOfAlphabet(String name) throws Exception {
        assertInvalidInput(name);
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "   ", "1alice", "-alice", "émile"})
    void returnsBadRequestForEmptyOrNonLetterInput(String name) throws Exception {
        assertInvalidInput(name);
    }

    @Test
    void returnsBadRequestWhenNameIsMissing() throws Exception {
        mockMvc.perform(get(ENDPOINT))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().json("{\"error\":\"Invalid Input\"}", true));
    }

    @Test
    void escapesSpecialCharactersInResponse() throws Exception {
        mockMvc.perform(get(ENDPOINT).param("name", "al\"ice"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Hello Al\"ice"));
    }

    @Test
    void joinsRepeatedNameParametersBeforeValidating() throws Exception {
        mockMvc.perform(get(ENDPOINT).param("name", "alice", "bob"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Hello Alice,bob"));

        mockMvc.perform(get(ENDPOINT).param("name", "zoe", "alice"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Invalid Input"));
    }

    @Test
    void rejectsUnsupportedHttpMethod() throws Exception {
        mockMvc.perform(post(ENDPOINT).param("name", "alice"))
                .andExpect(status().isMethodNotAllowed());
    }

    private void assertInvalidInput(String name) throws Exception {
        mockMvc.perform(get(ENDPOINT).param("name", name))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().json("{\"error\":\"Invalid Input\"}", true));
    }
}
