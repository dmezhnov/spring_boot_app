package com.example.register;

import com.example.dto.ProductRequestImpl;
import com.example.dto.ProductResponseImpl;

public interface ProductRegister {
	ProductResponseImpl createProduct(ProductRequestImpl request);
	ProductResponseImpl applyDiscount(ProductRequestImpl request, double discountPercent);
}
