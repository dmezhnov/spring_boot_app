package com.example;

/**
 * Application interface for the Spring Boot application.
 *
 * <p>Usage example:
 * {@code
 * // Inject the marker interface into another Spring-managed component
 * // to express a dependency on the running application.
 * private final Application application;
 * }
 */
public interface Application {
    // Marker interface for the application entry point.
}
