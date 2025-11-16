package com.example.controller;

import com.example.dto.UserRequest;
import com.example.dto.UserResponse;
import org.springframework.http.ResponseEntity;

public interface UserController {
	ResponseEntity<UserResponse> processUser(UserRequest request);
	ResponseEntity<UserResponse> validateUser(UserRequest request);
	ResponseEntity<UserResponse> registerUser(UserRequest request);
	ResponseEntity<String> health();
}
