package com.example.register;

import com.example.dto.ProductRequest;
import com.example.dto.ProductResponse;
import com.example.register.ProductRegister;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

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
        assertEquals("Phone", resp.getTitle());
        assertTrue(resp.isAvailable());
        assertEquals(600.0, resp.getTotalValue());
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
        assertEquals("DISCOUNTED", resp.getCategory());
        assertEquals(90.0, resp.getPrice());
    }
}
