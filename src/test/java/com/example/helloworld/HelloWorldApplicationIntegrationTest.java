package com.example.helloworld;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * End-to-end tests that start the full application on a random port and
 * exercise the endpoint over real HTTP.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class HelloWorldApplicationIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void returnsGreetingForValidName() {
        ResponseEntity<String> response = restTemplate.getForEntity("/hello-world?name=alice", String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getHeaders().getContentType()).isNotNull();
        assertThat(response.getHeaders().getContentType().isCompatibleWith(MediaType.APPLICATION_JSON)).isTrue();
        assertThat(response.getBody()).isEqualTo("{\"message\":\"Hello Alice\"}");
    }

    @Test
    void returnsBadRequestForSecondHalfOfAlphabet() {
        ResponseEntity<String> response = restTemplate.getForEntity("/hello-world?name=zoe", String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isEqualTo("{\"error\":\"Invalid Input\"}");
    }

    @Test
    void returnsBadRequestWhenNameIsMissing() {
        ResponseEntity<String> response = restTemplate.getForEntity("/hello-world", String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isEqualTo("{\"error\":\"Invalid Input\"}");
    }

    @Test
    void returnsBadRequestWhenNameIsEmpty() {
        ResponseEntity<String> response = restTemplate.getForEntity("/hello-world?name=", String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isEqualTo("{\"error\":\"Invalid Input\"}");
    }
}
