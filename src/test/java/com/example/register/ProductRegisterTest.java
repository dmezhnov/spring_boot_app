package com.example.register;

import com.example.dto.ProductRequest;
import com.example.dto.ProductResponse;
import com.example.register.ProductRegister;

import org.testng.annotations.Test;
import static org.testng.Assert.*;

class ProductRegisterTest {

    private final ProductRegister service = new ProductRegister();

    @Test
    void createProductValid() {
        ProductRequest req = ProductRequest.builder()
                .title("Phone")
                .description("Smartphone")
                .price(300.0)
                .quantity(2)
                .build();

        ProductResponse resp = service.createProduct(req);
        assertEquals(resp.getTitle(), "Phone");
        assertTrue(resp.isAvailable());
        assertEquals(resp.getTotalValue(), 600.0, 1e-6);
    }

    @Test
    void applyDiscountCalculates() {
        ProductRequest req = ProductRequest.builder()
                .title("Item")
                .description("")
                .price(100.0)
                .quantity(3)
                .build();

        ProductResponse resp = service.applyDiscount(req, 10.0);
        assertEquals(resp.getCategory(), "DISCOUNTED");
        assertEquals(resp.getPrice(), 90.0, 1e-6);
    }
}
