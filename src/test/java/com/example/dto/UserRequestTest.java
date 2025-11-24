package com.example.dto;

import org.testng.annotations.Test;
import static org.testng.Assert.*;

/**
 * Tests for {@link UserRequestImpl} builder and public fields.
 *
 * <p>Usage example:
 * {@code
 * UserRequestTest test = new UserRequestTest();
 * test.builderAndFields();
 * }
 */
class UserRequestTest {

    @Test
    void builderAndFields() {
        UserRequestImpl r = UserRequestImpl.builder()
                .name("N")
                .email("e@x.com")
                .age(30)
                .build();

        assertEquals(r.name, "N");
        assertEquals(r.age, 30);
    }
}
