package com.example.controller;

import com.example.dto.UserRequest;
import com.example.dto.UserResponse;
import com.example.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for user operations
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * POST /api/users/process
     * Accepts UserRequest JSON and returns UserResponse JSON
     */
    @PostMapping("/process")
    public ResponseEntity<UserResponse> processUser(@RequestBody UserRequest request) {
        try {
            UserResponse response = userService.processUser(request);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * POST /api/users/validate
     * Validates user data and returns response
     */
    @PostMapping("/validate")
    public ResponseEntity<UserResponse> validateUser(@RequestBody UserRequest request) {
        try {
            UserResponse response = userService.validateUser(request);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    /**
     * POST /api/users/register
     * Registers a new user
     */
    @PostMapping("/register")
    public ResponseEntity<UserResponse> registerUser(@RequestBody UserRequest request) {
        if (request.getName() == null || request.getEmail() == null) {
            return ResponseEntity.badRequest().build();
        }

        UserResponse response = userService.processUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * GET /api/users/health
     * Health check endpoint
     */
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("User service is healthy");
    }
}
