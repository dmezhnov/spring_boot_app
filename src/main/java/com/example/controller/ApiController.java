package com.example.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

/**
 * General API Controller with various JSON response examples
 */
@RestController
@RequestMapping("/api")
public class ApiController {

    /**
     * GET /api/welcome
     * Returns welcome message in JSON
     */
    @GetMapping("/welcome")
    public ResponseEntity<Map<String, Object>> welcome() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Welcome to Spring Boot REST API");
        response.put("version", "1.0.0");
        response.put("status", "running");
        return ResponseEntity.ok(response);
    }

    /**
     * POST /api/echo
     * Echo JSON request back with additional metadata
     */
    @PostMapping("/echo")
    public ResponseEntity<Map<String, Object>> echo(@RequestBody Map<String, Object> request) {
        Map<String, Object> response = new HashMap<>();
        response.put("received", request);
        response.put("timestamp", System.currentTimeMillis());
        response.put("type", "echo_response");
        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/info
     * Returns API information
     */
    @GetMapping("/info")
    public ResponseEntity<Map<String, String>> info() {
        Map<String, String> info = new HashMap<>();
        info.put("application", "Spring Boot REST API");
        info.put("java_version", System.getProperty("java.version"));
        info.put("java_vendor", System.getProperty("java.vendor"));
        info.put("os_name", System.getProperty("os.name"));
        info.put("os_version", System.getProperty("os.version"));
        return ResponseEntity.ok(info);
    }

    /**
     * POST /api/transform
     * Transforms input JSON to output JSON with calculations
     */
    @PostMapping("/transform")
    public ResponseEntity<Map<String, Object>> transform(@RequestBody Map<String, Object> data) {
        Map<String, Object> result = new HashMap<>();
        result.put("input", data);
        result.put("keys_count", data.size());
        result.put("keys", data.keySet());
        result.put("processed", true);
        return ResponseEntity.ok(result);
    }
}
