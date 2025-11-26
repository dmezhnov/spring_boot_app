package com.example.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

/**
 * Implementation for product response DTO using public fields and Lombok builder.
 *
 * <p>Usage example:
 * {@code
 * ProductResponseImpl response = ProductResponseImpl.builder()
 *     .id(1L)
 *     .title("Phone")
 *     .description("Smartphone")
 *     .price(100.0)
 *     .quantity(2)
 *     .totalValue(200.0)
 *     .category("GENERAL")
 *     .available(true)
 *     .build();
 * }
 */
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductResponseImpl implements ProductResponse {
    public Long id;
    public String title;
    public String description;
    public double price;
    public int quantity;
    public double totalValue;
    public String category;
    public boolean available;
}
