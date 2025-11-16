package com.example.controller;

import com.example.dto.ProductRequest;
import com.example.dto.ProductResponse;
import org.springframework.http.ResponseEntity;

public interface ProductController {
	ResponseEntity<ProductResponse> createProduct(ProductRequest request);
	ResponseEntity<ProductResponse> applyDiscount(ProductRequest request, double discount);
	ResponseEntity<ProductResponse> calculateStats(ProductRequest request);
	ResponseEntity<String> health();
}
