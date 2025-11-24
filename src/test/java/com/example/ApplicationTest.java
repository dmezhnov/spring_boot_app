package com.example;

import org.testng.annotations.Test;
import static org.testng.Assert.*;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Test suite verifying that the Spring Boot application is correctly configured.
 *
 * <p>Usage example:
 * {@code
 * ApplicationTest test = new ApplicationTest();
 * test.applicationClassHasSpringBootApplication();
 * }
 */
class ApplicationTest {

    @Test
    void applicationClassHasSpringBootApplication() {
        Class<ApplicationImpl> cls = ApplicationImpl.class;
        assertTrue(cls.isAnnotationPresent(SpringBootApplication.class));
    }
}
