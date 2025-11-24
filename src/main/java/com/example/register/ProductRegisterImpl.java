package com.example.register;

import com.example.dto.ProductRequestImpl;
import com.example.dto.ProductResponseImpl;
import com.example.repository.ProductRepositoryImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Service implementation of {@link ProductRegister} that builds product responses and persists them if a repository is available.
 *
 * <p>Usage example:
 * {@code
 * ProductRegister register = new ProductRegisterImpl(productRepository);
 * ProductResponseImpl response = register.createProduct(request);
 * }
 */
@Service
public class ProductRegisterImpl implements ProductRegister {

	private final ProductRepositoryImpl productRepository;

	@Autowired
	public ProductRegisterImpl(ProductRepositoryImpl productRepository) {
		this.productRepository = productRepository;
	}

	ProductRegisterImpl() {
		this.productRepository = null;
	}

	@Override
	public ProductResponseImpl createProduct(ProductRequestImpl request) {
		if (request.title == null || request.title.isEmpty()) {
			throw new IllegalArgumentException("Product title is required");
		}
		if (request.price < 0) {
			throw new IllegalArgumentException("Price cannot be negative");
		}

		double totalValue = request.price * request.quantity;
		boolean available = request.quantity > 0;

		ProductResponseImpl response = new ProductResponseImpl(
				null,
				request.title,
				request.description,
				request.price,
				request.quantity,
				totalValue,
				"GENERAL",
				available
		);

		return saveIfRepositoryPresent(response);
	}

	@Override
	public ProductResponseImpl applyDiscount(ProductRequestImpl request, double discountPercent) {
		if (discountPercent < 0 || discountPercent > 100) {
			throw new IllegalArgumentException("Discount must be between 0 and 100");
		}

		double discountedPrice = request.price * (1 - discountPercent / 100.0);
		double totalValue = discountedPrice * request.quantity;

		ProductResponseImpl response = new ProductResponseImpl(
				null,
				request.title,
				request.description,
				discountedPrice,
				request.quantity,
				totalValue,
				"DISCOUNTED",
				request.quantity > 0
		);

		return saveIfRepositoryPresent(response);
	}

	private ProductResponseImpl saveIfRepositoryPresent(ProductResponseImpl response) {
		if (productRepository != null) {
			return productRepository.save(response);
		}
		return response;
	}
}
