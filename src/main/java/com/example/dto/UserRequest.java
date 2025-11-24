package com.example.dto;

/**
 * Interface for user request DTO.
 * Implementations must expose data via public fields, not getters/setters.
 *
 * <p>Usage example:
 * {@code
 * UserRequestImpl request = UserRequestImpl.builder()
 *     .name("Alice")
 *     .email("alice@example.com")
 *     .age(30)
 *     .build();
 * }
 */
public interface UserRequest {
    // Marker interface for user request DTO.
}
