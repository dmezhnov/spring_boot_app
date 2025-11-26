package com.example.dto;

/**
 * Interface for product request DTO.
 * Implementations must expose data via public fields, not getters/setters.
 *
 * <p>Usage example:
 * {@code
 * ProductRequestImpl request = ProductRequestImpl.builder()
 *     .title("Phone")
 *     .price(100.0)
 *     .quantity(2)
 *     .build();
 * }
 */
public interface ProductRequest {
    // Marker interface for product request DTO.
}
