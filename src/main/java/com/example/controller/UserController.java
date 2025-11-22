package com.example.controller;

import com.example.dto.UserRequestImpl;
import com.example.dto.UserResponseImpl;
import com.example.register.UserRegisterImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

	@Autowired
	private UserRegisterImpl userService;

	@PostMapping("/process")
	public ResponseEntity<UserResponseImpl> processUser(@RequestBody UserRequestImpl request) {
		try {
			UserResponseImpl response = userService.processUser(request);
			return ResponseEntity.ok(response);
		} catch (IllegalArgumentException e) {
			return ResponseEntity.badRequest().build();
		}
	}

	@PostMapping("/validate")
	public ResponseEntity<UserResponseImpl> validateUser(@RequestBody UserRequestImpl request) {
		try {
			UserResponseImpl response = userService.validateUser(request);
			return ResponseEntity.ok(response);
		} catch (IllegalArgumentException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		}
	}

	@PostMapping("/register")
	public ResponseEntity<UserResponseImpl> registerUser(@RequestBody UserRequestImpl request) {
		if (request.name == null || request.email == null) {
			return ResponseEntity.badRequest().build();
		}

		UserResponseImpl response = userService.processUser(request);
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	@GetMapping("/health")
	public ResponseEntity<String> health() {
		return ResponseEntity.ok("User service is healthy");
	}
}
