package com.example.controller;

import com.example.dto.ProductRequestImpl;
import com.example.dto.ProductResponseImpl;
import com.example.register.ProductRegisterImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/products")
public class ProductController {

	@Autowired
	private ProductRegisterImpl productService;

	@PostMapping("/create")
	public ResponseEntity<ProductResponseImpl> createProduct(@RequestBody ProductRequestImpl request) {
		try {
			ProductResponseImpl response = productService.createProduct(request);
			return ResponseEntity.status(HttpStatus.CREATED).body(response);
		} catch (IllegalArgumentException e) {
			return ResponseEntity.badRequest().build();
		}
	}

	@PostMapping("/discount")
	public ResponseEntity<ProductResponseImpl> applyDiscount(
			@RequestBody ProductRequestImpl request,
			@RequestParam(defaultValue = "10") double discount) {
		try {
			ProductResponseImpl response = productService.applyDiscount(request, discount);
			return ResponseEntity.ok(response);
		} catch (IllegalArgumentException e) {
			return ResponseEntity.badRequest().build();
		}
	}

	@PostMapping("/calculate")
	public ResponseEntity<ProductResponseImpl> calculateStats(@RequestBody ProductRequestImpl request) {
		try {
			ProductResponseImpl response = productService.createProduct(request);
			return ResponseEntity.ok(response);
		} catch (IllegalArgumentException e) {
			return ResponseEntity.badRequest().build();
		}
	}

	@GetMapping("/health")
	public ResponseEntity<String> health() {
		return ResponseEntity.ok("Product service is healthy");
	}
}
