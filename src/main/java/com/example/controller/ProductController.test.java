package com.example.controller;

import com.example.dto.ProductRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testCreateProduct() throws Exception {
        ProductRequest request = ProductRequest.builder()
                .title("Laptop")
                .description("High-performance laptop")
                .price(999.99)
                .quantity(5)
                .build();

        mockMvc.perform(post("/api/products/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Laptop"))
                .andExpect(jsonPath("$.available").value(true));
    }

    @Test
    void testApplyDiscount() throws Exception {
        ProductRequest request = ProductRequest.builder()
                .title("Mouse")
                .description("Wireless mouse")
                .price(50.0)
                .quantity(10)
                .build();

        mockMvc.perform(post("/api/products/discount?discount=20")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.category").value("DISCOUNTED"));
    }

    @Test
    void testHealth() throws Exception {
        mockMvc.perform(get("/api/products/health"))
                .andExpect(status().isOk());
    }
}
