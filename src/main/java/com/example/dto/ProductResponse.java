package com.example.dto;

/**
 * Interface for product response DTO.
 * Implementations must expose data via public fields, not getters/setters.
 *
 * <p>Usage example:
 * {@code
 * ProductResponseImpl response = ProductResponseImpl.builder()
 *     .id(1L)
 *     .title("Phone")
 *     .totalValue(200.0)
 *     .build();
 * }
 */
public interface ProductResponse {
    // Marker interface for product response DTO.
}
