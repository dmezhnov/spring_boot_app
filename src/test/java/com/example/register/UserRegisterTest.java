package com.example.register;

import com.example.dto.UserRequestImpl;
import com.example.dto.UserResponseImpl;

import org.testng.annotations.Test;
import static org.testng.Assert.*;

class UserRegisterTest {

    private final UserRegisterImpl service = new UserRegisterImpl();

    @Test
    void processUserCreatesActive() {
        UserRequestImpl req = UserRequestImpl.builder()
                .name("Alice")
                .email("alice@example.com")
                .age(28)
                .build();

        UserResponseImpl resp = service.processUser(req);
        assertEquals(resp.name, "ALICE");
        assertEquals(resp.status, "ACTIVE");
        assertEquals(resp.age, 28);
    }

    @Test
    void validateUserReturnsValidated() {
        UserRequestImpl req = UserRequestImpl.builder()
                .name("Bob")
                .email("bob@example.com")
                .age(40)
                .build();

        UserResponseImpl resp = service.validateUser(req);
        assertEquals(resp.status, "VALIDATED");
    }
}
