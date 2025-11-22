package com.example.register;

import com.example.dto.UserRequestImpl;
import com.example.dto.UserResponseImpl;

public interface UserRegister {
	UserResponseImpl processUser(UserRequestImpl request);
	UserResponseImpl validateUser(UserRequestImpl request);
}
