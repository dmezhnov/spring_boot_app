package com.example.register;

import com.example.dto.ProductRequest;
import com.example.dto.ProductResponse;
import org.springframework.stereotype.Service;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class ProductRegisterImpl implements ProductRegister {

	private final AtomicLong idGenerator = new AtomicLong(1000);

	@Override
	public ProductResponse createProduct(ProductRequest request) {
		if (request.title == null || request.title.isEmpty()) {
			throw new IllegalArgumentException("Product title is required");
		}
		if (request.price < 0) {
			throw new IllegalArgumentException("Price cannot be negative");
		}

		double totalValue = request.price * request.quantity;
		boolean available = request.quantity > 0;

		return ProductResponse.builder()
				.id(idGenerator.getAndIncrement())
				.title(request.title)
				.description(request.description)
				.price(request.price)
				.quantity(request.quantity)
				.totalValue(totalValue)
				.category("GENERAL")
				.available(available)
				.build();
	}

	@Override
	public ProductResponse applyDiscount(ProductRequest request, double discountPercent) {
		if (discountPercent < 0 || discountPercent > 100) {
			throw new IllegalArgumentException("Discount must be between 0 and 100");
		}

		double discountedPrice = request.price * (1 - discountPercent / 100.0);
		double totalValue = discountedPrice * request.quantity;

		return ProductResponse.builder()
				.id(idGenerator.getAndIncrement())
				.title(request.title)
				.description(request.description)
				.price(discountedPrice)
				.quantity(request.quantity)
				.totalValue(totalValue)
				.category("DISCOUNTED")
				.available(request.quantity > 0)
				.build();
	}
}
