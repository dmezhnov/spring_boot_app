package com.example.repository;

import com.example.dto.ProductResponseImpl;

/**
 * Repository contract for persisting products.
 */
public interface ProductRepository {

    ProductResponseImpl save(ProductResponseImpl product);

    ProductResponseImpl findByTitle(String title);
}
