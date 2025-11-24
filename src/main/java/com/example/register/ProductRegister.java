package com.example.register;

import com.example.dto.ProductRequestImpl;
import com.example.dto.ProductResponseImpl;

/**
 * Use-case boundary for creating products and applying discounts.
 *
 * <p>Usage example:
 * {@code
 * ProductResponseImpl response = productRegister.createProduct(request);
 * }
 */
public interface ProductRegister {
	ProductResponseImpl createProduct(ProductRequestImpl request);
	ProductResponseImpl applyDiscount(ProductRequestImpl request, double discountPercent);
}
