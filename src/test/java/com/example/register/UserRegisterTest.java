package com.example.register;

import com.example.dto.UserRequest;
import com.example.dto.UserResponse;
import com.example.register.UserRegister;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

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
        assertEquals("ALICE", resp.getName());
        assertEquals("ACTIVE", resp.getStatus());
        assertEquals(28, resp.getAge());
    }

    @Test
    void validateUserReturnsValidated() {
        UserRequest req = UserRequest.builder()
                .name("Bob")
                .email("bob@example.com")
                .age(40)
                .build();

        UserResponse resp = service.validateUser(req);
        assertEquals("VALIDATED", resp.getStatus());
    }
}
