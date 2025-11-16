package com.example.controller;

import com.example.dto.ProductRequest;
import com.example.dto.ProductResponse;
import com.example.register.ProductRegister;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/products")
public class ProductControllerImpl implements ProductController {

	@Autowired
	private ProductRegister productService;

	@Override
	@PostMapping("/create")
	public ResponseEntity<ProductResponse> createProduct(@RequestBody ProductRequest request) {
		try {
			ProductResponse response = productService.createProduct(request);
			return ResponseEntity.status(HttpStatus.CREATED).body(response);
		} catch (IllegalArgumentException e) {
			return ResponseEntity.badRequest().build();
		}
	}

	@Override
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

	@Override
	@PostMapping("/calculate")
	public ResponseEntity<ProductResponse> calculateStats(@RequestBody ProductRequest request) {
		try {
			ProductResponse response = productService.createProduct(request);
			return ResponseEntity.ok(response);
		} catch (IllegalArgumentException e) {
			return ResponseEntity.badRequest().build();
		}
	}

	@Override
	@GetMapping("/health")
	public ResponseEntity<String> health() {
		return ResponseEntity.ok("Product service is healthy");
	}
}
