package com.example;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.springframework.boot.autoconfigure.SpringBootApplication;

class ApplicationTest {

    @Test
    void applicationClassHasSpringBootApplication() {
        Class<Application> cls = Application.class;
        assertTrue(cls.isAnnotationPresent(SpringBootApplication.class));
    }
}
