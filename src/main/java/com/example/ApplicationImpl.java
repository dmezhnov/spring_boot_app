package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main Spring Boot application implementation and entry point.
 *
 * <p>Usage example:
 * {@code
 * public static void main(String[] args) {
 *     ApplicationImpl.main(args);
 * }
 * }
 */
@SpringBootApplication
public class ApplicationImpl implements Application {

    public static void main(String[] args) {
        SpringApplication.run(ApplicationImpl.class, args);
    }
}
