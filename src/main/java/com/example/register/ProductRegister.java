package com.example.register;

import com.example.dto.ProductRequest;
import com.example.dto.ProductResponse;
import org.springframework.stereotype.Service;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Service for product operations
 */
@Service
public class ProductRegister {

    private final AtomicLong idGenerator = new AtomicLong(1000);

    /**
     * Create product from request
     */
    public ProductResponse createProduct(ProductRequest request) {
        if (request.getTitle() == null || request.getTitle().isEmpty()) {
            throw new IllegalArgumentException("Product title is required");
        }
        if (request.getPrice() < 0) {
            throw new IllegalArgumentException("Price cannot be negative");
        }

        double totalValue = request.getPrice() * request.getQuantity();
        boolean available = request.getQuantity() > 0;

        return ProductResponse.builder()
                .id(idGenerator.getAndIncrement())
                .title(request.getTitle())
                .description(request.getDescription())
                .price(request.getPrice())
                .quantity(request.getQuantity())
                .totalValue(totalValue)
                .category("GENERAL")
                .available(available)
                .build();
    }

    /**
     * Calculate discount for product
     */
    public ProductResponse applyDiscount(ProductRequest request, double discountPercent) {
        if (discountPercent < 0 || discountPercent > 100) {
            throw new IllegalArgumentException("Discount must be between 0 and 100");
        }

        double discountedPrice = request.getPrice() * (1 - discountPercent / 100.0);
        double totalValue = discountedPrice * request.getQuantity();

        return ProductResponse.builder()
                .id(idGenerator.getAndIncrement())
                .title(request.getTitle())
                .description(request.getDescription())
                .price(discountedPrice)
                .quantity(request.getQuantity())
                .totalValue(totalValue)
                .category("DISCOUNTED")
                .available(request.getQuantity() > 0)
                .build();
    }
}
