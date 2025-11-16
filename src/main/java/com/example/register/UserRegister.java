package com.example.register;

import com.example.dto.UserRequest;
import com.example.dto.UserResponse;

public interface UserRegister {
	UserResponse processUser(UserRequest request);
	UserResponse validateUser(UserRequest request);
}
