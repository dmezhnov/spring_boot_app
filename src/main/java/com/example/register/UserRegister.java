package com.example.register;

import com.example.dto.UserRequestImpl;
import com.example.dto.UserResponseImpl;

/**
 * Use-case boundary for processing and validating user data.
 *
 * <p>Usage example:
 * {@code
 * UserResponseImpl response = userRegister.processUser(request);
 * }
 */
public interface UserRegister {
	UserResponseImpl processUser(UserRequestImpl request);
	UserResponseImpl validateUser(UserRequestImpl request);
}
