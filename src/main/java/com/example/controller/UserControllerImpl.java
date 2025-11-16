package com.example.controller;

import com.example.dto.UserRequest;
import com.example.dto.UserResponse;
import com.example.register.UserRegister;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserControllerImpl implements UserController {

	@Autowired
	private UserRegister userService;

	@Override
	@PostMapping("/process")
	public ResponseEntity<UserResponse> processUser(@RequestBody UserRequest request) {
		try {
			UserResponse response = userService.processUser(request);
			return ResponseEntity.ok(response);
		} catch (IllegalArgumentException e) {
			return ResponseEntity.badRequest().build();
		}
	}

	@Override
	@PostMapping("/validate")
	public ResponseEntity<UserResponse> validateUser(@RequestBody UserRequest request) {
		try {
			UserResponse response = userService.validateUser(request);
			return ResponseEntity.ok(response);
		} catch (IllegalArgumentException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		}
	}

	@Override
	@PostMapping("/register")
	public ResponseEntity<UserResponse> registerUser(@RequestBody UserRequest request) {
		if (request.name == null || request.email == null) {
			return ResponseEntity.badRequest().build();
		}

		UserResponse response = userService.processUser(request);
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	@Override
	@GetMapping("/health")
	public ResponseEntity<String> health() {
		return ResponseEntity.ok("User service is healthy");
	}
}
