package com.example.dto;

import org.testng.annotations.Test;
import static org.testng.Assert.*;

class ProductRequestTest {

    @Test
    void builderAndFields() {
        ProductRequestImpl r = ProductRequestImpl.builder()
                .title("T")
                .description("D")
                .price(1.5)
                .quantity(2)
                .build();

        assertEquals(r.title, "T");
        assertEquals(r.description, "D");
        assertEquals(r.price, 1.5, 1e-6);
        assertEquals(r.quantity, 2);
    }
}
