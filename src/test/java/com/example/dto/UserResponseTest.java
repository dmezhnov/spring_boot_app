package com.example.dto;

import org.testng.annotations.Test;
import static org.testng.Assert.*;
import java.time.LocalDateTime;

/**
 * Tests for {@link UserResponseImpl} builder and public fields.
 *
 * <p>Usage example:
 * {@code
 * UserResponseTest test = new UserResponseTest();
 * test.builderAndFields();
 * }
 */
class UserResponseTest {

    @Test
    void builderAndFields() {
        LocalDateTime now = LocalDateTime.now();
        UserResponseImpl r = UserResponseImpl.builder()
                .id(1L)
                .name("U")
                .email("u@example.com")
                .age(20)
                .status("S")
                .createdAt(now)
                .build();

        assertEquals(r.name, "U");
        assertEquals(r.createdAt, now);
    }
}
