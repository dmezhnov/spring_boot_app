package com.example.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

/**
 * Implementation for product request DTO.
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
