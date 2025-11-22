package com.example.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

/**
 * Implementation for user request DTO.
 */
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRequestImpl implements UserRequest {
    public String name;
    public String email;
    public int age;
}
