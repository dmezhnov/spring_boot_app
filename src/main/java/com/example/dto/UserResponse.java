package com.example.dto;

/**
 * Interface for user response DTO.
 * Implementations must expose data via public fields, not getters/setters.
 *
 * <p>Usage example:
 * {@code
 * UserResponseImpl response = UserResponseImpl.builder()
 *     .id(1L)
 *     .name("Alice")
 *     .email("alice@example.com")
 *     .age(30)
 *     .status("ACTIVE")
 *     .build();
 * }
 */
public interface UserResponse {
    // Marker interface for user response DTO.
}
