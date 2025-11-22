package com.example.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * Implementation for user response DTO.
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
