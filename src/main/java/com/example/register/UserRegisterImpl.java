package com.example.register;

import com.example.dto.UserRequest;
import com.example.dto.UserResponse;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class UserRegisterImpl implements UserRegister {

	private final AtomicLong idGenerator = new AtomicLong(1);

	@Override
	public UserResponse processUser(UserRequest request) {
		if (request.name == null || request.name.isEmpty()) {
			throw new IllegalArgumentException("Name is required");
		}
		if (request.age < 0 || request.age > 150) {
			throw new IllegalArgumentException("Age must be between 0 and 150");
		}

		return UserResponse.builder()
				.id(idGenerator.getAndIncrement())
				.name(request.name.toUpperCase())
				.email(request.email)
				.age(request.age)
				.status("ACTIVE")
				.createdAt(LocalDateTime.now())
				.build();
	}

	@Override
	public UserResponse validateUser(UserRequest request) {
		if (request.name == null || request.name.isEmpty()) {
			throw new IllegalArgumentException("Name cannot be empty");
		}
		if (request.email == null || !request.email.contains("@")) {
			throw new IllegalArgumentException("Invalid email format");
		}

		return UserResponse.builder()
				.id(idGenerator.getAndIncrement())
				.name(request.name)
				.email(request.email)
				.age(request.age)
				.status("VALIDATED")
				.createdAt(LocalDateTime.now())
				.build();
	}
}
