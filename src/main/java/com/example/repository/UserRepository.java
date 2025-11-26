package com.example.repository;

import com.example.dto.UserResponseImpl;

/**
 * Repository contract for persisting users.
 * Implementations must use JDBC-based access (JdbcTemplate) and public-field DTOs.
 *
 * <p>Usage example:
 * {@code
 * UserResponseImpl saved = userRepository.save(user);
 * UserResponseImpl loaded = userRepository.findByEmail(\"user@example.com\");
 * }
 */
public interface UserRepository {

    UserResponseImpl save(UserResponseImpl user);

    UserResponseImpl findByEmail(String email);
}
