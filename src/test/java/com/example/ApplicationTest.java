package com.example;

import org.testng.annotations.Test;
import static org.testng.Assert.*;
import org.springframework.boot.autoconfigure.SpringBootApplication;

class ApplicationTest {

    @Test
    void applicationClassHasSpringBootApplication() {
        Class<ApplicationImpl> cls = ApplicationImpl.class;
        assertTrue(cls.isAnnotationPresent(SpringBootApplication.class));
    }
}
