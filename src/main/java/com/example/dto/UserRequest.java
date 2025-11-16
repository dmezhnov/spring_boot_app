package com.example.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for user data
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRequest {
    public String name;
    public String email;
    public int age;
}
