package com.example.controller;

import com.example.dto.ProductRequestImpl;
import com.example.dto.ProductResponseImpl;
import com.example.register.ProductRegisterImpl;
import com.example.repository.ProductRepositoryImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/products")
public class ProductController {

	@Autowired
	private ProductRegisterImpl productService;

	@Autowired
	private ProductRepositoryImpl productRepository;

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

	@GetMapping("/by-title")
	public ResponseEntity<ProductResponseImpl> getByTitle(@RequestParam String title) {
		if (title == null || title.isEmpty()) {
			return ResponseEntity.badRequest().build();
		}
		try {
			ProductResponseImpl response = productRepository.findByTitle(title);
			return ResponseEntity.ok(response);
		} catch (EmptyResultDataAccessException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
	}
}
