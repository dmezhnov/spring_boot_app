package com.example.repository;

import com.example.dto.UserResponseImpl;

/**
 * Repository contract for persisting users.
 * Implementations must use JDBC-based access (JdbcTemplate) and public-field DTOs.
 */
public interface UserRepository {

    UserResponseImpl save(UserResponseImpl user);

    UserResponseImpl findByEmail(String email);
}
