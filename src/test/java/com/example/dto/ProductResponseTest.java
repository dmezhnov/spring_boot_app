package com.example.dto;

import org.testng.annotations.Test;
import static org.testng.Assert.*;

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

        assertEquals(r.id, 10L);
        assertTrue(r.available);
    }
}
