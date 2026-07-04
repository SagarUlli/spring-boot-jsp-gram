package org.jsp.jsp_gram.mapper;

import org.jsp.jsp_gram.dto.User;
import org.jsp.jsp_gram.dto.UserResponse;

public class UserMapper {

	private UserMapper() {
	}

	public static UserResponse toResponse(User user) {

		if (user == null) {
			return null;
		}

		return new UserResponse(user.getId(), user.getFirstname(), user.getLastname(), user.getUsername(),
				user.getEmail(), user.getMobile(), user.getGender(), user.getImageUrl(), user.isPrime());
	}
}