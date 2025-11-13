package com.example.controller;

import com.example.dto.ProductRequest;
import com.example.dto.ProductResponse;
import com.example.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for product operations
 */
@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    /**
     * POST /api/products/create
     * Creates a new product from request JSON
     */
    @PostMapping("/create")
    public ResponseEntity<ProductResponse> createProduct(@RequestBody ProductRequest request) {
        try {
            ProductResponse response = productService.createProduct(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * POST /api/products/discount
     * Applies discount to product
     * Query parameter: discount (0-100)
     */
    @PostMapping("/discount")
    public ResponseEntity<ProductResponse> applyDiscount(
            @RequestBody ProductRequest request,
            @RequestParam(defaultValue = "10") double discount) {
        try {
            ProductResponse response = productService.applyDiscount(request, discount);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * POST /api/products/calculate
     * Calculates product statistics
     */
    @PostMapping("/calculate")
    public ResponseEntity<ProductResponse> calculateStats(@RequestBody ProductRequest request) {
        try {
            ProductResponse response = productService.createProduct(request);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * GET /api/products/health
     * Health check endpoint
     */
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Product service is healthy");
    }
}
