package com.example.controller;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.springframework.http.ResponseEntity;
import java.util.Map;

class ApiControllerTest {

    private final ApiController controller = new ApiController();

    @Test
    void welcomeReturnsExpectedKeys() {
        ResponseEntity<Map<String, Object>> resp = controller.welcome();
        assertEquals(200, resp.getStatusCodeValue());
        Map<String, Object> body = resp.getBody();
        assertNotNull(body);
        assertTrue(body.containsKey("message"));
        assertTrue(body.containsKey("version"));
        assertTrue(body.containsKey("status"));
    }

    @Test
    void echoReturnsReceived() {
        Map<String, Object> request = Map.of("foo", "bar");
        ResponseEntity<Map<String, Object>> resp = controller.echo(request);
        assertEquals(200, resp.getStatusCodeValue());
        Map<String, Object> body = resp.getBody();
        assertNotNull(body);
        assertTrue(body.containsKey("received"));
    }

    @Test
    void transformCountsKeys() {
        Map<String, Object> data = Map.of("a", 1, "b", 2);
        ResponseEntity<Map<String, Object>> resp = controller.transform(data);
        Map<String, Object> body = resp.getBody();
        assertEquals(2, body.get("keys_count"));
        assertEquals(true, body.get("processed"));
    }
}
