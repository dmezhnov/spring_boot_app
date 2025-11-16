package com.example.dto;

import org.testng.annotations.Test;
import static org.testng.Assert.*;
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

        assertEquals(r.getName(), "U");
        assertEquals(r.getCreatedAt(), now);
    }
}
