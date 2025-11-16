package com.example.register;

import com.example.dto.ProductRequest;
import com.example.dto.ProductResponse;

public interface ProductRegister {
	ProductResponse createProduct(ProductRequest request);
	ProductResponse applyDiscount(ProductRequest request, double discountPercent);
}
