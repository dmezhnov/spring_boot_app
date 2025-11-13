package com.example.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ProductRequestTest {

    @Test
    void builderAndGetters() {
        ProductRequest r = ProductRequest.builder()
                .title("T")
                .description("D")
                .price(1.5)
                .quantity(2)
                .build();

        assertEquals("T", r.getTitle());
        assertEquals("D", r.getDescription());
        assertEquals(1.5, r.getPrice());
        assertEquals(2, r.getQuantity());
    }
}
