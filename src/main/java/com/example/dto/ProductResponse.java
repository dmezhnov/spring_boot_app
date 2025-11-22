package com.example.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

/**
 * Response DTO for product
 */
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductResponse {
    public Long id;
    public String title;
    public String description;
    public double price;
    public int quantity;
    public double totalValue;
    public String category;
    public boolean available;
}
