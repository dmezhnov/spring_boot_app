package com.example.controller;

import com.example.dto.UserRequestImpl;
import com.example.dto.UserResponseImpl;
import com.example.register.UserRegisterImpl;
import com.example.repository.UserRepositoryImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for basic user operations such as processing, validation and registration.
 *
 * <p>Usage example:
 * {@code
 * UserRequestImpl request = UserRequestImpl.builder()
 *     .name("Alice")
 *     .email("alice@example.com")
 *     .age(30)
 *     .build();
 * ResponseEntity<UserResponseImpl> response = userController.registerUser(request);
 * }
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

	@Autowired
	private UserRegisterImpl userService;

	@Autowired
	private UserRepositoryImpl userRepository;

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

	@GetMapping("/by-email")
	public ResponseEntity<UserResponseImpl> getByEmail(@RequestParam String email) {
		if (email == null || email.isEmpty()) {
			return ResponseEntity.badRequest().build();
		}
		try {
			UserResponseImpl response = userRepository.findByEmail(email);
			return ResponseEntity.ok(response);
		} catch (EmptyResultDataAccessException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
	}
}
