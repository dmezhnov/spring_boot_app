package com.example.dto;

import org.testng.annotations.Test;
import static org.testng.Assert.*;

class ProductRequestTest {

    @Test
    void builderAndGetters() {
        ProductRequest r = ProductRequest.builder()
                .title("T")
                .description("D")
                .price(1.5)
                .quantity(2)
                .build();

        assertEquals(r.getTitle(), "T");
        assertEquals(r.getDescription(), "D");
        assertEquals(r.getPrice(), 1.5, 1e-6);
        assertEquals(r.getQuantity(), 2);
    }
}
