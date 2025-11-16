package com.example.register;

import com.example.dto.UserRequest;
import com.example.dto.UserResponse;
import com.example.register.UserRegister;

import org.testng.annotations.Test;
import static org.testng.Assert.*;

class UserRegisterTest {

    private final UserRegister service = new UserRegister();

    @Test
    void processUserCreatesActive() {
        UserRequest req = UserRequest.builder()
                .name("Alice")
                .email("alice@example.com")
                .age(28)
                .build();

        UserResponse resp = service.processUser(req);
        assertEquals(resp.getName(), "ALICE");
        assertEquals(resp.getStatus(), "ACTIVE");
        assertEquals(resp.getAge(), 28);
    }

    @Test
    void validateUserReturnsValidated() {
        UserRequest req = UserRequest.builder()
                .name("Bob")
                .email("bob@example.com")
                .age(40)
                .build();

        UserResponse resp = service.validateUser(req);
        assertEquals(resp.getStatus(), "VALIDATED");
    }
}
