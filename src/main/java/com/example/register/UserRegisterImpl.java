package com.example.register;

import com.example.dto.UserRequestImpl;
import com.example.dto.UserResponseImpl;
import com.example.repository.UserRepositoryImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class UserRegisterImpl implements UserRegister {

	private final UserRepositoryImpl userRepository;

	@Autowired
	public UserRegisterImpl(UserRepositoryImpl userRepository) {
		this.userRepository = userRepository;
	}

	UserRegisterImpl() {
		this.userRepository = null;
	}

	@Override
	public UserResponseImpl processUser(UserRequestImpl request) {
		if (request.name == null || request.name.isEmpty()) {
			throw new IllegalArgumentException("Name is required");
		}
		if (request.age < 0 || request.age > 150) {
			throw new IllegalArgumentException("Age must be between 0 and 150");
		}

		UserResponseImpl response = UserResponseImpl.builder()
				.id(null)
				.name(request.name.toUpperCase())
				.email(request.email)
				.age(request.age)
				.status("ACTIVE")
				.createdAt(LocalDateTime.now())
				.build();

		return saveIfRepositoryPresent(response);
	}

	@Override
	public UserResponseImpl validateUser(UserRequestImpl request) {
		if (request.name == null || request.name.isEmpty()) {
			throw new IllegalArgumentException("Name cannot be empty");
		}
		if (request.email == null || !request.email.contains("@")) {
			throw new IllegalArgumentException("Invalid email format");
		}

		UserResponseImpl response = UserResponseImpl.builder()
				.id(null)
				.name(request.name)
				.email(request.email)
				.age(request.age)
				.status("VALIDATED")
				.createdAt(LocalDateTime.now())
				.build();

		return saveIfRepositoryPresent(response);
	}

	private UserResponseImpl saveIfRepositoryPresent(UserResponseImpl response) {
		if (userRepository != null) {
			return userRepository.save(response);
		}
		return response;
	}
}
