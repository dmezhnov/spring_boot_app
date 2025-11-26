package com.example.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

/**
 * Implementation for product request DTO using public fields and Lombok builder.
 *
 * <p>Usage example:
 * {@code
 * ProductRequestImpl request = ProductRequestImpl.builder()
 *     .title("Phone")
 *     .description("Smartphone")
 *     .price(100.0)
 *     .quantity(2)
 *     .build();
 * }
 */
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductRequestImpl implements ProductRequest {
    public String title;
    public String description;
    public double price;
    public int quantity;
}
