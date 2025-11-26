package com.example.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

/**
 * Implementation for user request DTO using public fields and Lombok builder.
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
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRequestImpl implements UserRequest {
    public String name;
    public String email;
    public int age;
}
