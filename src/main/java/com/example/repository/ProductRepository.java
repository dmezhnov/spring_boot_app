package com.example.repository;

import com.example.dto.ProductResponseImpl;

/**
 * Repository contract for persisting products.
 *
 * <p>Usage example:
 * {@code
 * ProductResponseImpl saved = productRepository.save(product);
 * ProductResponseImpl loaded = productRepository.findByTitle("Phone");
 * }
 */
public interface ProductRepository {

    ProductResponseImpl save(ProductResponseImpl product);

    ProductResponseImpl findByTitle(String title);
}
