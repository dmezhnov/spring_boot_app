package com.example.register;

import com.example.dto.ProductRequest;
import com.example.dto.ProductResponse;
import com.example.register.ProductRegisterImpl;

import org.testng.annotations.Test;
import static org.testng.Assert.*;

class ProductRegisterTest {

    private final ProductRegisterImpl service = new ProductRegisterImpl();

    @Test
    void createProductValid() {
        ProductRequest req = ProductRequest.builder()
                .title("Phone")
                .description("Smartphone")
                .price(300.0)
                .quantity(2)
                .build();

        ProductResponse resp = service.createProduct(req);
        assertEquals(resp.title, "Phone");
        assertTrue(resp.available);
        assertEquals(resp.totalValue, 600.0, 1e-6);
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
        assertEquals(resp.category, "DISCOUNTED");
        assertEquals(resp.price, 90.0, 1e-6);
    }
}
