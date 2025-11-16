package com.example.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class UserRequestTest {

    @Test
    void builderAndGetters() {
        UserRequest r = UserRequest.builder()
                .name("N")
                .email("e@x.com")
                .age(30)
                .build();

        assertEquals("N", r.getName());
        assertEquals(30, r.getAge());
    }
}
