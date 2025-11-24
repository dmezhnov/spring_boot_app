package com.example.repository;

import com.example.dto.UserResponseImpl;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.LocalDateTime;

/**
 * JDBC-based implementation of {@link UserRepository} using Spring {@link JdbcTemplate}.
 *
 * <p>Usage example:
 * {@code
 * UserRepository repository = new UserRepositoryImpl(jdbcTemplate);
 * UserResponseImpl saved = repository.save(user);
 * UserResponseImpl loaded = repository.findByEmail(\"user@example.com\");
 * }
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
                ON CONFLICT (email) DO UPDATE
                SET name = EXCLUDED.name,
                    age = EXCLUDED.age,
                    status = EXCLUDED.status,
                    created_at = EXCLUDED.created_at
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
