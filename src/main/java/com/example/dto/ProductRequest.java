package com.example.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for product
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductRequest {
    private String title;
    private String description;
    private double price;
    private int quantity;
}
