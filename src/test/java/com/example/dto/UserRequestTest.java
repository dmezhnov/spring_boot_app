package com.example.dto;

import org.testng.annotations.Test;
import static org.testng.Assert.*;

class UserRequestTest {

    @Test
    void builderAndGetters() {
        UserRequest r = UserRequest.builder()
                .name("N")
                .email("e@x.com")
                .age(30)
                .build();

        assertEquals(r.getName(), "N");
        assertEquals(r.getAge(), 30);
    }
}
