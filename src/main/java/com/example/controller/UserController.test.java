package com.example.controller;

import com.example.dto.UserRequest;
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
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testProcessUser() throws Exception {
        UserRequest request = UserRequest.builder()
                .name("John Doe")
                .email("john@example.com")
                .age(30)
                .build();

        mockMvc.perform(post("/api/users/process")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("JOHN DOE"))
                .andExpect(jsonPath("$.status").value("ACTIVE"));
    }

    @Test
    void testValidateUser() throws Exception {
        UserRequest request = UserRequest.builder()
                .name("Jane Doe")
                .email("jane@example.com")
                .age(25)
                .build();

        mockMvc.perform(post("/api/users/validate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("VALIDATED"));
    }

    @Test
    void testHealth() throws Exception {
        mockMvc.perform(get("/api/users/health"))
                .andExpect(status().isOk());
    }
}
