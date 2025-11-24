package com.example.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * Implementation for user response DTO using public fields and Lombok builder.
 *
 * <p>Usage example:
 * {@code
 * UserResponseImpl response = UserResponseImpl.builder()
 *     .id(1L)
 *     .name("Alice")
 *     .email("alice@example.com")
 *     .age(30)
 *     .status("ACTIVE")
 *     .createdAt(LocalDateTime.now())
 *     .build();
 * }
 */
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponseImpl implements UserResponse {
    public Long id;
    public String name;
    public String email;
    public int age;
    public String status;
    public LocalDateTime createdAt;
}
