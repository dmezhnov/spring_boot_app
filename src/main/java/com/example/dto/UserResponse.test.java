package com.example.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDateTime;

class UserResponseTest {

    @Test
    void builderAndFields() {
        LocalDateTime now = LocalDateTime.now();
        UserResponse r = UserResponse.builder()
                .id(1L)
                .name("U")
                .email("u@example.com")
                .age(20)
                .status("S")
                .createdAt(now)
                .build();

        assertEquals("U", r.getName());
        assertEquals(now, r.getCreatedAt());
    }
}
