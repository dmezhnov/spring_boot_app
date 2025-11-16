package com.example.register;

import com.example.dto.UserRequest;
import com.example.dto.UserResponse;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Service for user operations
 */
@Service
public class UserRegister {

    private final AtomicLong idGenerator = new AtomicLong(1);

    /**
     * Process user request and return response
     */
    public UserResponse processUser(UserRequest request) {
        // Validation
        if (request.getName() == null || request.getName().isEmpty()) {
            throw new IllegalArgumentException("Name is required");
        }
        if (request.getAge() < 0 || request.getAge() > 150) {
            throw new IllegalArgumentException("Age must be between 0 and 150");
        }

        // Create response
        return UserResponse.builder()
                .id(idGenerator.getAndIncrement())
                .name(request.getName().toUpperCase())
                .email(request.getEmail())
                .age(request.getAge())
                .status("ACTIVE")
                .createdAt(LocalDateTime.now())
                .build();
    }

    /**
     * Validate user data
     */
    public UserResponse validateUser(UserRequest request) {
        if (request.getName() == null || request.getName().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be empty");
        }
        if (request.getEmail() == null || !request.getEmail().contains("@")) {
            throw new IllegalArgumentException("Invalid email format");
        }

        return UserResponse.builder()
                .id(idGenerator.getAndIncrement())
                .name(request.getName())
                .email(request.getEmail())
                .age(request.getAge())
                .status("VALIDATED")
                .createdAt(LocalDateTime.now())
                .build();
    }
}
