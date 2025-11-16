package com.example;

import org.testng.annotations.Test;
import static org.testng.Assert.*;
import org.springframework.boot.autoconfigure.SpringBootApplication;

class ApplicationTest {

    @Test
    void applicationClassHasSpringBootApplication() {
        Class<Application> cls = Application.class;
        assertTrue(cls.isAnnotationPresent(SpringBootApplication.class));
    }
}
