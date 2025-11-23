package com.example.repository;

import com.example.dto.UserResponseImpl;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.LocalDateTime;

/**
 * JDBC-based implementation of UserRepository using Spring JdbcTemplate.
 */
@Repository
public class UserRepositoryImpl implements UserRepository {

    private final JdbcTemplate jdbcTemplate;

    public UserRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public UserResponseImpl save(UserResponseImpl user) {
        if (user == null) {
            throw new IllegalArgumentException("User must not be null");
        }
        if (user.createdAt == null) {
            user.createdAt = LocalDateTime.now();
        }

        String sql = """
                INSERT INTO users (name, email, age, status, created_at)
                VALUES (?, ?, ?, ?, ?)
                RETURNING id
                """;

        Long generatedId = jdbcTemplate.queryForObject(
                sql,
                Long.class,
                user.name,
                user.email,
                user.age,
                user.status,
                Timestamp.valueOf(user.createdAt)
        );

        if (generatedId == null) {
            throw new IllegalStateException("Failed to generate user ID");
        }

        user.id = generatedId;
        return user;
    }

    @Override
    public UserResponseImpl findByEmail(String email) {
        if (email == null || email.isEmpty()) {
            throw new IllegalArgumentException("Email must not be null or empty");
        }

        String sql = """
                SELECT id, name, email, age, status, created_at
                FROM users
                WHERE email = ?
                """;

        return jdbcTemplate.queryForObject(
                sql,
                (rs, rowNum) -> UserResponseImpl.builder()
                        .id(rs.getLong("id"))
                        .name(rs.getString("name"))
                        .email(rs.getString("email"))
                        .age(rs.getInt("age"))
                        .status(rs.getString("status"))
                        .createdAt(rs.getTimestamp("created_at").toLocalDateTime())
                        .build(),
                email
        );
    }
}
