package com.example.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ProductResponseTest {

    @Test
    void builderAndFields() {
        ProductResponse r = ProductResponse.builder()
                .id(10L)
                .title("X")
                .description("Y")
                .price(5.0)
                .quantity(1)
                .totalValue(5.0)
                .category("C")
                .available(true)
                .build();

        assertEquals(10L, r.getId());
        assertTrue(r.isAvailable());
    }
}
